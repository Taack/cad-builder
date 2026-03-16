package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslFace {

    CadDsl revolution(Vec from = new Vec(), Vec dir = new Vec(1)) {}
    CadDsl prism(Vec dir = new Vec(1)) {}
}
