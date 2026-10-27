package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
@Tag('measures')
class MeasureTest {
    private static final double TOLERANCE = 1e-6d

    // Vec has no equals
    private static void assertVec(Vec expected, Vec actual) {
        assertEquals(expected.toString(), actual.toString())
    }

    private static CadDslSolid box(Vec at) {
        cd().position(at) { box(10, 20, 30) }
    }

    @Test
    void "Measures of a box"() {
        def m = box(new Vec(0, 0, 0)).check('box').measure()
        assertEquals(6000d, m.volume, TOLERANCE)
        assertEquals(2200d, m.area, TOLERANCE)
        assertVec(new Vec(5, 10, 15), m.center)
        assertVec(new Vec(10, 20, 30), m.size)
        assertEquals(7.44d, m.mass(1.24d), TOLERANCE)
        assertTrue(m.valid)
    }

    @Test
    void "Overlap and distance of solids of two models"() {
        def a = box(new Vec(0, 0, 0))
        assertEquals(3000d, a.overlap(box(new Vec(5, 0, 0))), TOLERANCE)
        assertEquals(0d, a.overlap(box(new Vec(12, 0, 0))), TOLERANCE)
        assertEquals(2d, a.distance(box(new Vec(12, 0, 0))), TOLERANCE)
        assertEquals(0d, a.distance(box(new Vec(5, 0, 0))), TOLERANCE)
    }

    @Test
    void "Rotate at the root, then cut the rotated solid"() {
        def part = box(new Vec(0, 0, 0))
        part.rotate(new Vec(0, 0, 0), new Vec(0, 0, 1), 90)
        part.cut { position(new Vec(-15, 2, -1)) { box(10, 6, 40) } }
        def m = part.measure()
        assertVec(new Vec(-20, 0, 0), m.min)
        assertEquals(6000d - 1800d, m.volume, 1e-3d)
    }

    @Test
    void "Translate at the root, then cut the moved solid"() {
        def part = box(new Vec(0, 0, 0))
        part.translate(new Vec(100, 0, 0)).cut { position(new Vec(105, 0, 0)) { box(10, 20, 30) } }
        assertEquals(3000d, part.measure().volume, 1e-3d)
    }
}
