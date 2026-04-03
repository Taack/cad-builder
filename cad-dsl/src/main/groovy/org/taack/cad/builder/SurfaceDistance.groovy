package org.taack.cad.builder

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import static org.taack.occt.NativeLib.*



@CompileStatic
final class SurfaceDistance {
    final Vec v1
    final Vec v2
    final double dist

    SurfaceDistance(MemorySegment s1, MemorySegment s2) {
        CadDslVisitor.Tr.cur("SurfaceDistance($s1, $s2)")
        MemorySegment m = R7_BRepExtrema_DistShapeShape__s1_s2(s1, s2)
        if (m) {
            double p1x = m.get(ValueLayout.JAVA_DOUBLE,0)
            double p1y = m.get(ValueLayout.JAVA_DOUBLE,8)
            double p1z = m.get(ValueLayout.JAVA_DOUBLE,16)
            v1 = new Vec(p1x, p1y, p1z)
            double p2x = m.get(ValueLayout.JAVA_DOUBLE,24)
            double p2y = m.get(ValueLayout.JAVA_DOUBLE,32)
            double p2z = m.get(ValueLayout.JAVA_DOUBLE,40)
            v2 = new Vec(p2x, p2y, p2z)
            dist = m.get(ValueLayout.JAVA_DOUBLE,48)
        } else {
            v1 = new Vec()
            v2 = new Vec()
            dist = Double.NEGATIVE_INFINITY
        }
    }

    @Override
    String toString() {
        return "SurfaceDistance{" +
                "v1=" + v1 +
                ", v2=" + v2 +
                ", dist=" + dist +
                '}'
    }
}
