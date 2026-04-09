package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

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
        if (ptFrom)
            return ptFrom
        else
            return Vec2d.fromAPnt(new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(trimmedCurve2d()))
    }

    @Override
    Vec2d getEnd() {
        if (ptTo)
            return ptTo
        else
            return Vec2d.fromAPnt(new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(trimmedCurve2d()))
    }

    @Override
    boolean getReverse() {
        return false
    }

    @Override
    MemorySegment trimmedCurve2d() {
        if (!trimmed) {
            MemorySegment c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(circle2d.pos.toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), circle2d.radius)
            if (angle1 != angle2) trimmed = handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(c, angle1, angle2)
            else if (ptTo == null) trimmed = handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(c, ptFrom.toGpPnt2d(), angle1)
            else trimmed = handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(c, ptFrom.toGpPnt2d(), ptTo.toGpPnt2d())
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
