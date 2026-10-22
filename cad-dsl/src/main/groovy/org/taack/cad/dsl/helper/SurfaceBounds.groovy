package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.R4_Geom_Surface__Bounds

@CompileStatic
final class SurfaceBounds {
    final double u1
    final double u2
    final double v1
    final double v2

    SurfaceBounds(MemorySegment surface) {
        double[] m = Natives.doubles(R4_Geom_Surface__Bounds(surface), 4)
        u1 = m[0]
        u2 = m[1]
        v1 = m[2]
        v2 = m[3]
    }


    @Override
    String toString() {
        return "SurfaceBounds{" +
                "u1=" + u1 +
                ", u2=" + u2 +
                ", v1=" + v1 +
                ", v2=" + v2 +
                '}';
    }
}
