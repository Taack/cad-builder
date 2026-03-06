package org.taack.cad.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.builder.Vec

import static org.taack.cad.dsl.dump.direct.CadBuilder.cb

@CompileStatic
class SketchTest {

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


        Vec p1 = new Vec(-xValue1, 0.0, -zValue2)
        Vec p2 = new Vec(-xValue2, 0.0, -zValue1)
        Vec p3 = new Vec(-xValue2, 0.0, zValue1)
        Vec p4 = new Vec(xValue2, 0.0, zValue1)
        Vec p5 = new Vec(xValue2, 0.0, -zValue1)
        Vec p6 = new Vec(xValue1, 0.0, -zValue2)
        Vec p7 = p1

        cb().from(p1)
                .edge(p2)
                .edge(p3)
                .edge(p4)
                .arc(p5, new Vec(xValue2 + 0.05, 0.0, -zValue1 - 0.004))
                .edge(p6)
                .edge(p7)
                .toWire().toFace().from(new Vec(0.0, 1.0, 0.0)).prism().display()
    }

    @Test
    void "Extrude Square Face with Hole"() {
        BigDecimal length = 0.1

        Vec p1 = new Vec(-length, 0.0, -length)
        Vec p2 = new Vec(-length, 0.0, length)
        Vec p3 = new Vec(length, 0.0, length)
        Vec p4 = new Vec(length, 0.0, -length)

        Vec c1 = p1 * .2
        Vec c2 = p2 * .2
        Vec c3 = p3 * .2
        Vec c4 = p4 * .2

        cb().from(p1)
                .edge(p2)
                .edge(p3)
                .edge(p4)
                .edge(p1).toWire()
                .from(c1)
                .arc(c3, c4)
                .arc(c1, c2)
                .toWire().toFace().from(new Vec(0.0, 1.0, 0.0)).prism().display()
    }

}
