package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertNull
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.taack.cad.dsl.CadDsl.assembly
import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.TestImages.image

@CompileStatic
class DisplayOptionTest {

    @Test
    void "Builder keeps the settings"() {
        DisplayOption o = DisplayOption.builder()
                .edges(false, Color.RED)
                .camera(new Vec(100, -100, 100), new Vec())
                .perspective(45)
                .background(Color.WHITE, Color.GRAY, Color.BLACK)
                .light(2, new Vec(0, 0, 100), Color.YELLOW)
                .build()
        assertFalse(o.edgesVisible)
        assertEquals(Color.RED, o.edgeColor)
        assertEquals(Vec.vZ, o.cameraUp)
        assertEquals(45d, o.fieldOfView)
        assertNull(o.light(1))
        assertEquals(Color.YELLOW, o.light(2).color)
        assertThrows(IllegalArgumentException) { DisplayOption.builder().light(4, new Vec(), Color.WHITE) }
        assertThrows(IllegalArgumentException) { DisplayOption.builder().perspective(180) }
    }

    @Test
    @Tag('images')
    void "Box With Display Options"() {
        cd().box(80, 60, 10).topZ().wireFrom() {
            circle(20)
        }.toFace().prism(30).display(DisplayOption.builder()
                .edges(true, Color.BLACK)
                .camera(new Vec(150, -200, 150), new Vec(40, 30, 10))
                .perspective(30)
                .background(Color.hex('#1e3c72'), Color.hex('#7f8fa6'), Color.WHITE)
                .light(1, new Vec(200, -200, 300), Color.WHITE)
                .light(2, new Vec(-200, 100, 100), Color.ORANGE)
                .build(), image())
    }

    @Test
    @Tag('images')
    void "Transparent Background"() {
        cd().box(80, 60, 10).display(DisplayOption.builder().transparentBackground().build(), image())
    }

    @Test
    @Tag('images')
    void "Assembly With Display Options"() {
        assembly('pair') {
            place(part('cube', Color.BLUE) { box(20, 20, 20) })
            place(part('shaft', Color.ORANGE) { cylinder(8, 30) }) { translate(40, 0, 0) }
        }.display(DisplayOption.builder()
                .edges(false)
                .background(Color.WHITE, Color.WHITE, Color.GRAY)
                .build(), image())
    }

    @Test
    @Tag('images')
    void "Edges Of Colored Parts"() {
        assembly('pair') {
            place(part('cube', Color.BLUE) { box(20, 20, 20) })
            place(part('shaft', Color.ORANGE) { cylinder(8, 30) }) { translate(40, 0, 0) }
        }.display(DisplayOption.builder()
                .edges(true, Color.BLACK)
                .transparentBackground()
                .build(), image())
    }
}
