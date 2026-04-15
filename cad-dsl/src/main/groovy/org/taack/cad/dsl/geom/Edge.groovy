package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class Edge implements IOpenShape {
    Vec to

    Edge(Vec to) {
        this.to = to
    }

    @Override
    MemorySegment makeWireAdd(Vec fromLocal) {
        CadDslVisitor.Tr.cur("Edge from: $fromLocal, to: $to")
//        return handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(fromLocal.toGpPnt(), to.toGpPnt())
        return new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(fromLocal.toGpPnt(), to.toGpPnt())
    }

    @Override
    String toString() {
        return "Edge{" +
                "to=" + to +
                '}'
    }
}
