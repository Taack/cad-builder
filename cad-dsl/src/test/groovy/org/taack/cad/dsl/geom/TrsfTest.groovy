package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class TrsfTest {
    private static void assertVec(Vec expected, Vec actual) {
        assertEquals(expected.x, actual.x, 1e-9)
        assertEquals(expected.y, actual.y, 1e-9)
        assertEquals(expected.z, actual.z, 1e-9)
    }

    @Test
    void "Rotation is counterclockwise in degrees"() {
        assertVec(new Vec(0, 1, 0), Trsf.rotation(Vec.vZ, 90).apply(new Vec(1, 0, 0)))
        assertVec(new Vec(0, 0, 1), Trsf.rotation(Vec.vX, 90).apply(new Vec(0, 1, 0)))
    }

    @Test
    void "Rotation around a point keeps it"() {
        Vec around = new Vec(2, 3, 4)
        Trsf r = Trsf.rotation(new Vec(1, 1, 1), 33, around)
        assertVec(around, r.apply(around))
        assertVec(new Vec(2, 4, 0), Trsf.rotation(Vec.vZ, 180, new Vec(1, 2, 0)).apply(new Vec()))
    }

    @Test
    void "Product applies the right transformation first"() {
        Trsf t = Trsf.translation(new Vec(1, 0, 0)) * Trsf.rotation(Vec.vZ, 90)
        assertVec(new Vec(1, 1, 0), t.apply(new Vec(1, 0, 0)))
        Trsf u = Trsf.rotation(Vec.vZ, 90) * Trsf.translation(new Vec(1, 0, 0))
        assertVec(new Vec(0, 2, 0), u.apply(new Vec(1, 0, 0)))
    }
}
