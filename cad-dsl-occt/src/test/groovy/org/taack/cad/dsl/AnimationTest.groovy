package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static java.lang.Math.*
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.assembly

@CompileStatic
class AnimationTest {
    @TempDir
    File dir

    private static void assertVec(Vec expected, Vec actual) {
        assertEquals(expected.x, actual.x, 1e-6, "$actual")
        assertEquals(expected.y, actual.y, 1e-6, "$actual")
        assertEquals(expected.z, actual.z, 1e-6, "$actual")
    }

    /**
     * Two gears (discs with a marker) and a rack driven by the small one.
     */
    static CadDslAssembly gearTrain() {
        assembly('gear train') {
            // Parts
            def basePart = part('base', Color.GRAY) { box(60, 30, 2) }
            def pinPart = part('pin', Color.RED) { cylinder(0.5, 4) }
            def rackPart = part('rack', Color.GREEN) { box(4, 50, 3) }
            // a disc with a marker on top
            def wheel = { double radius, Color color ->
                part(color) {
                    cylinder(radius, 3).fuse {
                        position(new Vec(radius - 3, -1, 3)) { box(2, 2, 2) }
                    }
                }
            }

            // Placements
            def base = place(basePart) { translate(-20, -15, -2) }
            def big = place(wheel(10, Color.ORANGE)) {
                revolute(axis(), base.topZ(new Vec(0, 0, 0)))
            }
            def small = place(wheel(5, Color.BLUE)) {
                revolute(axis(), base.topZ(new Vec(15, 0, 0)))
            }
            // pins on top of the gears, they turn with them
            place(pinPart) { align(butZ(), small.topZ(new Vec(12, 0, 3)).reverse()) }
            place(pinPart) { align(butZ(), big.topZ(new Vec(-6, 0, 3)).reverse()) }
            // against the small gear, sliding along Y from the side of the base
            def rack = place(rackPart) {
                slider(butY(), base.butY(new Vec(22, -15, 1.5)))
            }

            // Motion
            gear(big, small, -2)
            // the rack moves by the arc length of the small gear, towards +Y (against its slider z)
            gear(small, rack, -PI * 5 / 180)
            animate(duration: 4, fps: 12) {
                big.angle = { double t -> -45 * t }
            }
        }
    }

    /**
     * Crank and slider: the crank turns on a wall, the piston slides up and down in front of it.
     * Without solver, the rod angle and the piston height are computed from the crank angle.
     */
    static CadDslAssembly crankSlider() {
        assembly('crank and slider') {
            // crank radius, rod length
            double r = 8, l = 24
            // Parts
            def basePart = part('base', Color.GRAY) { box(30, 24, 2) }
            def wallPart = part('wall', Color.hex('#a0a8b0')) { box(30, 4, 60) }
            def crankPart = part('crank', Color.ORANGE) {
                cylinder(4, 3).fuse {
                    position(new Vec(0, -2, 0)) { box(r, 4, 3) }
                    position(new Vec(r, 0, 0)) { cylinder(1, 6) }
                }
            }
            def rodPart = part('rod', Color.BLUE) {
                cylinder(2.5, 2).fuse {
                    position(new Vec(0, -1, 0)) { box(l, 2, 2) }
                    position(new Vec(l, 0, 0)) { cylinder(2.5, 2) }
                }
            }
            def pistonPart = part('piston', Color.RED) {
                position(new Vec(-5, -4, 0)) { box(10, 8, 8) }
            }

            // Placements
            def base = place(basePart) { translate(-15, -20, -2) }
            def wall = place(wallPart) { translate(-15, 0, 0) }
            // its axis 15 mm high, out of the wall
            def crank = place(crankPart) {
                revolute(axis(), wall.butY(new Vec(0, 0, 15)))
            }
            // on the crank pin, in front of the crank
            def rod = place(rodPart) {
                revolute(axis(new Vec(0, 0, 0)), crank.at(crankPart.axis(new Vec(r, 0, 3))))
            }
            // its bottom on the rod end, sliding up from the base, against the wall
            def piston = place(pistonPart) {
                slider(butZ().reverse(), base.topZ(new Vec(0, -4, 0)))
            }

            // Motion: one turn in 2 s
            // height of the rod end above the crank axis, the crank at a degrees
            Closure<Double> rise = { double a ->
                r * sin(toRadians(a)) + sqrt(l * l - pow(r * cos(toRadians(a)), 2))
            }
            animate(duration: 2, fps: 25) {
                crank.angle = { double t -> 180 * t }
                // from the crank pin to the rod end, relative to the crank
                rod.angle = { double t ->
                    double a = 180 * t
                    toDegrees(atan2(rise(a) - r * sin(toRadians(a)), -r * cos(toRadians(a)))) - a
                }
                piston.distance = { double t -> 15 + rise(180 * t) }
            }
        }
    }

    @Test
    void "Crank and slider: the rod end stays on the piston"() {
        def engine = crankSlider()
        CadDslInstance rod = engine.instances[3], piston = engine.instances[4]
        [0d, 0.25d, 0.6d, 1d, 1.3d, 1.85d].each { double t ->
            // the rod end axis, mid-height of its eye, and the bottom center of the piston
            assertVec(piston.locationAt(t).apply(new Vec(0, 0, 0)), rod.locationAt(t).apply(new Vec(24, 0, 1)))
        }
        assertVec(new Vec(0, -4, 15 + 32), piston.locationAt(0.5).apply(new Vec()))
    }

    @Test
    void "Revolute and slider joints move the part around and along the connectors"() {
        CadDslInstance wheel = null, piston = null
        assembly {
            def shaft = place(part { cylinder(1, 20) })
            wheel = place(part { cylinder(5, 2) }) { revolute(axis(), shaft.axis().offset(5), 30) }
            piston = place(part { cylinder(1, 5) }) { slider(axis(), shaft.axis(), 3) }
            animate(duration: 1) {
                wheel.angle = { double t -> 30 + 60 * t }
                piston.distance = { double t -> 3 + 10 * t }
            }
        }
        assertVec(new Vec(cos(PI / 6), sin(PI / 6), 0), wheel.location.applyDir(Vec.vX))
        assertVec(new Vec(0, 0, 5), wheel.location.apply(new Vec()))
        assertVec(Vec.vY, wheel.locationAt(1).applyDir(Vec.vX))
        assertVec(new Vec(0, 0, 3), piston.location.apply(new Vec()))
        assertVec(new Vec(0, 0, 8), piston.locationAt(0.5).apply(new Vec()))
    }

    @Test
    void "Parts placed on a moving instance follow it, gears turn the joints they link"() {
        def train = gearTrain()
        CadDslInstance big = train.instances[1], small = train.instances[2], smallPin = train.instances[3]
        CadDslInstance bigPin = train.instances[4], rack = train.instances[5]
        // after 2 s: big -90 degrees, small +180, the pins turned with them, the rack 5 PI further
        assertVec(-Vec.vY, big.locationAt(2).applyDir(Vec.vX))
        assertVec(-Vec.vX, small.locationAt(2).applyDir(Vec.vX))
        assertVec(new Vec(12, 0, 3), smallPin.location.apply(new Vec()))
        assertVec(new Vec(18, 0, 3), smallPin.locationAt(2).apply(new Vec()))
        assertVec(new Vec(0, 6, 3), bigPin.locationAt(2).apply(new Vec()))
        Vec rack0 = rack.location.apply(new Vec())
        assertVec(rack0 + new Vec(0, 5 * PI, 0), rack.locationAt(2).apply(new Vec()))
    }

    @Test
    void "Each frame of the animation is rendered"() {
        gearTrain().display(new File(dir, 'frames/f%02d.png').path)
        List<File> frames = new File(dir, 'frames').listFiles().toList()
        assertEquals((0..<48).collect { String.format('f%02d.png', it) }, frames*.name.sort())
        assertTrue(frames.every { it.length() > 0 })
    }

    @Test
    void "STEP export shows the animation start"() {
        File step = new File(dir, 'train.step')
        gearTrain().display(step.path)
        // base, the two gears, pin, rack
        assertEquals(5, step.text.count('MANIFOLD_SOLID_BREP'))
    }

    @Test
    void "Joints and gears are checked"() {
        assertThrows(IllegalStateException) {
            assembly {
                def a = place(part { box(1, 1, 1) })
                animate(duration: 1) { a.angle = { double t -> t } }
            }
        }
        assertThrows(IllegalStateException) {
            assembly {
                def s = place(part { cylinder(1, 5) })
                def p = place(part { cylinder(1, 1) }) { slider(axis(), s.axis()) }
                animate(duration: 1) { p.angle = { double t -> t } }
            }
        }
        assertThrows(IllegalArgumentException) {
            assembly {
                def s = place(part { cylinder(1, 5) })
                def a = place(part { cylinder(2, 1) }) { revolute(axis(), s.axis()) }
                def b = place(part { cylinder(2, 1) }) { revolute(axis(), s.axis()) }
                gear(a, b, 1)
                gear(b, a, 1)
            }
        }
        assertThrows(IllegalStateException) {
            assembly {
                def s = place(part { cylinder(1, 5) })
                def a = place(part { cylinder(2, 1) }) { revolute(axis(), s.axis()) }
                def b = place(part { cylinder(2, 1) }) { revolute(axis(), s.axis()) }
                gear(a, b, 1)
                animate(duration: 1) { b.angle = { double t -> t } }
            }
        }
    }
}
