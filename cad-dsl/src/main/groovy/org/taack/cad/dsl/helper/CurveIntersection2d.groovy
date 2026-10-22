package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernels

@CompileStatic
class CurveIntersection2d {
    List<Vec2d> results

    CurveIntersection2d(IClosedShape2d s1, IClosedShape2d s2) {
        results = CadKernels.get().intersections(s1, s2)
    }


    @Override
    String toString() {
        return "CurveIntersection2d{" +
                "results=" + results +
                '}';
    }
}
