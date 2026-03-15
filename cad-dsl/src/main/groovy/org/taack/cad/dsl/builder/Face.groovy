package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import static org.taack.occt.NativeLib.*

@CompileStatic
class Face extends Edge implements Selector {

    Face() {
    }


    CadBuilder topZ(@DelegatesTo(value = Face, strategy = Closure.DELEGATE_FIRST) c = null) {
        face(Axe.Z, c) as CadBuilder
    }

    Face face(Axe axe, @DelegatesTo(value = Face, strategy = Closure.DELEGATE_ONLY) operations = null) {
        double positionMax = -1

        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(currentShapeNative, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal());
             _TopExp_Explorer__More(aFaceExplorer);
             _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
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

    CadBuilder revolution(Vec dir = new Vec(0, 1, 0)) {
        def ax1 = new_gp_Ax1__p_dir(currentLoc.toGpPnt(), dir.toGpDir())

        this.currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(currentFaceNative, ax1)
        this as CadBuilder
    }

    CadBuilder prism(Vec dir = new Vec(1)) {
        this.currentShapeNative = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(currentFaceNative, dir.toGpVec())
        this as CadBuilder
    }
}
