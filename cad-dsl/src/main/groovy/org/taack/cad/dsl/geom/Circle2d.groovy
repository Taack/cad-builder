package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

@CompileStatic
class Circle2d implements IClosedShape2d {
    private final Vec2d _pos
    final double radius
    final boolean reverse
    final MemorySegment gpCircle

    Circle2d(Vec2d pos, double radius, boolean reverse) {
        this._pos = pos
        this.radius = radius
        this.reverse = reverse
        this.gpCircle = null
    }

    Circle2d(MemorySegment gpCircle, Number radius, boolean reverse = false) {
        this._pos = null
        this.radius = radius.toDouble()
        this.reverse = reverse
        this.gpCircle = gpCircle
    }

 Vec2d getPos() {
     _pos ?: Vec2d.fromAPnt(ref_gp_Pnt2d__gp_Ax22d__Location(ref_Position__gp_Circ2d__Position(gpCircle)))
 }

    @Override
    MemorySegment make2dCurve() {
        def c = gpCircle ?: own(new_gp_Circ2d__ax2d_r(own(new_gp_Ax2d__pt_dir(_pos.toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), NativeLib::delete_gp_Ax2d), radius), NativeLib::delete_gp_Circ2d)
        def aline = own(handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c), NativeLib::delete_handle_Geom2d_Circle)
        if (reverse) _Geom2d_Curve__Reverse(aline)
        return aline
    }


    @Override
    String toString() {
        return "Circle2d{" +
                "pos=" + pos +
                ", radius=" + radius +
                ", reverse=" + reverse +
                '}'
    }
}
