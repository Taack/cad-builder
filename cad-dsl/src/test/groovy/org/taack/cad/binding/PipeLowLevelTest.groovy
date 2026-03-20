package org.taack.cad.binding

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.ShapeEnum
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
class PipeLowLevelTest {
    @Test
    void "Build Pipe"() {
        MemorySegment S = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(new_BRepPrimAPI_MakeBox__x_y_z(400, 250, 300))
        def Ex = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(S, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal())
        _TopExp_Explorer__Next(Ex)
        _TopExp_Explorer__Next(Ex)
        def F1 = new_TopoDS_Face__TopExp_Explorer__Current(Ex)
        def surf = handle_Geom_Surface__TopoDS_Face(F1)
        def MW1 = new_BRepBuilderAPI_MakeWire()
        def p1 = new Vec2d(100, 100).toGpPnt2d()
        def p2 = new Vec2d(200, 100).toGpPnt2d()
        def aline = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p1, p2)

        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge MW1, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(aline, surf, 0d, gp_Pnt2d__Distance__p1_p2(p1, p2))
        p1 = p2
        p2 = new Vec2d(150, 200).toGpPnt2d()
        aline = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p1, p2)

        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge MW1, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(aline, surf, 0d, gp_Pnt2d__Distance__p1_p2(p1, p2))
        p1 = p2
        p2 = new Vec2d(100, 100).toGpPnt2d()
        aline = handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(p1, p2)

        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge MW1, new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(aline, surf, 0d, gp_Pnt2d__Distance__p1_p2(p1, p2))
        def MKF1 = new_BRepBuilderAPI_MakeFace()
        _BRepBuilderAPI_MakeFace__Init(MKF1, surf, 0, 0.001d)
        _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(MKF1, MW1)
        def FP = TopoDS_Face__BRepBuilderAPI_MakeFace__Face MKF1
        _BRepLib__BuildCurves3d__TopoDS_Shape FP
        def CurvePoles = new_TColgp_Array1OfPnt__Low_Up(1,3)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,0,150).toGpPnt(), 1)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(200,100,150).toGpPnt(), 2)
        _TColgp_Array1OfPnt__Ar_Pt_Indx(CurvePoles, new Vec(150,200,150).toGpPnt(), 3)
        def curve = handle_Geom_BezierCurve__TColgp_Array1OfPnt(CurvePoles)
        def E = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve curve
        def W = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1 E

        def MKPipe = new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(S,FP,F1,W,0,1)
        _BRepFeat_MakePipe__Perform(MKPipe)
        def res1 = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape MKPipe

        visualize(res1)
    }
}
