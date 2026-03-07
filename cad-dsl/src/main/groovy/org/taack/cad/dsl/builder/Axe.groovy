package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

@CompileStatic
enum Axe {
    X(Vec.vX), Y(Vec.vY), Z(Vec.vZ)

    Axe(Vec vec) {
        this.vec = vec
    }

    Vec vec
}

