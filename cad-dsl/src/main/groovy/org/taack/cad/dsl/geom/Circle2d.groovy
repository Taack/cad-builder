package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Circle2d implements IClosedShape2d {
    final Vec2d pos
    final double radius
    final boolean reverse

    Circle2d(Vec2d pos, double radius, boolean reverse) {
        this.pos = pos
        this.radius = radius
        this.reverse = reverse
    }

    @Override
    String toString() {
        return "Circle2d{" +
                "pos=" + pos +
                ", radius=" + radius +
                ", reverse=" + reverse +
                '}'
    }
}
