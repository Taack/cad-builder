package org.taack.cad.builder.cwrapper

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.Vec
import org.taack.cad.dsl.Vec2d

import static org.taack.cad.dsl.generator.direct.CadBuilder.cb

@CompileStatic
class SketchTest {

    @Test
    void "Extruded Cylindrical Plate"() {
        BigDecimal circleRadius = 50.0
        BigDecimal thickness = 13.0
        BigDecimal rectWidth = 13.0
        BigDecimal rectLength = 19.0

        cb().extrude(new Vec(thickness), cb().sketch {
            circle circleRadius
            rect rectWidth, rectLength
        }).display()
    }

    @Test
    void "Extruded Lines and Arcs"() {
        BigDecimal width = 2.0  // Overall width of the plate
        BigDecimal thickness = 0.25  // Thickness of the plate

        cb().extrude(new Vec(thickness), cb().sketch {
            origin()
                    .lineTo(width, 1.0)
                    .threePointArc(new Vec2d(1.0, 1.5), new Vec2d(0.0, 1.0))
                    .sagittaArc(new Vec2d(-0.5, 1.0), 0.2)
                    .radiusArc(new Vec2d(-0.7, -0.2), -1.5)
                    .close()
        }).display()
    }

}
