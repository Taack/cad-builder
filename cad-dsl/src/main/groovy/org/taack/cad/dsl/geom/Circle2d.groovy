package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib._Geom2d_TrimmedCurve__Reverse
import static org.taack.occt.NativeLib.handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d
import static org.taack.occt.NativeLib.new_gp_Ax2d__pt_dir
import static org.taack.occt.NativeLib.new_gp_Circ2d__ax2d_r

@CompileStatic
class Circle2d implements IClosedShape2d {
    final Vec2d pos
    final double radius
    final boolean reverse

    Circle2d(Vec2d pos, double radius, boolean reverse) {
        this.pos = pos
        this.radius = radius
        this.reverse = reverse
    }

    @Override
    MemorySegment make2dCurve() {
        def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), radius)
        def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
        if (reverse) _Geom2d_TrimmedCurve__Reverse(aline)
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
