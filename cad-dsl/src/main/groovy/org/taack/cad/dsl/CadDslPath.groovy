package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
class CadDslPath implements CadDslBase {
    /**
     * Radius of the tightest rounded bend, infinite without any
     */
    double smallestBend = Double.POSITIVE_INFINITY

    /**
     * Sweep a circle along the path, square to it at its start, to make a tube, a cable or a bent rod. The bends
     * between edges that are not tangent are mitred: draw them with arcs, or give the path the radius of its bends, to round them.
     * @param radius    Radius of the wire, smaller than the radius of the rounded bends, else the wire would cross itself
     */
    CadDslSolid wire(Number radius) {
        if (radius.toDouble() >= smallestBend) throw new IllegalArgumentException("wire($radius): not thinner than the tightest bend of the path, of radius $smallestBend")
        visitor.visitPathWire(radius.toDouble())
        new CadDslSolid(visitor: visitor)
    }
}
