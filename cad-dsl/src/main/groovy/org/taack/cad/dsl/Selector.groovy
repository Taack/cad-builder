package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

import java.lang.foreign.MemorySegment

@CompileStatic
trait Selector {

    static MemorySegment currentShape
    static MemorySegment currentFace


    @CompileStatic
    static class Loc2d {
        final BigDecimal x
        final BigDecimal y

        Loc2d(BigDecimal x, BigDecimal y) {
            this.x = x
            this.y = y
        }
    }

    @CompileStatic
    static final class Loc extends Loc2d {
        final BigDecimal z

        Loc(BigDecimal x, BigDecimal y, BigDecimal z) {
            super(x, y)
            this.z = z
        }

        Loc(Loc2d loc2d) {
            super(loc2d.x, loc2d.y)
            z = 0
        }

        Loc div(Loc other) {
            new Loc(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
        }

        Loc plus(Loc other) {
            new Loc(x + other.x, y + other.y, z + other.z)
        }

        Loc multiply(Loc other) {
            new Loc(x * other.x, y * other.y, z * other.z)
        }

        static Loc dirXToGlobal(Loc dir) {
            new Loc(0.0, 1.0, 0.0)/dir
        }

        static Loc dirYToGlobal(Loc dir) {
            new Loc(1.0, 0.0, 0.0)/dir
        }

        static Loc globalLocFromLocal(Loc center, Loc dir, Loc2d coords) {
            Loc coords3d = new Loc(coords)
            Loc vectorProd = dirXToGlobal(dir) * coords3d + dirYToGlobal(dir) * coords3d + dir * coords3d
            center + vectorProd
        }

        static Loc fromAPnt(MemorySegment aPnt) {
            new Loc(
                    nl.gp_pnt_x(aPnt).toBigDecimal(),
                    nl.gp_pnt_y(aPnt).toBigDecimal(),
                    nl.gp_pnt_z(aPnt).toBigDecimal()
            )
        }

        static Loc fromADir(MemorySegment aDir) {
            new Loc(
                    nl.gp_dir_x(aDir).toBigDecimal(),
                    nl.gp_dir_y(aDir).toBigDecimal(),
                    nl.gp_dir_z(aDir).toBigDecimal()
            )
        }

        MemorySegment toGpDir() {
            nl.gp_dir_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
        }

        MemorySegment toGpPnt() {
            nl.make_gp_pnt(x.doubleValue(), y.doubleValue(), z.doubleValue())
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
        public String toString() {
            return "Loc{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
        }
    }

    Loc currentLoc

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

    Selector edge(Axe axe, Dir dir, Qty qty, int v = 0, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_ONLY) operations = null) {

    }

}
