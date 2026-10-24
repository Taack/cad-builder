package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslNode

/**
 * Part of an assembly: the result of a model, in FreeCAD.
 */
@CompileStatic
class FreeCadPart implements ICadDslNode {
    final int handle
    // The model the part is made of, which FreeCAD keeps as long as the part is used
    final FreeCadVisitor model

    FreeCadPart(int handle, FreeCadVisitor model) {
        this.handle = handle
        this.model = model
    }
}
