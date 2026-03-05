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
        BigDecimal innerRadius = 0.8
        BigDecimal xValue1 = 0.05
        BigDecimal xValue2 = xValue1 * 2.0
        BigDecimal zValue1 = 0.025
        BigDecimal zValue2 = zValue1 * 2.0


        Vec p1 = new Vec(innerRadius - xValue1, 0.0, -zValue2)
        Vec p2 = new Vec(innerRadius - xValue2, 0.0, -zValue1)
        Vec p3 = new Vec(innerRadius - xValue2, 0.0, zValue1)
        Vec p4 = new Vec(innerRadius + xValue2, 0.0, zValue1)
        Vec p5 = new Vec(innerRadius + xValue2, 0.0, -zValue1)
        Vec p6 = new Vec(innerRadius + xValue1, 0.0, -zValue2)
        Vec p7 = p1

        cb().from(p1)
                .edge(p2)
                .edge(p3)
                .edge(p4)
                .arc(p5, new Vec(innerRadius + 0.15, 0.0, -0.029))
                .edge(p6)
                .edge(p7)
                .toWire().toFace().from(new Vec(0.0)).revolution().display()
    }

    @Test
    void "Extrude Edges with arcs"() {
        BigDecimal xValue1 = 0.05
        BigDecimal xValue2 = xValue1 * 2.0
        BigDecimal zValue1 = 0.025
        BigDecimal zValue2 = zValue1 * 2.0


        Vec p1 = new Vec(- xValue1, 0.0, -zValue2)
        Vec p2 = new Vec(- xValue2, 0.0, -zValue1)
        Vec p3 = new Vec(- xValue2, 0.0, zValue1)
        Vec p4 = new Vec(xValue2, 0.0, zValue1)
        Vec p5 = new Vec(xValue2, 0.0, -zValue1)
        Vec p6 = new Vec(xValue1, 0.0, -zValue2)
        Vec p7 = p1

        cb().from(p1)
                .edge(p2)
                .edge(p3)
                .edge(p4)
                .arc(p5, new Vec( xValue2 + 0.05, 0.0, -zValue1 - 0.004))
                .edge(p6)
                .edge(p7)
                .toWire().toFace().from(new Vec(0.0, 1.0, 0.0)).prism().display()
    }

}
