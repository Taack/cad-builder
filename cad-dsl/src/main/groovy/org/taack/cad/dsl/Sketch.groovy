package org.taack.cad.dsl

class Sketch {

    private Vec2d p = new Vec2d(0.0, 0.0)

    Sketch rect(BigDecimal sx, BigDecimal sy) {
        this
    }

    Sketch circle(BigDecimal radius) {
        this
    }

    Sketch pos(Vec2d pos) {
        p = pos
        this
    }

    Sketch extrude(Vec vect) {
        this
    }
}
