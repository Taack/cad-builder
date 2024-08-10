package org.taack.cad.builder

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.CadBuilder
import org.taack.cad.dsl.Vec

import static org.taack.cad.dsl.CadBuilder.cb

import groovy.transform.CompileStatic

@CompileStatic
class AllInOneTest {

    static CadBuilder generateSphere() {
        BigDecimal radius = 4.0
        BigDecimal angle = Math.atan(0.5d)
        BigDecimal featureDiameter = 0.5

        cb().sphere(radius, new Vec(1.0), -angle, angle).topZ().center {
            hole featureDiameter
        }
    }

    final static BigDecimal cylinderRadius = 1.2
    final static BigDecimal height = 10.0
    static final BigDecimal cloneRadius = 4.0

    static CadBuilder cylindersCut(CadBuilder other) {

        List<CadBuilder> cyls = []

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            cyls << cb().cylinder(cylinderRadius, height).move(new Vec(-height/2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0))
        }
        other.cut(cyls)
        return other
    }

    static CadBuilder cylindersCut2(CadBuilder other) {

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            other.cut(cb().cylinder(cylinderRadius, height).move(new Vec(-height/2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0)))
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
