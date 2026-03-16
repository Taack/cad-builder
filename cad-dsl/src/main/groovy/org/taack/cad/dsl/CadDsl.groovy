package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

@CompileStatic
class CadDsl {
    static CadDsl cd() {
        new CadDsl()
    }

    CadDsl from(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) c = null) {

    }

    CadDsl from(Vec2d pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) c = null) {

    }

    CadDslWire toWire() {

    }

    void display(String fileName = null) {

    }
}
