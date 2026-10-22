package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Ellipse2d implements IClosedShape2d {
    final Vec2d pos
    final Vec2d dir
    final double majRadius
    final double minRadius
    final boolean reverse = false

    Ellipse2d(Vec2d pos, Vec2d dir, double majRadius, double minRadius) {
        this.pos = pos
        this.dir = dir
        this.majRadius = majRadius
        this.minRadius = minRadius
    }
}
