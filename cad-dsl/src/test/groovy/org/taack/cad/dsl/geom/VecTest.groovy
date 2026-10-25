package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class VecTest {
    @Test
    void "Distance between points counts their z"() {
        assertEquals(8, new Vec(0, 0, 0).distance(new Vec(0, 0, 8)), 1e-12)
        assertEquals(3, new Vec(1, 2, 3).distance(new Vec(2, 4, 5)), 1e-12)
        // to a Vec2d, in the plane z = 0
        assertEquals(5, new Vec(3, 4, 7).distance(new Vec2d()), 1e-12)
    }

    @Test
    void "Distance counts z in dynamic Groovy too"() {
        Object a = new Vec(0, 0, 0), b = new Vec(0, 0, 8)
        assertEquals(8d, a.invokeMethod('distance', b))
    }
}
