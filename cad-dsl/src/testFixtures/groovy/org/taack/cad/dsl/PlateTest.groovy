package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class PlateTest {

    static CadDslFace plateThroughAPoint() {
        Vec p1 = new Vec(0, 0, 0)
        Vec p2 = new Vec(0, 10, 0)
        Vec p3 = new Vec(0, 10, 10)
        Vec p4 = new Vec(0, 0, 10)

        cd().plate {
            boundary(p1) {
                edge(p2)
                edge(p3)
                edge(p4)
                edge(p1)
            }
            through(new Vec(5, 5, 5))
        }
    }

    static CadDslFace plateWithInnerCurve() {
        Vec p1 = new Vec(0, 0, 0)
        Vec p2 = new Vec(10, 0, 0)
        Vec p3 = new Vec(10, 10, 0)
        Vec p4 = new Vec(0, 10, 0)

        cd().plate {
            boundary(p1) {
                arc(p2, new Vec(5, -1, 1))
                edge(p3)
                arc(p4, new Vec(5, 11, 1))
                edge(p1)
            }
            // a ridge across the plate
            curve(new Vec(2, 5, 1)) {
                arc(new Vec(8, 5, 1), new Vec(5, 5, 4))
            }
        }
    }

    static CadDslSolid solidUnderPlate() {
        plateWithInnerCurve().prism(new Vec(0, 0, -2))
    }

    static CadDslSolid domeOverACircle() {
        double radius = 5
        double height = 3

        cd().plate {
            boundary(new Vec(radius, 0, 0)) {
                arc(new Vec(-radius, 0, 0), new Vec(0, radius, 0))
                arc(new Vec(radius, 0, 0), new Vec(0, -radius, 0))
            }
            through(new Vec(0, 0, height))
        }.prism(new Vec(0, 0, -1))
    }

    @Test
    void "Plate Surface Through a Point"() {
        plateThroughAPoint().display()
    }

    @Test
    void "Plate Surface with an Inner Curve"() {
        plateWithInnerCurve().display()
    }

    @Test
    void "Solid Under a Plate Surface"() {
        solidUnderPlate().display()
    }

    @Test
    void "Dome over a Circle"() {
        domeOverACircle().display()
    }
}
