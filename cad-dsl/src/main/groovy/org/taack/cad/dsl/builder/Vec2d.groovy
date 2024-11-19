package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

@CompileStatic
class Vec2d {
    final BigDecimal x
    final BigDecimal y

    Vec2d(BigDecimal x, BigDecimal y) {
        this.x = x
        this.y = y
    }

    Vec2d(double x, double y) {
        this.x = x
        this.y = y
    }


    Vec2d div(Vec2d other) {
        new Vec2d(y * other.x - x * other.y, y * other.x - x * other.y)
    }

    Vec2d plus(Vec2d other) {
        new Vec2d(x + other.x, y + other.y)
    }

    Vec2d minus(Vec2d other) {
        new Vec2d(x - other.x, y - other.y)
    }

    Vec2d multiply(Vec2d other) {
        new Vec2d(x * other.x, y * other.y)
    }

}
