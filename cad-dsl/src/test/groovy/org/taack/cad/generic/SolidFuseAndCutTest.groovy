package org.taack.cad.generic

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.ISolid
import org.taack.cad.dsl.builder.Vec

@CompileStatic
class SolidFuseAndCutTest {

    ICad cad //TODO init

    static final Double radianSphere = 0.5
    static final BigDecimal radius = 4.0

    ICad generateSphere() {
        BigDecimal angle = Math.atan(radianSphere)
        BigDecimal featureDiameter = 0.5

        def c = cad.sphere(radius, new Vec(1.0d), -angle, angle).topZ().profile {
            circle featureDiameter
        }.hole()

        c.display()
        c
    }

    final static BigDecimal cylinderRadius = 1.2
    final static BigDecimal height = 10.0
    static final BigDecimal cloneRadius = 4.0

    ICad cylindersCut(ICad other) {

        ICad[] cylinders = []

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            cylinders << cad.cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0d))
        }
        other.cut(cylinders)
        return other
    }

    ICad cylindersCut2(ICad other) {

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            other.cut(cad.cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0d)))
        }
        return other
    }

    ICad fuseTorus(ICad other) {
        BigDecimal ringRadius = 1.0
        BigDecimal torusRadius = 4.0 - ringRadius
        def t = cad.torus(torusRadius, ringRadius)
        other.fuse(t)
        return other
    }

    ICad revolvedCut(ICad other) {
        // Create a hexagonal face. Use this face to create a solid by revolving
        // it around an axis. Subtract the generated shape (shown on the right) from
        // the input shape.
        BigDecimal faceInnerRadius = 0.8

        ISolid ring = cad.profile {
            origin faceInnerRadius - 0.05, -0.05
            lineTo faceInnerRadius - 0.10, -0.025
            lineTo faceInnerRadius - 0.10, 0.025
            lineTo faceInnerRadius + 0.10, 0.025
            lineTo faceInnerRadius + 0.10, -0.025
            lineTo faceInnerRadius + 0.05, -0.05
            lineTo faceInnerRadius - 0.05, -0.05
        }.revolution(new Vec(0.1))

        other.topZ().cut(ring)

        return other.topZ().cut(ring)
    }

    @Test
    void "Build Basic Shape Cut"() {
        cylindersCut2(generateSphere()).display()
    }


    @Test
    void "Build Basic Shape Cut2"() {
        cylindersCut(generateSphere()).display()
    }

    @Test
    void "Build Basic Shape Fuse"() {
        fuseTorus(cylindersCut2(generateSphere())).display()
    }

    @Test
    void "Build Basic Shape STEP"() {
        fuseTorus(cylindersCut2(generateSphere())).display("test.step")
    }

    @Test
    void "Build Basic Shape Revolution"() {
        revolvedCut(fuseTorus(cylindersCut2(generateSphere()))).display()
    }
}
