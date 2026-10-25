package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * Runs a Groovy script written with the DSL: cad-dsl &lt;script.groovy&gt; [args...]
 *
 * The DSL packages (org.taack.cad.dsl, .geom, .helper), CadDsl.cd() and java.lang.Math are imported,
 * the arguments following the script are available as args, and the classes in the directory of the script too.
 */
@CompileStatic
final class CadScript {
    private CadScript() {}

    /**
     * The imports listed in script-imports.properties, beside this class
     */
    static ImportCustomizer imports() {
        def listed = new Properties()
        CadScript.getResourceAsStream('script-imports.properties').withCloseable { listed.load(it) }
        new ImportCustomizer()
                .addStarImports(names(listed, 'starImports'))
                .addStaticStars(names(listed, 'staticStarImports'))
    }

    private static String[] names(Properties listed, String key) {
        listed.getProperty(key).split(',')*.trim() as String[]
    }

    static void run(File script, List<String> args) {
        def config = new CompilerConfiguration().addCompilationCustomizers(imports())
        // classes beside the script (Contour.groovy for a class Contour) are found as on a classpath
        config.classpathList = [script.absoluteFile.parent]
        CadParams.clear()
        new GroovyShell(CadScript.classLoader, config).run(script, args)
    }

    static void main(String[] args) {
        if (!args || args[0] in ['-h', '--help']) {
            System.err.println '''\
                Usage: cad-dsl <script.groovy> [args...]
                Runs a CAD DSL script; the arguments following the script are available as args.'''.stripIndent()
            System.exit(args ? 0 : 2)
        }
        File script = new File(args[0])
        if (!script.file) {
            System.err.println "cad-dsl: no such script: $script"
            System.exit(2)
        }
        run(script, args.toList().drop(1))
    }
}
