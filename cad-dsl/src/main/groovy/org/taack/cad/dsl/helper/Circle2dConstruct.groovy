package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernels

@CompileStatic
class Circle2dConstruct {

    final private List<Vec2d> centers
    final private double radius

    Circle2dConstruct(Vec2d p1, Vec2d p2, Number radius) {
        this.radius = radius.toDouble()
        centers = CadKernels.get().circleCenters(p1, p2, this.radius)
        if (centers.size() != 2)
            throw new RuntimeException()
    }

    Circle2d circle2d1(boolean reverse = false) {
        new Circle2d(centers[0], radius, reverse)
    }

    Circle2d circle2d2(boolean reverse = false) {
        new Circle2d(centers[1], radius, reverse)
    }

}
