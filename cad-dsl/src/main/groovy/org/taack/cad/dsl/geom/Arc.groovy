package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

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
        return own(handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(fromLocal.toGpPnt(), center.toGpPnt(), to.toGpPnt()), NativeLib::delete_handle_Geom_TrimmedCurve)
    }

    @Override
    String toString() {
        return "Arc2d{" +
                "to=" + to +
                ", center=" + center +
                '}'
    }
}
