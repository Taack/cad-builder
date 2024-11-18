package org.taack.cad.dsl.builder

final class Vec extends Vec2d implements IVec<Vec> {
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


    @Override
    Vec div(Vec other) {
        new Vec(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
    }

    @Override
    Vec plus(Vec other) {
        new Vec(x + other.x, y + other.y, z + other.z)
    }

    @Override
    Vec minus(Vec other) {
        new Vec(x - other.x, y - other.y, z - other.z)
    }

    @Override
    Vec multiply(Vec other) {
        new Vec(x * other.x, y * other.y, z * other.z)
    }
}
