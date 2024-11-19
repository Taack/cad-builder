package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

enum Dir {
    par, per
}

@CompileStatic
enum Axe {
    X(Vec.vX), Y(Vec.vY), Z(Vec.vZ)

    Axe(Vec vec) {
        this.vec = vec
    }

    Vec vec
}

enum Qty {
    max, min
}

enum FaceType {
    PLANAR, NOT_LINEAR
}

enum EdgeType {
    LINEAR, NOT_LINEAR
}

class ConstantEnums {
}
