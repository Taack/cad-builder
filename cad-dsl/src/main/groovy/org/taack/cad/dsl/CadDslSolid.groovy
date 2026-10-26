package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * A solid, and the selectors of its planar faces to draw on: topZ() is the face facing +Z the farthest along Z,
 * topZ(pos) the planar face closest to pos, and so on for X and Y, butX() ... for the faces facing -X ...
 * <p>
 * pos only chooses the face: the sketch of {@link CadDslFace#wireFrom} with no position starts at the center of mass of
 * the face, not at pos.
 */
@CompileStatic
class CadDslSolid implements CadDslBase {
    CadDslFace topDir(Vec dir) {}

    CadDslFace topX(Vec pos = null) {
        visitor.visitFace(new Vec(1, 0, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topY(Vec pos = null) {
        visitor.visitFace(new Vec(0, 1, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topZ(Vec pos = null) {
        visitor.visitFace(new Vec(1), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butX(Vec pos = null) {
        visitor.visitFace(new Vec(-1, 0, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butY(Vec pos = null) {
        visitor.visitFace(new Vec(0, -1, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butZ(Vec pos = null) {
        visitor.visitFace(new Vec(-1), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslSolid display(String fileName = null) {
        visitor.display(CadDisplay.target(fileName))
        this
    }

    CadDslSolid cut(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCut()
        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitCutEnd()
        this
    }

    CadDslSolid fuse(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFuse()
        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitFuseEnd()
        this
    }

    CadDslSolid common(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCommon()

        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitCommonEnd()
        this
    }

    CadDslSolid mirror(Vec pos, Vec dir) {
        visitor.visitSolidMirror(pos, dir)
        this
    }

    CadDslSolid translate(Vec distance) {
        visitor.visitSolidTranslate(distance)
        this
    }

    /**
     * @param degrees Angle, counterclockwise looking against direction
     * @param clone   Keep the solid and add the rotated copy
     */
    CadDslSolid rotate(Vec position, Vec direction, Number degrees, boolean clone = false) {
        visitor.visitSolidRotate(position, direction, Math.toRadians(degrees.toDouble()), clone)
        this
    }

    CadDslSolid note(String text, Number height, Vec pos) {

    }
}
