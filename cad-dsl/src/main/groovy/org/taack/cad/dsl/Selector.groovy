package org.taack.cad.dsl

class Selector {
    enum Dir {
        par, per
    }

    enum Axe {
        X, Y, Z
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

    Selector topZ(@DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) c = null) {
        face(Axe.Z, Dir.per, Qty.max, c)
    }

    Selector face(Axe axe, Dir dir, Qty qty, @DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) operations = null) {

    }

    Selector edge(Axe axe, Dir dir, Qty qty, int v = 0, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_ONLY) operations = null) {

    }

}
