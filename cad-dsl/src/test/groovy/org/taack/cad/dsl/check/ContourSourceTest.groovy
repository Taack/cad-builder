package org.taack.cad.dsl.check

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * The code drawing the contours, for the close ones: the same line run twice draws them once already. The kernel
 * tests (ContourCheckTest) cannot show it: their code is of the DSL package, its contours have no source.
 */
@CompileStatic
class ContourSourceTest {

    // a triangle in the plane y, as the bevel under the hinge of an elevon, in a model of its own
    private static Contour triangle(String source, int model, double y) {
        def c = new Contour(source, true, model, model, [0d, y, 0.5d] as double[])
        c.edge([0d, y, -20d] as double[])
        c.edge([20.5d, y, -20d] as double[])
        c.edge([0d, y, 0.5d] as double[])
        c
    }

    @Test
    void "Nothing for a line drawing the same contour once for each side"() {
        assertEquals([], ContourCheck.check([triangle('Plane.groovy:10', 1, 80d), triangle('Plane.groovy:10', 2, -80d)]))
    }

    @Test
    void "The same contour drawn by two lines, in parallel planes"() {
        assertEquals(['Plane.groovy:10, Plane.groovy:20: the same contour 2 times in parallel planes 160 mm apart at most, draw it once'],
                ContourCheck.check([triangle('Plane.groovy:10', 1, 80d), triangle('Plane.groovy:20', 2, -80d)]))
    }

    @Test
    void "Contours of unknown code are compared"() {
        assertEquals(1, ContourCheck.check([triangle('?', 1, 80d), triangle('?', 2, -80d)]).size())
    }
}
