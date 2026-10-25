package org.taack.cad.freecad

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.taack.cad.dsl.PlateOptions
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions
import org.taack.cad.dsl.geom.*
import org.taack.cad.dsl.svg.SvgContour

import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

/**
 * A FreeCADCmd process running cad_dsl_server.py, which the FreeCAD kernel sends its requests to, one JSON object per
 * line over a local socket.
 *
 * FreeCADCmd is the freecad.cmd system property, else the FREECAD_CMD environment variable, else found on the PATH.
 * The process is started on first use and stopped with the JVM; its messages go to the standard output.
 */
@CompileStatic
final class FreeCadSession {
    private static final List<String> SCRIPTS = ['cad_dsl_server.py', 'cad_dsl_freecad.py']
    private static final int CONNECT_TIMEOUT_MS = 120_000
    private static FreeCadSession session

    private final Process process
    private final Socket socket
    private final BufferedReader input
    private final Writer output
    // Ids of the 2D constructions (circles, arcs, ...) sent to FreeCAD, which refers to them afterward
    private final Map<Object, Integer> constructionIds = Collections.synchronizedMap(new WeakHashMap<Object, Integer>())
    private int lastId = 0
    private final Set<String> loadedModules = []

    /**
     * @return The running session, started if needed
     */
    static synchronized FreeCadSession get() {
        if (session == null || !session.process.alive) session = new FreeCadSession()
        session
    }

    private FreeCadSession() {
        File lib = extractScripts()
        ServerSocket server = new ServerSocket(0, 1, InetAddress.loopbackAddress)
        server.soTimeout = CONNECT_TIMEOUT_MS
        String command = freeCadCommand()
        ProcessBuilder builder = new ProcessBuilder(command, new File(lib, SCRIPTS[0]).path)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
        builder.environment().put('CAD_DSL_PORT', server.localPort.toString())
        builder.environment().put('CAD_DSL_LIB', lib.path)
        // the GUI of FreeCAD, for the colors and the visibility of the documents, without window
        builder.environment().put('QT_QPA_PLATFORM', 'offscreen')
        try {
            process = builder.start()
            process.outputStream.close()
            socket = server.accept()
        } catch (SocketTimeoutException e) {
            process?.destroy()
            throw new IllegalStateException("$command did not connect within ${CONNECT_TIMEOUT_MS / 1000} s", e)
        } finally {
            server.close()
        }
        input = new BufferedReader(new InputStreamReader(socket.inputStream, StandardCharsets.UTF_8))
        output = new BufferedWriter(new OutputStreamWriter(socket.outputStream, StandardCharsets.UTF_8))
        Runtime.runtime.addShutdownHook(new Thread({ stop() }))
    }

    /**
     * @return FreeCADCmd: the freecad.cmd system property, the FREECAD_CMD environment variable, or found on the PATH
     */
    static String freeCadCommand() {
        String configured = System.getProperty('freecad.cmd') ?: System.getenv('FREECAD_CMD')
        if (configured) return configured
        List<String> names = ['FreeCADCmd', 'freecadcmd', 'FreeCADCmd.exe', 'freecadcmd.exe']
        List<File> candidates = (System.getenv('PATH') ?: '').split(File.pathSeparator).toList().collectMany { String dir ->
            names.collect { new File(dir, it) }
        } + [new File('/Applications/FreeCAD.app/Contents/Resources/bin/freecadcmd')]
        File found = candidates.find { it.file && it.canExecute() }
        if (!found) throw new IllegalStateException('FreeCADCmd not found: install FreeCAD 1.0 or later, or set the freecad.cmd system property (or FREECAD_CMD) to its path')
        found.path
    }

    private static File extractScripts() {
        File dir = File.createTempDir('cad-dsl-freecad', '')
        dir.deleteOnExit()
        for (String script in SCRIPTS) {
            File f = new File(dir, script)
            FreeCadSession.getResourceAsStream(script).withCloseable { InputStream s -> f.bytes = s.bytes }
            f.deleteOnExit()
        }
        dir
    }

    // Not through call(), which may be running: the server stops at the end of its input
    private void stop() {
        socket.close()
        if (!process.waitFor(2, TimeUnit.SECONDS)) process.destroy()
        if (!process.waitFor(3, TimeUnit.SECONDS)) process.destroyForcibly()
    }

    /**
     * Run method on the model or assembly of handle (a function of the kernel when null) in FreeCAD
     * @return Its result, from JSON
     * @throws IllegalArgumentException when it failed on a ValueError (the model is wrong), IllegalStateException else
     */
    synchronized Object call(Integer handle, String method, Object... args) {
        String request = JsonOutput.toJson([o: handle, m: method, a: args.collect { encode(it) }])
        output.write(request)
        output.write('\n')
        output.flush()
        String line = input.readLine()
        if (line == null) throw new IllegalStateException("FreeCAD stopped while running $method, see its messages above")
        Map answer = (Map) new JsonSlurper().parseText(line)
        if (answer.containsKey('e')) {
            String message = answer.e as String
            String error = message.startsWith('ValueError: ') ? message.substring('ValueError: '.length()) : message
            if (message.startsWith('ValueError: ')) throw new IllegalArgumentException("$method: $error")
            throw new IllegalStateException("$method: $error\n${answer.t}")
        }
        answer.r
    }

    /**
     * Load Python code in FreeCAD as module name, once; its functions are then called as 'name.function'
     */
    synchronized void load(String name, URL source) {
        if (loadedModules.add(name)) call(null, 'loadModule', name, source.getText(StandardCharsets.UTF_8.name()))
    }

    /**
     * @return A new id, to give to a 2D construction made in FreeCAD with {@link #register}
     */
    synchronized int newId() {
        ++lastId
    }

    /**
     * Make the construction known by FreeCAD under id
     */
    def <T> T register(T construction, int id) {
        constructionIds.put(construction, id)
        construction
    }

    private Object encode(Object o) {
        switch (o) {
            case null:
            case Number:
            case Boolean:
            case String:
                return o
            case Vec:
                Vec v = (Vec) o
                return [v.x, v.y, v.z]
            case Vec2d:
                Vec2d v = (Vec2d) o
                return [v.x, v.y]
            case Enum:
                return ((Enum) o).name()
            case IConstruction:
            case IOpenShape2d:
                return construction(o)
            case TextOptions:
                TextOptions t = (TextOptions) o
                return [font  : t.font, bold: t.bold, italic: t.italic, align: t.align.name(), valign: t.valign.name(),
                        angle : t.angle, direction: encode(t.direction), normal: encode(t.normal)]
            case SvgOptions:
                SvgOptions s = (SvgOptions) o
                return [align : s.align.name(), valign: s.valign.name(), angle: s.angle, direction: encode(s.direction),
                        normal: encode(s.normal)]
            case PlateOptions:
                PlateOptions p = (PlateOptions) o
                return [degree     : p.degree, pointsOnCurve: p.pointsOnCurve, iterations: p.iterations,
                        tolerance  : p.tolerance, maxSegments: p.maxSegments, maxDegree: p.maxDegree]
            case SvgContour:
                SvgContour c = (SvgContour) o
                return [start: encode(c.start), segments: c.segments.collect { SvgContour.Segment s -> segment(s) }]
            case Trsf:
                return ((Trsf) o).values().toList()
            case Map:
                return ((Map) o).collectEntries { k, v -> [k.toString(), encode(v)] }
            case Collection:
                return ((Collection) o).collect { encode(it) }
            case Object[]:
                return ((Object[]) o).collect { encode(it) }
            default:
                throw new IllegalArgumentException("Cannot send ${o.getClass().name} to FreeCAD")
        }
    }

    private static List<Object> segment(SvgContour.Segment s) {
        switch (s) {
            case SvgContour.Line:
                return ['L', s.to.x, s.to.y] as List<Object>
            case SvgContour.Arc:
                SvgContour.Arc a = (SvgContour.Arc) s
                return ['A', a.via.x, a.via.y, a.to.x, a.to.y] as List<Object>
            default:
                SvgContour.Cubic c = (SvgContour.Cubic) s
                return ['C', c.control1.x, c.control1.y, c.control2.x, c.control2.y, c.to.x, c.to.y] as List<Object>
        }
    }

    // A 2D construction: described with a new id the first time, then referred to by its id
    private Map<String, Object> construction(Object o) {
        Integer id = constructionIds.get(o)
        if (id != null) return [ref: (Object) id]
        id = newId()
        constructionIds.put(o, id)
        Map<String, Object> d = [id: (Object) id, t: o.getClass().simpleName]
        switch (o) {
            case Circle2d:
                Circle2d c = (Circle2d) o
                d.putAll(pos: encode(c.pos), radius: c.radius, reverse: c.reverse)
                break
            case Ellipse2d:
                Ellipse2d e = (Ellipse2d) o
                d.putAll(pos: encode(e.pos), dir: encode(e.dir), major: e.majRadius, minor: e.minRadius)
                break
            case ArcOfCircle2d:
                ArcOfCircle2d a = (ArcOfCircle2d) o
                d.putAll(circle: construction(a.circle2d), angle1: a.angle1, angle2: a.angle2, from: encode(a.ptFrom),
                        to: encode(a.ptTo), reversed: a.reversed)
                break
            case TrimmedCurve2d:
                TrimmedCurve2d t = (TrimmedCurve2d) o
                d.putAll(curve: construction(t.curve), u1: t.u1, u2: t.u2, reversed: t.reversed)
                break
            case Mirrored2d:
                Mirrored2d m = (Mirrored2d) o
                d.putAll(curve: construction(m.curve), pos: encode(m.pos), dir: encode(m.dir))
                break
            case Spline2d:
                d.put('through', encode(((Spline2d) o).through))
                break
            case Edge2d:
                d.put('to', encode(((Edge2d) o).to))
                break
            case Arc2d:
                Arc2d a = (Arc2d) o
                d.putAll(to: encode(a.to), via: encode(a.via))
                break
            default:
                throw new IllegalArgumentException("Unsupported 2D shape: $o")
        }
        d
    }
}
