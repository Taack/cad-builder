package org.taack.cad.dsl.geom

class Circle implements IClosedShape {
    final Vec pos
    final double radius
    final boolean reverse = false

    Circle(Vec pos, double radius) {
        this.pos = pos
        this.radius = radius
    }
}
