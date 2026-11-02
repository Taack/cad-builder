package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.CatchStatement
import org.codehaus.groovy.ast.stmt.ForStatement
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.stc.StaticTypesMarker

/**
 * The local variables and parameters declared before {@link CursorProbe#CURSOR}, in the blocks around it.
 */
@CompileStatic
class VisibleVariables extends ClassCodeVisitorSupport {
    static class Variable {
        String name
        ClassNode type
        boolean parameter
        /** it, in a closure without parameters */
        boolean implicit
    }

    private final SourceUnit source
    private final Deque<List<Variable>> scopes = new ArrayDeque<>()
    private List<Variable> found

    private VisibleVariables(SourceUnit source) {
        this.source = source
    }

    /**
     * @return The variables, the innermost first, empty when the cursor is not found
     */
    static List<Variable> at(ModuleNode module) {
        VisibleVariables visitor = new VisibleVariables(module.context)
        for (ClassNode c in module.classes) {
            if (visitor.found != null) break
            visitor.visitClass(c)
        }
        visitor.found ?: []
    }

    @Override
    protected SourceUnit getSourceUnit() {
        source
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        inScope(node.parameters) { super.visitConstructorOrMethod(node, isConstructor) }
    }

    @Override
    void visitClosureExpression(ClosureExpression expression) {
        inScope(expression.parameterSpecified ? expression.parameters : [] as Parameter[], !expression.parameterSpecified) {
            super.visitClosureExpression(expression)
        }
    }

    @Override
    void visitBlockStatement(BlockStatement block) {
        inScope([] as Parameter[]) { super.visitBlockStatement(block) }
    }

    @Override
    void visitForLoop(ForStatement loop) {
        inScope([loop.variable] as Parameter[]) { super.visitForLoop(loop) }
    }

    @Override
    void visitCatchStatement(CatchStatement statement) {
        inScope([statement.variable] as Parameter[]) { super.visitCatchStatement(statement) }
    }

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
        super.visitDeclarationExpression(expression)
        List<Expression> declared = expression.multipleAssignmentDeclaration ?
                expression.tupleExpression.expressions : [expression.leftExpression] as List<Expression>
        for (Expression e in declared) {
            if (e instanceof VariableExpression && !scopes.isEmpty()) {
                VariableExpression v = (VariableExpression) e
                // the declared type, else the one inferred for def
                ClassNode inferred = v.getNodeMetaData(StaticTypesMarker.INFERRED_TYPE) as ClassNode
                ClassNode type = v.dynamicTyped && inferred != null ? inferred : v.originType
                scopes.peek() << new Variable(name: v.name, type: type)
            }
        }
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        if (expression.name == CursorProbe.CURSOR) snapshot()
    }

    @Override
    void visitPropertyExpression(PropertyExpression expression) {
        if (expression.propertyAsString == CursorProbe.CURSOR) snapshot()
        super.visitPropertyExpression(expression)
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression call) {
        if (call.methodAsString == CursorProbe.CURSOR) snapshot()
        super.visitMethodCallExpression(call)
    }

    private void snapshot() {
        if (found != null) return
        found = []
        Set<String> names = new HashSet<>()
        for (List<Variable> scope in scopes) {
            for (Variable v in scope.reverse()) {
                if (v.name != CursorProbe.CURSOR && names.add(v.name)) found << v
            }
        }
    }

    private void inScope(Parameter[] parameters, boolean implicitIt = false, Closure body) {
        List<Variable> scope = []
        if (implicitIt) scope << new Variable(name: 'it', type: ClassHelper.OBJECT_TYPE, parameter: true, implicit: true)
        for (Parameter p in parameters ?: [] as Parameter[]) {
            scope << new Variable(name: p.name, type: p.originType, parameter: true)
        }
        scopes.push(scope)
        try {
            body.call()
        } finally {
            scopes.pop()
        }
    }
}
