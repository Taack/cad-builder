package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.eclipse.lsp4j.*
import org.eclipse.lsp4j.jsonrpc.messages.Either
import org.eclipse.lsp4j.services.*

import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Language Server for Groovy code written with a DSL, see {@link GroovyCompletionEngine}.
 * <p>
 * Full text synchronization. Diagnostics are pulled (LSP 3.17), and pushed too when a client is connected, for the
 * clients without pull diagnostics. Over HTTP, an instance can live for one message only, see {@link JsonRpcHttp}: the
 * documents are then kept by the caller, in the HTTP session for instance.
 */
@CompileStatic
class GroovyLanguageServer implements LanguageServer, LanguageClientAware {
    private final Map<String, String> documents
    private final GroovyCompletionEngine engine
    private LanguageClient client

    GroovyLanguageServer(GroovyCompletionEngine engine, Map<String, String> documents = new ConcurrentHashMap<>()) {
        this.engine = engine
        this.documents = documents
    }

    private final TextDocumentService textDocumentService = new TextDocumentService() {
        @Override
        void didOpen(DidOpenTextDocumentParams params) {
            documents.put(params.textDocument.uri, params.textDocument.text)
            publish(params.textDocument.uri)
        }

        @Override
        void didChange(DidChangeTextDocumentParams params) {
            if (params.contentChanges) documents.put(params.textDocument.uri, params.contentChanges.last().text)
            publish(params.textDocument.uri)
        }

        @Override
        void didClose(DidCloseTextDocumentParams params) {
            documents.remove(params.textDocument.uri)
        }

        @Override
        void didSave(DidSaveTextDocumentParams params) {
        }

        @Override
        CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
            String text = documents.get(params.textDocument.uri)
            List<CompletionItem> items = text != null ? engine.complete(text, params.position) : []
            // isIncomplete: the items depend on the prefix, the client asks again when it changes
            CompletableFuture.completedFuture(Either.<List<CompletionItem>, CompletionList> forRight(new CompletionList(true, items)))
        }

        @Override
        CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
            String text = documents.get(params.textDocument.uri)
            List<Diagnostic> diagnostics = text != null ? engine.diagnose(text) : []
            CompletableFuture.completedFuture(new DocumentDiagnosticReport(new RelatedFullDocumentDiagnosticReport(diagnostics)))
        }
    }

    private final WorkspaceService workspaceService = new WorkspaceService() {
        @Override
        void didChangeConfiguration(DidChangeConfigurationParams params) {
        }

        @Override
        void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        }
    }

    private void publish(String uri) {
        if (client == null) return
        String text = documents.get(uri)
        if (text != null) client.publishDiagnostics(new PublishDiagnosticsParams(uri, engine.diagnose(text)))
    }

    @Override
    void connect(LanguageClient client) {
        this.client = client
    }

    @Override
    CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        ServerCapabilities capabilities = new ServerCapabilities()
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full)
        capabilities.setCompletionProvider(new CompletionOptions(false, ['.']))
        capabilities.setDiagnosticProvider(new DiagnosticRegistrationOptions(false, false))
        CompletableFuture.completedFuture(new InitializeResult(capabilities, new ServerInfo('groovy-dsl-lsp', '0.1')))
    }

    @Override
    CompletableFuture<Object> shutdown() {
        CompletableFuture.completedFuture(null)
    }

    @Override
    void exit() {
    }

    @Override
    TextDocumentService getTextDocumentService() {
        textDocumentService
    }

    @Override
    WorkspaceService getWorkspaceService() {
        workspaceService
    }
}
