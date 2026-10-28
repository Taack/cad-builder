package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.geom.Vec

import static java.lang.Math.PI
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    void "A split assembly is saved in a folder, each part and sub-assembly in a file linked to the others"() {
        System.setProperty('cad.freecad.split', 'true')
        try {
            assembly('robot') {
                def arm = assembly('arm') {
                    def box = part('box', Color.RED) { box(1, 2, 3) }
                    place(box)
                    place(box) { translate(10, 0, 0) }
                }
                place(arm)
                place(arm) { translate(0, 10, 0) }
                place(part('box', Color.BLUE) { cylinder(1, 1) })
            }.display(new File(dir, 'robot.FCStd').path)
        } finally {
            System.clearProperty('cad.freecad.split')
        }
        File folder = new File(dir, 'robot')
        assertEquals(['arm.FCStd', 'box.FCStd', 'box_2.FCStd', 'robot.FCStd'], folder.list().toList().sort())
        // the links found by their relative paths, from another folder
        File moved = new File(dir, 'moved')
        assertTrue(folder.renameTo(moved))
        FreeCadSession.get().call(null, 'loadModule', 'split_check', '''
import FreeCAD as App, Part
def volume(path):
    doc = App.openDocument(path)
    try:
        return Part.getShape(doc.getObject('Assembly')).Volume
    finally:
        for d in list(App.listDocuments().values()):
            if d.FileName.startswith(path.rsplit('/', 1)[0]):
                App.closeDocument(d.Name)
''')
        assertEquals(4 * 6 + PI, FreeCadSession.get().call(null, 'split_check.volume', new File(moved, 'robot.FCStd').path) as double, 1e-6)
    }
}
