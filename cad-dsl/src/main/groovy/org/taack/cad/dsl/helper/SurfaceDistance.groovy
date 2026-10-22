package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.MemorySegment
import static org.taack.occt.NativeLib.*



@CompileStatic
final class SurfaceDistance {
    final Vec v1
    final Vec v2
    final double dist

    SurfaceDistance(MemorySegment s1, MemorySegment s2) {
        double[] m = Natives.doubles(R7_BRepExtrema_DistShapeShape__s1_s2(s1, s2), 7)
        if (m) {
            v1 = new Vec(m[0], m[1], m[2])
            v2 = new Vec(m[3], m[4], m[5])
            dist = m[6]
        } else {
            v1 = new Vec()
            v2 = new Vec()
            dist = Double.NEGATIVE_INFINITY
        }
        CadDslVisitor.Tr.cur(this.toString())
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
