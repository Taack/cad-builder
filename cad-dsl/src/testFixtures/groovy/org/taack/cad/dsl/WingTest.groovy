package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.TestImages.image

@CompileStatic
class WingTest {

    @Test
    void "Tapered Wing with NACA Sections"() {
        cd().wing {
            section(new Vec(), 2).naca('2412')
            section(new Vec(0.6, 8, 0.4), 1, -3).naca('0012')
        }.display(image())
    }

    @Test
    void "Wing with Free Profile Sections"() {
        Vec2d te = new Vec2d(1, 0)
        Vec2d le = new Vec2d(0, 0)

        cd().wing {
            section(new Vec(), 2).profile(te) {
                spline(new Vec2d(0.5, 0.09), new Vec2d(0.15, 0.08), le)
                spline(new Vec2d(0.15, -0.04), new Vec2d(0.5, -0.03), te)
            }
            section(new Vec(0.3, 5, 0.3), 1.6, -1).profile(te) {
                spline(new Vec2d(0.5, 0.08), new Vec2d(0.15, 0.07), le)
                spline(new Vec2d(0.15, -0.035), new Vec2d(0.5, -0.025), te)
            }
            section(new Vec(0.9, 8, 0.8), 0.8, -3).profile(te) {
                spline(new Vec2d(0.5, 0.06), new Vec2d(0.15, 0.05), le)
                spline(new Vec2d(0.15, -0.03), new Vec2d(0.5, -0.02), te)
            }
        }.display(image())
    }

    @Test
    void "Wing with Flat Bottom Profile"() {
        Vec2d te = new Vec2d(1, 0)
        Vec2d le = new Vec2d(0, 0)

        cd().wing {
            section(new Vec(), 2).profile(te) {
                spline(new Vec2d(0.4, 0.1), new Vec2d(0.1, 0.07), le)
                edge(te)
            }
            section(new Vec(0, 6, 0), 2).profile(te) {
                spline(new Vec2d(0.4, 0.1), new Vec2d(0.1, 0.07), le)
                edge(te)
            }
        }.display(image())
    }

    @Test
    void "Three-blade Propeller"() {
        double hubRadius = 1.5
        double hubLength = 3
        double pitch = 6
        double tipRadius = 8
        // chord along the blade, from the root (inside the hub) to the tip
        Map<Double, Double> chords = [1.2d: 1.2d, 3d: 1.6d, 5.5d: 1.3d, 8d: 0.5d]

        cd().position(new Vec(-hubLength / 2)) {
            cylinder(hubRadius, hubLength)
        }.fuse {
            circularPattern(3) {
                wing {
                    chords.each { double r, double chord ->
                        // quarter chord on the blade axis, twisted like a helix of the propeller pitch
                        section(new Vec(-chord / 4, r, 0), chord, CadDslWing.pitchTwist(pitch, r)).naca(r < tipRadius ? '4412' : '0010')
                    }
                }
            }
        }.display(image())
    }
}
