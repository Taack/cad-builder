package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic


@CompileStatic
final class Vec extends Vec2d {

    static final Vec vX = new Vec(1.0, 0.0, 0.0)
    static final Vec vY = new Vec(0.0, 1.0, 0.0)
    static final Vec vZ = new Vec(1.0)

    BigDecimal z

    Vec(BigDecimal x, BigDecimal y, BigDecimal z) {
        super(x, y)
        this.z = z
    }

    Vec(BigDecimal z) {
        super(0.0, 0.0)
        this.z = z
    }


    Vec(double x, double y, double z) {
        super(x, y)
        this.z = z
    }

    Vec(double z) {
        super(0.0, 0.0)
        this.z = z
    }


    Vec div(Vec other) {
        new Vec(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    }

    Vec plus(Vec other) {
        new Vec(x + other.x, y + other.y, z + other.z)
    }

    Vec minus(Vec other) {
        new Vec(x - other.x, y - other.y, z - other.z)
    }

    Vec multiply(Vec other) {
        new Vec(x * other.x, y * other.y, z * other.z)
    }

    Vec multiply(BigDecimal other) {
        new Vec(x * other, y * other, z * other)
    }

}
