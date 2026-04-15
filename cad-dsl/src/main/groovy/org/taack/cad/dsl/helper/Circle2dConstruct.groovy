package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class Circle2dConstruct {

    final private MemorySegment solutions
    final private double radius

    Circle2dConstruct(Vec2d p1, Vec2d p2, Number radius) {
        this.radius = radius.toDouble()
        solutions = new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(p1.toGpPnt2d(), p2.toGpPnt2d(), this.radius, 0.01d)
        if (i_GccAna_Circ2d2TanRad__NbSolutions(solutions) != 2)
            throw new RuntimeException()
    }

    Circle2d circle2d1(boolean reverse = false) {
        new Circle2d(ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(solutions, 1), radius, reverse)
    }

    Circle2d circle2d2(boolean reverse = false) {
        new Circle2d(ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(solutions, 2), radius, reverse)
    }

}
