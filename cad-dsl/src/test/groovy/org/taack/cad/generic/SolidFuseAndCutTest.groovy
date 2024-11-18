package org.taack.cad.generic

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.builder.Vec
import org.taack.cad.dsl.builder.ICad


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

        ICad[] cyls //TODO init

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            cyls << cad.cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0d))
        }
        other.cut(cyls)
        return other
    }

    ICad cylindersCut2(ICad other) {

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            other.cut(cad.cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0d)))
        }
        return other
    }

    static ICad fuseTorus(ICad other) {
        BigDecimal ringRadius = 1.0
        BigDecimal torusRadius = 4.0 - ringRadius
        def t = cad.torus(torusRadius, ringRadius)
        other.fuse(t)
        return other
    }

    static ICad revolvedCut(ICad other) {
        // Create a hexagonal face. Use this face to create a solid by revolving
        // it around an axis. Subtract the generated shape (shown on the right) from
        // the input shape.
        BigDecimal face_inner_radius = 0.8

        other.topZ().center {
            cut(
                    cad.from(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                            .edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
                            .edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
                            .edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
                            .edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
                            .edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
                            .edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                            .toWire().toFace().from(new Vec(0.0)).revolution()
            )
        }

        return other
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
