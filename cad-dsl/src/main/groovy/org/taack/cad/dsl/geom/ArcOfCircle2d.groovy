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

    ArcOfCircle2d(Circle2d circle2d, double angle1, double angle2, boolean reverse) {
        this.reverse = reverse
        this.circle2d = circle2d
        this.angle1 = angle1
        this.angle2 = angle2
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
        if (!trimmed) {
            CadDslVisitor.Tr.cur "ArcOfCircle2d $this"
            def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(circle2d.pos.toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), circle2d.radius)
            trimmed = handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(c, angle1, angle2)
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
                "circle2d=" + circle2d +
                ", trimmed=" + trimmed +
                ", reverse=" + reverse +
                ", angle1=" + angle1 +
                ", angle2=" + angle2 +
                '}';
    }
}
