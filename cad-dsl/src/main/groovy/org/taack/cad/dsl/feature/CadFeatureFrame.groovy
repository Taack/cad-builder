package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * Frame of script code, a script or a class of the user (parts, helpers), not of the DSL nor of Groovy
 */
@CompileStatic
@EqualsAndHashCode
final class CadFeatureFrame {
    private static final List<String> NOT_SCRIPT = ['org.taack.cad.', 'org.codehaus.groovy.', 'org.apache.groovy.', 'groovy.', 'java.', 'jdk.', 'sun.']

    final String className
    final String methodName
    final String fileName
    final int line

    CadFeatureFrame(String className, String methodName, String fileName, int line) {
        this.className = className
        this.methodName = methodName
        this.fileName = fileName
        this.line = line
    }

    /**
     * @return The script frames of the current thread, outermost first
     */
    static List<CadFeatureFrame> current() {
        List<CadFeatureFrame> frames = StackWalker.instance.walk { s ->
            s.filter { StackWalker.StackFrame f -> isScript(f) }
                    .map { StackWalker.StackFrame f -> new CadFeatureFrame(f.className, f.methodName, f.fileName, f.lineNumber) }
                    .toList()
        }
        frames.reverse()
    }

    private static boolean isScript(StackWalker.StackFrame f) {
        f.fileName?.endsWith('.groovy') && f.lineNumber > 0 && !NOT_SCRIPT.any { f.className.startsWith(it) }
    }

    /**
     * @return Whether the frame is in a method of the user, not in a closure nor in the body of a script
     */
    boolean isMethod() {
        !(methodName in ['doCall', 'call', 'run', 'main']) && !methodName.startsWith('lambda$')
    }

    /**
     * @return The method in the same class and with the same name, at any line
     */
    boolean sameMethod(CadFeatureFrame other) {
        className == other.className && methodName == other.methodName
    }

    @Override
    String toString() {
        "$className.$methodName($fileName:$line)"
    }
}
