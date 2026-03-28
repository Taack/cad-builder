package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
class CadDslThruSection extends CadDslEdge2d {
    CadDsl wireFromSurface(CadDslSurface surface, @DelegatesTo(value = CadDslThruSection, strategy = Closure.DELEGATE_FIRST) Closure c = null) {

    }

}