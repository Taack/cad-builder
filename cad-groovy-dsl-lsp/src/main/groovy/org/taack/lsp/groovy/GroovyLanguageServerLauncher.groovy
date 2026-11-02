package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.eclipse.lsp4j.launch.LSPLauncher
import org.eclipse.lsp4j.jsonrpc.Launcher
import org.eclipse.lsp4j.services.LanguageClient

/**
 * Runs the server on stdin and stdout, for the editors starting a language server as a process:
 * <pre>
 * groovy-dsl-lsp [--classpath a.jar:classes] [--star-imports pkg1,pkg2] [--static-star-imports Class1,Class2]
 * </pre>
 */
@CompileStatic
final class GroovyLanguageServerLauncher {
    private GroovyLanguageServerLauncher() {}

    static void main(String[] args) {
        List<String> classpath = []
        ImportCustomizer imports = new ImportCustomizer()
        for (int i = 0; i + 1 < args.length; i += 2) {
            String[] values = args[i + 1].split(args[i] == '--classpath' ? File.pathSeparator : ',')*.trim() as String[]
            switch (args[i]) {
                case '--classpath': classpath.addAll(values); break
                case '--star-imports': imports.addStarImports(values); break
                case '--static-star-imports': imports.addStaticStars(values); break
                default:
                    System.err.println "groovy-dsl-lsp: unknown option ${args[i]}"
                    System.exit(2)
            }
        }
        def engine = new GroovyCompletionEngine(GroovyLanguageServerLauncher.classLoader, classpath, [imports] as List<CompilationCustomizer>)
        def server = new GroovyLanguageServer(engine)
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out)
        server.connect(launcher.remoteProxy)
        launcher.startListening().get()
    }
}
