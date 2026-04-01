package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d

@CompileStatic
class CadDslWire2d implements CadDslBase {

    CadDslFace toFace() {
        visitor.visitToFaceFrom2d()
        new CadDslFace(visitor: visitor)
    }

}
