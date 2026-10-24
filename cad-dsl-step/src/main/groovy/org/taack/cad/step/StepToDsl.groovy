package org.taack.cad.step

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadScript
import org.taack.cad.freecad.FreeCadSession

/**
 * Converts STEP files to CAD DSL scripts: step2dsl &lt;file.step&gt; [script.groovy] [--check]
 *
 * The STEP file is read by FreeCAD, which recognizes the solids (see step2dsl.py): the script rebuilds the assembly
 * tree, each distinct part once, from primitives, prisms, revolutions, and the holes and bosses on them.
 */
@CompileStatic
final class StepToDsl {
    private StepToDsl() {}

    private static FreeCadSession session() {
        FreeCadSession s = FreeCadSession.get()
        s.load('step2dsl', StepToDsl.getResource('step2dsl.py'))
        s
    }

    /**
     * @return The Groovy script rebuilding the STEP file with the DSL
     */
    static String convert(File step) {
        if (!step.file) throw new IllegalArgumentException("No such STEP file: $step")
        session().call(null, 'step2dsl.convert', step.absolutePath) as String
    }

    /**
     * Run the script with the FreeCAD kernel, and compare what it builds with the STEP file
     * @return The volume of the symmetric difference of both, relative to the STEP volume: 0 when the script rebuilds
     * the STEP file exactly
     */
    static double check(File step, File script) {
        File built = File.createTempFile('step2dsl-', '.step')
        try {
            CadScript.run(script, [built.path])
            double volume = session().call(null, 'step2dsl.volume', step.absolutePath) as double
            (session().call(null, 'step2dsl.difference', step.absolutePath, built.path) as double) / volume
        } finally {
            built.delete()
        }
    }

    static void main(String[] args) {
        List<String> files = args.findAll { !it.startsWith('-') }
        boolean checking = '--check' in args
        if (!files || files.size() > 2 || args.any { it.startsWith('-') && it != '--check' }) {
            System.err.println '''\
                Usage: step2dsl <file.step> [script.groovy] [--check]
                Writes the CAD DSL script rebuilding the STEP file, to the standard output when no script is given.
                --check runs the script with the FreeCAD kernel, and prints how far what it builds is from the STEP file.'''.stripIndent()
            System.exit(args.any { it in ['-h', '--help'] } ? 0 : 2)
        }
        File step = new File(files[0])
        String script = convert(step)
        File out = files.size() > 1 ? new File(files[1]) : null
        if (out != null) out.text = script
        else print script
        if (checking) {
            File toRun = out != null ? out : File.createTempFile('step2dsl-', '.groovy').with { it.text = script; it }
            try {
                double difference = check(step, toRun)
                System.err.println "step2dsl: the script differs from ${step.name} by ${String.format(Locale.ROOT, '%.4g', difference * 100)} % of its volume"
            } catch (RuntimeException e) {
                System.err.println "step2dsl: FreeCAD could not build the script: ${e.message}"
                System.exit(1)
            }
        }
        System.exit(0)
    }
}
