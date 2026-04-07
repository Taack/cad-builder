package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2
import static org.taack.occt.NativeLib.new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint
import static org.taack.occt.NativeLib.new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint

@CompileStatic
class ArcOfCircle2d implements ITrimmable2d {
    final Circle2d circle2d
    private MemorySegment trimmed = null
    final boolean reverse
    double angle1
    double angle2

    ArcOfCircle2d(Circle2d circle2d, boolean reverse) {
        this.reverse = reverse
        this.circle2d = circle2d
    }

    @Override
    Vec2d getStart() {
        return Vec2d.fromAPnt(new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(trimmedCurve2d()))
    }

    @Override
    Vec2d getEnd() {
        return Vec2d.fromAPnt(new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(trimmedCurve2d()))
    }

    @Override
    boolean getReverse() {
        return false
    }

    @Override
    MemorySegment trimmedCurve2d() {
        trimmed ?= handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(circle2d.make2dCurve(), angle1, angle2)
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
}
