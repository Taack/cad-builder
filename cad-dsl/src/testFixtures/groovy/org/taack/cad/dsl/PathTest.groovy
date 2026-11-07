package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertThrows

import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.TestImages.image

@CompileStatic
class PathTest {

    @Test
    void "Cable through Points"() {
        cd().path([new Vec(), new Vec(20, 0, 0), new Vec(20, 15, 5), new Vec(35, 15, 20), new Vec(35, -10, 20)]).wire(1.5).display(image())
    }

    @Test
    void "Bent Rod with an Arc"() {
        cd().path(new Vec()) {
            edge(new Vec(0, 0, 20))
            arcAround(new Vec(10, 0, 30), new Vec(10, 0, 20))
            edge(new Vec(40, 0, 30))
        }.wire(2).display(image())
    }

    @Test
    void "Pipe cut from a Tube"() {
        List<Vec> points = [new Vec(), new Vec(30, 0, 0), new Vec(30, 30, 0), new Vec(30, 30, 30)]
        cd().path(points).wire(3).cut {
            path(points).wire(2)
        }.display(image())
    }

    @Test
    void "Rod bent through Points"() {
        // three quarter turns in three planes, rounded R6 on the center line
        CadDslSolid rod = cd().path([new Vec(), new Vec(40, 0, 0), new Vec(40, 30, 0), new Vec(40, 30, 25), new Vec(0, 30, 25)], 6)
                .wire(2).display(image())
        assert rod.check('rod').measure().volume > 0d
    }

    @Test
    void "Paper Clip with Half Circles"() {
        // each end a half circle: two quarter turns whose radius is half the distance of their points
        double r1 = 4d, r2 = 3d, r3 = 2d
        List<Vec> points = [new Vec(12, 0, 0), new Vec(12, 0, -2 * r1 + 30), new Vec(12 - 2 * r1, 0, -2 * r1 + 30),
                            new Vec(12 - 2 * r1, 0, 0), new Vec(12 - 2 * r1 + 2 * r2, 0, 0),
                            new Vec(12 - 2 * r1 + 2 * r2, 0, 18), new Vec(12 - 2 * r1 + 2 * r2 - 2 * r3, 0, 18),
                            new Vec(12 - 2 * r1 + 2 * r2 - 2 * r3, 0, 8)]
        cd().path(points, [0, r1, r1, r2, r2, r3, r3, 0]).wire(0.5).check('paper clip').display(image())
    }

    @Test
    void "Bends overlapping, or tighter than the Wire"() {
        List<Vec> points = [new Vec(), new Vec(10, 0, 0), new Vec(10, 10, 0)]
        assertThrows(IllegalArgumentException) { cd().path(points, 15) }
        assertThrows(IllegalArgumentException) { cd().path(points, 3).wire(3) }
    }
}
