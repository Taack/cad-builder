package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class BlockTest {

    BigDecimal length = 80.0
    BigDecimal height = 60.0
    BigDecimal thickness = 10.0
    BigDecimal centerHoleDia = 4.0
    BigDecimal cboreHoleDiameter = 2.4
    BigDecimal cboreInset = 12.0

    @Test
    void "Basic Box on XY"() {
        cd().box(length, height, thickness).display()
    }

    @Test
    void "Block With Bored Center Hole"() {
        cd().box(length, height, thickness).topZ().center {
            circle(centerHoleDia)
        }.hole(thickness).display()
    }

    @Test
    void "Pillow Block With Counterbored Holes"() {
        cd().box(length, height, thickness).topZ().center {
            rect(length - cboreInset, height - cboreInset) {
                circle(centerHoleDia)
            }
        }.hole(cboreHoleDiameter).topZ().center {
            circle(centerHoleDia)
        }.hole(cboreHoleDiameter).display()
    }
}
