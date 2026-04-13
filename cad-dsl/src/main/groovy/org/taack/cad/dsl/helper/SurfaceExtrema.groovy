package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import static org.taack.occt.NativeLib.*

@CompileStatic
final class SurfaceExtrema {
    final double p1x
    final double p1y
    final double p1z
    final double p2x
    final double p2y
    final double p2z

    SurfaceExtrema(MemorySegment extrema, int i) {
        MemorySegment m = R6_GeomAPI_ExtremaCurveSurface__NbExtrema(extrema, i)
        p1x = m.get(ValueLayout.JAVA_DOUBLE,0)
        p1y = m.get(ValueLayout.JAVA_DOUBLE,8)
        p1z = m.get(ValueLayout.JAVA_DOUBLE,16)
        p2x = m.get(ValueLayout.JAVA_DOUBLE,24)
        p2y = m.get(ValueLayout.JAVA_DOUBLE,32)
        p2z = m.get(ValueLayout.JAVA_DOUBLE,40)
    }


    @Override
    String toString() {
        return "SurfaceExtrema{" +
                "p1x=" + p1x +
                ", p1y=" + p1y +
                ", p1z=" + p1z +
                ", p2x=" + p2x +
                ", p2y=" + p2y +
                ", p2z=" + p2z +
                '}';
    }
}
