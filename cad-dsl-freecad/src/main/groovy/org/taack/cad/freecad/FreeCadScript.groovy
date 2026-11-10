package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadParams
import org.taack.cad.dsl.CadScript

/**
 * Runs a script with the FreeCAD kernel: cad-dsl-freecad &lt;script.groovy&gt; [args...], as {@link CadScript}.
 *
 * When the script saves FCStd files and declares parameters, it is run again, once per parameter moved by a
 * thousandth, then once more to save the files: each number of a feature that moves with parameters is bound to an
 * affine expression of their cells in the Params spreadsheet (Params.width / 2 - 10), the others are left as they
 * are. The extra runs check, export and save nothing but the FCStd files, and do not open FreeCAD. A parameter whose
 * run fails, or makes other features, binds the numbers equal to it.
 */
@CompileStatic
final class FreeCadScript {
    /**
     * Whether the script is run again by {@link #run}: display() then opens nothing
     */
    static volatile boolean rerun

    private FreeCadScript() {}

    /**
     * @param classpath Jars and directories of classes the script uses, beside those of the DSL
     */
    static void run(File script, List<String> args, List<String> classpath = []) {
        FreeCadSession session = FreeCadSession.get()
        try {
            int saved = runOnce(session, 'record', null, 0d) {
                CadScript.run(script, args, classpath)
            }
            Map<String, Double> params = CadParams.values()
            if (!saved || !params) return
            rerun = true
            params.each { String name, Double value ->
                double delta = Math.max(Math.abs(value), 1d) * 1e-3
                String property = CadParams.PROPERTY_PREFIX + name
                String previous = System.getProperty(property)
                System.setProperty(property, Double.toString(value + delta))
                try {
                    runOnce(session, 'probe', name, delta) { CadScript.run(script, args, classpath) }
                } catch (Throwable failure) {
                    System.err.println "cad-dsl-freecad: the numbers equal to $name are bound to it, the script failed with $name = ${value + delta}: ${failure.message}"
                } finally {
                    if (previous == null) System.clearProperty(property)
                    else System.setProperty(property, previous)
                }
            }
            runOnce(session, 'final', null, 0d) { CadScript.run(script, args, classpath) }
        } finally {
            rerun = false
            session.call(null, 'clearRuns')
        }
    }

    // Run the script in a mode of the FreeCAD kernel, see Sensitivity in cad_dsl_freecad.py; the number of FCStd files saved
    private static int runOnce(FreeCadSession session, String mode, String name, double delta, Runnable script) {
        session.call(null, 'beginRun', mode, name, delta)
        boolean ok = false
        int saved
        try {
            script.run()
            ok = true
        } finally {
            saved = session.call(null, 'endRun', ok) as int
        }
        saved
    }

    static void main(String[] args) {
        if (!args || args[0] in ['-h', '--help']) {
            System.err.println '''\
                Usage: cad-dsl-freecad <script.groovy> [args...]
                Runs a CAD DSL script with the FreeCAD kernel; the arguments following the script are available as args.'''.stripIndent()
            System.exit(args ? 0 : 2)
        }
        File script = new File(args[0])
        if (!script.file) {
            System.err.println "cad-dsl-freecad: no such script: $script"
            System.exit(2)
        }
        run(script, args.toList().drop(1))
    }
}
