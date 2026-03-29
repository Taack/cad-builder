package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslSurface implements CadDslBase {
    CadDslSurface cylindricalSurface(Number radius) {
        visitor.visitCylindricalSurface(radius)
        this
    }

    CadDslSurface position(Vec pos, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = this
            c.call()
        }
        visitor.visitFromEnd(pos)

        this
    }

    CadDslSurface direction(Vec axis, Vec normal = null) {
        visitor.visitDirection(axis, normal)
        this
    }

}
