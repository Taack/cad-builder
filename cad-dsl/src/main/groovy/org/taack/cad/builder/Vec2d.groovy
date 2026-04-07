package org.taack.cad.builder

import groovy.transform.CompileStatic
import static org.taack.occt.NativeLib.*

import java.lang.foreign.MemorySegment
import static java.lang.Math.*

@CompileStatic
class Vec2d {
    final double x
    final double y

    Vec2d(Vec v) {
        this.x = v.x
        this.y = v.y
    }

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
        double cb = cos(beta)
        double sb = sin(beta)
        new Vec2d(cb * x - sb * y, sb * x + cb * y)
    }

    double distance(Vec2d other) {
        sqrt(pow(x - other.x, 2) + pow(y - other.y, 2))
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

    double cord(Vec axe) {
        axe.x * x + axe.y * y
    }

    @Override
    String toString() {
        return "Vec2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
