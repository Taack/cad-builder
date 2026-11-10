package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static java.lang.Math.PI
import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class FreeCadScriptTest {
    @TempDir
    File dir

    @BeforeAll
    static void loadCheck() {
        FreeCadSession.get().call(null, 'loadModule', 'script_check', '''
import FreeCAD as App, Part

def edited(path, name, value):
    """[volume, x max, degrees of freedom of the sketches, symmetric constraints] of the saved document, its parameter
    name set to value"""
    doc = App.openDocument(path)
    try:
        sheet = doc.getObject('Params')
        sheet.set(sheet.getCellFromAlias(name), repr(value))
        doc.recompute()
        failed = [o.Name for o in doc.Objects if 'Invalid' in o.State or 'Error' in o.State]
        if failed:
            raise ValueError('failed: %s' % failed)
        dof = 0
        symmetric = 0
        for o in doc.Objects:
            if o.TypeId == 'Sketcher::SketchObject':
                if o.solve() != 0:
                    raise ValueError('%s: redundant or conflicting constraints' % o.Name)
                dof += o.DoF
                symmetric += len([c for c in o.Constraints if c.Type == 'Symmetric'])
        root = doc.getObject('Assembly')
        if root is not None:
            shape = Part.getShape(root)
        else:
            shape = [o for o in doc.Objects if o.Visibility and o.TypeId != 'Spreadsheet::Sheet'][0].Shape
        return [shape.Volume, shape.BoundBox.XMax, dof, symmetric]
    finally:
        App.closeDocument(doc.Name)
''')
    }

    private List<Double> edited(File file, String name, double value) {
        ((List) FreeCadSession.get().call(null, 'script_check.edited', file.path, name, value)).collect { it as double }
    }

    private File script(String text) {
        File script = new File(dir, 'model.groovy')
        script.text = text
        script
    }

    @Test
    void "Sketches are fully constrained, symmetric, and their numbers follow the parameters they are computed from"() {
        File script = script('''
            double width = param('width', 60)
            double radius = param('radius', 5)
            double hole = param('hole', 3)
            cd().face(Frame.xy(0), Outline.roundedRectangle(width, 40, radius),
                    new Outline().circle(width / 2 - 10, 0, hole), new Outline().circle(10 - width / 2, 0, hole))
                .prism(8).display(args[0])
        ''')
        File fcstd = new File(dir, 'plate.FCStd')
        FreeCadScript.run(script, [fcstd.path])
        Closure<Double> volume = { double w, double r, double h -> (w * 40 - (4 - PI) * r * r - 2 * PI * h * h) * 8 }
        // the corners, the tangent points and the holes, symmetric about the axes and the origin
        assertClose([volume(60, 5, 3), 30d, 0d, 8d], edited(fcstd, 'width', 60))
        assertClose([volume(80, 5, 3), 40d, 0d, 8d], edited(fcstd, 'width', 80))
        assertClose([volume(60, 8, 3), 30d, 0d, 8d], edited(fcstd, 'radius', 8))
        assertClose([volume(60, 5, 5), 30d, 0d, 8d], edited(fcstd, 'hole', 5))
    }

    @Test
    void "The positions of the parts of an assembly follow the parameters"() {
        File script = script('''
            double gap = param('gap', 4)
            assembly('pair') {
                def block = part('block') { box(10, 10, 10) }
                place(block)
                place(block) { translate(10 + gap, 0, 0) }
            }.display(args[0])
        ''')
        File fcstd = new File(dir, 'pair.FCStd')
        FreeCadScript.run(script, [fcstd.path])
        assertClose([2000d, 24d, 0d, 0d], edited(fcstd, 'gap', 4))
        assertClose([2000d, 27d, 0d, 0d], edited(fcstd, 'gap', 7))
    }

    private static void assertClose(List<Double> expected, List<Double> actual) {
        assertEquals(expected.size(), actual.size())
        expected.eachWithIndex { double e, int i -> assertEquals(e, actual[i], 1e-6d, "$i of $actual") }
    }
}
