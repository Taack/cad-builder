package org.taack.lsp.groovy

import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * Completion of CAD DSL scripts (../cad-builder), a jar compiled by another Groovy and only on the classpath of the
 * engine, with the default imports of its scripts. Skipped when the jar is missing.
 */
class CadDslCompletionTest {
    GroovyCompletionEngine engine

    @BeforeEach
    void engine() {
        File jar = new File(System.getProperty('cadDslJar') ?: '')
        Assumptions.assumeTrue(jar.file, "no CAD DSL jar at $jar")
        def imports = new ImportCustomizer()
                .addStarImports('org.taack.cad.dsl', 'org.taack.cad.dsl.geom', 'org.taack.cad.dsl.helper')
                .addStaticStars('org.taack.cad.dsl.CadDsl', 'java.lang.Math')
        engine = new GroovyCompletionEngine(new GroovyOnlyClassLoader(), [jar.path], [imports])
    }

    List<String> labels(String source) {
        int cursor = source.indexOf('|')
        String text = source.substring(0, cursor) + source.substring(cursor + 1)
        engine.complete(text, SourceText.positionOf(text, cursor))*.label
    }

    @Test
    void assembly() {
        def names = labels('''
            assembly('motor') {
                |
            }
        ''')
        // CadDslAssembly, then the static imports
        assertTrue(names.containsAll(['part', 'cd', 'param', 'sqrt']), names.toString())
        assertTrue(names.indexOf('part') < names.indexOf('cd'), names.toString())
    }

    @Test
    void wire() {
        def names = labels('''
            double side = param('side', 20)
            cd().wireFrom(new Vec(0, 0, 0)) {
                |
            }
        ''')
        // CadDslEdge first, the variables of the script before
        assertTrue(names.take(4) == ['side', 'move', 'to', 'edge'], names.toString())
        assertTrue(names.contains('at3d') && !names.contains('getAt3d'), names.toString())
    }

    @Test
    void classesOfTheApplicationAreHidden() {
        def diagnostics = engine.diagnose('def x = new org.taack.lsp.groovy.fixture.Html()\ndef y = new org.junit.jupiter.api.Test()')
        assertTrue(diagnostics.size() == 2 && diagnostics.every { it.message.contains('unable to resolve class') }, diagnostics.toString())
    }

    @Test
    void chain() {
        def names = labels('cd().box(1, 2, 3).|')
        assertTrue(names.containsAll(['cut', 'fuse']), names.toString())
    }
}
