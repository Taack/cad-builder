package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

@CompileStatic
class Face extends Edge implements Selector {

    Face() {
    }


    Face topZ(@DelegatesTo(value = Face, strategy = Closure.DELEGATE_FIRST) c = null) {
        face(Axe.Z, Qty.max, c)
    }

    Face face(Axe axe, Qty qty, @DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) operations = null) {
        double positionMax = -1

        for (def aFaceExplorer = nl.top_exp_explorer(currentShape, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             nl.top_exp_explorer_more(aFaceExplorer);
             nl.top_exp_explorer_next(aFaceExplorer)) {
            def aFace = nl.top_exp_explorer_current_face(aFaceExplorer)
            def aSurface = nl.brep_tool_surface(aFace)
            if (nl.geom_surface_is_geom_plane(aSurface) == 1) {
                def aPlan = nl.downcast_geom_plane(aSurface)
                def aPnt = nl.geom_plane_location(aPlan)
                currentLoc = Loc.fromAPnt(aPnt)
                double aZ = currentLoc.cord(axe)
                println "COUCOU $aZ $positionMax"
                if (aZ > positionMax) {
                    positionMax = aZ
                    currentFace = aFace
                    println "KIKI $currentFace"
                }
            }
        }
        return this as Face
    }
}
