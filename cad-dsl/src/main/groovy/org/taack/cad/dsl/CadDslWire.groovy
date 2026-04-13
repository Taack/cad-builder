package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

@CompileStatic
class CadDslWire implements CadDslBase {

    CadDslFace toFace() {
        visitor.visitToFace()
        new CadDslFace(visitor: visitor)
    }

    CadDslWire move(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        this

    }
}
