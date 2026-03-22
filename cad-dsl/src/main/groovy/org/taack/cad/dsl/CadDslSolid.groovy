package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslSolid implements CadDslBase {
    CadDslFace topDir(Vec dir) {}

    CadDslFace topX() {}

    CadDslFace topY() {}

    CadDslFace topZ() {
        visitor.visitFace(new Vec(1))
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butX() {}

    CadDslFace butY() {}

    CadDslFace butZ() {}

    void display(String fileName = null) {
        visitor.display(fileName)
    }

}
