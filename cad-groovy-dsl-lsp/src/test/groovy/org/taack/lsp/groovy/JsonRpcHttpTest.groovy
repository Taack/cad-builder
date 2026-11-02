package org.taack.lsp.groovy

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The messages as the Taack editor (LspClient.kt) sends them, one per POST, with a new server for each one
 */
class JsonRpcHttpTest {
    final Map<String, String> documents = [:]
    final GroovyCompletionEngine engine = new GroovyCompletionEngine(JsonRpcHttpTest.classLoader, [],
            [new ImportCustomizer().addStaticStars('org.taack.lsp.groovy.fixture.Html')])

    def post(Map message) {
        String response = JsonRpcHttp.handle(JsonOutput.toJson([jsonrpc: '2.0'] + message), new GroovyLanguageServer(engine, documents))
        response ? new JsonSlurper().parseText(response) : null
    }

    @Test
    void editorSession() {
        def init = post(id: 1, method: 'initialize', params: [processId: null, rootUri: null, capabilities: [:]])
        assertEquals('groovy-dsl-lsp', init.result.serverInfo.name)
        assertNull(post(method: 'initialized', params: [:]))

        String uri = 'taack:/page/lsp'
        post(method: 'textDocument/didOpen', params: [textDocument: [uri: uri, languageId: 'groovy', version: 0, text: 'html {\n  bo\n}']])
        def completion = post(id: 2, method: 'textDocument/completion', params: [textDocument: [uri: uri], position: [line: 1, character: 4]])
        assertEquals(['body'], completion.result.items*.label)
        assertEquals([start: [line: 1, character: 2], end: [line: 1, character: 4]], completion.result.items[0].textEdit.range)

        post(method: 'textDocument/didChange', params: [textDocument: [uri: uri, version: 1], contentChanges: [[text: 'html {\n  bod()\n}']]])
        def diagnostics = post(id: 3, method: 'textDocument/diagnostic', params: [textDocument: [uri: uri]])
        assertEquals(1, diagnostics.result.items.size())
        assertEquals(1, diagnostics.result.items[0].range.start.line)
    }
}
