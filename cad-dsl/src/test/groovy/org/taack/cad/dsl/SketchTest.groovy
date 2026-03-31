package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static org.taack.cad.builder.CadBuilder.cb
import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.CadDslVisitor.*
import static org.taack.cad.dsl.CadDslVisitor.*

@CompileStatic
class SketchTest {

    @Test
    void "Revolut Edges with arcs from 2D Vectors"() {
        double innerRadius = 0.8
        double xValue1 = 0.05
        double xValue2 = xValue1 * 2.0
        double yValue1 = 0.025
        double yValue2 = yValue1 * 2.0

        Vec2d pOri = new Vec2d(innerRadius, 0)
        Vec2d p2 = new Vec2d(-xValue2, -yValue1)
        Vec2d p3 = new Vec2d(-xValue2, yValue1)
        Vec2d p4 = new Vec2d(+xValue2, yValue1)
        Vec2d p5 = new Vec2d(+xValue2, -yValue1)
        Vec2d p6 = new Vec2d(+xValue1, -yValue2)
        Vec2d p7 = new Vec2d()

        cd().wireFrom(pOri) {
            edge(p2)
            edge(p3)
            edge(p4)
            arc(p5, new Vec2d(+0.15, -0.029))
            edge(p6)
            edge(p7)
        }.toFace().revolution(new Vec(2, 0, 0), new Vec(0, 1, 0)).display()
    }

    @Test
    void "Extrude Square Face with Hole using 2D Vectors"() {
        double length = 0.1

        Vec2d pOri = new Vec2d()
        Vec2d p1 = new Vec2d(0, length)
        Vec2d p2 = new Vec2d(length, length)
        Vec2d p3 = new Vec2d(length, 0)
        Vec2d p4 = new Vec2d()

        Vec2d cOri = p2 * .4
        Vec2d c1 = p1 * .2
        Vec2d c2 = p2 * .2
        Vec2d c3 = p3 * .2
        Vec2d c4 = p4 * .2

        cd().wireFrom(pOri) {
            closedWire {
                edge(p3)
                edge(p2)
                edge(p1)
                edge(p4)
            }
            closedWire {
                to cOri
                arc(c2, c1)
                arc(c4, c3)
            }
        }.toFace().prism(-1).display()
    }

    @Test
    void "Extrude Square Face with Hole using 2D Vectors with hole"() {
        double length = 0.1

        Vec2d pOri = new Vec2d()
        Vec2d p1 = new Vec2d(0, length)
        Vec2d p2 = new Vec2d(length, length)
        Vec2d p3 = new Vec2d(length, 0)
        Vec2d p4 = new Vec2d()

        Vec2d cOri = p2 * .4
        Vec2d c1 = p1 * .2
        Vec2d c2 = p2 * .2
        Vec2d c3 = p3 * .2
        Vec2d c4 = p4 * .2

        cd().wireFrom(pOri) {
            closedWire {
                edge(p3)
                edge(p2)
                edge(p1)
                edge(p4)
            }
            closedWire {
                to(cOri)
                arc(c2, c1)
                arc(c4, c3)
            }
        }.toFace().prism().topX().wireFrom() {
            CadDslVisitor.Tr.cur "Circle topY 0.1,-0.5"
            circle(0.02)
        }.toFace().prism(0.02).display()
    }

    @Test
    void "Revolut Edges 3D Vectors"() {
        BigDecimal face_inner_radius = 0.8

        cd().wireFrom(new Vec(face_inner_radius - 0.05, 0.0, -0.05)) {
            edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
            edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
            edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
            edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
            edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
            edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))

        }.toFace().revolution(new Vec(1.0)).display()
    }

    @Test
    void "Revolut Edges 3D Vectors using Mirror"() {
        BigDecimal face_inner_radius = 0.8

        cd().wireFrom(new Vec(face_inner_radius, 0.0, -0.05)) {
            edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
            edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
            edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
            edge(new Vec(face_inner_radius, 0.0, 0.025))
        }.mirror(new Vec(face_inner_radius, 0, 0), new Vec(1)).toFace().revolution(new Vec(1.0)).display()
    }


    @Test
    void "Extrude Square Face using 2D Vectors with mirror"() {
        double length = 0.1

        Vec2d p1 = new Vec2d(-length, -length)
        Vec2d p2 = new Vec2d(-length, length)
        Vec2d p3 = -p1
        Vec2d p4 = -p2

        cd().wireFrom(p1) {
            edge(p4)
            edge(p3)
        }.mirror(p1, p3 - p1).toFace().prism().display()
    }

    @Test
    void "Extrude Square Face using 3D Vectors with mirror"() {
        double length = 0.1

        Vec p1 = new Vec(-length, -length, 0)
        Vec p2 = new Vec(-length, length, 0)
        Vec p3 = -p1
        Vec p4 = -p2

        cd().wireFrom(p1) {
            edge(p4)
            edge(p3)
        }.mirror(p1, p3 - p1).toFace().prism().display()
    }

}
