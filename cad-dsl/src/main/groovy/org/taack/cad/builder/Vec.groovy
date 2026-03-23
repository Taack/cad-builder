package org.taack.cad.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import static java.lang.Math.*


@CompileStatic
final class Vec extends Vec2d {
    final double z
    final static Vec vX = new Vec(1.0, 0.0, 0.0)
    final static Vec vY = new Vec(0.0, 1.0, 0.0)
    final static Vec vZ = new Vec(0.0, 0.0, 1.0)

    Vec() {
        super(0.0, 0.0)
        z = 0
    }
    Vec(double z) {
        super(0.0, 0.0)
        this.z = z
    }

    Vec(Number z) {
        super(0.0, 0.0)
        this.z = z.toDouble()
    }

    Vec(double x, double y, double z) {
        super(x, y)
        this.z = z
    }

    Vec(Number x, Number y, Number z) {
        super(x, y)
        this.z = z.toDouble()
    }

    Vec(Vec2d loc2d) {
        super(loc2d.x, loc2d.y)
        z = 0
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

    Vec negative() {
        new Vec(-x, -y, -z)
    }

    Vec multiply(Vec other) {
        new Vec(x * other.x, y * other.y, z * other.z)
    }

    Vec multiply(Number fac) {
        double f = fac.toDouble()
        new Vec(x * f, y * f, z * f)
    }

    static Vec dirXToGlobal(Vec dir) {
        new Vec(0.0, 1.0, 0.0)/dir
    }

    static Vec dirYToGlobal(Vec dir) {
        new Vec(1.0, 0.0, 0.0)/dir
    }

    static Vec globalLocFromLocal(Vec center, Vec dir, Vec2d coords) {
        Vec coords3d = new Vec(coords)
        Vec vectorProd = dirXToGlobal(dir) * coords3d + dirYToGlobal(dir) * coords3d + dir * coords3d
        center + vectorProd
    }

    static Vec fromAPnt(MemorySegment aPnt) {
        new Vec(
                gp_Pnt__X(aPnt),
                gp_Pnt__Y(aPnt),
                gp_Pnt__Z(aPnt)
        )
    }

    static Vec fromADir(MemorySegment aDir) {
        new Vec(
                gp_Dir__X(aDir),
                gp_Dir__Y(aDir),
                gp_Dir__Z(aDir)
        )
    }

    MemorySegment toGpDir() {
        new_gp_Dir__x_y_z(x, y, z)
    }

    MemorySegment toGpPnt() {
        new_gp_Pnt__x_y_z(x, y, z)
    }

    MemorySegment toGpVec() {
        new_gp_Vec__x_y_z(x, y, z)
    }

    MemorySegment toGpPln(Number t = 0) {
        new_gp_Pln__x_y_z_d(x, y, z, t.toDouble())
    }

    double cord(Vec axe) {
        axe.x * x + axe.y * y + axe.z * z
    }

    Vec2d coordsProjection(Vec direction, Vec paramsValue11) {
//        new Vec2d( direction.z * (-x) + direction.x * z + direction.y * z, direction.z * y + direction.x * y + direction.y * (-x))
//        new Vec2d( direction.z * y + direction.x * y + direction.y * x, direction.z * x + direction.x * z + direction.y * (z))
        double sx = signum(paramsValue11.x) < 0 ? -1d : 1d
        double sy = signum(paramsValue11.y) < 0 ? -1d : 1d
        double sz = signum(paramsValue11.z) < 0 ? -1d : 1d
        Vec sign = new Vec(sx, sy, sz)
        Vec absDir = new Vec(abs(direction.x), abs(direction.y), abs(direction.z))
        Vec d = absDir
        Vec2d r = new Vec2d(d.z * y + d.x * y + d.y * x, d.z * x + d.x * z + d.y * z) * sign

        println "r: $r"
//        new Vec2d(sy * abs(direction.z) * y + sy * abs(direction.x) * y + sx * abs(direction.y) * x, sx * abs(direction.z) * x + sz * abs(direction.x) * z + sz * abs(direction.y) * z)
//        new Vec2d( direction.z * y + direction.x * y + direction.y * x, direction.z * -x - direction.x * z + direction.y * (-z))
        r
    }

    @Override
    String toString() {
        return "Loc{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
