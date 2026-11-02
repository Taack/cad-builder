package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.*
import org.codehaus.groovy.control.customizers.CompilationCustomizer
import org.codehaus.groovy.control.messages.Message

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Compiles a source up to the type checking, without generating classes, and keeps going after the errors once the
 * source is parsed: the AST of code being typed is what matters.
 */
@CompileStatic
class ProbingCompiler {
    private static final Logger log = Logger.getLogger(ProbingCompiler.name)

    static class Result {
        /** null when the source does not parse */
        ModuleNode module
        List<Message> errors = []
        List<Message> warnings = []

        CursorContext getCursorContext() {
            module?.getNodeMetaData(CursorProbe.CONTEXT) as CursorContext
        }
    }

    private final GroovyClassLoader classpathLoader
    private final List<CompilationCustomizer> customizers

    /**
     * @param parentLoader Loader of the classes the source uses
     * @param classpath    Jars and directories added to it
     * @param customizers  Those of the DSL, e.g. its default imports
     */
    ProbingCompiler(ClassLoader parentLoader, List<String> classpath, List<CompilationCustomizer> customizers) {
        // One loader for all the compilations: the caches of the type checker keep class nodes from one compilation
        // to the next, they must still resolve the classes they refer to. The probe is found even when the parent
        // loader does not see this library.
        this.classpathLoader = new GroovyClassLoader(new JoinedClassLoader(parentLoader, CursorProbe.classLoader))
        classpath.each { classpathLoader.addClasspath(it) }
        this.customizers = customizers
    }

    /**
     * @param use Reads the result
     */
    def <T> T compile(String text, java.util.function.Function<Result, T> use) {
        CompilerConfiguration config = new CompilerConfiguration()
        config.disabledGlobalASTTransformations = ['groovy.grape.GrabAnnotationTransformation'] as Set
        config.addCompilationCustomizers(customizers as CompilationCustomizer[])
        config.addCompilationCustomizers(new ProbeCustomizer())

        // without classpath of its own, the loader of a compilation needs no closing
        GroovyClassLoader loader = new GroovyClassLoader(classpathLoader, config)
        LenientCompilationUnit unit = new LenientCompilationUnit(config, loader)
        SourceUnit source = unit.addSource('Script1.groovy', text)
        Result result = new Result()
        try {
            unit.compile(Phases.CONVERSION)
            unit.collector.lenient = true
            unit.compile(Phases.INSTRUCTION_SELECTION)
        } catch (CompilationFailedException ignored) {
            // the errors are in the collector
        } catch (Exception | LinkageError e) {
            log.log(Level.FINE, 'Groovy compiler failed', e)
        }
        result.module = unit.collector.lenient ? source.AST : null
        result.errors.addAll(unit.collector.errors ?: [])
        result.warnings.addAll(unit.collector.warnings ?: [])
        use.apply(result)
    }

    private static class LenientErrorCollector extends ErrorCollector {
        boolean lenient

        LenientErrorCollector(CompilerConfiguration config) {
            super(config)
        }

        @Override
        protected void failIfErrors() throws CompilationFailedException {
            if (!lenient) super.failIfErrors()
        }
    }

    private static class LenientCompilationUnit extends CompilationUnit {
        final LenientErrorCollector collector

        LenientCompilationUnit(CompilerConfiguration config, GroovyClassLoader loader) {
            super(config, null, loader, loader)
            collector = new LenientErrorCollector(config)
            errorCollector = collector
        }
    }

    /**
     * The parent loader, then the classes of this library from the loader of the probe
     */
    private static class JoinedClassLoader extends ClassLoader {
        private static final String PACKAGE = CursorProbe.package.name
        private final ClassLoader probeLoader

        JoinedClassLoader(ClassLoader parent, ClassLoader probeLoader) {
            super(parent)
            this.probeLoader = probeLoader
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.substring(0, Math.max(name.lastIndexOf('.'), 0)) != PACKAGE) throw new ClassNotFoundException(name)
            probeLoader.loadClass(name)
        }

        @Override
        protected URL findResource(String name) {
            name.substring(0, Math.max(name.lastIndexOf('/'), 0)) == PACKAGE.replace('.', '/') ? probeLoader.getResource(name) : null
        }
    }
}
