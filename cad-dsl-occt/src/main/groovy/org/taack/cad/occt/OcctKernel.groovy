package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernel

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.occt.Natives.own
import static org.taack.cad.occt.Occt.*

/**
 * Open CASCADE kernel, through the cad-dsl-native C API.
 */
@CompileStatic
class OcctKernel implements CadKernel {

    @Override
    ICadDslVisitor newVisitor() {
        new CadDslVisitor()
    }

    @Override
    ICadDslAssemblyVisitor newAssemblyVisitor(String name) {
        new OcctAssemblyVisitor(name)
    }

    @Override
    Vec2d startPoint(ITrimmable2d curve) {
        vec2d(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(OcctGeom.trimmedCurve2d(curve)), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    Vec2d endPoint(ITrimmable2d curve) {
        vec2d(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(OcctGeom.trimmedCurve2d(curve)), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    List<Vec2d> intersections(IClosedShape2d c1, IClosedShape2d c2) {
        def inter = own(new_Geom2dAPI_InterCurveCurve__curve1_curve2(OcctGeom.curve2d(c1), OcctGeom.curve2d(c2)), NativeLib::delete_Geom2dAPI_InterCurveCurve)
        int num_points = int_Geom2dAPI_InterCurveCurve__NbPoints(inter)
        CadDslVisitor.Tr.cur("CurveIntersection2d $num_points")

        List<Vec2d> results = []
        for (int i = 1; i <= num_points; i++) {
            results << vec2d(own(new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(inter, i), NativeLib::delete_gp_Pnt2d))
        }
        results
    }

    @Override
    List<Vec2d> circleCenters(Vec2d p1, Vec2d p2, double radius) {
        MemorySegment solutions = own(new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(gpPnt2d(p1), gpPnt2d(p2), radius, 0.01d), NativeLib::delete_GccAna_Circ2d2TanRad)
        List<Vec2d> centers = []
        for (int i = 1; i <= i_GccAna_Circ2d2TanRad__NbSolutions(solutions); i++) {
            MemorySegment circle = own(ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(solutions, i), NativeLib::delete_gp_Circ2d)
            centers << vec2d(ref_gp_Pnt2d__gp_Ax22d__Location(ref_Position__gp_Circ2d__Position(circle)))
        }
        centers
    }
}
