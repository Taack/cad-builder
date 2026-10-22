package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

@CompileStatic
class ArcOfCircle2d implements ITrimmable2d {
    final Circle2d circle2d
    private MemorySegment trimmed = null
    final boolean reverse
    final double angle1
    final double angle2

    final Vec2d ptFrom
    final Vec2d ptTo


    ArcOfCircle2d(Circle2d circle2d, double angle1, double angle2, boolean reverse) {
        this.reverse = reverse
        this.circle2d = circle2d
        this.angle1 = angle1
        this.angle2 = angle2
        this.ptFrom = null
        this.ptTo = null
    }

    ArcOfCircle2d(Circle2d circle2d, Vec2d ptFrom, Vec2d ptTo, boolean reverse) {
        this.reverse = reverse
        this.circle2d = circle2d
        this.angle1 = 0
        this.angle2 = 0
        this.ptFrom = ptFrom
        this.ptTo = ptTo
    }

    ArcOfCircle2d(Circle2d circle2d, Vec2d ptFrom, double angle, boolean reverse) {
        this.reverse = reverse
        this.circle2d = circle2d
        this.angle1 = angle
        this.angle2 = angle
        this.ptFrom = ptFrom
        this.ptTo = null
    }

    @Override
    Vec2d getStart() {
        return Vec2d.fromAPnt(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(trimmedCurve2d()), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    Vec2d getEnd() {
         return Vec2d.fromAPnt(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(trimmedCurve2d()), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    boolean getReverse() {
        return false
    }

    @Override
    MemorySegment trimmedCurve2d() {
        if (!trimmed) {
            MemorySegment c = own(new_gp_Circ2d__ax2d_r(own(new_gp_Ax2d__pt_dir(circle2d.pos.toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), NativeLib::delete_gp_Ax2d), circle2d.radius), NativeLib::delete_gp_Circ2d)
            if (angle1 != angle2) trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(c, angle1, angle2), NativeLib::delete_handle_Geom2d_TrimmedCurve)
            else if (ptTo == null) trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(c, ptFrom.toGpPnt2d(), angle1), NativeLib::delete_handle_Geom2d_TrimmedCurve)
            else trimmed = own(handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(c, ptFrom.toGpPnt2d(), ptTo.toGpPnt2d()), NativeLib::delete_handle_Geom2d_TrimmedCurve)
            if (reverse) _Geom2d_TrimmedCurve__Reverse(trimmed)
        }
        return trimmed
    }

    @Override
    MemorySegment makeWireAdd(Vec2d fromLocal) {
        return trimmedCurve2d()
    }

    @Override
    Vec2d getTo() {
        return getEnd()
    }

    @Override
    String toString() {
        return "ArcOfCircle2d{" +
                "start=" + start +
                ", end=" + end +
                ", reverse=" + reverse +
                ", angle1=" + angle1 +
                ", angle2=" + angle2 +
                '}';
    }
}
