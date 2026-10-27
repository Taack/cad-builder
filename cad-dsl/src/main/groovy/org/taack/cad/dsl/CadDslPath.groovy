package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
class CadDslPath implements CadDslBase {

    /**
     * Sweep a circle along the path, square to it at its start, to make a tube, a cable or a bent rod. The bends
     * between edges that are not tangent are mitred: draw them with arcs to round them.
     * @param radius    Radius of the wire
     */
    CadDslSolid wire(Number radius) {
        visitor.visitPathWire(radius.toDouble())
        new CadDslSolid(visitor: visitor)
    }
}
