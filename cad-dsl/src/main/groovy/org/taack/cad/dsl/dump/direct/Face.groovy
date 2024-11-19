package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

@CompileStatic
class Face extends Edge implements Selector {

    Face() {
    }


    CadBuilder topZ(@DelegatesTo(value = Face, strategy = Closure.DELEGATE_FIRST) c = null) {
        face(Axe.Z, Qty.max, c) as CadBuilder
    }

    Face face(Axe axe, Qty qty, @DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) operations = null) {
        double positionMax = -1

        for (def aFaceExplorer = nl.top_exp_explorer(currentShapeNative, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             nl.top_exp_explorer_more(aFaceExplorer);
             nl.top_exp_explorer_next(aFaceExplorer)) {
            def aFace = nl.top_exp_explorer_current_face(aFaceExplorer)
            def aSurface = nl.brep_tool_surface(aFace)
            if (nl.geom_surface_is_geom_plane(aSurface) == 1) {
                def aPlan = nl.downcast_geom_plane(aSurface)
                def aPnt = nl.geom_plane_location(aPlan)
                currentLoc = Vec.fromAPnt(aPnt)
                double aZ = currentLoc.cord(axe)
                if (aZ > positionMax) {
                    positionMax = aZ
                    currentFaceNative = aFace
                }
            }
        }
        return this as Face
    }

    CadBuilder revolution(Vec dir = new Vec(1.0)) {
        def ax1 = nl.gp_ax1_new(currentLoc.toGpPnt(), dir.toGpDir())

        this.currentShapeNative = nl.brep_primapi_makerevol(currentFaceNative, ax1)
        this as CadBuilder
    }
}
