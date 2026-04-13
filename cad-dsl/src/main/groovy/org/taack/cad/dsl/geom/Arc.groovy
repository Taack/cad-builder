package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3

@CompileStatic
class Arc implements IOpenShape {
    Vec to
    Vec center

    Arc(Vec to, Vec center) {
        this.to = to
        this.center = center
    }

    @Override
    MemorySegment makeWireAdd(Vec fromLocal) {
        return handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(fromLocal.toGpPnt(), center.toGpPnt(), to.toGpPnt())
    }

    @Override
    String toString() {
        return "Arc2d{" +
                "to=" + to +
                ", center=" + center +
                '}'
    }
}
