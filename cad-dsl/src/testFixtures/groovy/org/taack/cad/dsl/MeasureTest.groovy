package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Outline
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
@Tag('measures')
class MeasureTest {
    private static final double TOLERANCE = 1e-6d

    @TempDir
    File dir

    // Vec has no equals
    private static void assertVec(Vec expected, Vec actual) {
        assertEquals(expected.toString(), actual.toString())
    }

    private static CadDslSolid box(Vec at) {
        cd().position(at) { box(10, 20, 30) }
    }

    @Test
    void "Measures of a box"() {
        def m = box(new Vec(0, 0, 0)).check('box').measure()
        assertEquals(6000d, m.volume, TOLERANCE)
        assertEquals(2200d, m.area, TOLERANCE)
        assertVec(new Vec(5, 10, 15), m.center)
        assertVec(new Vec(10, 20, 30), m.size)
        assertEquals(7.44d, m.mass(1.24d), TOLERANCE)
        assertTrue(m.valid)
    }

    @Test
    void "A prism and a hole drawn on a face are valid"() {
        def boss = cd().box(20, 20, 2).topZ().wireFrom() { circle(5) }.toFace().prism(1).check('boss').measure()
        assertEquals(800d + Math.PI * 25d, boss.volume, TOLERANCE)
        def hole = cd().box(20, 20, 2).topZ().wireFrom() { circle(5) }.toFace().hole(-1).check('hole').measure()
        assertEquals(800d - Math.PI * 25d, hole.volume, TOLERANCE)
    }

    @Test
    void "Overlap and distance of solids of two models"() {
        def a = box(new Vec(0, 0, 0))
        assertEquals(3000d, a.overlap(box(new Vec(5, 0, 0))), TOLERANCE)
        assertEquals(0d, a.overlap(box(new Vec(12, 0, 0))), TOLERANCE)
        assertEquals(2d, a.distance(box(new Vec(12, 0, 0))), TOLERANCE)
        assertEquals(0d, a.distance(box(new Vec(5, 0, 0))), TOLERANCE)
        def closest = a.closest(box(new Vec(12, 5, 0)))
        assertEquals(2d, closest.distance, TOLERANCE)
        assertEquals(10d, closest.from.x, TOLERANCE)
        assertEquals(12d, closest.to.x, TOLERANCE)
    }

    // A plate 0.8 thick with a hole, as converted from a STEP file
    private static CadDslSolid plate(double dx, double dy) {
        cd().position(new Vec(dx, dy, 0)) { box(0.8d, 10, 10) }
                .cut { position(new Vec(-1 + dx, 5 + dy, 5)) { direction(Vec.vX); cylinder(0.6d, 3) } }
    }

    @Test
    void "Overlap of solids whose faces nearly coincide"() {
        // OCCT finds no common, warning only, unless faces this close are one
        assertEquals(80d - Math.PI * 0.36d * 0.8d, plate(0, 0).overlap(plate(0.000094d, 0.000116d)), 0.1d)
    }

    @Test
    void "A prism widening by its draft keeps its sharp corners, and booleans find it"() {
        double a = 1.002d, b = 51.64d, h = 30d, k = Math.tan(Math.toRadians(0.5d))
        CadDsl prism = cd()
        prism.face(new Frame(new Vec(60.426d, -4.157d, -20.3d), Vec.vX, Vec.vY), Outline.rectangle(a, b)).prism(new Vec(h, 0, 0), draft: -0.5d)
        assertEquals(a * b * h + (a + b) * k * h * h + 4 * k * k * h * h * h / 3, prism.measure().volume, 1e-3d)
        // LocOpe_DPrism made its corners cones of apex the corners, which a common lost
        def cylinder = cd().position(new Vec(-147.554d, 35.643d, -48.2d)) { cylinder(229.0d, 56.0d) }
        assertEquals(1049.1d, prism.overlap(cylinder), 0.1d)
    }

    @Test
    void "The normal given with the direction turns the primitives and starts the helices"() {
        // x along the normal y, y along z x y = -x
        def box = cd().direction(Vec.vZ, Vec.vY).box(10, 20, 30).measure()
        assertEquals(-20d, box.min.x, TOLERANCE)
        assertEquals(10d, box.max.y, TOLERANCE)
        // a quarter turn from (0, 5) to (-5, 0)
        def helix = cd().direction(Vec.vZ, Vec.vY).helix(5, 1, 0.25).wire(0.2).measure()
        assertEquals(-5.2d, helix.min.x, 0.01d)
        assertEquals(5.2d, helix.max.y, 0.01d)
    }

    @Test
    void "Move lifts the pen in a 3D wire, for a text or another contour"() {
        def letters = cd().wireFrom(new Vec(0, 0, 0)) {
            text('A', 5)
            move(new Vec(10, 0, 0))
            text('B', 5)
        }.toFace().prism(new Vec(0, 0, 1)).measure()
        assertEquals(13.1d, letters.max.x, 0.1d)    // B from x = 10.5 to 13.1, not over A
        def squares = cd().wireFrom(new Vec(0, 0, 0)) {
            edge(new Vec(2, 0, 0)); edge(new Vec(2, 2, 0)); edge(new Vec(0, 2, 0)); edge(new Vec(0, 0, 0))
            move(new Vec(5, 0, 0))
            edge(new Vec(7, 0, 0)); edge(new Vec(7, 2, 0)); edge(new Vec(5, 2, 0)); edge(new Vec(5, 0, 0))
        }.toFace().prism(new Vec(0, 0, 1)).measure()
        assertEquals(8d, squares.volume, TOLERANCE)
        assertEquals(7d, squares.max.x, TOLERANCE)
    }

    @Test
    void "Rotate at the root, then cut the rotated solid"() {
        def part = box(new Vec(0, 0, 0))
        part.rotate(new Vec(0, 0, 0), new Vec(0, 0, 1), 90)
        part.cut { position(new Vec(-15, 2, -1)) { box(10, 6, 40) } }
        def m = part.measure()
        assertVec(new Vec(-20, 0, 0), m.min)
        assertEquals(6000d - 1800d, m.volume, 1e-3d)
    }

    // A square 4 x 4 at (x, y), in the plane z = -1, its prism through the box
    private static void squarePrism(CadDsl dsl, double x, double y) {
        dsl.wireFrom(new Vec(x, y, -1)) {
            edge(new Vec(x + 4, y, -1))
            edge(new Vec(x + 4, y + 4, -1))
            edge(new Vec(x, y + 4, -1))
            edge(new Vec(x, y, -1))
        }.toFace().prism(new Vec(0, 0, 32))
    }

    @Test
    void "Prisms made in the closures of cut and common are their solids"() {
        CadDsl cut = cd()
        cut.position(new Vec(0, 0, 0)) { box(10, 20, 30) }
        cut.cut { squarePrism(delegate, 3, 3); squarePrism(delegate, 3, 13) }
        assertEquals(6000d - 2 * 480d, cut.check('cut').measure().volume, 1e-3d)
        CadDsl common = cd()
        common.position(new Vec(0, 0, 0)) { box(10, 20, 30) }
        common.common { squarePrism(delegate, 3, 3) }
        assertEquals(480d, common.check('common').measure().volume, 1e-3d)
    }

    @Test
    void "Cut, fuse and common in a closure work on the solid they are called on"() {
        // a 4 x 4 x 20 bar through a 10 mm cube, less its part above z = 5: a pocket 4 x 4 x 5 in the cube
        def cut = cd().box(10, 10, 10).cut {
            position(new Vec(3, 3, -5)) { box(4, 4, 20) }.cut { position(new Vec(0, 0, 5)) { box(10, 10, 15) } }
        }
        assertEquals(1000d - 80d, cut.check('cut').measure().volume, 1e-3d)
        // two 4 mm cubes at opposite corners
        def fuse = cd().box(10, 10, 10).cut {
            box(4, 4, 4).fuse { position(new Vec(6, 6, 0)) { box(4, 4, 4) } }
        }
        assertEquals(1000d - 128d, fuse.check('fuse').measure().volume, 1e-3d)
        // a 4 mm cube less its part under z = 2
        def common = cd().box(10, 10, 10).cut {
            box(4, 4, 4).common { position(new Vec(0, 0, 2)) { box(10, 10, 20) } }
        }
        assertEquals(1000d - 32d, common.check('common').measure().volume, 1e-3d)
        // the result is the last solid of the closure: translate moves it
        def moved = cd().box(10, 10, 10).cut {
            box(4, 4, 4).cut { position(new Vec(0, 0, 2)) { box(10, 10, 20) } }
            translate(new Vec(6, 6, 0))
        }
        assertEquals(1000d - 32d, moved.check('moved').measure().volume, 1e-3d)
        assertVec(new Vec(0, 0, 0), moved.measure().min)
    }

    @Test
    void "Translate at the root, then cut the moved solid"() {
        def part = box(new Vec(0, 0, 0))
        part.translate(new Vec(100, 0, 0)).cut { position(new Vec(105, 0, 0)) { box(10, 20, 30) } }
        assertEquals(3000d, part.measure().volume, 1e-3d)
    }

    @Test
    void "Import a STEP file at the current position and direction"() {
        File step = new File(dir, 'box.step')
        box(new Vec(0, 0, 0)).display(step.path)
        def m = cd().position(new Vec(100, 0, 0)) { importStep(step.path) }.measure()
        assertEquals(6000d, m.volume, 1e-3d)
        assertVec(new Vec(100, 0, 0), m.min)
        // along X, the z of the file goes along X
        def turned = cd().position(new Vec(0, 0, 0)) { direction(new Vec(1, 0, 0)).importStep(step.path) }.measure()
        assertEquals(30d, turned.size.x, 1e-3d)
        // a cut of the imported solid
        def cut = cd()
        cut.importStep(step.path)
        cut.cut { position(new Vec(0, 0, 0)) { box(5, 20, 30) } }
        assertEquals(3000d, cut.measure().volume, 1e-3d)
    }
}
