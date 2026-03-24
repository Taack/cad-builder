package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslFace implements CadDslBase {

    CadDslSolid revolution(Vec from = new Vec(), Vec dir = new Vec(1)) {
        visitor.visitRevolution(from, dir)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid prism(Vec dir = new Vec(1)) {
        visitor.visitPrism(dir)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid hole(Number depth) {
        visitor.visitHole(depth)
        new CadDslSolid(visitor: visitor)
    }

    CadDslFace center(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCenter()
        if (c) {
            c.delegate = new CadDslEdge2d(visitor: visitor)
            c.call()
        }
        visitor.visitCenterEnd()
        this
    }


}
