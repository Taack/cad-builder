//
// C API: 3D geometry (gp, Geom, GC, GeomAPI, GeomPlate).
//

#include "capi_internal.hxx"

#include <BRepAdaptor_Curve.hxx>
#include <BRepFill_CurveConstraint.hxx>
#include <GC_MakeArcOfCircle.hxx>
#include <GC_MakeSegment.hxx>
#include <GeomAPI.hxx>
#include <GeomAPI_ExtremaCurveSurface.hxx>
#include <GeomPlate_BuildPlateSurface.hxx>
#include <GeomPlate_MakeApprox.hxx>
#include <GeomPlate_PointConstraint.hxx>
#include <GeomPlate_Surface.hxx>
#include <Geom2d_Curve.hxx>
#include <Geom_BSplineSurface.hxx>
#include <Geom_BezierCurve.hxx>
#include <Geom_CylindricalSurface.hxx>
#include <Geom_Ellipse.hxx>
#include <Geom_Line.hxx>
#include <Geom_Plane.hxx>
#include <Geom_SurfaceOfLinearExtrusion.hxx>
#include <Geom_SurfaceOfRevolution.hxx>
#include <Geom_TrimmedCurve.hxx>
#include <Precision.hxx>
#include <TopoDS_Edge.hxx>
#include <gp.hxx>
#include <gp_Ax1.hxx>
#include <gp_Ax2.hxx>
#include <gp_Ax3.hxx>
#include <gp_Dir.hxx>
#include <gp_Pln.hxx>
#include <gp_Pnt.hxx>
#include <gp_Trsf.hxx>
#include <gp_Vec.hxx>

/*

    gp

*/

extern "C" gp_Pnt *new_gp_Pnt__x_y_z(double theXp, double theYp, double theZp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp << " theZp " << theZp);
    return guard(__func__, [&] { return new gp_Pnt(theXp, theYp, theZp); });
}

extern "C" double gp_Pnt__X(const gp_Pnt *pnt) {
    TRACE("");
    return pnt->X();
}

extern "C" double gp_Pnt__Y(const gp_Pnt *pnt) {
    TRACE("");
    return pnt->Y();
}

extern "C" double gp_Pnt__Z(const gp_Pnt *pnt) {
    TRACE("");
    return pnt->Z();
}

extern "C" gp_Vec *new_gp_Vec__x_y_z(double theXp, double theYp, double theZp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp << " theZp " << theZp);
    return guard(__func__, [&] { return new gp_Vec(theXp, theYp, theZp); });
}

extern "C" const gp_Dir *new_gp_Dir_DZ(void) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Dir(gp::DZ()); });
}

extern "C" gp_Dir *new_gp_Dir__x_y_z(double theXv, double theYv, double theZv) {
    TRACE2(std::ostringstream{} << " theXv " << theXv << " theYv " << theYv << " theZv " << theZv);
    return guard(__func__, [&] { return new gp_Dir(theXv, theYv, theZv); });
}

extern "C" double gp_Dir__X(const gp_Dir *dir) {
    TRACE("");
    return dir->X();
}

extern "C" double gp_Dir__Y(const gp_Dir *dir) {
    TRACE("");
    return dir->Y();
}

extern "C" double gp_Dir__Z(const gp_Dir *dir) {
    TRACE("");
    return dir->Z();
}

extern "C" gp_Ax1 *new_gp_Ax1__p_dir(const gp_Pnt *theP, const gp_Dir *theV) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax1(*theP, *theV); });
}

extern "C" const gp_Ax1 *gp__OX(void) {
    TRACE("");
    return &gp::OX();
}

extern "C" const gp_Ax2 *new_gp_Ax2__gp_Pnt_gp_Dir(const gp_Pnt *loc, const gp_Dir *dir) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax2(*loc, *dir); });
}

extern "C" const gp_Ax2 *new_gp_Ax2__gp_Pnt_gp_Dir_Normal(const gp_Pnt *loc, const gp_Dir *dir, const gp_Dir *normal) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax2(*loc, *dir, *normal); });
}

extern "C" const gp_Ax2 *new_gp_Ax2_DZ(void) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax2(gp_Pnt(0.0, 0.0, 0.0), gp::DZ()); });
}

extern "C" const gp_Ax3 *new_gp_Ax3__p_dN_dX(const gp_Pnt *loc, const gp_Dir *dirN, const gp_Dir *dirX) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Ax3(*loc, *dirN, *dirX); });
}

extern "C" gp_Pln *new_gp_Pln__x_y_z_d(double x, double y, double z, double d) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pln(x, y, z, d); });
}

extern "C" gp_Pln *new_gp_Pln__pt_dir(const gp_Pnt *pt, const gp_Dir *dir) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pln(*pt, *dir); });
}

extern "C" gp_Pln *new_gp_Pln__gp_Ax3(const gp_Ax3 *ax3) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pln(*ax3); });
}

extern "C" gp_Trsf *new_gp_Trsf(void) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Trsf(); });
}

extern "C" void _gp_Trsf__SetValues(gp_Trsf *trsf, double a11, double a12, double a13, double a14, double a21, double a22, double a23, double a24, double a31, double a32, double a33, double a34) {
    TRACE("");
    guard(__func__, [&] { trsf->SetValues(a11, a12, a13, a14, a21, a22, a23, a24, a31, a32, a33, a34); });
}

extern "C" void _gp_Trsf__SetMirror__gp_Ax1(gp_Trsf *trsf, const gp_Ax1 *ax1) {
    TRACE("");
    guard(__func__, [&] { trsf->SetMirror(*ax1); });
}

extern "C" void _gp_Trsf__SetMirror__gp_Ax2(gp_Trsf *trsf, const gp_Ax2 *ax2) {
    TRACE("");
    guard(__func__, [&] { trsf->SetMirror(*ax2); });
}

extern "C" void _gp_Trsf__SetTranslation__gp_Vec(gp_Trsf *gp_trsf, const gp_Vec *translation) {
    TRACE("");
    guard(__func__, [&] { gp_trsf->SetTranslation(*translation); });
}

extern "C" void _gp_Trsf__SetRotation__gp_Vec(gp_Trsf *gp_trsf, const gp_Ax1 *ax1, double angle) {
    TRACE("");
    guard(__func__, [&] { gp_trsf->SetRotation(*ax1, angle); });
}

extern "C" void _gp_Trsf__SetScale__gp_Pnt_s(gp_Trsf *gp_trsf, const gp_Pnt *center, double scale) {
    TRACE("");
    guard(__func__, [&] { gp_trsf->SetScale(*center, scale); });
}

extern "C" void _gp_Trsf__Multiply__gp_Trsf(gp_Trsf *gp_trsf, const gp_Trsf *right) {
    TRACE("");
    guard(__func__, [&] { gp_trsf->Multiply(*right); });
}

/*

    Curves

*/

extern "C" occ::handle<Geom_TrimmedCurve> *handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(
    const occ::handle<Geom_Curve> *C, double U1, double U2) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_TrimmedCurve>(new Geom_TrimmedCurve(*C, U1, U2)); });
}

extern "C" occ::handle<Geom_TrimmedCurve> *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(
    const gp_Pnt *pnt1, const gp_Pnt *pnt2, const gp_Pnt *pnt3) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom_TrimmedCurve>(GC_MakeArcOfCircle(*pnt1, *pnt2, *pnt3).Value());
    });
}

extern "C" occ::handle<Geom_TrimmedCurve> *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_vtangente_p2(
    const gp_Pnt *pnt1, const gp_Vec *tan, const gp_Pnt *pnt3) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom_TrimmedCurve>(GC_MakeArcOfCircle(*pnt1, *tan, *pnt3).Value());
    });
}

extern "C" occ::handle<Geom_TrimmedCurve> *handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(const gp_Pnt *pnt1, const gp_Pnt *pnt2) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_TrimmedCurve>(GC_MakeSegment(*pnt1, *pnt2).Value()); });
}

extern "C" gp_Pnt *new_gp_Pnt__Geom_TrimmedCurve__StartPoint(const occ::handle<Geom_TrimmedCurve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt((*curve)->StartPoint()); });
}

extern "C" gp_Pnt *new_gp_Pnt__Geom_TrimmedCurve__EndPoint(const occ::handle<Geom_TrimmedCurve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt((*curve)->EndPoint()); });
}

extern "C" occ::handle<Geom_Line> *handle_Geom_Line__ax1(const gp_Ax1 *A1) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_Line>(new Geom_Line(*A1)); });
}

extern "C" occ::handle<Geom_Curve> *handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(
    const occ::handle<Geom2d_Curve> *curve, const gp_Pln *plan) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_Curve>(GeomAPI::To3d(*curve, *plan)); });
}

extern "C" TColgp_Array1OfPnt *new_TColgp_Array1OfPnt__Low_Up(int Low, int Up) {
    TRACE("");
    return guard(__func__, [&] { return new TColgp_Array1OfPnt(Low, Up); });
}

extern "C" void _TColgp_Array1OfPnt__Ar_Pt_Indx(TColgp_Array1OfPnt *ar, const gp_Pnt *pt, int indx) {
    TRACE("");
    guard(__func__, [&] { (*ar)(indx) = *pt; });
}

extern "C" occ::handle<Geom_BezierCurve> *handle_Geom_BezierCurve__TColgp_Array1OfPnt(const TColgp_Array1OfPnt *CurvePoles) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_BezierCurve>(new Geom_BezierCurve(*CurvePoles)); });
}

extern "C" occ::handle<Geom_Ellipse> *handle_Geom_Ellipse__gp_Ax2_rM_rm(const gp_Ax2 *A2, double MajorRadius, double MinorRadius) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom_Ellipse>(new Geom_Ellipse(*A2, MajorRadius, MinorRadius));
    });
}

/*

    Surfaces

*/

extern "C" occ::handle<Geom_Plane> *handle_Geom_Plan__gp_Pln(const gp_Pln *pln) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_Plane>(new Geom_Plane(*pln)); });
}

extern "C" int int_Geom_Surface__is__Geom_Plane(const occ::handle<Geom_Surface> *surface) {
    TRACE("");
    return guard(__func__, [&] { return (*surface)->DynamicType() == STANDARD_TYPE(Geom_Plane) ? 1 : 0; });
}

extern "C" occ::handle<Geom_Plane> *handle_Geom_Plane__handle_Geom_Surface(const occ::handle<Geom_Surface> *surface) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_Plane>(occ::down_cast<Geom_Plane>(*surface)); });
}

extern "C" gp_Pnt *new_gp_Pnt__Geom_Plane(const occ::handle<Geom_Plane> *plane) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt((*plane)->Location()); });
}

extern "C" occ::handle<Geom_CylindricalSurface> *handle_Geom_CylindricalSurface__ax2_radius(const gp_Ax3 *ax2, double radius) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom_CylindricalSurface>(new Geom_CylindricalSurface(*ax2, radius));
    });
}

extern "C" occ::handle<Geom_SurfaceOfLinearExtrusion> *handle_Geom_SurfaceOfLinearExtrusion__Geom_Curve_gp_Dir(
    const occ::handle<Geom_Curve> *C, const gp_Dir *V) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<Geom_SurfaceOfLinearExtrusion>(new Geom_SurfaceOfLinearExtrusion(*C, *V));
    });
}

extern "C" occ::handle<Geom_SurfaceOfRevolution> *handle_Geom_SurfaceOfRevolution__Geom_Curve_gp_Ax1(
    const occ::handle<Geom_Curve> *C, const gp_Ax1 *V) {
    TRACE("");
    return guard(__func__, [&] {
        occ::handle<Geom_SurfaceOfRevolution> surface = new Geom_SurfaceOfRevolution(*C, *V);
        if (surface->IsVPeriodic()) TRACE("IsVPeriodic true");
        if (surface->IsUPeriodic()) TRACE("IsUPeriodic true");
        if (surface->IsUClosed()) TRACE("IsUClosed true");
        if (surface->IsVClosed()) TRACE("IsVClosed true");
        return new occ::handle<Geom_SurfaceOfRevolution>(surface);
    });
}

extern "C" double *R4_Geom_Surface__Bounds(const occ::handle<Geom_Surface> *S) {
    TRACE("");
    return guard(__func__, [&] {
        double U1, U2, V1, V2;
        (*S)->Bounds(U1, U2, V1, V2);

        if (Precision::IsInfinite(U1)) TRACE("U1 is infinite");
        if (Precision::IsInfinite(U2)) TRACE("U2 is infinite");
        if (Precision::IsInfinite(V1)) TRACE("V1 is infinite");
        if (Precision::IsInfinite(V2)) TRACE("V2 is infinite");

        return new double[4]{U1, U2, V1, V2};
    });
}

extern "C" gp_Pnt *gp_Pnt__Geom_Surface__Value(const occ::handle<Geom_Surface> *S, double U, double V) {
    TRACE("");
    return guard(__func__, [&] { return new gp_Pnt((*S)->Value(U, V)); });
}

/*

    Extrema

*/

extern "C" GeomAPI_ExtremaCurveSurface *new_GeomAPI_ExtremaCurveSurface__curve_surface(
    const occ::handle<Geom_Curve> *Curve, const occ::handle<Geom_Surface> *Surface) {
    TRACE("");
    return guard(__func__, [&] { return new GeomAPI_ExtremaCurveSurface(*Curve, *Surface); });
}

extern "C" int i_GeomAPI_ExtremaCurveSurface__NbExtrema(const GeomAPI_ExtremaCurveSurface *extrema) {
    TRACE("");
    return guard(__func__, [&] { return extrema->NbExtrema(); });
}

extern "C" double r_GeomAPI_ExtremaCurveSurface__Distance__index(const GeomAPI_ExtremaCurveSurface *extrema, int Index) {
    TRACE2(std::ostringstream{} << "Index " << Index);
    return guard(__func__, [&] { return extrema->Distance(Index); });
}

extern "C" double *R6_GeomAPI_ExtremaCurveSurface__NbExtrema(const GeomAPI_ExtremaCurveSurface *extrema, int Index) {
    TRACE2(std::ostringstream{} << "Index " << Index);
    return guard(__func__, [&] {
        gp_Pnt p1;
        gp_Pnt p2;
        extrema->Points(Index, p1, p2);
        return new double[6]{p1.X(), p1.Y(), p1.Z(), p2.X(), p2.Y(), p2.Z()};
    });
}

/*

    Plate surfaces

*/

extern "C" GeomPlate_BuildPlateSurface *new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(int Degree, int NbPtsOnCur, int NbIter) {
    TRACE("");
    return guard(__func__, [&] { return new GeomPlate_BuildPlateSurface(Degree, NbPtsOnCur, NbIter); });
}

extern "C" occ::handle<GeomPlate_PointConstraint> *handle_GeomPlate_PointConstraint__gp_Pnt_order(const gp_Pnt *p, int order) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<GeomPlate_PointConstraint>(new GeomPlate_PointConstraint(*p, order));
    });
}

extern "C" void _GeomPlate_BuildPlateSurface__Add__Cont(
    GeomPlate_BuildPlateSurface *s, const occ::handle<GeomPlate_PointConstraint> *Cont) {
    TRACE("");
    guard(__func__, [&] { s->Add(*Cont); });
}

extern "C" void _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(
    GeomPlate_BuildPlateSurface *s, const occ::handle<BRepFill_CurveConstraint> *Cont) {
    TRACE("");
    guard(__func__, [&] { s->Add(*Cont); });
}

extern "C" void _GeomPlate_BuildPlateSurface__Perform(GeomPlate_BuildPlateSurface *s) {
    TRACE("");
    guard(__func__, [&] { s->Perform(); });
}

extern "C" occ::handle<GeomPlate_Surface> *handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface(
    const GeomPlate_BuildPlateSurface *s) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<GeomPlate_Surface>(s->Surface()); });
}

extern "C" double r_GeomPlate_BuildPlateSurface__G0Error(const GeomPlate_BuildPlateSurface *s) {
    TRACE("");
    return guard(__func__, [&] { return s->G0Error(); });
}

extern "C" double *r4_GeomPlate_Surface__Bounds(const occ::handle<GeomPlate_Surface> *s) {
    TRACE("");
    return guard(__func__, [&] {
        double U1, U2, V1, V2;
        (*s)->Bounds(U1, U2, V1, V2);
        return new double[4]{U1, U2, V1, V2};
    });
}

extern "C" occ::handle<BRepAdaptor_Curve> *handle_BRepAdaptor_Curve(void) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<BRepAdaptor_Curve>(new BRepAdaptor_Curve()); });
}

extern "C" void _BRepAdaptor_Curve__Initialize(occ::handle<BRepAdaptor_Curve> *adaptor, const TopoDS_Edge *edge) {
    TRACE("");
    guard(__func__, [&] { (*adaptor)->Initialize(*edge); });
}

extern "C" occ::handle<BRepFill_CurveConstraint> *handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(
    const occ::handle<Adaptor3d_Curve> *Boundary, int Tang) {
    TRACE("");
    return guard(__func__, [&] {
        return new occ::handle<BRepFill_CurveConstraint>(new BRepFill_CurveConstraint(*Boundary, Tang));
    });
}

extern "C" GeomPlate_MakeApprox *new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(
    const occ::handle<GeomPlate_Surface> *SurfPlate, double Tol3d, int Nbmax, int dgmax, double dmax, int CritOrder) {
    TRACE("");
    return guard(__func__, [&] { return new GeomPlate_MakeApprox(*SurfPlate, Tol3d, Nbmax, dgmax, dmax, CritOrder); });
}

extern "C" occ::handle<Geom_BSplineSurface> *handle_Geom_Surface__GeomPlate_MakeApprox__Surface(const GeomPlate_MakeApprox *mApp) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_BSplineSurface>(mApp->Surface()); });
}
