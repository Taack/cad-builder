package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class Circle2dConstruct {

    final private MemorySegment solutions

    Circle2dConstruct(Vec2d p1, Vec2d p2, Number radius) {
        solutions = new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(p1.toGpPnt2d(), p2.toGpPnt2d(), radius.toDouble(), 0.01d)
        if (i_GccAna_Circ2d2TanRad__NbSolutions(solutions) != 2)
            throw new RuntimeException()
    }

    Circle2d circle2d1() {

    }

    Circle2d circle2d2() {

    }

}
