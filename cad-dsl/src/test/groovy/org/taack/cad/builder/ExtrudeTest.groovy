package org.taack.cad.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.Vec

import static org.taack.cad.dsl.CadBuilder.cb

@CompileStatic
class ExtrudeTest {

    BigDecimal circleRadius = 50.0
    BigDecimal thickness = 13.0
    BigDecimal rectWidth = 13.0
    BigDecimal rectLength = 19.0

    @Test
    void "Extruded Cylindrical Plate"() {
        cb().sketch {
            circle circleRadius
            rect rectWidth, rectLength
            extrude new Vec(3.0)
        }
        .display()
    }

}
