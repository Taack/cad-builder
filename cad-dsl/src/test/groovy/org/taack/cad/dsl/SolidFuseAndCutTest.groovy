package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec

import static org.taack.cad.dsl.CadDsl.cd
import static java.lang.Math.*

@CompileStatic
class SolidFuseAndCutTest {
    static final Double radianSphere = 0.5
    static final BigDecimal radius = 4.0

    static CadDsl generateSphere() {
        BigDecimal angle = atan(radianSphere)
        BigDecimal featureDiameter = 0.5

        def c = cd().sphere(radius, -angle, angle).topZ().wireFrom() {
            circle featureDiameter
        }.toFace().prism(new Vec(-100))
        c.toCadDsl()
    }

    final static BigDecimal cylinderRadius = 1.2
    final static BigDecimal height = 10.0
    static final BigDecimal cloneRadius = 4.0

    static CadDsl cylindersCut(CadDsl other) {

        other.cut {
            for (i in 0..7) {
                double angle = i * PI / 4.0
                position(new Vec(-height / 2.0) + new Vec(cos(angle) * cloneRadius, sin(angle) * cloneRadius, 0.0)) {
                    cylinder(cylinderRadius, height)
                }
            }
        }
        return other
    }

    static CadDsl fuseTorus(CadDsl other) {
        BigDecimal ringRadius = 1.0
        BigDecimal torusRadius = 4.0 - ringRadius
        other.fuse {
            position(new Vec()) {
                torus(torusRadius, ringRadius)
            }
        }
        return other
    }

    static CadDsl revolvedCut(CadDsl other) {
        // Create a hexagonal face. Use this face to create a solid by revolving
        // it around an axis. Subtract the generated shape (shown on the right) from
        // the input shape.
        BigDecimal face_inner_radius = 0.8

        other.cut {
            topZ().wireFrom() {
                wireFrom(new Vec(face_inner_radius - 0.05, 0.0, -0.05)) {
                    edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
                    edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
                    edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
                    edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
                    edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
                    edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
                }.toFace().revolution()
            }
        }

        return other.toCadDsl()
    }

    @Test
    void "Build Basic Shape Cut"() {
        cylindersCut(generateSphere()).display()
    }

    @Test
    void "Build Basic Shape Fuse"() {
        fuseTorus(cylindersCut(generateSphere())).display()
    }

    @Test
    void "Build Basic Shape STEP"() {
        fuseTorus(cylindersCut(generateSphere())).display("test.step")
    }

    @Test
    void "Build Basic Shape Revolution"() {
        revolvedCut(fuseTorus(cylindersCut(generateSphere()))).display()
    }
}
