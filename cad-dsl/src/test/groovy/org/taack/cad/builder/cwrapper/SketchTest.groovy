package org.taack.cad.builder.cwrapper

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.dump.direct.Vec
import org.taack.cad.dsl.dump.direct.Vec2d

import static org.taack.cad.dsl.dump.direct.CadBuilder.cb

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

    @Test
    void "Revolut Edges"() {
        BigDecimal face_inner_radius = 0.8


        cb().from(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                .edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
                .edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
                .edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
                .edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
                .edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
                .edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                .toWire().toFace().from(new Vec(0.0)).revolution().display()
    }

    @Test
    void "Revolut Edges with arcs"() {
        BigDecimal face_inner_radius = 0.8


        cb().from(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
//                .arc(new Vec(face_inner_radius - 0.10, 0.0, -0.025), new Vec(face_inner_radius - 0.0, 0.0, -0.0))
                .edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
                .edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
                .edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
                .arc(new Vec(face_inner_radius + 0.10, 0.0, -0.025), new Vec(face_inner_radius + 0.15, 0.0, -0.029))
                .edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
                .edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                .toWire().toFace().from(new Vec(0.0)).revolution().display()
    }

}
