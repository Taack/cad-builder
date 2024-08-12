package org.taack.cad.dsl

class Sketch {

    private Vec2d sketchPos = new Vec2d(0.0, 0.0)

    Sketch origin(Vec2d sketchPos = new Vec2d(0.0, 0.0)) {
        this.sketchPos = sketchPos
        this
    }

    Sketch lineTo(BigDecimal sx, BigDecimal sy) {

        this
    }

    Sketch threePointArc(Vec2d sx, Vec2d sy) {
        this
    }

    Sketch radiusArc(Vec2d sx, BigDecimal radius) {
        this
    }

    Sketch close() {
        this
    }

    Sketch sagittaArc(Vec2d sx, BigDecimal sy) {
        this
    }

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
