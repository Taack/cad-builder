package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

import java.lang.foreign.MemorySegment

@CompileStatic
trait Selector {

    MemorySegment currentShape
    MemorySegment currentFace

    @CompileStatic
    static final class Loc {
        final BigDecimal x
        final BigDecimal y
        final BigDecimal z

        Loc(BigDecimal x, BigDecimal y, BigDecimal z) {
            this.x = x
            this.y = y
            this.z = z
        }

        static Loc fromAPnt(MemorySegment aPnt) {
            new Loc(
                    nl.gp_pnt_x(aPnt).toBigDecimal(),
                    nl.gp_pnt_y(aPnt).toBigDecimal(),
                    nl.gp_pnt_z(aPnt).toBigDecimal()
            )

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

    Face topZ(@DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) c = null) {
        face(Axe.Z, Qty.max, c)
    }

    Face face(Axe axe, Qty qty, @DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) operations = null) {
        double positionMax = -1

        for (def aFaceExplorer = nl.top_exp_explorer(currentShape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             nl.top_exp_explorer_more(aFaceExplorer);
             nl.top_exp_explorer_next(aFaceExplorer)) {
            def aFace = nl.top_exp_explorer_current_face(aFaceExplorer)
            def aSurface = nl.brep_tool_surface(aFace)
            if (nl.geom_surface_is_geom_plane(aSurface) == 1) {
                def aPlan = nl.downcast_geom_plane(aSurface)
                def aPnt = nl.geom_plane_location(aPlan)
                currentLoc = Loc.fromAPnt(aPnt)
                double aZ = currentLoc.cord(axe)
                println "COUCOU $aZ $positionMax"
                if (aZ > positionMax) {
                    positionMax = aZ
                    currentFace = aFace
                    println "KIKI $currentFace"
                }
            }
        }
        return this as Face
    }

    Selector edge(Axe axe, Dir dir, Qty qty, int v = 0, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_ONLY) operations = null) {

    }

}
