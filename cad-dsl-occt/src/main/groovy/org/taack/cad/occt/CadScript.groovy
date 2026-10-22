package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

/**
 * Runs a Groovy script written with the DSL: cad-dsl &lt;script.groovy&gt; [args...]
 *
 * The DSL packages (org.taack.cad.dsl, .geom, .helper), CadDsl.cd() and java.lang.Math are imported,
 * and the arguments following the script are available as args.
 */
@CompileStatic
final class CadScript {
    private CadScript() {}

    static ImportCustomizer imports() {
        new ImportCustomizer()
                .addStarImports('org.taack.cad.dsl', 'org.taack.cad.dsl.geom', 'org.taack.cad.dsl.helper')
                .addStaticStars('org.taack.cad.dsl.CadDsl', 'java.lang.Math')
    }

    static void run(File script, List<String> args) {
        def config = new CompilerConfiguration().addCompilationCustomizers(imports())
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
