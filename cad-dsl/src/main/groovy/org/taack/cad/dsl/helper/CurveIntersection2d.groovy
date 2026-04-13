package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.Vec2d

import static org.taack.occt.NativeLib.*

@CompileStatic
class CurveIntersection2d {
    List<Vec2d> results

    CurveIntersection2d(IClosedShape2d s1, IClosedShape2d s2) {
        def inter = new_Geom2dAPI_InterCurveCurve__curve1_curve2(s1.make2dCurve(), s2.make2dCurve())
        int num_points = int_Geom2dAPI_InterCurveCurve__NbPoints(inter)
        CadDslVisitor.Tr.cur("CurveIntersection2d $num_points")

        results = []

        for (int i = 1; i <= num_points ; i++) {
            results << Vec2d.fromAPnt(new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(inter, i))
        }
    }


    @Override
    String toString() {
        return "CurveIntersection2d{" +
                "results=" + results +
                '}';
    }
}
