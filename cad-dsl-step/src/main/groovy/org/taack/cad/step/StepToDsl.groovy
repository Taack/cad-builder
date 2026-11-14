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
     * The difference of what the script builds from the STEP file, solid by solid
     * @param label The label of the solid in the STEP file
     * @param volume Its volume
     * @param difference The volume of the symmetric difference of it and the solid built closest to it, null when
     * FreeCAD fails to compute it (booleans between free-form faces)
     * @param builtVolume The volume of the solid built closest to it
     */
    @groovy.transform.Immutable
    static final class SolidDifference {
        String label
        double volume
        Double difference
        double builtVolume

        Double getRelative() { difference == null ? null : volume > 0 ? difference / volume : 0d }
    }

    /**
     * Run the script with the FreeCAD kernel, and compare what it builds with the STEP file, solid by solid
     * @return The differences of the solids of the STEP file, all 0 when the script rebuilds it exactly
     * @throws IllegalStateException when the script cannot be built
     */
    static List<SolidDifference> checkSolids(File step, File script) {
        File built = File.createTempFile('step2dsl-', '.step')
        try {
            try {
                CadScript.run(script, [built.path])
            } catch (RuntimeException e) {
                throw new IllegalStateException("FreeCAD could not build the script: ${e.message}", e)
            }
            (session().call(null, 'step2dsl.differences', step.absolutePath, built.path) as List<List>).collect {
                new SolidDifference(it[0] as String, it[1] as double, it[2] as Double, it[3] as double)
            }
        } finally {
            built.delete()
        }
    }

    private static String percent(double ratio) {
        String.format(Locale.ROOT, '%.4g', ratio * 100)
    }

    /**
     * @return The volume of the symmetric differences of the solids built by the script and those of the STEP file,
     * relative to the STEP volume: 0 when the script rebuilds the STEP file exactly; the solids FreeCAD cannot
     * compare are left out
     */
    static double check(File step, File script) {
        relative(checkSolids(step, script))
    }

    private static double relative(List<SolidDifference> differences) {
        List<SolidDifference> compared = differences.findAll { it.difference != null }
        double volume = compared.sum(0d) { SolidDifference d -> d.volume } as double
        volume > 0 ? (compared.sum(0d) { SolidDifference d -> d.difference } as double) / volume : 0d
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
                List<SolidDifference> differences = checkSolids(step, toRun)
                if (differences.size() > 1) differences.each {
                    System.err.println "step2dsl: ${it.label} ${it.difference == null ? "could not be compared (FreeCAD booleans failed), its volume differs by ${percent(it.volume > 0 ? (it.builtVolume - it.volume) / it.volume : 0d)} %" : "differs by ${percent(it.relative)} % of its volume"}"
                }
                List<String> uncompared = differences.findAll { it.difference == null }*.label
                System.err.println "step2dsl: the script differs from ${step.name} by ${percent(relative(differences))} % of its volume" +
                        (uncompared ? ", without ${uncompared.join(', ')}" : '')
            } catch (RuntimeException e) {
                System.err.println "step2dsl: ${e.message}"
                System.exit(1)
            }
        }
        System.exit(0)
    }
}
