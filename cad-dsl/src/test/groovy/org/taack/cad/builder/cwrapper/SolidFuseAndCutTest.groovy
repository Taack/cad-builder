package org.taack.cad.builder.cwrapper

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.dump.direct.CadBuilder
import org.taack.cad.dsl.dump.direct.Vec

import static org.taack.cad.dsl.dump.direct.CadBuilder.cb

import groovy.transform.CompileStatic

@CompileStatic
class SolidFuseAndCutTest {
    static final Double radianSphere = 0.5
    static final BigDecimal radius = 4.0

    static CadBuilder generateSphere() {
        BigDecimal angle = Math.atan(radianSphere)
        BigDecimal featureDiameter = 0.5

        def c = cb().sphere(radius, new Vec(1.0), -angle, angle).topZ().center {
            hole featureDiameter
        }

        c.display()
        c
    }

    final static BigDecimal cylinderRadius = 1.2
    final static BigDecimal height = 10.0
    static final BigDecimal cloneRadius = 4.0

    static CadBuilder cylindersCut(CadBuilder other) {

        List<CadBuilder> cyls = []

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            cyls << cb().cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0))
        }
        other.cut(cyls)
        return other
    }

    static CadBuilder cylindersCut2(CadBuilder other) {

        for (i in 0..7) {
            double angle = i * Math.PI / 4.0
            other.cut(cb().cylinder(cylinderRadius, height).move(new Vec(-height / 2.0)).move(new Vec(Math.cos(angle) * cloneRadius, Math.sin(angle) * cloneRadius, 0.0)))
        }
        return other
    }

    static CadBuilder fuseTorus(CadBuilder other) {
        BigDecimal ringRadius = 1.0
        BigDecimal torusRadius = 4.0 - ringRadius
        def t = cb().torus(torusRadius, ringRadius)
        other.fuse(t)
        return other
    }

    static CadBuilder revolvedCut(CadBuilder other) {
        // Create a hexagonal face. Use this face to create a solid by revolving
        // it around an axis. Subtract the generated shape (shown on the right) from
        // the input shape.
        BigDecimal face_inner_radius = 0.8

        other.topZ().center {
            cut(
                    cb().from(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
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
