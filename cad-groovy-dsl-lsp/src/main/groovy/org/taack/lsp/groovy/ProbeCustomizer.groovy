package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.ListExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer

/**
 * Type checks every class with {@link CursorProbe} as extension: a class without {@code @TypeChecked} or
 * {@code @CompileStatic} gets {@code @TypeChecked}, the existing annotations get the extension added.
 * <p>
 * Also removes the annotations running code at compile time ({@code @ASTTest}), since the code being edited is compiled
 * on each request.
 */
@CompileStatic
class ProbeCustomizer extends CompilationCustomizer {
    private static final Set<String> CHECKING = ['TypeChecked', 'CompileStatic'] as Set
    private static final Set<String> COMPILE_TIME_CODE = ['ASTTest'] as Set

    ProbeCustomizer() {
        super(CompilePhase.CONVERSION)
    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        removeCompileTimeCode(source, classNode)

        boolean checked = false
        for (AnnotationNode a in classNode.annotations) {
            if (isChecking(a)) {
                addProbe(a)
                checked = true
            }
        }
        for (MethodNode m in classNode.methods + (classNode.declaredConstructors as List<MethodNode>)) {
            for (AnnotationNode a in m.annotations) if (isChecking(a)) addProbe(a)
        }
        // inner classes are checked with their outer class
        if (!checked && classNode.outerClass == null) {
            AnnotationNode typeChecked = new AnnotationNode(ClassHelper.make(TypeChecked))
            addProbe(typeChecked)
            classNode.addAnnotation(typeChecked)
        }
    }

    private static boolean isChecking(AnnotationNode a) {
        String name = a.classNode.nameWithoutPackage
        if (!CHECKING.contains(name)) return false
        Expression mode = a.getMember('value')
        !(mode instanceof PropertyExpression && ((PropertyExpression) mode).propertyAsString == 'SKIP')
    }

    private static void addProbe(AnnotationNode a) {
        ListExpression extensions = new ListExpression()
        Expression existing = a.getMember('extensions')
        if (existing instanceof ListExpression) ((ListExpression) existing).expressions.each { extensions.addExpression(it) }
        else if (existing != null) extensions.addExpression(existing)
        extensions.addExpression(new ConstantExpression(CursorProbe.name))
        a.setMember('extensions', extensions)
    }

    private static void removeCompileTimeCode(SourceUnit source, ClassNode classNode) {
        new ClassCodeVisitorSupport() {
            @Override
            protected SourceUnit getSourceUnit() {
                source
            }

            @Override
            void visitAnnotations(AnnotatedNode node) {
                node.annotations.removeIf { AnnotationNode a -> COMPILE_TIME_CODE.contains(a.classNode.nameWithoutPackage) }
                super.visitAnnotations(node)
            }
        }.visitClass(classNode)
    }
}
