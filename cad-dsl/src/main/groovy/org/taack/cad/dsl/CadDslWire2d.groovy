package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

@CompileStatic
class CadDslWire2d implements CadDslBase {

    CadDslFace toFace() {
        visitor.visitToFaceFrom2d()
        new CadDslFace(visitor: visitor)
    }

    CadDslWire2d move(Vec2d pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge2d(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        this
    }

    CadDslWire2d mirror(Vec2d pos, Vec2d dir) {
        visitor.visitMirrorWire2d(pos, dir)
        this
    }

}
