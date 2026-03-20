package org.taack.cad.binding

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.ShapeEnum
import org.taack.cad.builder.Vec2d

import java.beans.Visibility

import static org.taack.occt.NativeLib.*
import static java.lang.Math.*

@CompileStatic
class TaperedPrismLowLevelTest {
    @Test
    void "Build Tapered Prism"() {
        def S = new_BRepPrimAPI_MakeBox__x_y_z(400d, 250d, 300d)
        S = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(S)
        def Ex = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid S, TopAbs_ShapeEnumFromOrdinal(ShapeEnum.TopAbs_FACE.ordinal()), TopAbs_ShapeEnumFromOrdinal(ShapeEnum.TopAbs_SHAPE.ordinal())
        _TopExp_Explorer__Next Ex
        _TopExp_Explorer__Next Ex
        _TopExp_Explorer__Next Ex
        _TopExp_Explorer__Next Ex
        _TopExp_Explorer__Next Ex
        def F = new_TopoDS_Face__TopExp_Explorer__Current Ex
        def surf = handle_Geom_Surface__TopoDS_Face F
        def c = new_gp_Circ2d__ax2d_r(new_gp_Ax2d__pt_dir(new Vec2d(200, 130).toGpPnt2d(), new Vec2d(200, 130).toGpDir2d()), 50)
        def MW = new_BRepBuilderAPI_MakeWire()
        def aline = handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(c)
        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(aline, surf, 0d, PI))
        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(MW, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(aline, surf, PI, 2d * PI))
        def MKF = new_BRepBuilderAPI_MakeFace()
        _BRepBuilderAPI_MakeFace__Init(MKF, surf, 0, 0.01d)
        _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(MKF, MW)
        def FP = TopoDS_Face__BRepBuilderAPI_MakeFace__Face(MKF)
        _BRepLib__BuildCurves3d__TopoDS_Shape FP
        def MKDP = new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(S, FP, F, 10 * PI / 180, 1, 1)
        _BRepFeat_MakeDPrism__Perform__Height(MKDP, 200)
        def res1 = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape MKDP

        visualize(res1)
    }
}
