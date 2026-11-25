package org.taack.cad.dsl.check

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.feature.CadFeatureFrame

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
    void "The lines calling a helper drawing the contour, up to the code out of a method"() {
        // the script calls part(), which calls the helper face() twice, from line 30 and 31
        List<CadFeatureFrame> frames = [new CadFeatureFrame('Plane', 'run', 'Plane.groovy', 5),
                                        new CadFeatureFrame('Plane', 'part', 'Plane.groovy', 31),
                                        new CadFeatureFrame('Plane$Profile', 'face', 'Plane.groovy', 90)]
        assertEquals('Plane.groovy:31, Plane.groovy:5', ContourRecorder.Visitor.callers(frames))
        // drawn in a closure: no helper
        assertEquals('', ContourRecorder.Visitor.callers([new CadFeatureFrame('Plane', 'run', 'Plane.groovy', 5),
                                                          new CadFeatureFrame('Plane$_run_closure1', 'doCall', 'Plane.groovy', 7)]))
        def c = triangle('Plane.groovy:90', 1, 0d)
        assertEquals('Plane.groovy:90', c.where)
    }

    @Test
    void "Elements turned by the DSL are no loop to write, written out they are"() {
        // a contour repeating 3 elements 4 times
        def teeth = { int turns ->
            def c = new Contour('Gear.groovy:10', true, 1, 1, [10d, 0d, 0d] as double[])
            c.turns = turns
            for (int k = 0; k < 4; k++) {
                double a = Math.PI / 2d * k
                for (double[] p : [[10d, 2d], [12d, 2d], [12d, 4d]]) {
                    double r = Math.hypot(p[0], p[1]), t = Math.atan2(p[1], p[0]) + a
                    c.edge([r * Math.cos(t), r * Math.sin(t), 0d] as double[])
                }
                double t = Math.PI / 2d * (k + 1)
                c.edge([10d * Math.cos(t), 10d * Math.sin(t), 0d] as double[])
            }
            c
        }
        assertEquals(1, ContourCheck.check([teeth(1)]).size())
        assertEquals([], ContourCheck.check([teeth(4)]))
    }

    @Test
    void "Contours of unknown code are compared"() {
        assertEquals(1, ContourCheck.check([triangle('?', 1, 80d), triangle('?', 2, -80d)]).size())
    }
}
