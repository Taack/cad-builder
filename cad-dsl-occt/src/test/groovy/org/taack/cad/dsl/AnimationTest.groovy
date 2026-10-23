package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static java.lang.Math.PI
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

    private static double cos(double a) { Math.cos(a) }

    private static double sin(double a) { Math.sin(a) }

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
