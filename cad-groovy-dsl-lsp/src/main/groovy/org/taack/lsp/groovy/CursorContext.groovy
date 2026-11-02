package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.ClassNode

/**
 * What the type checker knows at the cursor, see {@link CursorProbe}.
 */
@CompileStatic
class CursorContext {
    /**
     * A closure around the cursor with a delegate type, from {@code @DelegatesTo}
     */
    static class Delegate {
        ClassNode type
        /** A strategy of {@link Closure}, e.g. {@link Closure#DELEGATE_FIRST} */
        int strategy
    }

    /** The type before the dot, null when the cursor is on a name alone */
    ClassNode receiver
    /** The receiver is a class name: its static members are completed */
    boolean staticReceiver
    /** The delegates of the closures around the cursor, the innermost first */
    List<Delegate> delegates = []
    ClassNode enclosingClass
    boolean staticContext
}
