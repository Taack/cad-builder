package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadParams
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslNode
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Assembly built in a FreeCAD document: an App::Part of App::Links to the parts and sub-assemblies, copied in it.
 * The colors are kept when the GUI of FreeCAD starts off screen (see cad_dsl_server.py), not the animations.
 */
@CompileStatic
class FreeCadAssemblyVisitor implements ICadDslAssemblyVisitor {
    final FreeCadSession session = FreeCadSession.get()
    final int handle

    FreeCadAssemblyVisitor(String name) {
        handle = session.call(null, 'newAssembly', name) as int
    }

    @Override
    ICadDslNode visitPart(String name, Color color, ICadDslVisitor solid) {
        FreeCadVisitor model = (FreeCadVisitor) solid
        new FreeCadPart(session.call(handle, 'visitPart', name, color ? [color.r, color.g, color.b] : null, model.handle) as int, model)
    }

    private static int handleOf(ICadDslNode node) {
        node instanceof FreeCadAssemblyVisitor ? ((FreeCadAssemblyVisitor) node).handle : ((FreeCadPart) node).handle
    }

    @Override
    void visitPlace(ICadDslNode node, Trsf location) {
        session.call(handle, 'visitPlace', handleOf(node), location)
    }

    @Override
    void visitMove(int component, Trsf location, List<Trsf> frames) {
        session.call(handle, 'visitMove', component, location, null)
    }

    @Override
    void visitAnimation(int frameCount, double fps) {
        session.call(handle, 'visitAnimation', frameCount, fps)
    }

    @Override
    Frame visitFaceFrame(ICadDslNode part, Vec direction, Vec position) {
        frame(session.call(handle, 'visitFaceFrame', handleOf(part), direction, position))
    }

    @Override
    Frame visitAxisFrame(ICadDslNode part, Vec position) {
        frame(session.call(handle, 'visitAxisFrame', handleOf(part), position))
    }

    private static Frame frame(Object values) {
        if (values == null) return null
        List<Double> f = ((List) values).collect { it as double }
        new Frame(new Vec(f[0], f[1], f[2]), new Vec(f[3], f[4], f[5]))
    }

    /**
     * @param fileName null to check the assembly (and show it in FreeCAD with -Dcad.freecad.gui=true), else a .step
     * (with the assembly tree), .stl, .brep, .iges or .FCStd file
     */
    @Override
    void display(String fileName) {
        session.call(null, 'setParams', CadParams.values())
        session.call(handle, 'display', fileName)
        if (!fileName && FreeCadKernel.guiEnabled()) FreeCadKernel.show(session, handle)
    }
}
