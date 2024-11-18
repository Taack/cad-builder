package org.taack.cad.generic

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.builder.ICad

@CompileStatic
class BlockTest {

    ICad cad

    BigDecimal length = 80.0
    BigDecimal height = 60.0
    BigDecimal thickness = 10.0
    BigDecimal centerHoleDia = 4.0
    BigDecimal cboreHoleDiameter = 2.4
    BigDecimal cboreInset = 12.0
    BigDecimal cboreDiameter = 4.4
    BigDecimal cboreDepth = 2.1

    @Test
    void "Basic Box on XY"() {
        cad.box(length, height, thickness).display()
    }

    @Test
    void "Block With Bored Center Hole"() {
        cad.box(length, height, thickness).topZ().toPlane().profile {
            circle(centerHoleDia)
        }.hole().display()
    }


    @Test
    void "Pillow Block With Counterbored Holes"() {
        cad.box(length, height, thickness).topZ().profile {
            final borders = rect(length - cboreInset, height - cboreInset).circle(cboreHoleDiameter)
            final center = circle(centerHoleDia)
            union borders, center
        }.hole().profile {
            rect(length - cboreInset, height - cboreInset).circle(cboreDiameter)
        }.pocket(cboreDepth).display()
    }

}
