package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class SketchTest {

    @Test
    void "Revolut Edges with arcs from 2D Vectors"() {
        double innerRadius = 0.8
        double xValue1 = 0.05
        double xValue2 = xValue1 * 2.0
        double yValue1 = 0.025
        double yValue2 = yValue1 * 2.0


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
        }.toFace().revolution(new Vec( 2,0,0), new Vec(0,1,0)).display()
    }

    @Test
    void "Extrude Square Face with Hole using 2D Vectors"() {
        double length = 0.1

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
        double length = 0.1

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
