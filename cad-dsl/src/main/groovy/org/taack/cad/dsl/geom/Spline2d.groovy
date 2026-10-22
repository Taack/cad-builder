package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Spline2d implements IOpenShape2d {
    final List<Vec2d> through

    Spline2d(List<Vec2d> through) {
        this.through = through
    }

    @Override
    Vec2d getTo() {
        through.last()
    }

    @Override
    String toString() {
        return "Spline2d{" +
                "through=" + through +
                '}'
    }
}
