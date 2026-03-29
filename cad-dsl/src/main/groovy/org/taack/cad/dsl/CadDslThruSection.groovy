package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
class CadDslThruSection extends CadDslSurface {
    CadDsl wireFromSurface(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitWireFromSurface()
        c.delegate = this
        c.call()
        visitor.visitWireFromSurfaceEnd()
        new CadDsl(visitor: visitor)
    }

}