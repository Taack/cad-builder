package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.ImportNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.messages.*
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.stc.ExtensionMethodCache
import org.eclipse.lsp4j.*

/**
 * Completion and diagnostics of Groovy code, the members of the closures delegates declared by {@code @DelegatesTo}
 * included.
 * <p>
 * Completion puts an identifier at the cursor, type checks the source and reads what the type checker knows there,
 * see {@link CursorProbe}. When the source does not parse, the code after the cursor is replaced by the brackets
 * closing those open before it. Nothing is generated or run, the engine is stateless and thread safe.
 */
@CompileStatic
class GroovyCompletionEngine {
    private final ClassLoader loader
    private final ProbingCompiler compiler

    /** Severity of the errors of the type checker, which the engine runs on all the code, dynamic code included */
    DiagnosticSeverity typeCheckingSeverity = DiagnosticSeverity.Warning
    int maxItems = 300

    /**
     * @param loader      Loader of the classes the code uses: those of the DSL, or a {@link GroovyOnlyClassLoader} with
     *                    the DSL in classpath
     * @param classpath   Jars and directories added to it
     * @param customizers The compilation customizers of the DSL, e.g. an {@link ImportCustomizer} for default imports
     */
    GroovyCompletionEngine(ClassLoader loader = GroovyCompletionEngine.classLoader, List<String> classpath = [],
                           List<CompilationCustomizer> customizers = []) {
        this.loader = loader
        this.compiler = new ProbingCompiler(loader, classpath, customizers)
    }

    List<CompletionItem> complete(String text, Position position) {
        int offset = SourceText.offsetOf(text, position)
        int start = SourceText.identifierStart(text, offset)
        int end = SourceText.identifierEnd(text, offset)
        String closers = SourceText.closersAt(text, start)
        if (closers == null) return []

        String before = text.substring(0, start) + CursorProbe.CURSOR
        String prefix = text.substring(start, offset)
        Range range = new Range(SourceText.positionOf(text, start), SourceText.positionOf(text, end))
        for (String source in [before + text.substring(end), before + '\n' + closers]) {
            List<CompletionItem> items = compiler.compile(source) { ProbingCompiler.Result result ->
                CursorContext context = result.cursorContext
                if (context == null) return (List<CompletionItem>) null
                CompletionCandidates list = new CompletionCandidates(prefix)
                fill(list, context, result.module)
                list.items(range, maxItems)
            }
            if (items != null) return items
        }
        []
    }

    List<Diagnostic> diagnose(String text) {
        analyse(text).diagnostics
    }

    /**
     * The semantic tokens of the source, encoded as LSP expects them, see {@link SemanticTokenizer}
     */
    List<Integer> semanticTokens(String text) {
        analyse(text).tokens
    }

    private static final class Analysis {
        List<Diagnostic> diagnostics
        List<Integer> tokens
    }

    /** The last texts analysed: the diagnostics and the tokens of a text are asked one after the other */
    private final Map<String, Analysis> analyses = Collections.synchronizedMap(new LinkedHashMap<String, Analysis>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Analysis> eldest) {
            size() > 16
        }
    })

    private Analysis analyse(String text) {
        Analysis cached = analyses.get(text)
        if (cached != null) return cached
        Analysis a = compiler.compile(text) { ProbingCompiler.Result r ->
            Set<String> seen = new HashSet<>()
            List<Diagnostic> diagnostics = []
            for (Message m in r.errors + r.warnings) {
                Diagnostic d = diagnostic(m, text)
                if (d != null && seen.add("${d.range} ${d.message}".toString())) diagnostics << d
            }
            new Analysis(diagnostics: diagnostics, tokens: SemanticTokenizer.tokens(text, r.module))
        }
        analyses.put(text, a)
        a
    }

    private void fill(CompletionCandidates list, CursorContext context, ModuleNode module) {
        if (context.receiver != null) {
            boolean self = context.receiver.redirect() == context.enclosingClass?.redirect()
            list.addMembers(context.receiver, context.staticReceiver, self)
            list.addExtensionMethods(context.receiver, context.staticReceiver, extensionMethods())
            return
        }

        List<VisibleVariables.Variable> variables = VisibleVariables.at(module)
        variables.findAll { !it.implicit }.each { list.addVariable(it) }

        // the delegates before the owner, or after it, as their resolve strategy says
        List<ClassNode> afterOwner = []
        boolean ownerReachable = true
        for (CursorContext.Delegate d in context.delegates) {
            switch (d.strategy) {
                case Closure.DELEGATE_FIRST:
                    list.addMembers(d.type, false, false)
                    break
                case Closure.DELEGATE_ONLY:
                    list.addMembers(d.type, false, false)
                    ownerReachable = false
                    break
                case Closure.OWNER_FIRST:
                    afterOwner << d.type
                    break
                case Closure.TO_SELF:
                    ownerReachable = false
                    break
            }
            if (!ownerReachable) break
        }
        if (ownerReachable) {
            for (ClassNode c = context.enclosingClass; c != null; c = c.outerClass) {
                list.addMembers(c, context.staticContext, true)
            }
        }
        afterOwner.each { list.addMembers(it, false, false) }
        addStaticImports(list, module)
        // rarely used in a closure with a delegate
        variables.findAll { it.implicit }.take(1).each { list.addVariable(it) }
    }

    private static void addStaticImports(CompletionCandidates list, ModuleNode module) {
        for (ImportNode i in module.staticImports.values()) {
            MethodNode m = i.type.getMethods(i.fieldName).find()
            if (m != null) list.add(i.alias, CompletionItemKind.Method, CompletionCandidates.signature(m), i.alias)
            else list.add(i.alias, CompletionItemKind.Field, CompletionCandidates.typeName(i.type.getField(i.fieldName)?.type))
        }
        for (ImportNode i in module.staticStarImports.values()) list.addMembers(i.type, true, false)
    }

    private Map<String, List<MethodNode>> extensionMethods() {
        ExtensionMethodCache.INSTANCE.get(loader)
    }

    private Diagnostic diagnostic(Message m, String text) {
        String message
        int line = 1, column = 1, endLine = -1, endColumn = -1
        DiagnosticSeverity severity = DiagnosticSeverity.Error
        if (m instanceof SyntaxErrorMessage) {
            SyntaxException e = ((SyntaxErrorMessage) m).cause
            message = e.originalMessage
            line = e.line
            column = e.startColumn
            endLine = e.endLine
            endColumn = e.endColumn
        } else if (m instanceof ExceptionMessage) {
            message = ((ExceptionMessage) m).cause.message
        } else if (m instanceof SimpleMessage) {
            message = ((SimpleMessage) m).message
        } else if (m instanceof WarningMessage) {
            WarningMessage w = (WarningMessage) m
            message = w.message
            severity = DiagnosticSeverity.Warning
            if (w.context != null) {
                line = w.context.startLine
                column = w.context.startColumn
            }
        } else {
            return null
        }
        if (message == null) return null
        message = message.trim()
        String typeChecking = '[Static type checking] - '
        if (message.startsWith(typeChecking)) {
            message = message.substring(typeChecking.length())
            severity = typeCheckingSeverity
        }

        Position start = new Position(Math.max(line - 1, 0), Math.max(column - 1, 0))
        Position stop = endLine > 0 && endColumn > 0 ? new Position(endLine - 1, endColumn - 1) : null
        if (stop == null || stop.line < start.line || (stop.line == start.line && stop.character <= start.character)) {
            // to the end of the word, or of the line
            String[] lines = text.split('\n', -1)
            String l = start.line < lines.length ? lines[start.line] : ''
            int s = Math.min(start.character, l.length())
            int e = s < l.length() && Character.isJavaIdentifierPart(l.charAt(s)) ? SourceText.identifierEnd(l, s) : l.length()
            stop = new Position(start.line, Math.max(e, s + 1))
        }
        new Diagnostic(new Range(start, stop), message, severity, 'groovy')
    }
}
