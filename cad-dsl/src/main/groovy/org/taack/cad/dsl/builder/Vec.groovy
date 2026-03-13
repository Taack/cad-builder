package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*


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

    Vec multiply(BigDecimal fac) {
        new Vec(x * fac, y * fac, z * fac)
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

    BigDecimal cord(Axe axe) {
        switch (axe) {
            case Axe.X:
                return x
            case Axe.Y:
                return y
            case Axe.Z:
                return z
        }
    }

    @Override
    String toString() {
        return "Loc{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
