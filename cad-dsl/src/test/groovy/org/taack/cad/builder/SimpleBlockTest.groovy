package org.taack.cad.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import static org.taack.cad.dsl.CadBuilder.cb

@CompileStatic
class SimpleBlockTest {

    BigDecimal length = 80.0
    BigDecimal width = 100.0
    BigDecimal height = 60.0
    BigDecimal thickness = 10.0
    BigDecimal centerHoleDia = 22.0
    BigDecimal cboreHoleDiameter = 2.4
    BigDecimal cboreInset = 12.0
    BigDecimal cboreDiameter = 4.4
    BigDecimal cboreDepth = 2.1

    @Test
    void "Basic Box on XY"() {
        cb().box(length, height, thickness).display()
    }

    @Test
    void "Block With Bored Center Hole"() {
        cb().box(length, height, thickness).topZ().center {
            hole(centerHoleDia)
        }.display()
    }

    @Test
    void "Pillow Block With Counterbored Holes"() {
        cb().box(length, height, thickness).topZ().rect(length - cboreInset, width - cboreInset) {
            counterboredHole(cboreHoleDiameter, cboreDiameter, cboreDepth)
        }.display()
    }
}
