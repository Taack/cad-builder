package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.assembly
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class AssemblyTest {
    @TempDir
    File dir

    /**
     * A chassis on two axles, each axle a sub-assembly of a shaft and two wheels, and a cap on a wheel.
     */
    static CadDslAssembly cart() {
        assembly('cart') {
            def chassis = part('chassis', Color.hex('#3080ff')) {
                box(40, 20, 4)
            }
            def wheel = part('wheel', Color.ORANGE) {
                cylinder(5, 2).cut { cylinder(1, 2) }
            }
            CadDslInstance shaft = null, outerWheel = null
            def axle = assembly('axle') {
                shaft = place(part('shaft') { cylinder(1, 26) })
                // on the shaft, flush with its ends
                place(wheel) { align(axis(), shaft.axis()) }
                outerWheel = place(wheel) { align(axis(), shaft.axis().offset(24)) }
            }
            def c = place(chassis) { translate(-20, 0, 4) }
            // under the chassis, 3 mm out of its sides
            place(axle) { mate(shaft.butZ(), c.butY(new Vec(-14, 0, 3)).offset(-23)) }
            def rear = place(axle) { mate(shaft.butZ(), c.butY(new Vec(14, 0, 3)).offset(-23)) }
            place(part('cap', Color.RED) { cylinder(2, 0.5) }) { mate(butZ(), rear.at(outerWheel.topZ())) }
        }
    }

    @Test
    void "STEP export shares the parts and sub-assemblies placed several times"() {
        File step = new File(dir, 'cart.step')
        cart().display(step.path)
        String content = step.text
        // chassis, wheel, shaft, cap written once
        assertEquals(4, content.count('MANIFOLD_SOLID_BREP'))
        // cart: chassis + 2 axles + cap, axle: shaft + 2 wheels
        assertEquals(7, content.count('NEXT_ASSEMBLY_USAGE_OCCURRENCE('))
        ['cart', 'axle', 'chassis', 'wheel', 'shaft', 'cap'].each { assertTrue(content.contains("'$it'"), it) }
        assertTrue(content.contains('COLOUR_RGB'))
    }

    @Test
    void "Assembly exports to STL and image"() {
        File stl = new File(dir, 'cart.stl')
        File png = new File(dir, 'cart.png')
        cart().display(stl.path).display(png.path)
        assertTrue(stl.length() > 0)
        assertTrue(png.length() > 0)
    }

    @Test
    void "Bodies are parts placed once"() {
        File step = new File(dir, 'bodies.step')
        CadDslSolid shaft = cd().cylinder(0.5, 6)
        assembly {
            body(shaft)
            body(Color.RED) { box(1, 1, 1).rotate(new Vec(), Vec.vZ, 45) }
        }.display(step.path)
        assertEquals(2, step.text.count('MANIFOLD_SOLID_BREP'))
    }

    private static void assertVec(Vec expected, Vec actual) {
        assertEquals(expected.x, actual.x, 1e-6, "$actual")
        assertEquals(expected.y, actual.y, 1e-6, "$actual")
        assertEquals(expected.z, actual.z, 1e-6, "$actual")
    }

    @Test
    void "Mate puts the connectors face against face"() {
        CadDslInstance base = null, onTop = null, onSide = null, turned = null
        assembly {
            def block = part { box(10, 10, 10) }
            base = place(block) { rotate(Vec.vZ, 90) }
            // butZ center (5, 5, 0) on the top face of base, at (-5, 5, 10)
            onTop = place(block) { mate(butZ(), base.topZ()) }
            // with a 2 mm gap, on the side facing +Y, upside down: topZ against it
            onSide = place(block) { mate(topZ(), base.topY(new Vec(-3, 20, 4)).offset(2)) }
            // turned 90 degrees around the normal
            turned = place(block) { mate(butZ(), base.topZ().rotate(90)) }
        }
        assertVec(new Vec(-5, 5, 10), onTop.location.apply(new Vec(5, 5, 0)))
        assertVec(Vec.vZ, onTop.location.applyDir(Vec.vZ))
        // the connector x direction belongs to the part: onTop follows the orientation of base
        assertVec(Vec.vY, onTop.location.applyDir(Vec.vX))
        assertVec(new Vec(-3, 12, 4), onSide.location.apply(new Vec(5, 5, 10)))
        assertVec(-Vec.vY, onSide.location.applyDir(Vec.vZ))
        assertVec(-Vec.vX, turned.location.applyDir(Vec.vX))
    }

    @Test
    void "Connectors come from the instance coordinates and through sub-assemblies"() {
        CadDslInstance shaft = null, rear = null
        def cart = assembly {
            CadDslInstance s = null
            def axle = assembly { s = place(part { cylinder(1, 26) }) }
            shaft = s
            rear = place(axle) { rotate(Vec.vX, 90); translate(14, 23, 3) }
        }
        // shaft along -Y once placed: its bottom face at y = 23, facing +Y
        def bottom = rear.at(shaft.butZ())
        assertTrue(bottom.owner.is(cart))
        assertVec(new Vec(14, 23, 3), bottom.frame.origin)
        assertVec(Vec.vY, bottom.frame.z)
    }

    @Test
    void "Align puts axes of cylindrical faces on each other"() {
        CadDslInstance plate = null, pin = null, wheel = null, pinAt = null
        assembly {
            def p = part {
                box(20, 10, 2).cut {
                    position(new Vec(5, 5, 0)) { cylinder(1, 2) }
                    position(new Vec(15, 5, 0)) { cylinder(1.5, 2) }
                }
            }
            plate = place(p) { rotate(Vec.vX, -90) }
            // the largest hole, along +Y once the plate is turned
            pin = place(part { cylinder(1.5, 6) }) { align(axis(), plate.axis().offset(-2)) }
            // the hole closest to a point, origin at the point projected on its axis
            pinAt = place(part { cylinder(1, 6) }) { align(axis(), plate.axis(new Vec(5, 1, -5.2))) }
            // on the outer face of the pin, from its top
            wheel = place(part { cylinder(4, 1) }) { align(axis(new Vec(0, 0, 1)), pin.axis(new Vec(15, 4, -5))) }
        }
        assertVec(new Vec(15, -2, -5), pin.location.apply(new Vec()))
        assertVec(Vec.vY, pin.location.applyDir(Vec.vZ))
        assertVec(new Vec(5, 1, -5), pinAt.location.apply(new Vec()))
        assertVec(new Vec(15, 3, -5), wheel.location.apply(new Vec()))
        assertThrows(IllegalArgumentException) {
            assembly { part { box(1, 1, 1) }.axis() }
        }
    }

    @Test
    void "Mate checks the coordinates of its connectors"() {
        assertThrows(IllegalArgumentException) {
            assembly {
                def block = part { box(1, 1, 1) }
                def other = part { box(1, 1, 1) }
                def b = place(block)
                place(block) { mate(other.butZ(), b.topZ()) }
            }
        }
        assertThrows(IllegalArgumentException) {
            assembly {
                def block = part { box(1, 1, 1) }
                place(block) { mate(butZ(), block.topZ()) }
            }
        }
    }

    @Test
    void "Faces are selected on parts, not on assemblies"() {
        assertThrows(UnsupportedOperationException) {
            assembly {
                def sub = assembly { place(part { box(1, 1, 1) }) }
                sub.topZ()
            }
        }
    }

    @Test
    void "An empty part is rejected"() {
        assertThrows(IllegalArgumentException) {
            assembly {
                part(Color.RED) {}
            }
        }
    }

    @Test
    void "An assembly cannot be placed in itself, nor changed once built"() {
        assertThrows(IllegalArgumentException) {
            assembly {
                place(delegate as CadDslAssembly)
            }
        }
        CadDslAssembly built = assembly {}
        assertThrows(IllegalStateException) {
            built.place(built.part { box(1, 1, 1) })
        }
    }
}
