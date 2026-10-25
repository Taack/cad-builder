package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

@CompileStatic
class CadDslFace implements CadDslBase {

    /**
     * @param degrees Revolution angle
     */
    CadDslSolid revolution(Number degrees) {
        revolution(new Vec(), new Vec(1), degrees)
    }

    /**
     * @param degrees Revolution angle, a full turn by default
     */
    CadDslSolid revolution(Vec from = new Vec(), Vec dir = new Vec(1), Number degrees = 360) {
        double d = degrees.toDouble()
        visitor.visitRevolution(from, dir, Math.abs(d) >= 360 ? 0d : Math.toRadians(d))
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid prism(Vec dir) {
        visitor.visitPrism(dir, false)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid hole(Vec dir) {
        visitor.visitPrism(dir, true)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid prism(Number high = 1) {
        visitor.visitPrism(high.toDouble(), false)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid hole(Number depth = -1) {
        visitor.visitPrism(depth.toDouble(), true)
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Prism with a draft, as a molded part: its side faces lean by the draft angle, the section narrowing along dir
     * (widening for a negative draft), the walls of the holes of the face leaning the other way.
     * @param options draft: angle in degrees, from the direction of the prism
     * @param dir     Normal to the face
     */
    CadDslSolid prism(Map<String, ? extends Number> options, Vec dir) {
        visitor.visitPrism(dir, false, draft(options))
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Hole with a draft: the solid cut is the prism with a draft along dir, so a positive draft makes a pocket narrower
     * at its bottom.
     * @param options draft: angle in degrees, from the direction of the hole
     */
    CadDslSolid hole(Map<String, ? extends Number> options, Vec dir) {
        visitor.visitPrism(dir, true, draft(options))
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid prism(Map<String, ? extends Number> options, Number high) {
        visitor.visitPrism(high.toDouble(), false, draft(options))
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid hole(Map<String, ? extends Number> options, Number depth) {
        visitor.visitPrism(depth.toDouble(), true, draft(options))
        new CadDslSolid(visitor: visitor)
    }

    private static double draft(Map<String, ? extends Number> options) {
        Set<String> unknown = options.keySet() - ['draft']
        if (unknown) throw new IllegalArgumentException("prism, hole: unknown options $unknown, expected draft")
        Math.toRadians((options.draft ?: 0).doubleValue())
    }

//    CadDslSolid hole(Number depth) {
//        visitor.visitHole(depth)
//        new CadDslSolid(visitor: visitor)
//    }

//    CadDslWire2d center(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
//        visitor.visitCenter()
//        if (c) {
//            c.delegate = new CadDslEdge2d(visitor: visitor)
//            c.call()
//        }
//        visitor.visitCenterEnd()
//        new CadDslWire2d(visitor: visitor)
//    }

    CadDslWire2d wireFrom(Vec2d pos = null, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitFrom(pos)
        if (pos == null) visitor.visitCenter()
        c.delegate = new CadDslEdge2d(visitor: visitor)
        c.call()
        if (pos == null) visitor.visitCenterEnd()
        visitor.visitFromEnd(pos)
        new CadDslWire2d(visitor: visitor)
    }


    CadDslSolid fillet(Number radius) {
        visitor.visitFillet(radius.toDouble())
        new CadDsl(visitor: visitor)
    }

    CadDslSolid hollowedSolid(Number thickness) {
        visitor.visitHollowedSolid(thickness.toDouble())
        new CadDsl(visitor: visitor)
    }

    CadDslFace display(String fileName = null) {
        visitor.display(fileName)
        this
    }
}
