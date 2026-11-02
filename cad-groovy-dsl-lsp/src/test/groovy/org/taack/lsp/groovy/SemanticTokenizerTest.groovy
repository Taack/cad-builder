package org.taack.lsp.groovy

import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class SemanticTokenizerTest {
    final GroovyCompletionEngine engine = new GroovyCompletionEngine(SemanticTokenizerTest.classLoader, [],
            [new ImportCustomizer().addStaticStars('org.taack.lsp.groovy.fixture.Html')])

    /**
     * The tokens as "text:type[/modifiers]", decoded from the LSP encoding
     */
    static List<String> decode(String text, List<Integer> data) {
        String[] lines = text.split('\n', -1)
        List<String> res = []
        int line = 0, column = 0
        for (int i = 0; i < data.size(); i += 5) {
            if (data[i] > 0) column = 0
            line += data[i]
            column += data[i + 1]
            String t = lines[line].substring(column, column + data[i + 2])
            List<String> mods = SemanticTokenizer.MODIFIERS.findIndexValues { true }.findAll { (data[i + 4] >> (int) it) & 1 }
                    .collect { SemanticTokenizer.MODIFIERS[(int) it] }
            res << "$t:${SemanticTokenizer.TYPES[data[i + 3]]}${mods ? '/' + mods.join(',') : ''}".toString()
        }
        res
    }

    @Test
    void lexicalAndSemantic() {
        String text = '''\
import groovy.transform.CompileStatic
/** doc
 * more */
@CompileStatic
class Plane {
    static final double RIGHT = 1.5d // right
    String name = "a${RIGHT}b"

    double span(double factor) {
        double s = RIGHT * factor
        name.size() + s
    }
}
'''
        List<String> tokens = decode(text, engine.semanticTokens(text))
        [
                'import:keyword', '/** doc:comment', ' * more */:comment', '@:decorator', 'CompileStatic:decorator',
                'class:keyword', 'Plane:type', 'static:keyword', 'final:keyword', 'double:keyword',
                'RIGHT:property/declaration,static', '1.5d:number', '// right:comment', 'String:type',
                'name:property/declaration', '"a$:string', 'RIGHT:property/static', 'b":string',
                'span:method/declaration', 'factor:parameter/declaration', 's:variable/declaration',
                'factor:parameter', 'name:property', 'size:method', 's:variable'
        ].each { assertTrue(tokens.contains(it), "$it in $tokens") }
    }

    @Test
    void dslAndUnparsableCode() {
        String dsl = 'html {\n  body { }\n}'
        assertEquals(['html:method/static', 'body:method'], decode(dsl, engine.semanticTokens(dsl)))

        // lexical tokens only
        String broken = 'def x = "a" +\nif (true) {'
        assertEquals(['def:keyword', '"a":string', 'if:keyword', 'true:keyword'], decode(broken, engine.semanticTokens(broken)))
    }
}
