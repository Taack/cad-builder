package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3

@CompileStatic
class Arc2d implements IOpenShape2d {
    Vec2d to
    Vec2d center

    Arc2d(Vec2d to, Vec2d center) {
        this.to = to
        this.center = center
    }

    @Override
    MemorySegment makeWireAdd(Vec2d fromLocal) {
        CadDslVisitor.Tr.cur("Arc2d from: $fromLocal, to: $to")
        return handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3((fromLocal).toGpPnt2d(), (center).toGpPnt2d(), (to).toGpPnt2d())
    }

    @Override
    String toString() {
        return "Arc2d{" +
                "to=" + to +
                ", center=" + center +
                '}'
    }
}
