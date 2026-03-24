package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
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
        cd().box(length, height, thickness).topZ().center {
            circle(centerHoleDia)
        }.hole(thickness * 1.1).display()
    }

    @Test
    void "Pillow Block With Counterbored Holes"() {
        cd().box(length, height, thickness).topZ().center {
            rect(length - cboreInset, height - cboreInset) {
                circle(centerHoleDia)
            }
        }.hole(cboreHoleDiameter).topZ().center {
            circle(centerHoleDia)
        }.hole(cboreHoleDiameter).display()
    }

    @Test
    void "Pillow Block With Counterbored Holes TopY"() {
        cd().box(length, length, length).topY().center {
            rect((length - cboreInset), (length - cboreInset)) {
                circle(centerHoleDia)
            }
        }.hole(cboreHoleDiameter).display()
    }

    @Test
    void "Squared hole"() {
        cd().box(length, length, length).butZ().center {
            to(new Vec2d(length / 2, length / 2))
            rect(centerHoleDia, centerHoleDia)
        }.hole(cboreHoleDiameter).display()
    }

    @Test
    void "A Die"() {
        cd().box(length, length, length).topZ().center {
            println "Face 6 topZ 40,40"
            to(new Vec2d(cboreInset, cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            to(new Vec2d(cboreInset, length - cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(+length / 2 - cboreInset, 0))
            circle(centerHoleDia)
            move(new Vec2d(+length / 2 - cboreInset, 0))
            circle(centerHoleDia)
        }.hole(cboreHoleDiameter).butZ().center {
            println "Face 1 butZ 40,40"
            rect(centerHoleDia, centerHoleDia)
        }.hole(cboreHoleDiameter).topY().center {
            println "Face 4 topY 40,40"
            rect(length / 2, length / 2) {
                rect(centerHoleDia, centerHoleDia)
            }
        }.hole(cboreHoleDiameter).butY().center {
            println "Face 3 butY 40,40"
            circle(centerHoleDia)
            move(new Vec2d(-length / 2 + cboreInset, -length / 2 + cboreInset))
            circle(centerHoleDia)
            to(new Vec2d(length / 2, length / 2))
            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
            circle(centerHoleDia)
        }.hole(cboreHoleDiameter).butX().center {
            println "Face 5 butX 40,-40"
            rect(length / 2, length / 2) {
                rect(centerHoleDia, centerHoleDia)
            }
            circle(centerHoleDia)

        }.hole(cboreHoleDiameter).topX().center {
            println "Face 2 topX 40,-40"
            move(new Vec2d(-length / 2 + cboreInset, -length / 2 + cboreInset))
            circle(centerHoleDia)
            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
            move(new Vec2d(length / 2 - cboreInset, length / 2 - cboreInset))
            circle(centerHoleDia)
        }.hole(cboreHoleDiameter).display()
    }

}
