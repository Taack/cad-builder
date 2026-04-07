package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2

@CompileStatic
class Edge implements IOpenShape {
    Vec to

    Edge(Vec to) {
        this.to = to
    }

    @Override
    MemorySegment makeWireAdd(Vec fromLocal) {
        return handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(fromLocal.toGpPnt(), to.toGpPnt())
    }

    @Override
    String toString() {
        return "Edge{" +
                "to=" + to +
                '}'
    }
}
