package org.taack.cad.dsl

import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

class CadDslWire {


    CadDslWire mirror(Vec2d pos, Vec2d dir) {}
    CadDslWire mirror(Vec pos, Vec dir) {}
    CadDslFace toFace() {}
}
