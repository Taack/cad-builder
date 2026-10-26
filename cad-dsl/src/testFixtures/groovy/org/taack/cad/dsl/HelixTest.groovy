package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.TestImages.image

@CompileStatic
class HelixTest {

    @Test
    void "Spring"() {
        cd().helix(10, 4, 40).wire(1).display(image())
    }

    @Test
    void "Conical Spring"() {
        cd().helix(10, 5, 30, 4).wire(1).display(image())
    }

    @Test
    void "Left-handed Spring along X"() {
        cd().direction(new Vec(1, 0, 0)).helix(10, -4, 40).wire(1).display(image())
    }

    @Test
    void "Threaded Rod with Trapezoidal Profile"() {
        double depth = 0.8
        double pitch = 2

        Vec2d pOri = new Vec2d(-0.2, -0.7)
        Vec2d p2 = new Vec2d(depth, -0.25)
        Vec2d p3 = new Vec2d(depth, 0.25)
        Vec2d p4 = new Vec2d(-0.2, 0.7)

        cd().cylinder(5, 30).fuse {
            position(new Vec(2)) {
                helix(5, pitch, 26).profile(pOri) {
                    edge(p2)
                    edge(p3)
                    edge(p4)
                    edge(pOri)
                }
            }
        }.display(image())
    }

    @Test
    void "Coil with Arc Profile"() {
        Vec2d pOri = new Vec2d(-1, -1)
        Vec2d p2 = new Vec2d(1, -1)
        Vec2d p3 = new Vec2d(1, 1)
        Vec2d p4 = new Vec2d(-1, 1)

        cd().helix(10, 6, 30).profile(pOri) {
            edge(p2)
            arc(p3, new Vec2d(1.6, 0))
            edge(p4)
            edge(pOri)
        }.display(image())
    }
}
