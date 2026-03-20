package org.taack.cad.binding

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.SurfaceBounds
import org.taack.cad.builder.Vec
import static org.taack.occt.NativeLib.*
import static java.lang.Math.*

@CompileStatic
class PlateSurfaceLowLevelTest {
    @Test
    void "Build Plate Surface"() {
        int NbCurFront = 4,
            NbPointConstraint = 1
        Vec P1c = new Vec(0, 0, 0)
        Vec P2c = new Vec(0, 10, 0)
        Vec P3c = new Vec(0, 10, 10)
        Vec P4c = new Vec(0, 0, 10)
        Vec P5c = new Vec(5, 5, 5)

        def W = new_BRepBuilderAPI_MakePolygon()
        _BRepBuilderAPI_MakePolygon__Add__pt(W, P1c.toGpPnt())
        _BRepBuilderAPI_MakePolygon__Add__pt(W, P2c.toGpPnt())
        _BRepBuilderAPI_MakePolygon__Add__pt(W, P3c.toGpPnt())
        _BRepBuilderAPI_MakePolygon__Add__pt(W, P4c.toGpPnt())
        _BRepBuilderAPI_MakePolygon__Add__pt(W, P1c.toGpPnt())

        println "Initialize a BuildPlateSurface"
        def BPSurf = new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(3, 15, 2)

        println "Create the curve constraints"
        def anExp = new_BRepTools_WireExplorer()

        _BRepTools_WireExplorer__Init__BRepBuilderAPI_MakePolygon(anExp, W)
        int c = b_BRepTools_WireExplorer__More(anExp) // /PPPFfff
        while (b_BRepTools_WireExplorer__More(anExp) == c) {
            def E = _BRepTools_WireExplorer__Current(anExp)
            def C = handle_BRepAdaptor_Curve()
            _BRepAdaptor_Curve__Initialize(C, E)
            def Cont = handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(C, 0)
            _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(BPSurf, Cont)
            _BRepTools_WireExplorer__Next(anExp)
        }
        println "Point constraint"
        def PCont = handle_GeomPlate_PointConstraint__gp_Pnt_order(P5c.toGpPnt(), 0)
        _GeomPlate_BuildPlateSurface__Add__Cont(BPSurf, PCont)

        println "Compute the Plate surface"
        _GeomPlate_BuildPlateSurface__Perform BPSurf

        println "Approximation of the Plate surface"
        int MaxSeg = 9
        int MaxDegree = 8
        int CritOrder = 0
        double dmax, Tol

        def PSurf = handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface BPSurf
        dmax = max(0.0001d, 10d * r_GeomPlate_BuildPlateSurface__G0Error(BPSurf))
        Tol = 0.0001
        def Mapp = new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(PSurf, Tol, MaxSeg, MaxDegree, dmax, CritOrder)
        def Surf = handle_Geom_Surface__GeomPlate_MakeApprox__Surface(Mapp)

        println "create a face corresponding to the approximated Plate Surface"
        def s = new SurfaceBounds(r4_GeomPlate_Surface__Bounds(PSurf))

        def MF = new_BRepBuilderAPI_MakeFace__s_um_uM_vm_vM_Tol(Surf, s.u1, s.u2, s.v1, s.v2, 0.01d)

        visualize(new_TopoDS_Face__BRepBuilderAPI_MakeFace MF)

    }
}
