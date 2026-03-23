package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslSolid implements CadDslBase {
    CadDslFace topDir(Vec dir) {}

    CadDslFace topX() {}

    CadDslFace topY() {
        visitor.visitFace(new Vec(0, 1, 0))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topZ() {
        visitor.visitFace(new Vec(1))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butX() {}

    CadDslFace butY() {}

    CadDslFace butZ() {
        visitor.visitFace(new Vec(-1))
        new CadDslFace(visitor: visitor)
    }

    void display(String fileName = null) {
        visitor.display(fileName)
    }

}
