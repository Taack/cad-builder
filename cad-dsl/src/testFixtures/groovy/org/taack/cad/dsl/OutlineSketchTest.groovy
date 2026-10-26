package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Outline
import org.taack.cad.dsl.geom.Vec

import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.cad.dsl.TestImages.image

@CompileStatic
class OutlineSketchTest {

    @Test
    void "Molded housing drawn as dimensioned, its walls leaning 2°"() {
        double t = Math.tan(Math.toRadians(2))
        CadDsl housing = cd()
        // corners rounded R32, notched R7 over the feet, R8 fillets: dimensioned at mid-height, z = 15
        Outline body = new Outline()
                .circle(22, 22, 32).fillet(8).circle(48, 48, -7).fillet(8).circle(22, 22, 32)
                .quarterTurns()
        housing.face(Frame.xy(), body.inset(-15 * t)).prism(new Vec(0, 0, 30), draft: 2)
        // a pocket in the top, dimensioned at its mouth, around an island, and a knob rounded R2 beside it
        housing.face(Frame.xy(30), Outline.roundedRectangle(30, 16, 4), Outline.roundedRectangle(8, 4, 1))
                .hole(new Vec(0, 0, -10), draft: 2)
        housing.fuse {
            wireFrom(new Vec(0, 22, 30)) {
                edge(new Vec(6, 22, 30))
                edge(new Vec(6, 22, 34))
                arcAround(new Vec(4, 22, 36), new Vec(4, 22, 34))
                edge(new Vec(0, 22, 36))
                edge(new Vec(0, 22, 30))
            }.toFace().revolution(new Vec(0, 22, 0), Vec.vZ)
        }
        housing.display(image())
    }
}
