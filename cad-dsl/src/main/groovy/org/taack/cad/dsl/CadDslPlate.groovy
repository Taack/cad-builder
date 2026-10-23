package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * Constraints of a plate surface: the surface spans its boundary and passes through its curves and points.
 * Positions are relative to the plate position.
 */
@CompileStatic
class CadDslPlate implements CadDslBase {

    /**
     * Draw the contour of the plate, a closed 3D wire drawn as with {@link CadDsl#wireFrom(Vec, Closure)}.
     * @param from  First (and last) point of the contour
     */
    CadDslPlate boundary(Vec from, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c) {
        drawCurve(from, true, c)
    }

    /**
     * Draw a curve the plate passes through, an open or closed 3D wire inside the boundary.
     * @param from  First point of the curve
     */
    CadDslPlate curve(Vec from, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c) {
        drawCurve(from, false, c)
    }

    /**
     * Make the plate pass through points.
     */
    CadDslPlate through(Vec... points) {
        for (Vec point in points) visitor.visitPlateThrough(point)
        this
    }

    private CadDslPlate drawCurve(Vec from, boolean boundary, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitPlateCurve(from, boundary)
        c.delegate = new CadDslEdge(visitor: visitor)
        c.call()
        visitor.visitPlateCurveEnd(from)
        this
    }
}
