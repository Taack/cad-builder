package org.taack.cad.builder

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.CadBuilder
import org.taack.cad.dsl.Vec

import static org.taack.cad.dsl.CadBuilder.cb

import groovy.transform.CompileStatic

@CompileStatic
class AllInOneTest {

    static CadBuilder GenerateSphere() {
        BigDecimal radius = 1.0
        BigDecimal angle = Math.atan(0.5d)
        BigDecimal featureDiameter = 0.8

        cb().sphere(radius, new Vec(1.0), -angle, angle)
    }

    @Test
    void "Build Basic Shape"() {
        GenerateSphere().display()
    }
}
