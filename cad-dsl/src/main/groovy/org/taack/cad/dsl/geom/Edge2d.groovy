package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

@CompileStatic
class Edge2d implements IOpenShape2d {
    Vec2d to

    Edge2d(Vec2d to) {
        this.to = to
    }

    @Override
    MemorySegment makeWireAdd(Vec2d fromLocal) {
        CadDslVisitor.Tr.cur("Edge2d from: $fromLocal, to: $to")
        return own(handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2((fromLocal).toGpPnt2d(), (to).toGpPnt2d()), NativeLib::delete_handle_Geom2d_TrimmedCurve)
    }

    @Override
    String toString() {
        return "Edge2d{" +
                "to=" + to +
                '}'
    }

}
