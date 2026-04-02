package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslSolid implements CadDslBase {
    CadDslFace topDir(Vec dir) {}

    CadDslFace topX(Vec pos = null) {
        visitor.visitFace(new Vec(1, 0, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topY(Vec pos = null) {
        visitor.visitFace(new Vec(0, 1, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace topZ(Vec pos = null) {
        visitor.visitFace(new Vec(1), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butX(Vec pos = null) {
        visitor.visitFace(new Vec(-1, 0, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butY(Vec pos = null) {
        visitor.visitFace(new Vec(0, -1, 0), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslFace butZ(Vec pos = null) {
        visitor.visitFace(new Vec(-1), pos)
        new CadDslFace(visitor: visitor)
    }

    CadDslSolid display(String fileName = null) {
        visitor.display(fileName)
        this
    }

    CadDslSolid cut(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCut()
        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitCutEnd()
        this
    }

    CadDslSolid fuse(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFuse()
        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitFuseEnd()
        this
    }

    CadDslSolid common(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCommon()

        if (c) {
            c.delegate = new CadDsl(visitor: visitor)
            c.call()
        }
        visitor.visitCommonEnd()
        this
    }

}
