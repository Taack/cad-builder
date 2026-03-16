package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec

import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class SketchTest {

    @Test
    void "Revolut Edges 3D Vectors"() {
        BigDecimal face_inner_radius = 0.8

//        cd().from(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
//                .edge(new Vec(face_inner_radius - 0.10, 0.0, -0.025))
//                .edge(new Vec(face_inner_radius - 0.10, 0.0, 0.025))
//                .edge(new Vec(face_inner_radius + 0.10, 0.0, 0.025))
//                .edge(new Vec(face_inner_radius + 0.10, 0.0, -0.025))
//                .edge(new Vec(face_inner_radius + 0.05, 0.0, -0.05))
//                .edge(new Vec(face_inner_radius - 0.05, 0.0, -0.05))
//                .toWire().toFace().from(new Vec(0.0)).revolution(new Vec(1.0)).display()
    }

}
