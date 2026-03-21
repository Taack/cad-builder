package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslFace {

    CadDslSolid revolution(Vec from = new Vec(), Vec dir = new Vec(1)) {}

    CadDslSolid prism(Vec dir = new Vec(1)) {}

    CadDslSolid hole(Number depth) {}

    CadDslFace center(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) c = null) {}


}
