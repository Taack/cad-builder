package org.taack.cad.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

@CompileStatic
final class SurfaceBounds {
    final double u1
    final double u2
    final double v1
    final double v2

    SurfaceBounds(MemorySegment m) {
        u1 = m.get(ValueLayout.JAVA_DOUBLE,0)
//        u1 = u2 = v1 = v2 = 0.0d
        u2 = m.get(ValueLayout.JAVA_DOUBLE,8)
        v1 = m.get(ValueLayout.JAVA_DOUBLE,16)
        v2 = m.get(ValueLayout.JAVA_DOUBLE,24)
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
