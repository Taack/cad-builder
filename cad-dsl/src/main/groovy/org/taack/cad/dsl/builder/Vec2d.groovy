package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import static org.taack.occt.NativeLib.*

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

    Vec2d minus(Vec2d other) {
        new Vec2d(x - other.x, y - other.y)
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
        double o = other.toDouble()
        new Vec2d(x * o, y * o)
    }

    Vec2d rotate(double beta) {
        double cb = Math.cos(beta)
        double sb = Math.sin(beta)
        new Vec2d(cb * x - sb * y, sb * x + cb * y)
    }

    static Vec2d fromAPnt(MemorySegment aPnt) {
        new Vec2d(
                gp_Pnt2d__X(aPnt),
                gp_Pnt2d__Y(aPnt),
        )
    }

    static Vec2d fromADir(MemorySegment aDir) {
        new Vec2d(
                gp_Dir2d__X(aDir),
                gp_Dir2d__Y(aDir),
        )
    }

    MemorySegment toGpDir2d() {
        new_gp_Dir2d__x_y(x, y)
    }

    MemorySegment toGpPnt2d() {
        new_gp_Pnt2d__x_y(x, y)
    }

    MemorySegment toGpVec() {
        new_gp_Vec2d__x_y(x, y)
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
