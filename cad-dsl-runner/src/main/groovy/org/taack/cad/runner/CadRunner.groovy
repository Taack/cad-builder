package org.taack.cad.runner

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.codehaus.groovy.runtime.StackTraceUtils
import org.taack.cad.dsl.CadDisplay
import org.taack.cad.dsl.CadParams
import org.taack.cad.dsl.CadScript
import org.taack.cad.dsl.feature.CadFeatureRecorder
import org.taack.cad.dsl.feature.RecordingKernel
import org.taack.cad.dsl.kernel.CadKernel
import org.taack.cad.dsl.kernel.CadKernels

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

/**
 * Runs CAD DSL scripts for an IDE, one at a time, in a JVM that stays up between the runs: cad-dsl-runner &lt;port&gt;
 *
 * The runner connects to port on the loopback interface, reads one JSON request per line from it, and answers with
 * one JSON event per line. Its standard output and error are left to the scripts and the kernel.
 * The DSL keeps the parameters in static state: the runs are sequential.
 *
 * Requests:
 * <pre>
 * {"type": "run", "id": 1, "script": "/models/rail.groovy", "args": [], "params": {"length": 120},
 *  "classpath": ["/home/me/.m2/repository/com/acme/parts/1.0/parts-1.0.jar"]}
 * {"type": "shape", "id": 2, "feature": "assembly:rail/part:body/cut0"}
 * {"type": "stop"}
 * </pre>
 * The classpath of a run, optional, holds the jars and directories of classes its script uses: shared parts for
 * instance. The classes of the runner, the DSL and Groovy among them, come first.
 * Events:
 * <pre>
 * {"type": "ready", "protocol": 1}                                  once connected
 * {"type": "display", "id": 1, "file": "/tmp/.../run-1/1.glb", "feature": "assembly:rail"}
 *                                                                   for each display() given no file name
 * {"type": "done", "id": 1, "params": {"length": 120.0}, "millis": 840, "features": [...]}
 * {"type": "error", "id": 1, "message": "...", "trace": "...", "params": {}, "millis": 12, "features": [...]}
 * </pre>
 * {"type": "shape", "id": 2, "file": "/tmp/.../run-1/shape-2.glb"}   no file when the feature has no shape
 * </pre>
 * The features are the tree of the operations of the script, see CadFeatureRecorder.tree() in cad-dsl: up to the
 * failing one, marked with "error": true, when the run fails. The feature of a display event is the one displayed.
 * A shape request writes the shapes of a feature of the last run (a primitive, a prism, the tools of a cut...) to a
 * glTF file, in the coordinates of its part.
 * The glTF files of a run are deleted when the next run starts, and all of them when the runner stops.
 */
@CompileStatic
final class CadRunner {
    static final int PROTOCOL = 1

    private final File workDir
    private int runCount = 0
    private File runDir
    // Features of the last run, with their shapes
    private CadFeatureRecorder recorder

    CadRunner(File workDir) {
        this.workDir = workDir
    }

    /**
     * Answers the requests read from input, until a stop request or the end of input
     */
    void serve(BufferedReader input, Writer output) {
        Consumer<Map> emit = { Map event -> send(output, event) }
        emit.accept([type: 'ready', protocol: PROTOCOL])
        for (String line = input.readLine(); line != null; line = input.readLine()) {
            if (!line.trim()) continue
            Map request = (Map) new JsonSlurper().parseText(line)
            if (request.type == 'stop') return
            if (request.type == 'run') run(request, emit)
            else if (request.type == 'shape') shape(request, emit)
            else emit.accept([type: 'error', id: request.id, message: "Unknown request type: ${request.type}".toString()])
        }
    }

    /**
     * Runs the script of request, its display() calls written to glTF files, and emits an event per file, then a done
     * or error event
     */
    void run(Map request, Consumer<Map> emit) {
        Object id = request.id
        File dir = newRunDir()
        List<File> displayed = []
        CadDisplay.redirect {
            File gltf = new File(dir, "${displayed.size() + 1}${CadDisplay.GLTF_EXTENSION}")
            displayed << gltf
            gltf.path
        }
        Map<String, String> previous = setParams((Map) request.params ?: [:])
        CadKernel kernel = CadKernels.get()
        recorder = new CadFeatureRecorder()
        CadKernels.set(new RecordingKernel(kernel, recorder))
        long start = System.nanoTime()
        Map end
        try {
            CadScript.run(new File((String) request.script), (List<String>) request.args ?: [], (List<String>) request.classpath ?: [])
            end = [type: 'done', id: id]
        } catch (Throwable failure) {
            // compile errors and failed assertions too: the IDE shows them, the runner goes on
            end = [type: 'error', id: id, message: failure.message ?: failure.class.name, trace: stackTrace(failure)]
            recorder.failed = true
        } finally {
            CadKernels.set(kernel)
            CadDisplay.redirect(null)
            restoreParams(previous)
        }
        long millis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start)
        List<Map<String, Object>> features = recorder.tree()
        displayed.findAll { it.file }.each { File gltf ->
            emit.accept([type: 'display', id: id, file: gltf.path, feature: recorder.displayedId(gltf.path)])
        }
        end.params = CadParams.values()
        end.millis = millis
        end.features = features
        emit.accept(end)
    }

    /**
     * Writes the shapes of a feature of the last run to a glTF file, and emits a shape event, without a file when the
     * feature has no shape
     */
    void shape(Map request, Consumer<Map> emit) {
        Map event = [type: 'shape', id: request.id]
        try {
            File gltf = new File(runDir, "shape-${request.id}${CadDisplay.GLTF_EXTENSION}")
            if (recorder?.writeShapes((String) request.feature, gltf.path)) event.file = gltf.path
        } catch (Throwable failure) {
            event = [type: 'error', id: request.id, message: failure.message ?: failure.class.name, trace: stackTrace(failure)]
        }
        emit.accept(event)
    }

    private File newRunDir() {
        runDir?.deleteDir()
        runDir = new File(workDir, "run-${++runCount}")
        runDir.mkdirs()
        runDir
    }

    // Sets the cad.param.<name> system properties, returns their previous values
    private static Map<String, String> setParams(Map params) {
        Map<String, String> previous = [:]
        params.each { Object name, Object value ->
            String property = CadParams.PROPERTY_PREFIX + name
            previous[property] = System.getProperty(property)
            System.setProperty(property, value.toString())
        }
        previous
    }

    private static void restoreParams(Map<String, String> previous) {
        previous.each { String property, String value ->
            if (value == null) System.clearProperty(property)
            else System.setProperty(property, value)
        }
    }

    private static String stackTrace(Throwable failure) {
        StringWriter trace = new StringWriter()
        StackTraceUtils.deepSanitize(failure).printStackTrace(new PrintWriter(trace))
        trace.toString()
    }

    private static void send(Writer output, Map event) {
        output.write(JsonOutput.toJson(event))
        output.write('\n')
        output.flush()
    }

    static void main(String[] args) {
        if (args.length != 1 || !args[0].isInteger()) {
            System.err.println '''\
                Usage: cad-dsl-runner <port>
                Connects to port on the loopback interface and runs the CAD DSL scripts it is sent.'''.stripIndent()
            System.exit(2)
        }
        File workDir = Files.createTempDirectory('cad-dsl-runner').toFile()
        Runtime.runtime.addShutdownHook(new Thread({ workDir.deleteDir() }))
        try (Socket socket = new Socket(InetAddress.loopbackAddress, args[0] as int)) {
            new CadRunner(workDir).serve(
                    new BufferedReader(new InputStreamReader(socket.inputStream, StandardCharsets.UTF_8)),
                    new OutputStreamWriter(socket.outputStream, StandardCharsets.UTF_8))
        }
    }
}
