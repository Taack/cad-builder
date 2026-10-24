package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class TextTest {
    @TempDir
    File dir

    @Test
    void "Engraved text keeps the islands of its letters"() {
        File step = new File(dir, 'engraved.step')
        cd().box(30, 20, 5).topZ().wireFrom() {
            text('O', 10, align: 'center', valign: 'center')
        }.toFace().hole(-1).display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // 5 faces of the box, its top around the O, the island inside the O, the bottom of the groove
        assertEquals(8, content.count('PLANE('))
    }

    @Test
    void "Text in absolute coordinates"() {
        File step = new File(dir, 'absolute.step')
        CadDsl c = cd()
        c.box(30, 20, 5)
        // on the top face, at its middle
        c.wireFrom(new Vec(15, 10, 5)) {
            text('O', 10, align: 'center', valign: 'center')
        }.toFace().hole(new Vec(0, 0, -1))
        c.display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        assertEquals(8, content.count('PLANE('))
    }

    @Test
    void "Embossed text and a circle in the same sketch"() {
        File step = new File(dir, 'embossed.step')
        cd().box(60, 20, 5).topZ().wireFrom() {
            text('CITEL', 6, font: 'serif', bold: true, align: 'center', valign: 'center')
            move(-22, 0)
            circle(3)
        }.toFace().prism(1).display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // the circle, among the letters
        assertEquals(1, content.count('CYLINDRICAL_SURFACE'))
    }

    @Test
    void "Text alone, a solid per letter"() {
        File step = new File(dir, 'letters.step')
        cd().wireFrom(new Vec2d()) {
            text('Hi', 10)
        }.toFace().prism(2).display(step.path)
        // the H, the stem and the dot of the i
        assertEquals(3, step.text.count('MANIFOLD_SOLID_BREP'))
    }

    @Test
    void "Unknown font"() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException) {
            cd().box(10, 10, 1).topZ().wireFrom() {
                text('x', 5, font: 'No Such Font Family')
            }
        }
        assertTrue(e.message.contains('font not found'), e.message)
    }

    @Test
    void "Unknown option"() {
        assertThrows(IllegalArgumentException) {
            new TextOptions(colour: 'red')
        }
    }
}
