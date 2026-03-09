package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import org.taack.occt.NativeLib

import java.lang.foreign.MemorySegment

@CompileStatic
class Vec2d {
    final double x
    final double y

    Vec2d(Number x, Number y) {
        this.x = x.toDouble()
        this.y = y.toDouble()
    }

    Vec2d(double x, double y) {
        this.x = x
        this.y = y
    }

    Vec2d() {
        x = 0d
        y = 0d
    }

    Vec2d div(Vec2d other) {
        new Vec2d(y * other.x - x * other.y, y * other.x - x * other.y)
    }

    Vec2d plus(Vec2d other) {
        new Vec2d(x + other.x, y + other.y)
    }

    Vec2d negative() {
        new Vec2d(-x, -y)
    }

    Vec2d bitwiseNegate() {
        new Vec2d(y, x)
    }

    Vec2d multiply(Vec2d other) {
        new Vec2d(x * other.x, y * other.y)
    }

    Vec2d multiply(Number other) {
        new Vec2d(x * other.toDouble(), y * other.toDouble())
    }

    Vec2d rotate(double beta) {
        double cb = Math.cos(beta)
        double sb = Math.sin(beta)
        new Vec2d(cb * x - sb * y, sb * x + cb * y)
    }

    static Vec2d fromAPnt(MemorySegment aPnt) {
        new Vec2d(
                NativeLib.gp_pnt2d_x(aPnt).toBigDecimal(),
                NativeLib.gp_pnt2d_y(aPnt).toBigDecimal(),
        )
    }

    static Vec2d fromADir(MemorySegment aDir) {
        new Vec2d(
                NativeLib.gp_dir2d_x(aDir).toBigDecimal(),
                NativeLib.gp_dir2d_y(aDir).toBigDecimal(),
        )
    }

    MemorySegment toGpDir2d() {
        NativeLib.gp_dir2d_new(x.doubleValue(), y.doubleValue())
    }

    MemorySegment toGpPnt2d() {
        NativeLib.gp_pnt2d_new(x.doubleValue(), y.doubleValue())
    }

    MemorySegment toGpVec() {
        NativeLib.gp_vec2d_new(x.doubleValue(), y.doubleValue())
    }

    BigDecimal cord(Axe axe) {
        switch (axe) {
            case Axe.X:
                return x
            case Axe.Y:
                return y
        }
    }

}
