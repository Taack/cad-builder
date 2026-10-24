package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class SvgTest {
    @TempDir
    File dir

    // faces with a hole: each face has a bound, plus one per hole
    private static int holes(String step) {
        step.count('FACE_BOUND(') - step.count('ADVANCED_FACE(')
    }

    @Test
    void "Embossed path with a hole"() {
        File step = new File(dir, 'ring.step')
        cd().box(30, 30, 5).topZ().wireFrom() {
            // a square around a square: the even-odd rule makes a ring of it
            svg('M0 0H10V10H0Z M3 3H7V7H3Z', 10, align: 'center', valign: 'center')
        }.toFace().prism(1).display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // the top of the ring, and the top of the box around it
        assertEquals(2, holes(content))
    }

    @Test
    void "Embossed sun of the CITEL logo"() {
        File step = new File(dir, 'sun.step')
        cd().box(30, 30, 5).topZ().wireFrom() {
            svg('../citel-sun.svg', 20, align: 'center', valign: 'center')
        }.toFace().prism(1).display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // the two arcs of the disc stay cylinders
        assertEquals(2, content.count('CYLINDRICAL_SURFACE'))
    }

    @Test
    void "Engraved circle, exact cylinders"() {
        File step = new File(dir, 'circle.step')
        cd().box(30, 30, 5).topZ().wireFrom() {
            svg('<svg xmlns="http://www.w3.org/2000/svg"><circle cx="0" cy="0" r="1"/></svg>', 8, align: 'center', valign: 'center')
        }.toFace().hole(-1).display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // two half cylinders: the circle is read as two arcs
        assertEquals(2, content.count('CYLINDRICAL_SURFACE'))
    }

    @Test
    void "Bezier curves of a document, in absolute coordinates"() {
        File drawing = new File(dir, 'drop.svg')
        drawing.text = '''<?xml version="1.0" encoding="UTF-8"?>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20">
                <g transform="translate(10 10) scale(1 -1)"><path d="M-8 -8 C-8 8 8 8 8 -8 Q0 -2 -8 -8 Z"/></g>
            </svg>'''
        File step = new File(dir, 'drop.step')
        CadDsl c = cd()
        c.box(30, 30, 5)
        c.wireFrom(new Vec(15, 15, 5)) {
            svg(drawing.path, 12, align: 'center', valign: 'center')
        }.toFace().hole(new Vec(0, 0, -1))
        c.display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // the sides of the two curves
        assertEquals(2, content.count('SURFACE_OF_LINEAR_EXTRUSION'))
    }

    @Test
    void "Errors"() {
        assertThrows(IllegalArgumentException) {
            cd().box(10, 10, 1).topZ().wireFrom() { svg('M0 0 L1', 5) }
        }
        assertThrows(IllegalArgumentException) {
            cd().box(10, 10, 1).topZ().wireFrom() { svg('M0 0 H1 V1 Z', 5, size: 3) }
        }
    }
}
