package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static java.lang.Math.sqrt
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

/**
 * Clearances and creepage distances on the examples of IEC 60664-1, annex A: two electrodes 1 x 10 x 10 on an
 * insulating plate, what stands between them along y longer than they are, for no path to go around it.
 */
@CompileStatic
@Tag('measures')
class InsulationTest {
    // the meshes are flat here: the paths are found within a few hundredths
    private static final double TOLERANCE = 0.02d

    private static CadDsl electrode(double x) {
        cd().position(new Vec(x, 0, 0)) { box(1, 10, 10) }
    }

    // A plate 5 thick, its top at z = 0, from x to x + length
    private static CadDsl plate(double x, double length) {
        cd().position(new Vec(x, -30, -5)) { box(length, 70, 5) }
    }

    private static Insulation insulation(CadDsl left, CadDsl right, List<? extends CadDslSolid> insulators) {
        new Insulation([left: [left], right: [right]], insulators)
    }

    @Test
    void "Over a wall, the clearance and the creepage distance follow it, across the grooves narrower than X"() {
        def wall = plate(-10, 24).fuse { position(new Vec(1.5d, -30, 0)) { box(1, 70, 15) } }
        def insulation = insulation(electrode(0), electrode(3), [wall])
        Gap clearance = insulation.clearance('left', 'right')
        assertEquals(2 * sqrt(0.25d + 25) + 1, clearance.length, TOLERANCE)
        assertEquals(10d, clearance.path.first().z, TOLERANCE)
        // the 90° corners at its foot are not bridged
        assertEquals(0.5d + 15 + 1 + 15 + 0.5d, insulation.creepage('left', 'right', 0.25d).length, TOLERANCE)
        // the groove between the electrodes and the wall, 0.5 wide, is crossed by a link X long from the top of the
        // electrodes, the wall facing them
        assertEquals(2 * (1 + 5 - sqrt(0.75d)) + 1, insulation.creepage('left', 'right', 1.0d).length, TOLERANCE)
    }

    @Test
    void "Beyond its limit, a path is not searched: the gap is longer"() {
        def wall = plate(-10, 24).fuse { position(new Vec(1.5d, -30, 0)) { box(1, 70, 15) } }
        def insulation = insulation(electrode(0), electrode(3), [wall])
        Gap clearance = insulation.clearance('left', 'right', 5.0d)
        assertTrue(!clearance.found)
        assertEquals(5d, clearance.length, TOLERANCE)
        assertTrue(!insulation.creepage('left', 'right', 0.25d, 20.0d).found)
        assertTrue(insulation.creepage('left', 'right', 1.0d, 20.0d).found)
    }

    @Test
    void "A groove with parallel sides is crossed when narrower than X, else followed"() {
        def grooved = plate(-10, 24).cut { position(new Vec(2.75d, -31, -2)) { box(0.5d, 72, 3) } }
        def insulation = insulation(electrode(0), electrode(6), [grooved])
        assertEquals(5d, insulation.clearance('left', 'right').length, TOLERANCE)
        assertEquals(5d, insulation.creepage('left', 'right', 1.0d).length, TOLERANCE)
        assertEquals(5d + 2 * 2, insulation.creepage('left', 'right', 0.25d).length, TOLERANCE)
    }

    @Test
    void "A V-shaped groove under 80° is bridged where it is X wide"() {
        // 2 wide, 4 deep
        def grooved = plate(-10, 25).cut {
            wireFrom(new Vec(2.75d, -31, 1)) {
                edge(new Vec(5.25d, -31, 1))
                edge(new Vec(4, -31, -4))
                edge(new Vec(2.75d, -31, 1))
            }.toFace().prism(new Vec(0, 72, 0))
        }
        def insulation = insulation(electrode(0), electrode(7), [grooved])
        assertEquals(6d, insulation.clearance('left', 'right').length, TOLERANCE)
        // down the sides to where the groove is X wide
        assertEquals(4 + 2 * sqrt(0.25d + 4) + 1, insulation.creepage('left', 'right', 1.0d).length, TOLERANCE)
        assertEquals(4 + 2 * sqrt(0.875d * 0.875d + 3.5d * 3.5d) + 0.25d, insulation.creepage('left', 'right', 0.25d).length, TOLERANCE)
    }

    @Test
    void "The creepage distance crosses the joint between two insulating parts"() {
        def insulation = insulation(electrode(0), electrode(7), [plate(-10, 14), plate(4, 14)])
        Gap creepage = insulation.creepage('left', 'right', 0.25d)
        assertEquals(6d, creepage.length, TOLERANCE)
        assertEquals(1d, creepage.path.first().x, TOLERANCE)
        assertEquals(7d, creepage.path.last().x, TOLERANCE)
    }

    @Test
    void "The gaps between every two conductors, searched in parallel"() {
        def insulation = new Insulation([left: [electrode(0)], middle: [electrode(3)], right: [electrode(7)]], [plate(-10, 24)])
        assertEquals(['left to middle', 'left to right', 'middle to right'], insulation.clearances().collect { Gap g -> "$g.from to $g.to".toString() })
        List<Gap> creepages = insulation.creepages(1.0d)
        assertEquals(insulation.creepage('left', 'right', 1.0d).length, creepages[1].length, 1e-9d)
        assertEquals(3d, creepages[2].length, TOLERANCE)
    }

    @Test
    void "Conductors enclosed have no path between them"() {
        def hollow = cd().position(new Vec(-5, -5, -5)) { box(20, 20, 20) }.cut { position(new Vec(-1, -1, -1)) { box(12, 12, 12) } }
        def inside = cd().position(new Vec(4, 4, 4)) { box(2, 2, 2) }
        def outside = cd().position(new Vec(20, 0, 0)) { box(2, 2, 2) }
        def insulation = new Insulation([inside: [inside], outside: [outside]], [hollow])
        assertTrue(Double.isInfinite(insulation.clearance('inside', 'outside').length))
        assertTrue(Double.isInfinite(insulation.creepage('inside', 'outside', 1.0d).length))
        assertTrue(!insulation.clearance('inside', 'outside').found)
    }
}
