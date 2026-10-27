package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

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
}
