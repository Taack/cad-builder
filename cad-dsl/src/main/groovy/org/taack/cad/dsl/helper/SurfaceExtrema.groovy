package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment
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
        double[] m = Natives.doubles(R6_GeomAPI_ExtremaCurveSurface__NbExtrema(extrema, i), 6)
        p1x = m[0]
        p1y = m[1]
        p1z = m[2]
        p2x = m[3]
        p2y = m[4]
        p2z = m[5]
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
