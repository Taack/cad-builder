package org.taack.cad.builder

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.CadBuilder
import org.taack.cad.dsl.Vec

import static org.taack.cad.dsl.CadBuilder.cb

import groovy.transform.CompileStatic

@CompileStatic
class AllInOneTest {

    static CadBuilder generateSphere() {
        BigDecimal radius = 5.0
        BigDecimal angle = Math.atan(0.2d)
        BigDecimal featureDiameter = 0.3

        cb().sphere(radius, new Vec(1.0), -angle, angle).topZ().center {
            hole featureDiameter
        }
    }

    static CadBuilder cylindersCut(CadBuilder other) {
        BigDecimal radius = 0.25
        BigDecimal height = 2.0

        List<CadBuilder> cyls = []

        for (i in 0..<7) {
            double angle = i * Math.PI / 4.0
            double cloneRadius = 1.0
            cyls << cb().cylinder(radius, height).move(new Vec(-height/2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0))
        }
        other.cut(cyls)
        return other
    }

    static CadBuilder cylindersCut2(CadBuilder other) {
        BigDecimal radius = 0.25
        BigDecimal height = 2.0

        for (i in 0..<7) {
            double angle = i * Math.PI / 4.0
            double cloneRadius = 1.0
            other.cut(cb().cylinder(radius, height).move(new Vec(-height/2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0)))
        }
        return other
    }

    @Test
    void "Build Basic Shape"() {
        cylindersCut2(generateSphere()).display()
    }


    @Test
    void "Build Basic Shape List"() {
        cylindersCut(generateSphere()).display()
    }

}
