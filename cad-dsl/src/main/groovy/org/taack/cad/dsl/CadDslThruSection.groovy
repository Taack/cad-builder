package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
class CadDslThruSection extends CadDslSurface {
    CadDsl wireFromSurface(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitWireFromSurface()
        c.delegate = new CadDslEdge2d(visitor: visitor)
        c.call()
        visitor.visitWireFromSurfaceEnd()
        new CadDsl(visitor: visitor)
    }

}