package org.taack.lsp.groovy

import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.Diagnostic
import org.eclipse.lsp4j.DiagnosticSeverity
import org.eclipse.lsp4j.Position
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class GroovyCompletionEngineTest {
    static final ImportCustomizer imports = new ImportCustomizer()
            .addStarImports('org.taack.lsp.groovy.fixture')
            .addStaticStars('org.taack.lsp.groovy.fixture.Html')
    final GroovyCompletionEngine engine = new GroovyCompletionEngine(GroovyCompletionEngineTest.classLoader, [], [imports])

    /**
     * @param source The code, with | at the cursor
     */
    List<CompletionItem> complete(String source) {
        int cursor = source.indexOf('|')
        String text = source.substring(0, cursor) + source.substring(cursor + 1)
        engine.complete(text, SourceText.positionOf(text, cursor))
    }

    static List<String> labels(List<CompletionItem> items) {
        items*.label
    }

    @Test
    void delegateOfTheInnermostClosureFirst() {
        def names = labels(complete('''
            html {
                body {
                    |
                }
            }
        '''))
        assertTrue(names.containsAll(['p', 'div', 'cssClass', 'table']), names.toString())
        // the outer delegate is reachable through the owner
        assertTrue(names.contains('body'), names.toString())
        assertTrue(names.indexOf('p') < names.indexOf('body'), names.toString())
    }

    @Test
    void prefixFiltersAndIsReplaced() {
        def items = complete('''
            html {
                body {
                    di|
                }
            }
        ''')
        assertEquals(['div'], labels(items))
        def edit = items[0].textEdit.getLeft()
        assertEquals(3, edit.range.start.line)
        assertEquals(20, edit.range.start.character)
        assertEquals(22, edit.range.end.character)
        assertEquals('div', edit.newText)
    }

    @Test
    void identifierAfterTheCursorIsReplaced() {
        def items = complete('''
            html {
                bo|dy {
                }
            }
        ''')
        assertEquals(['body'], labels(items))
        assertEquals(20, items[0].textEdit.getLeft().range.end.character)
    }

    @Test
    void membersOfAnExpression() {
        def names = labels(complete('''
            html {
                body {
                    p('text').|
                }
            }
        '''))
        assertEquals(['bold', 'italic'], names.take(2))
        // GDK methods follow
        assertTrue(names.contains('with'), names.toString())
    }

    @Test
    void incompleteCode() {
        assertEquals(['bold'], labels(complete('''
            html {
                body {
                    p('text').b|''')))
        assertEquals(['cssClass'], labels(complete('''
            html {
                body {
                    p(css|''')))
    }

    @Test
    void delegateOnly() {
        def names = labels(complete('''
            html {
                body {
                    table {
                        |
                    }
                }
            }
        '''))
        assertTrue(names.contains('row'), names.toString())
        assertFalse(names.contains('p'), names.toString())
    }

    @Test
    void genericTarget() {
        def names = labels(complete('''
            configure(new Paragraph()) {
                |
            }
        '''))
        assertTrue(names.containsAll(['bold', 'italic']), names.toString())
    }

    @Test
    void localVariablesBeforeTheCursor() {
        def items = complete('''
            def answer = 42
            String name = 'x'
            html {
                body { String inner = ''; p(name) }
                a|
            }
            def after = 1
        ''')
        assertEquals(['answer'], labels(items))
        assertEquals('int', items[0].detail)
    }

    @Test
    void staticMembersOfAClass() {
        def names = labels(complete('Html.|'))
        assertTrue(names.containsAll(['html', 'configure']), names.toString())
        assertFalse(names.contains('head'), names.toString())
    }

    @Test
    void signaturesShowTheDelegates() {
        def item = complete('html { bo| }').find { it.label == 'body' }
        assertEquals('Body body(@DelegatesTo(Body) Closure)', item.detail)
    }

    @Test
    void insideACompileStaticClass() {
        def names = labels(complete('''
            import groovy.transform.CompileStatic

            @CompileStatic
            class Page {
                private int count

                static Html page() {
                    html {
                        head {
                            |
                        }
                    }
                }
            }
        '''))
        assertTrue(names.containsAll(['title', 'meta']), names.toString())
        // static context: the instance field is not offered
        assertFalse(names.contains('count'), names.toString())
    }

    @Test
    void membersOfTheEnclosingClass() {
        def names = labels(complete('''
            class Page {
                private int count
                void render() {
                    html { co| }
                }
            }
        '''))
        assertTrue(names.containsAll(['count', 'configure']), names.toString())
    }

    @Test
    void nothingInStringsAndComments() {
        assertEquals([], complete("html { body { p('a|') } }"))
        assertEquals([], complete('html { // body |\n }'))
    }

    @Test
    void diagnostics() {
        List<Diagnostic> syntax = engine.diagnose('html {\n  body {\n}')
        assertEquals(DiagnosticSeverity.Error, syntax[0].severity, syntax.toString())

        List<Diagnostic> unresolved = engine.diagnose('def x = new Unknown()')
        assertTrue(unresolved*.message.any { it.contains('unable to resolve class Unknown') }, unresolved.toString())

        List<Diagnostic> typing = engine.diagnose('html {\n  body {\n    paragraph("x")\n  }\n}')
        assertEquals(1, typing.size(), typing.toString())
        assertEquals(DiagnosticSeverity.Warning, typing[0].severity)
        assertEquals(2, typing[0].range.start.line)
        assertTrue(typing[0].message.contains('paragraph'), typing[0].message)

        assertEquals([], engine.diagnose('html { body { p("x").bold() } }'))
    }

    @Test
    void noCodeRunAtCompileTime() {
        System.clearProperty('lsp.ran')
        engine.diagnose('''
            @groovy.transform.ASTTest(value = { System.setProperty('lsp.ran', 'yes') })
            def x = 1
        ''')
        engine.complete('@groovy.transform.ASTTest(value = { System.setProperty("lsp.ran", "yes") })\ndef x = 1\nx.', new Position(2, 2))
        assertNull(System.getProperty('lsp.ran'))
    }
}
