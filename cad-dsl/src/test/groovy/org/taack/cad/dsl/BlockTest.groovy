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
    void "Pillow Block With Counterbored Holes"() {
        cd().box(length, height, thickness).topZ().wireFrom() {
            double rectSX = length - cboreInset
            double rectSY = height - cboreInset
            move(new Vec2d(-(rectSX) / 2, -(rectSY) / 2))
            circle(centerHoleDia)
            move(new Vec2d(0, rectSY))
            circle(centerHoleDia)
            move(new Vec2d(rectSX, 0))
            circle(centerHoleDia)
            move(new Vec2d(0, -(rectSY)))
            circle(centerHoleDia)
        }.toFace().hole(new Vec(-cboreHoleDiameter)).topZ().wireFrom() {
            circle(centerHoleDia)
        }.toFace().hole(new Vec(-cboreHoleDiameter)).display()
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
            Vec2d p1 = new Vec2d(-length, -length)
            Vec2d p2 = new Vec2d(-length, length)
            Vec2d p3 = -p1
            Vec2d p4 = -p2
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
            Vec2d p1 = new Vec2d(-length, -length)
            Vec2d p2 = new Vec2d(-length, length)
            Vec2d p3 = -p1
            Vec2d p4 = -p2
            to p1
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
//            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            to(new Vec2d(cboreInset, length - cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
//            circle(centerHoleDia)
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
                    closedWire {
                        to(new Vec2d(x + centerHoleDia, y + centerHoleDia))
                        println "Cannot fix position ..."
                        [[-1, 1], [-1, -1], [1, -1], [1, 1]].each { double i2, double j2 ->
                            double x2 = x + centerHoleDia * i2
                            double y2 = y + centerHoleDia * j2
                            edge(new Vec2d(x2, y2))
                        }
                    }
                }
            }
        }.toFace().prism(cboreHoleDiameter).display()
//            rect(length / 2, length / 2) {
//                rect(centerHoleDia, centerHoleDia)
//            }
    }

//            println "Face 5 butX 40,-40"
////            rect(length / 2, length / 2) {
////                rect(centerHoleDia, centerHoleDia)
////            }
//            circle(centerHoleDia)

//        }.toFace().prism(new Vec(-cboreHoleDiameter)).topX().wireFrom() {
//            println "Face 2 topX 40,-40"
//            move(new Vec2d(-length / 2 + cboreInset, -length / 2 + cboreInset))
//            circle(centerHoleDia)
//            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
//            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
//            circle(centerHoleDia)
//        }.toFace().hole(-cboreHoleDiameter).display()
//        }


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
        }.toFace().prism(new Vec(-cboreHoleDiameter)).display()
    }

}