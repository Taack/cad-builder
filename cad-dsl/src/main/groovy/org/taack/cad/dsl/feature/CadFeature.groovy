package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic

/**
 * Operation of a script recorded by {@link RecordingKernel}: a primitive, a boolean, a sketch, a part, a placement...
 */
@CompileStatic
final class CadFeature {
    String kind
    String label
    /**
     * Frames of the script code that called the operation, outermost first, see {@link CadFeatureFrame}
     */
    final List<CadFeatureFrame> stack
    /**
     * Sketch element (edge, arc, text...), folded under its sketch
     */
    final boolean detail
    final List<CadFeature> children = []
    /**
     * Feature placed, for a placement
     */
    CadFeature placed
    /**
     * Shape the operation made, given by the kernel visitor when it is {@link ICadDslFeatureShapes}
     */
    Object shape
    ICadDslFeatureShapes shapes

    CadFeature(String kind, String label, List<CadFeatureFrame> stack, boolean detail = false) {
        this.kind = kind
        this.label = label
        this.stack = stack
        this.detail = detail
    }

    /**
     * @return Where the script calls the operation, null when no script code called it
     */
    CadFeatureFrame getSource() {
        stack ? stack.last() : null
    }

    @Override
    String toString() {
        "$kind $label"
    }
}
