package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernel

import static java.lang.Math.sqrt

/**
 * FreeCAD kernel: the models are built as FreeCAD documents by a FreeCADCmd process (see {@link FreeCadSession}),
 * and can be saved as editable FCStd files.
 */
@CompileStatic
class FreeCadKernel implements CadKernel {

    @Override
    ICadDslVisitor newVisitor() {
        new FreeCadVisitor()
    }

    @Override
    ICadDslAssemblyVisitor newAssemblyVisitor(String name) {
        new FreeCadAssemblyVisitor(name)
    }

    @Override
    Vec2d startPoint(ITrimmable2d curve) {
        vec2d(FreeCadSession.get().call(null, 'startPoint', curve))
    }

    @Override
    Vec2d endPoint(ITrimmable2d curve) {
        vec2d(FreeCadSession.get().call(null, 'endPoint', curve))
    }

    @Override
    List<Vec2d> intersections(IClosedShape2d c1, IClosedShape2d c2) {
        ((List) FreeCadSession.get().call(null, 'intersections', c1, c2)).collect { vec2d(it) }
    }

    /**
     * @return The centers in the order of GccAna_Circ2d2TanRad: on the left of p1 -> p2 first
     */
    @Override
    List<Vec2d> circleCenters(Vec2d p1, Vec2d p2, double radius) {
        double dx = p2.x - p1.x, dy = p2.y - p1.y
        double d = sqrt(dx * dx + dy * dy)
        if (d == 0 || d > 2 * radius) return []
        double h = sqrt(radius * radius - d * d / 4)
        Vec2d middle = new Vec2d((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
        Vec2d left = new Vec2d(-dy / d * h, dx / d * h)
        [middle + left, middle - left]
    }

    private static Vec2d vec2d(Object xy) {
        List l = (List) xy
        new Vec2d(l[0] as double, l[1] as double)
    }

    /**
     * @return Whether display() shows the model in FreeCAD: -Dcad.freecad.gui=true, or CAD_DSL_FREECAD_GUI=true
     */
    static boolean guiEnabled() {
        (System.getProperty('cad.freecad.gui') ?: System.getenv('CAD_DSL_FREECAD_GUI')) == 'true'
    }

    /**
     * Open the model (or assembly) of handle in the FreeCAD GUI: freecad.gui system property, FREECAD_GUI environment
     * variable, else FreeCAD on the PATH
     */
    static void show(FreeCadSession session, int handle) {
        File file = File.createTempFile('cad-dsl-', '.FCStd')
        session.call(handle, 'save', file.path)
        String gui = System.getProperty('freecad.gui') ?: System.getenv('FREECAD_GUI') ?: 'FreeCAD'
        new ProcessBuilder(gui, file.path).inheritIO().start()
    }
}
