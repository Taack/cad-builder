package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslSolid implements CadDslBase {
    CadDslFace topDir(Vec dir) {}

    CadDslFace topX() {
        visitor.visitFace(new Vec(1, 0, 0))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topY() {
        visitor.visitFace(new Vec(0, 1, 0))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topZ() {
        visitor.visitFace(new Vec(1))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butX() {
        visitor.visitFace(new Vec(-1, 0, 0))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butY() {
        visitor.visitFace(new Vec(0, -1, 0))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butZ() {
        visitor.visitFace(new Vec(-1))
        new CadDslFace(visitor: visitor)
    }

    CadDslSolid display(String fileName = null) {
        visitor.display(fileName)
        this
    }

}
