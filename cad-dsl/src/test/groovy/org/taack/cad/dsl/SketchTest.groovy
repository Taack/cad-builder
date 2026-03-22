package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class SketchTest {

    @Test
    void "Revolut Edges 3D Vectors"() {
        BigDecimal face_inner_radius = 0.8

        cd().from(new Vec(face_inner_radius - 0.05, 0.0, -0.05)) {
            edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
            edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
            edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
            edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
            edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
            edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
        }.toFace().revolution(new Vec(1.0)).display()
    }

    @Test
    void "Revolut Edges with arcs from 2D Vectors"() {
        BigDecimal innerRadius = 0.8
        BigDecimal xValue1 = 0.05
        BigDecimal xValue2 = xValue1 * 2.0
        BigDecimal yValue1 = 0.025
        BigDecimal yValue2 = yValue1 * 2.0


        Vec2d p1 = new Vec2d(innerRadius - xValue1, -yValue2)
        Vec2d p2 = new Vec2d(innerRadius - xValue2, -yValue1)
        Vec2d p3 = new Vec2d(innerRadius - xValue2, yValue1)
        Vec2d p4 = new Vec2d(innerRadius + xValue2, yValue1)
        Vec2d p5 = new Vec2d(innerRadius + xValue2, -yValue1)
        Vec2d p6 = new Vec2d(innerRadius + xValue1, -yValue2)
        Vec2d p7 = p1

        cd().from(p1) {
            edge(p2)
            edge(p3)
            edge(p4)
            arc(p5, new Vec2d(innerRadius + 0.15, -0.029))
            edge(p6)
            edge(p7)
        }.toFace().revolution().display()
    }

    @Test
    void "Extrude Square Face with Hole using 2D Vectors"() {
        BigDecimal length = 0.1

        Vec2d p1 = new Vec2d(-length, -length)
        Vec2d p2 = new Vec2d(-length, length)
        Vec2d p3 = -p1
        Vec2d p4 = -p2

        Vec2d c1 = p1 * .2
        Vec2d c2 = p2 * .2
        Vec2d c3 = p3 * .2
        Vec2d c4 = p4 * .2

        cd().from(p1) {
            edge(p4)
            edge(p3)
            edge(p2)
            edge(p1)
        }.move(c1) {
            arc(c3, c2)
            arc(c1, c4)
        }.toFace().prism().display()
    }

    @Test
    void "Extrude Square Face with Hole using 2D Vectors with mirror"() {
        BigDecimal length = 0.1

        Vec2d p1 = new Vec2d(-length, -length)
        Vec2d p2 = new Vec2d(-length, length)
        Vec2d p3 = -p1
        Vec2d p4 = -p2

        cd().from(p1) {
            edge(p4)
            edge(p3)
        }.mirror(p1, p3 - p1).toFace().prism().display()

    }

}
