package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

import java.lang.foreign.MemorySegment


enum Dir {
    par, per
}

enum Axe {
    X, Y, Z
}

enum Qty {
    max, min
}

enum FaceType {
    PLANAR, NOT_LINEAR
}

enum EdgeType {
    LINEAR, NOT_LINEAR
}

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
}

@CompileStatic
final class Vec extends Vec2d {
    final BigDecimal z

    Vec(BigDecimal z) {
        super(0.0, 0.0)
        this.z = z
    }

    Vec(double x, double y, BigDecimal z) {
        super(x, y)
        this.z = z
    }

    Vec(BigDecimal x, BigDecimal y, BigDecimal z) {
        super(x, y)
        this.z = z
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

    Vec multiply(Vec other) {
        new Vec(x * other.x, y * other.y, z * other.z)
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
                nl.gp_pnt_x(aPnt).toBigDecimal(),
                nl.gp_pnt_y(aPnt).toBigDecimal(),
                nl.gp_pnt_z(aPnt).toBigDecimal()
        )
    }

    static Vec fromADir(MemorySegment aDir) {
        new Vec(
                nl.gp_dir_x(aDir).toBigDecimal(),
                nl.gp_dir_y(aDir).toBigDecimal(),
                nl.gp_dir_z(aDir).toBigDecimal()
        )
    }

    MemorySegment toGpDir() {
        nl.gp_dir_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
    }

    MemorySegment toGpPnt() {
        nl.gp_pnt_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
    }

    MemorySegment toGpVec() {
        nl.gp_vec_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
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


@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative


    Vec currentLoc


}
