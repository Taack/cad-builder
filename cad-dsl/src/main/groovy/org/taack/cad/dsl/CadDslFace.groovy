package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

@CompileStatic
class CadDslFace implements CadDslBase {

    CadDslSolid revolution(Number angle) {
        revolution(new Vec(), new Vec(1), angle)
    }
    CadDslSolid revolution(Vec from = new Vec(), Vec dir = new Vec(1), Number angle = 0) {
        visitor.visitRevolution(from, dir, angle.toDouble())
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
        visitor.visitFillet(radius)
        new CadDsl(visitor: visitor)
    }

    CadDslSolid hollowedSolid(Number thickness) {
        visitor.visitHollowedSolid(thickness)
        new CadDsl(visitor: visitor)
    }
}
