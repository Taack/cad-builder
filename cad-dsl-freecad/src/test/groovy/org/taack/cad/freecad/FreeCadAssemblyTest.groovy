package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.geom.Vec

import static java.lang.Math.PI
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.taack.cad.dsl.CadDsl.assembly

@CompileStatic
class FreeCadAssemblyTest {
    @TempDir
    File dir

    @Test
    void "Parts copied in an assembly keep the placement of their sketches"() {
        File step = new File(dir, 'ring.step')
        assembly('a') {
            // a 2 x 2 square in the plane y = 0, turned around Z: a ring of radii 1 and 3, 2 high
            place(part('ring') {
                wireFrom(new Vec(1, 0, 0)) {
                    edge(new Vec(3, 0, 0))
                    edge(new Vec(3, 0, 2))
                    edge(new Vec(1, 0, 2))
                    edge(new Vec(1, 0, 0))
                }.toFace().revolution(new Vec(), new Vec(0, 0, 1), 360)
            })
        }.display(step.path)
        assertEquals(PI * (9 - 1) * 2, FreeCadSession.get().call(null, 'volume', step.path) as double, 1e-6)
    }
}
