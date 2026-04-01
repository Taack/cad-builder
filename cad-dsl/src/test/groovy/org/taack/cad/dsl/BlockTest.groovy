package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class BlockTest {

    double length = 80.0
    double height = 60.0
    double thickness = 10.0
    double centerHoleDia = 4.0
    double cboreHoleDiameter = 2.4
    double cboreInset = 12.0

    @Test
    void "Basic Box on XY"() {
        cd().box(length, height, thickness).display()
    }

    @Test
    void "Block With Bored Center Hole"() {
        cd().box(length, height, thickness).topZ().wireFrom() {
            circle(centerHoleDia)
        }.toFace().hole(-cboreHoleDiameter).display()
    }

    @Test
    void "Block With Bored Center Hole and Prism"() {
        cd().box(length, height, thickness).topZ().wireFrom() {
            circle(centerHoleDia)
        }.toFace().hole(new Vec(-cboreHoleDiameter)).topZ().wireFrom() {
            to new Vec2d(thickness, thickness)
            circle(centerHoleDia / 2)
        }.toFace().prism(new Vec(cboreHoleDiameter)).display()
    }

    @Test
    void "Pillow Block With Counterbored Holes and Prism"() {
        cd().box(length, height, thickness).topZ().wireFrom() {
            double rectSX = length - cboreInset
            double rectSY = height - cboreInset
            [[-rectSX / 2, -rectSY / 2], [0, rectSY], [rectSX, 0], [0, -rectSY]].each { double cX, double cY ->
                move(cX, cY)
                circle(centerHoleDia)
            }
        }.toFace().hole(-cboreHoleDiameter).topZ().wireFrom() {
            circle(centerHoleDia)
        }.toFace().prism(cboreHoleDiameter).display()
    }

    @Test
    void "Pillow Block With Counterbored Holes TopY"() {
        cd().box(length, length, length).topY().wireFrom() {
            circle(centerHoleDia)
        }.toFace().hole(-10).display()
    }

    @Test
    void "Squared hole"() {
        cd().box(length, length, length).butZ().wireFrom() {
            println "Wire 2D rect"
            double length = length / 10
            Vec2d p1 = new Vec2d()
            Vec2d p2 = new Vec2d(0, length)
            Vec2d p3 = new Vec2d(length, length)
            Vec2d p4 = new Vec2d(length, 0)
            to p1
            edge(p4)
            edge(p3)
            edge(p2)
            edge(p1)
        }.toFace().hole(new Vec(10)).butZ().wireFrom() {
            circle(length / 30)
        }.toFace().hole(new Vec(10)).display()//.hole(cboreHoleDiameter).display()
    }

    @Test
    void "Squared hole from numbers"() {
        cd().box(length, length, length).butZ().wireFrom() {
            println "Wire 2D rect"
            double length = length / 10
            Vec2d p1 = new Vec2d()
            Vec2d p2 = new Vec2d(0, length)
            Vec2d p3 = new Vec2d(length, length)
            Vec2d p4 = new Vec2d(length, 0)
            to p3
            edge(p4)
            edge(p3)
            edge(p2)
            edge(p1)
        }.toFace().hole(-10).butZ().wireFrom() {
            circle(length / 30)
        }.toFace().hole(-10).display()//.hole(cboreHoleDiameter).display()
    }

    @Test
    void "A Die"() {
        cd().box(length, length, length).topZ().wireFrom() { // 6
            to(new Vec2d(cboreInset, cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            to(new Vec2d(cboreInset, length - cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
        }.toFace().prism(cboreHoleDiameter).butY().wireFrom() { // 4
            to(new Vec2d(cboreInset, cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            to(new Vec2d(cboreInset, length - cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
        }.toFace().hole(-cboreHoleDiameter).topY().wireFrom() { // 3
            circle(centerHoleDia)
            move(new Vec2d(-length / 2 + cboreInset, -length / 2 + cboreInset))
            circle(centerHoleDia)
            to(new Vec2d(length / 2, length / 2))
            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
            circle(centerHoleDia)
        }.toFace().hole(-cboreHoleDiameter).butX().wireFrom() { // 5
            circle(centerHoleDia)
            [5d, length - 5d].each { double x ->
                [-5d, -length + 4.0d].each { double y ->
                    to(new Vec2d(x, y))
                    closedWire {
                        CadDslVisitor.Tr.cur "Could fix position ..."
                        [[1, 0], [1, 1], [0, 1], [0, 0]].each { double i2, double j2 ->
                            double x2 = centerHoleDia * i2
                            double y2 = centerHoleDia * j2
                            edge(new Vec2d(x2, y2))
                        }
                    }
                }
            }
        }.toFace().prism(cboreHoleDiameter).butZ().wireFrom() { // 1
            circle(3)
        }.toFace().prism(5).topX().wireFrom() {
            [[5d, -5d], [length - 5d, -length + 4.0d]].each { double x, double y ->
                to(new Vec2d(x, y))
                closedWire {
                    [[1, 0], [1, 1], [0, 1], [0, 0]].each { double i2, double j2 ->
                        double x2 = centerHoleDia * i2
                        double y2 = centerHoleDia * j2
                        edge(new Vec2d(x2, y2))
                    }
                }
            }
        }.toFace().hole(-3).display()
    }

    @Test
    void "Tetrahedron"() {
        cd().direction(new Vec(1, 1, 1)).box(100, 100, 100).common {
            position(new Vec(100, 100, 100) * (5 / 10)) {
                direction(new Vec(1))
                box(500, 500, 500)
            }
        }.display()
    }

    @Test
    void "Tetrahedron and Hole"() {
        cd().direction(new Vec(1, 1, 1)).box(100, 100, 100).common {
            position(new Vec(100, 100, 100) * (5 / 10)) {
                direction(new Vec(1))
                box(500, 500, 500)
            }
        }.butZ().wireFrom() {
            circle(3)
        }.toFace().hole(-cboreHoleDiameter).display()
    }

}