package org.taack.cad.dsl

import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

class CadDslWire {


    CadDslWire mirror(Vec pos, Vec dir) {}
    CadDslFace toFace() {}
    CadDslWire move(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) c = null) {}
}
