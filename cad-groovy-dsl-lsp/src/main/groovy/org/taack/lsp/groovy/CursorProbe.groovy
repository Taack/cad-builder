package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor
import org.codehaus.groovy.transform.stc.TypeCheckingContext
import org.codehaus.groovy.transform.stc.TypeCheckingExtension

import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Type checking extension recording what the type checker knows where the cursor is.
 * <p>
 * The completion engine puts the identifier {@link #CURSOR} at the cursor, which the type checker cannot resolve: it
 * then calls this extension while it is still inside the closures around the cursor, the only moment their
 * {@code @DelegatesTo} types are known. The {@link CursorContext} is left on the module, under the key {@link #CONTEXT}.
 * The extension never resolves anything, the type checker reports the cursor as an error, which completion ignores.
 */
@CompileStatic
class CursorProbe extends TypeCheckingExtension {
    static final String CURSOR = '__lspCursor__'
    static final String CONTEXT = 'org.taack.lsp.groovy.CursorContext'

    // TypeCheckingContext.delegationMetadata and the DelegationMetadata class are not public
    private static final Field DELEGATION_METADATA = accessibleField(TypeCheckingContext, 'delegationMetadata')
    private static final Class<?> METADATA_CLASS = Class.forName('org.codehaus.groovy.transform.stc.DelegationMetadata')
    private static final Method METADATA_TYPE = accessibleMethod(METADATA_CLASS, 'getType')
    private static final Method METADATA_STRATEGY = accessibleMethod(METADATA_CLASS, 'getStrategy')
    private static final Method METADATA_PARENT = accessibleMethod(METADATA_CLASS, 'getParent')

    CursorProbe(StaticTypeCheckingVisitor visitor) {
        super(visitor)
    }

    @Override
    boolean handleUnresolvedVariableExpression(VariableExpression expression) {
        if (expression.name == CURSOR) record(null, false)
        false
    }

    @Override
    boolean handleUnresolvedProperty(PropertyExpression expression) {
        if (expression.propertyAsString == CURSOR) recordMember(expression.objectExpression)
        false
    }

    @Override
    boolean handleUnresolvedAttribute(AttributeExpression expression) {
        if (expression.propertyAsString == CURSOR) recordMember(expression.objectExpression)
        false
    }

    @Override
    List<MethodNode> handleMissingMethod(ClassNode receiver, String name, ArgumentListExpression argumentList,
                                         ClassNode[] argumentTypes, MethodCall call) {
        if (name == CURSOR) {
            if (call instanceof MethodCallExpression && !((MethodCallExpression) call).implicitThis) {
                recordMember(((MethodCallExpression) call).objectExpression)
            } else {
                record(null, false)
            }
        }
        []
    }

    private void recordMember(Expression receiver) {
        if (receiver instanceof ClassExpression) record(receiver.type, true)
        else record(getType(receiver), false)
    }

    private void record(ClassNode receiver, boolean staticReceiver) {
        TypeCheckingContext context = typeCheckingVisitor.typeCheckingContext
        ModuleNode module = context.source.AST
        if (module.getNodeMetaData(CONTEXT) != null) return
        MethodNode method = context.enclosingMethod
        module.putNodeMetaData(CONTEXT, new CursorContext(
                receiver: receiver,
                staticReceiver: staticReceiver,
                delegates: delegates(context),
                enclosingClass: context.enclosingClassNode,
                staticContext: method != null && method.isStatic()))
    }

    private static List<CursorContext.Delegate> delegates(TypeCheckingContext context) {
        List<CursorContext.Delegate> res = []
        Object metadata = DELEGATION_METADATA.get(context)
        while (metadata != null) {
            res << new CursorContext.Delegate(type: (ClassNode) METADATA_TYPE.invoke(metadata),
                    strategy: (int) METADATA_STRATEGY.invoke(metadata))
            metadata = METADATA_PARENT.invoke(metadata)
        }
        res
    }

    private static Field accessibleField(Class<?> type, String name) {
        Field f = type.getDeclaredField(name)
        f.accessible = true
        f
    }

    private static Method accessibleMethod(Class<?> type, String name) {
        Method m = type.getDeclaredMethod(name)
        m.accessible = true
        m
    }
}
