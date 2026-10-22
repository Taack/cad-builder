//
// C API: 2D geometry (gp 2D, Geom2d, GCE2d, Geom2dAPI, GccAna).
//

#include "capi_internal.hxx"

#include <GCE2d_MakeArcOfCircle.hxx>
#include <GCE2d_MakeCircle.hxx>
#include <GCE2d_MakeLine.hxx>
#include <GCE2d_MakeSegment.hxx>
#include <GccAna_Circ2d2TanRad.hxx>
#include <Geom2dAPI_InterCurveCurve.hxx>
#include <Geom2d_Circle.hxx>
#include <Geom2d_Ellipse.hxx>
#include <Geom2d_Line.hxx>
#include <Geom2d_TrimmedCurve.hxx>
#include <gp_Ax22d.hxx>
#include <gp_Ax2d.hxx>
#include <gp_Circ2d.hxx>
#include <gp_Dir2d.hxx>
#include <gp_Pnt2d.hxx>
#include <gp_Vec2d.hxx>

extern "C" gp_Pnt2d *new_gp_Pnt2d__x_y(double theXp, double theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return guard(__func__, [&] { return new gp_Pnt2d(theXp, theYp); });
}

extern "C" gp_Pnt2d *new_gp_Pnt2d__Geom2d_Curve__Value__u(const occ::handle<Geom2d_Curve> *geom2d_curve, double U) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt2d((*geom2d_curve)->Value(U)); });
}

extern "C" gp_Pnt2d *new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(const occ::handle<Geom2d_TrimmedCurve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt2d((*curve)->EndPoint()); });
}

extern "C" gp_Pnt2d *new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(const occ::handle<Geom2d_TrimmedCurve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt2d((*curve)->StartPoint()); });
}

extern "C" gp_Vec2d *new_gp_Vec2d__x_y(double theXp, double theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return guard(__func__, [&] { return new gp_Vec2d(theXp, theYp); });
}

extern "C" gp_Ax2d *new_gp_Ax2d(void) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax2d(); });
}

extern "C" gp_Ax2d *new_gp_Ax2d__pt_dir(const gp_Pnt2d *theP, const gp_Dir2d *theV) {
    TRACE2(std::ostringstream{} << " theP " << theP << " theV " << theV);
    return guard(__func__, [&] { return new gp_Ax2d(*theP, *theV); });
}

extern "C" gp_Dir2d *new_gp_Dir2d(void) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Dir2d(); });
}

extern "C" gp_Dir2d *new_gp_Dir2d__x_y(double theXp, double theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return guard(__func__, [&] { return new gp_Dir2d(theXp, theYp); });
}

extern "C" gp_Circ2d *new_gp_Circ2d__ax2d_r(const gp_Ax2d *ax2d, double theRadius) {
    TRACE2(std::ostringstream{} << " ax2d " << ax2d << " theRadius " << theRadius);
    return guard(__func__, [&] { return new gp_Circ2d(*ax2d, theRadius); });
}

extern "C" double gp_Pnt2d__Distance__p1_p2(const gp_Pnt2d *theOne, const gp_Pnt2d *theOther) {
    TRACE("");
    return theOne->Distance(*theOther);
}

extern "C" double gp_Pnt2d__X(const gp_Pnt2d *pnt) {
    TRACE("");
    return pnt->X();
}

extern "C" double gp_Pnt2d__Y(const gp_Pnt2d *pnt) {
    TRACE("");
    return pnt->Y();
}

extern "C" double gp_Dir2d__X(const gp_Dir2d *dir) {
    TRACE("");
    return dir->X();
}

extern "C" double gp_Dir2d__Y(const gp_Dir2d *dir) {
    TRACE("");
    return dir->Y();
}

extern "C" occ::handle<Geom2d_Ellipse> *handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(
    const gp_Ax2d *MajorAxis, double MajorRadius, double MinorRadius, int Sense) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_Ellipse>(new Geom2d_Ellipse(*MajorAxis, MajorRadius, MinorRadius, to_bool(Sense)));
    });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__curve_u1_u2(
    const occ::handle<Geom2d_Curve> *C, double U1, double U2, int Sense, int theAdjustPeriodic) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_TrimmedCurve>(
            new Geom2d_TrimmedCurve(*C, U1, U2, to_bool(Sense), to_bool(theAdjustPeriodic)));
    });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(
    const gp_Pnt2d *P1, const gp_Pnt2d *P2) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom2d_TrimmedCurve>(GCE2d_MakeSegment(*P1, *P2).Value()); });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(
    const gp_Circ2d *circ2d, const gp_Pnt2d *p1, const gp_Pnt2d *p2) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_TrimmedCurve>(GCE2d_MakeArcOfCircle(*circ2d, *p1, *p2).Value());
    });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(
    const gp_Circ2d *circ2d, double angle1, double angle2) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_TrimmedCurve>(GCE2d_MakeArcOfCircle(*circ2d, angle1, angle2).Value());
    });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(
    const gp_Pnt2d *pt1, const gp_Pnt2d *pt2, const gp_Pnt2d *pt3) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_TrimmedCurve>(GCE2d_MakeArcOfCircle(*pt1, *pt2, *pt3).Value());
    });
}

extern "C" occ::handle<Geom2d_TrimmedCurve> *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(
    const gp_Circ2d *circ2d, const gp_Pnt2d *pt1, double angle1) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom2d_TrimmedCurve>(GCE2d_MakeArcOfCircle(*circ2d, *pt1, angle1).Value());
    });
}

extern "C" void _Geom2d_TrimmedCurve__Mirror__ax2(occ::handle<Geom2d_TrimmedCurve> *curve, const gp_Ax2d *ax2d) {
    TRACE("");
    guard(__func__, [&] { (*curve)->Mirror(*ax2d); });
}

extern "C" void _Geom2d_TrimmedCurve__Reverse(occ::handle<Geom2d_TrimmedCurve> *curve) {
    TRACE("");
    guard(__func__, [&] { (*curve)->Reverse(); });
}

// Geom2d_TrimmedCurve::Reverse() is final since OCCT 8.0: calling it through a trimmed curve
// handle that holds another curve type is no longer dispatched to the right override.
extern "C" void _Geom2d_Curve__Reverse(occ::handle<Geom2d_Curve> *curve) {
    TRACE("");
    guard(__func__, [&] { (*curve)->Reverse(); });
}

extern "C" occ::handle<Geom2d_Circle> *handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(const gp_Circ2d *cir2d) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom2d_Circle>(GCE2d_MakeCircle(*cir2d).Value()); });
}

extern "C" occ::handle<Geom2d_Line> *handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(const gp_Pnt2d *p1, const gp_Pnt2d *p2) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom2d_Line>(GCE2d_MakeLine(*p1, *p2).Value()); });
}

extern "C" occ::handle<Geom2d_Geometry> *handle_Geom2d_Geometry__Copy(const occ::handle<Geom2d_Geometry> *toCpy) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom2d_Geometry>((*toCpy)->Copy()); });
}

extern "C" Geom2dAPI_InterCurveCurve *new_Geom2dAPI_InterCurveCurve__curve1_curve2(
    const occ::handle<Geom2d_Curve> *C1, const occ::handle<Geom2d_Curve> *C2) {
    TRACE("");
    return guard(__func__, [&] { return new Geom2dAPI_InterCurveCurve(*C1, *C2); });
}

extern "C" int int_Geom2dAPI_InterCurveCurve__NbPoints(const Geom2dAPI_InterCurveCurve *inter_curve_curve) {
    TRACE("");
    return guard(__func__, [&] { return inter_curve_curve->NbPoints(); });
}

extern "C" gp_Pnt2d *new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(
    const Geom2dAPI_InterCurveCurve *inter_curve_curve, int index) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt2d(inter_curve_curve->Point(index)); });
}

extern "C" GccAna_Circ2d2TanRad *new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(
    const gp_Pnt2d *Point1, const gp_Pnt2d *Point2, double Radius, double Tolerance) {
    TRACE("");
    return guard(__func__, [&] { return new GccAna_Circ2d2TanRad(*Point1, *Point2, Radius, Tolerance); });
}

extern "C" int i_GccAna_Circ2d2TanRad__NbSolutions(const GccAna_Circ2d2TanRad *circ2d2TanRad) {
    TRACE("");
    return guard(__func__, [&] { return circ2d2TanRad->NbSolutions(); });
}

extern "C" gp_Circ2d *ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(
    const GccAna_Circ2d2TanRad *circ2d2TanRad, int numStartingAt1) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Circ2d(circ2d2TanRad->ThisSolution(numStartingAt1)); });
}

extern "C" const gp_Ax22d *ref_Position__gp_Circ2d__Position(const gp_Circ2d *cir2d) {
    TRACE("");
    return &cir2d->Position();
}

extern "C" const gp_Pnt2d *ref_gp_Pnt2d__gp_Ax22d__Location(const gp_Ax22d *ax22d) {
    TRACE("");
    return &ax22d->Location();
}
