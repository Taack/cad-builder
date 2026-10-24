package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.taack.cad.dsl.CadDsl.cd
import static java.lang.Math.*

@CompileStatic
class SolidFuseAndCutTest {
    @TempDir
    File dir

    static final Double radianSphere = 0.5
    static final BigDecimal radius = 4.0

    static CadDsl generateSphere() {
        double angle = toDegrees(atan(radianSphere))
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

    @Test
    void "Prism overlapping the solid merges with it"() {
        File step = new File(dir, 'prism.step')
        CadDsl c = cd()
        c.box(10, 10, 10)
        // a bar sunk 1 mm into the side of the box
        c.wireFrom(new Vec(9, 2, 2)) {
            edge(new Vec(12, 2, 2))
            edge(new Vec(12, 2, 5))
            edge(new Vec(9, 2, 5))
            edge(new Vec(9, 2, 2))
        }.toFace().prism(new Vec(0, 3, 0))
        c.display(step.path)
        assertEquals(1, step.text.count('MANIFOLD_SOLID_BREP'))
    }

    @Test
    void "Prism of contours side by side, each with a hole"() {
        File step = new File(dir, 'rings.step')
        CadDsl c = cd()
        c.box(40, 20, 5).topZ().wireFrom() {
            to(10, 10)
            circle(6)
            circle(4, true)
            to(30, 10)
            circle(6)
            circle(4, true)
        }.toFace().prism(5)
        c.display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // outside and inside of both rings
        assertEquals(4, content.count('CYLINDRICAL_SURFACE'))
    }

    @Test
    void "Hole after a revolution"() {
        File step = new File(dir, 'disc.step')
        CadDsl c = cd()
        // a disc, then a slot across it: the hole must not reuse the profile of the revolution
        c.wireFrom(new Vec(0, 0, 0)) {
            edge(new Vec(5, 0, 0))
            edge(new Vec(5, 0, 2))
            edge(new Vec(0, 0, 2))
            edge(new Vec(0, 0, 0))
        }.toFace().revolution(new Vec(), Vec.vZ)
        c.wireFrom(new Vec(-1, -6, 0.5)) {
            edge(new Vec(1, -6, 0.5))
            edge(new Vec(1, -6, 1.5))
            edge(new Vec(-1, -6, 1.5))
            edge(new Vec(-1, -6, 0.5))
        }.toFace().hole(new Vec(0, 12, 0))
        c.display(step.path)
        String content = step.text
        assertEquals(1, content.count('MANIFOLD_SOLID_BREP'))
        // both ends of the disc and the 4 sides of the slot
        assertEquals(6, content.count('PLANE('))
    }
}
