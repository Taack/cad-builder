package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

@CompileStatic
class CadDslFace implements CadDslBase {

    CadDslSolid revolution(Vec from = new Vec(), Vec dir = new Vec(1)) {
        visitor.visitRevolution(from, dir)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid prism(Vec dir = new Vec(1)) {
        visitor.visitPrism(dir, false)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid hole(Vec dir = new Vec(-1)) {
        visitor.visitPrism(dir, true)
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
