/*
 * C API over Open CASCADE Technology (OCCT 8.0), consumed by jextract (cad-dsl) and
 * implemented in src/main/cpp/capi/ (one file per section below).
 *
 * This header is the single source of truth for the exported symbols: the C++ sources
 * include it, so any drift between a declaration here and its definition is a compile error.
 *
 * Conventions:
 *  - Every OCCT object crosses the boundary as an opaque pointer. Seen from C, the types
 *    below are incomplete structs; seen from C++, they are the real OCCT classes.
 *  - OCCT_HANDLE(T) is a heap-allocated occ::handle<T> (a reference-counted pointer to T).
 *  - Function prefixes describe ownership of the returned pointer:
 *      new_ / handle_ / util_ / ptr / R<n>_ / r4_  -> caller owns it, release with the
 *                                                     matching delete_* or occt_free_doubles
 *      ref_ / gp__                                 -> borrowed, lives as long as its owner
 *  - Boolean flags are passed and returned as int (0 = false, anything else = true).
 *  - No C++ exception crosses this boundary. On failure, a function returns NULL / 0 and
 *    the message is available from occt_last_error().
 */

#ifndef OCCT_CAPI_H
#define OCCT_CAPI_H

#ifdef __cplusplus

#include <NCollection_Array1.hxx>
#include <NCollection_List.hxx>
#include <Standard_Handle.hxx>
#include <TopoDS_Shape.hxx>
#include <gp_Pnt.hxx>

#define OCCT_CAPI extern "C"
#define OCCT_HANDLE(T) occ::handle<T>
#define OCCT_DECLARE_OBJECT(T) class T;
#define OCCT_DECLARE_HANDLE(T) class T;

// Package collection typedefs were dropped from OCCT 8.0; keep their names in the C API.
typedef NCollection_List<TopoDS_Shape> TopTools_ListOfShape;
typedef NCollection_Array1<gp_Pnt> TColgp_Array1OfPnt;

#else

#define OCCT_CAPI
#define OCCT_HANDLE(T) struct Handle_##T
#define OCCT_DECLARE_OBJECT(T) typedef struct T T;
#define OCCT_DECLARE_HANDLE(T) struct Handle_##T;

typedef struct TopTools_ListOfShape TopTools_ListOfShape;
typedef struct TColgp_Array1OfPnt TColgp_Array1OfPnt;

#endif

/* Value and algorithm classes passed by pointer. Each one gets a delete_<T>() function. */
#define OCCT_CAPI_OBJECT_TYPES(X) \
    X(gp_Pnt2d) X(gp_Vec2d) X(gp_Dir2d) X(gp_Ax2d) X(gp_Ax22d) X(gp_Circ2d) \
    X(gp_Pnt) X(gp_Vec) X(gp_Dir) X(gp_Ax1) X(gp_Ax2) X(gp_Ax3) X(gp_Pln) X(gp_Trsf) \
    X(Geom2dAPI_InterCurveCurve) X(GeomAPI_ExtremaCurveSurface) X(GccAna_Circ2d2TanRad) \
    X(GeomPlate_BuildPlateSurface) X(GeomPlate_MakeApprox) \
    X(TopoDS_Edge) X(TopoDS_Wire) X(TopoDS_Face) X(TopoDS_Compound) X(TopoDS_Builder) X(BRep_Builder) \
    X(TopExp_Explorer) X(BRepTools_WireExplorer) \
    X(BRepBuilderAPI_MakeShape) X(BRepBuilderAPI_MakeEdge) X(BRepBuilderAPI_MakeWire) \
    X(BRepBuilderAPI_MakeFace) X(BRepBuilderAPI_MakePolygon) X(BRepBuilderAPI_Transform) \
    X(BRepPrimAPI_MakeBox) X(BRepPrimAPI_MakeCylinder) X(BRepFilletAPI_MakeFillet) \
    X(BRepOffsetAPI_MakeThickSolid) X(BRepOffsetAPI_ThruSections) X(BRepAlgoAPI_Cut) \
    X(BRepFeat_MakePipe) X(BRepFeat_MakeDPrism)

/* Transient classes held through OCCT_HANDLE(T). Each one gets a delete_handle_<T>() function. */
#define OCCT_CAPI_HANDLE_TYPES(X) \
    X(Geom2d_Geometry) X(Geom2d_Curve) X(Geom2d_TrimmedCurve) X(Geom2d_Ellipse) X(Geom2d_Circle) X(Geom2d_Line) \
    X(Geom_Curve) X(Geom_TrimmedCurve) X(Geom_Line) X(Geom_BezierCurve) X(Geom_Ellipse) \
    X(Geom_Surface) X(Geom_Plane) X(Geom_CylindricalSurface) X(Geom_SurfaceOfLinearExtrusion) \
    X(Geom_SurfaceOfRevolution) X(Geom_BSplineSurface) \
    X(GeomPlate_Surface) X(GeomPlate_PointConstraint) X(BRepFill_CurveConstraint) \
    X(Adaptor3d_Curve) X(BRepAdaptor_Curve) X(ShapeExtend_WireData)

OCCT_CAPI_OBJECT_TYPES(OCCT_DECLARE_OBJECT)
OCCT_CAPI_HANDLE_TYPES(OCCT_DECLARE_HANDLE)
#ifndef __cplusplus
typedef struct TopoDS_Shape TopoDS_Shape;
#endif

/***********************************************************************************************************************

    Runtime: errors and memory (capi_runtime.cpp)

***********************************************************************************************************************/

/* Message of the last failure on the calling thread, or NULL. Valid until the next failing call. */
OCCT_CAPI const char *occt_last_error(void);

OCCT_CAPI void occt_clear_last_error(void);

/* Releases the arrays returned by the R<n>_ / r4_ functions. */
OCCT_CAPI void occt_free_doubles(double *values);

#define OCCT_DECLARE_DELETE(T) OCCT_CAPI void delete_##T(T *ptr);
#define OCCT_DECLARE_DELETE_HANDLE(T) OCCT_CAPI void delete_handle_##T(OCCT_HANDLE(T) *ptr);
OCCT_CAPI_OBJECT_TYPES(OCCT_DECLARE_DELETE)
OCCT_CAPI_HANDLE_TYPES(OCCT_DECLARE_DELETE_HANDLE)
OCCT_DECLARE_DELETE(TopoDS_Shape)
OCCT_DECLARE_DELETE(TopTools_ListOfShape)
OCCT_DECLARE_DELETE(TColgp_Array1OfPnt)
#undef OCCT_DECLARE_DELETE
#undef OCCT_DECLARE_DELETE_HANDLE

/* TopAbs_ShapeEnum ordinal (COMPOUND = 0 ... SHAPE = 8), clamped to SHAPE when out of range. */
OCCT_CAPI int TopAbs_ShapeEnumFromOrdinal(int ordinal);

/***********************************************************************************************************************

    2D geometry (capi_geom2d.cpp)

***********************************************************************************************************************/

OCCT_CAPI gp_Pnt2d *new_gp_Pnt2d__x_y(double theXp, double theYp);

OCCT_CAPI gp_Pnt2d *new_gp_Pnt2d__Geom2d_Curve__Value__u(const OCCT_HANDLE(Geom2d_Curve) *geom2d_curve, double U);

OCCT_CAPI gp_Pnt2d *new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(const OCCT_HANDLE(Geom2d_TrimmedCurve) *curve);

OCCT_CAPI gp_Pnt2d *new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(const OCCT_HANDLE(Geom2d_TrimmedCurve) *curve);

OCCT_CAPI gp_Vec2d *new_gp_Vec2d__x_y(double theXp, double theYp);

OCCT_CAPI gp_Ax2d *new_gp_Ax2d(void);

OCCT_CAPI gp_Ax2d *new_gp_Ax2d__pt_dir(const gp_Pnt2d *theP, const gp_Dir2d *theV);

OCCT_CAPI gp_Dir2d *new_gp_Dir2d(void);

OCCT_CAPI gp_Dir2d *new_gp_Dir2d__x_y(double theXp, double theYp);

OCCT_CAPI gp_Circ2d *new_gp_Circ2d__ax2d_r(const gp_Ax2d *ax2d, double theRadius);

OCCT_CAPI double gp_Pnt2d__Distance__p1_p2(const gp_Pnt2d *theOne, const gp_Pnt2d *theOther);

OCCT_CAPI double gp_Pnt2d__X(const gp_Pnt2d *pnt);

OCCT_CAPI double gp_Pnt2d__Y(const gp_Pnt2d *pnt);

OCCT_CAPI double gp_Dir2d__X(const gp_Dir2d *dir);

OCCT_CAPI double gp_Dir2d__Y(const gp_Dir2d *dir);

OCCT_CAPI OCCT_HANDLE(Geom2d_Ellipse) *handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(
    const gp_Ax2d *MajorAxis, double MajorRadius, double MinorRadius, int Sense);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__curve_u1_u2(
    const OCCT_HANDLE(Geom2d_Curve) *C, double U1, double U2, int Sense, int theAdjustPeriodic);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(
    const gp_Pnt2d *P1, const gp_Pnt2d *P2);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(
    const gp_Circ2d *circ2d, const gp_Pnt2d *p1, const gp_Pnt2d *p2);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(
    const gp_Circ2d *circ2d, double angle1, double angle2);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(
    const gp_Pnt2d *pt1, const gp_Pnt2d *pt2, const gp_Pnt2d *pt3);

OCCT_CAPI OCCT_HANDLE(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(
    const gp_Circ2d *circ2d, const gp_Pnt2d *pt1, double angle1);

OCCT_CAPI void _Geom2d_TrimmedCurve__Mirror__ax2(OCCT_HANDLE(Geom2d_TrimmedCurve) *curve, const gp_Ax2d *ax2d);

/* curve must really be a trimmed curve; use _Geom2d_Curve__Reverse for any other curve. */
OCCT_CAPI void _Geom2d_TrimmedCurve__Reverse(OCCT_HANDLE(Geom2d_TrimmedCurve) *curve);

/* curve may hold any Geom2d_Curve subclass (circle, ellipse, line, trimmed curve, ...). */
OCCT_CAPI void _Geom2d_Curve__Reverse(OCCT_HANDLE(Geom2d_Curve) *curve);

OCCT_CAPI OCCT_HANDLE(Geom2d_Circle) *handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(const gp_Circ2d *cir2d);

OCCT_CAPI OCCT_HANDLE(Geom2d_Line) *handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(const gp_Pnt2d *p1, const gp_Pnt2d *p2);

OCCT_CAPI OCCT_HANDLE(Geom2d_Geometry) *handle_Geom2d_Geometry__Copy(const OCCT_HANDLE(Geom2d_Geometry) *toCpy);

OCCT_CAPI Geom2dAPI_InterCurveCurve *new_Geom2dAPI_InterCurveCurve__curve1_curve2(
    const OCCT_HANDLE(Geom2d_Curve) *C1, const OCCT_HANDLE(Geom2d_Curve) *C2);

OCCT_CAPI int int_Geom2dAPI_InterCurveCurve__NbPoints(const Geom2dAPI_InterCurveCurve *inter_curve_curve);

OCCT_CAPI gp_Pnt2d *new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(
    const Geom2dAPI_InterCurveCurve *inter_curve_curve, int index);

OCCT_CAPI GccAna_Circ2d2TanRad *new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(
    const gp_Pnt2d *Point1, const gp_Pnt2d *Point2, double Radius, double Tolerance);

OCCT_CAPI int i_GccAna_Circ2d2TanRad__NbSolutions(const GccAna_Circ2d2TanRad *circ2d2TanRad);

/* Despite the ref_ prefix, the returned circle is a copy owned by the caller. */
OCCT_CAPI gp_Circ2d *ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(
    const GccAna_Circ2d2TanRad *circ2d2TanRad, int numStartingAt1);

OCCT_CAPI const gp_Ax22d *ref_Position__gp_Circ2d__Position(const gp_Circ2d *cir2d);

OCCT_CAPI const gp_Pnt2d *ref_gp_Pnt2d__gp_Ax22d__Location(const gp_Ax22d *ax22d);

/***********************************************************************************************************************

    3D geometry (capi_geom.cpp)

***********************************************************************************************************************/

OCCT_CAPI gp_Pnt *new_gp_Pnt__x_y_z(double theXp, double theYp, double theZp);

OCCT_CAPI double gp_Pnt__X(const gp_Pnt *pnt);

OCCT_CAPI double gp_Pnt__Y(const gp_Pnt *pnt);

OCCT_CAPI double gp_Pnt__Z(const gp_Pnt *pnt);

OCCT_CAPI gp_Vec *new_gp_Vec__x_y_z(double theXp, double theYp, double theZp);

OCCT_CAPI const gp_Dir *new_gp_Dir_DZ(void);

OCCT_CAPI gp_Dir *new_gp_Dir__x_y_z(double theXv, double theYv, double theZv);

OCCT_CAPI double gp_Dir__X(const gp_Dir *dir);

OCCT_CAPI double gp_Dir__Y(const gp_Dir *dir);

OCCT_CAPI double gp_Dir__Z(const gp_Dir *dir);

OCCT_CAPI gp_Ax1 *new_gp_Ax1__p_dir(const gp_Pnt *theP, const gp_Dir *theV);

/* Borrowed: the global OX axis. */
OCCT_CAPI const gp_Ax1 *gp__OX(void);

OCCT_CAPI const gp_Ax2 *new_gp_Ax2__gp_Pnt_gp_Dir(const gp_Pnt *loc, const gp_Dir *dir);

OCCT_CAPI const gp_Ax2 *new_gp_Ax2__gp_Pnt_gp_Dir_Normal(const gp_Pnt *loc, const gp_Dir *dir, const gp_Dir *normal);

OCCT_CAPI const gp_Ax2 *new_gp_Ax2_DZ(void);

OCCT_CAPI const gp_Ax3 *new_gp_Ax3__p_dN_dX(const gp_Pnt *loc, const gp_Dir *dirN, const gp_Dir *dirX);

OCCT_CAPI gp_Pln *new_gp_Pln__x_y_z_d(double x, double y, double z, double d);

OCCT_CAPI gp_Pln *new_gp_Pln__pt_dir(const gp_Pnt *pt, const gp_Dir *dir);

OCCT_CAPI gp_Pln *new_gp_Pln__gp_Ax3(const gp_Ax3 *ax3);

OCCT_CAPI gp_Trsf *new_gp_Trsf(void);

OCCT_CAPI void _gp_Trsf__SetMirror__gp_Ax1(gp_Trsf *trsf, const gp_Ax1 *ax1);

OCCT_CAPI void _gp_Trsf__SetMirror__gp_Ax2(gp_Trsf *trsf, const gp_Ax2 *ax2);

OCCT_CAPI void _gp_Trsf__SetTranslation__gp_Vec(gp_Trsf *gp_trsf, const gp_Vec *translation);

OCCT_CAPI void _gp_Trsf__SetRotation__gp_Vec(gp_Trsf *gp_trsf, const gp_Ax1 *ax1, double angle);

OCCT_CAPI OCCT_HANDLE(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(
    const OCCT_HANDLE(Geom_Curve) *C, double U1, double U2);

OCCT_CAPI OCCT_HANDLE(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(
    const gp_Pnt *pnt1, const gp_Pnt *pnt2, const gp_Pnt *pnt3);

OCCT_CAPI OCCT_HANDLE(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_vtangente_p2(
    const gp_Pnt *pnt1, const gp_Vec *tan, const gp_Pnt *pnt3);

OCCT_CAPI OCCT_HANDLE(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(const gp_Pnt *pnt1, const gp_Pnt *pnt2);

OCCT_CAPI gp_Pnt *new_gp_Pnt__Geom_TrimmedCurve__StartPoint(const OCCT_HANDLE(Geom_TrimmedCurve) *curve);

OCCT_CAPI gp_Pnt *new_gp_Pnt__Geom_TrimmedCurve__EndPoint(const OCCT_HANDLE(Geom_TrimmedCurve) *curve);

OCCT_CAPI OCCT_HANDLE(Geom_Line) *handle_Geom_Line__ax1(const gp_Ax1 *A1);

OCCT_CAPI OCCT_HANDLE(Geom_Curve) *handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(
    const OCCT_HANDLE(Geom2d_Curve) *curve, const gp_Pln *plan);

OCCT_CAPI TColgp_Array1OfPnt *new_TColgp_Array1OfPnt__Low_Up(int Low, int Up);

OCCT_CAPI void _TColgp_Array1OfPnt__Ar_Pt_Indx(TColgp_Array1OfPnt *ar, const gp_Pnt *pt, int indx);

OCCT_CAPI OCCT_HANDLE(Geom_BezierCurve) *handle_Geom_BezierCurve__TColgp_Array1OfPnt(const TColgp_Array1OfPnt *CurvePoles);

OCCT_CAPI OCCT_HANDLE(Geom_Ellipse) *handle_Geom_Ellipse__gp_Ax2_rM_rm(const gp_Ax2 *A2, double MajorRadius, double MinorRadius);

OCCT_CAPI OCCT_HANDLE(Geom_Plane) *handle_Geom_Plan__gp_Pln(const gp_Pln *pln);

OCCT_CAPI int int_Geom_Surface__is__Geom_Plane(const OCCT_HANDLE(Geom_Surface) *surface);

OCCT_CAPI OCCT_HANDLE(Geom_Plane) *handle_Geom_Plane__handle_Geom_Surface(const OCCT_HANDLE(Geom_Surface) *surface);

OCCT_CAPI gp_Pnt *new_gp_Pnt__Geom_Plane(const OCCT_HANDLE(Geom_Plane) *plane);

OCCT_CAPI OCCT_HANDLE(Geom_CylindricalSurface) *handle_Geom_CylindricalSurface__ax2_radius(const gp_Ax3 *ax2, double radius);

OCCT_CAPI OCCT_HANDLE(Geom_SurfaceOfLinearExtrusion) *handle_Geom_SurfaceOfLinearExtrusion__Geom_Curve_gp_Dir(
    const OCCT_HANDLE(Geom_Curve) *C, const gp_Dir *V);

OCCT_CAPI OCCT_HANDLE(Geom_SurfaceOfRevolution) *handle_Geom_SurfaceOfRevolution__Geom_Curve_gp_Ax1(
    const OCCT_HANDLE(Geom_Curve) *C, const gp_Ax1 *V);

/* {U1, U2, V1, V2}, release with occt_free_doubles. */
OCCT_CAPI double *R4_Geom_Surface__Bounds(const OCCT_HANDLE(Geom_Surface) *S);

OCCT_CAPI gp_Pnt *gp_Pnt__Geom_Surface__Value(const OCCT_HANDLE(Geom_Surface) *S, double U, double V);

OCCT_CAPI GeomAPI_ExtremaCurveSurface *new_GeomAPI_ExtremaCurveSurface__curve_surface(
    const OCCT_HANDLE(Geom_Curve) *Curve, const OCCT_HANDLE(Geom_Surface) *Surface);

OCCT_CAPI int i_GeomAPI_ExtremaCurveSurface__NbExtrema(const GeomAPI_ExtremaCurveSurface *extrema);

OCCT_CAPI double r_GeomAPI_ExtremaCurveSurface__Distance__index(const GeomAPI_ExtremaCurveSurface *extrema, int Index);

/* {p1x, p1y, p1z, p2x, p2y, p2z} of extremum Index, release with occt_free_doubles. */
OCCT_CAPI double *R6_GeomAPI_ExtremaCurveSurface__NbExtrema(const GeomAPI_ExtremaCurveSurface *extrema, int Index);

OCCT_CAPI GeomPlate_BuildPlateSurface *new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(int Degree, int NbPtsOnCur, int NbIter);

OCCT_CAPI OCCT_HANDLE(GeomPlate_PointConstraint) *handle_GeomPlate_PointConstraint__gp_Pnt_order(const gp_Pnt *p, int order);

OCCT_CAPI void _GeomPlate_BuildPlateSurface__Add__Cont(
    GeomPlate_BuildPlateSurface *s, const OCCT_HANDLE(GeomPlate_PointConstraint) *Cont);

OCCT_CAPI void _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(
    GeomPlate_BuildPlateSurface *s, const OCCT_HANDLE(BRepFill_CurveConstraint) *Cont);

OCCT_CAPI void _GeomPlate_BuildPlateSurface__Perform(GeomPlate_BuildPlateSurface *s);

OCCT_CAPI OCCT_HANDLE(GeomPlate_Surface) *handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface(
    const GeomPlate_BuildPlateSurface *s);

OCCT_CAPI double r_GeomPlate_BuildPlateSurface__G0Error(const GeomPlate_BuildPlateSurface *s);

/* {U1, U2, V1, V2}, release with occt_free_doubles. */
OCCT_CAPI double *r4_GeomPlate_Surface__Bounds(const OCCT_HANDLE(GeomPlate_Surface) *s);

OCCT_CAPI OCCT_HANDLE(BRepAdaptor_Curve) *handle_BRepAdaptor_Curve(void);

OCCT_CAPI void _BRepAdaptor_Curve__Initialize(OCCT_HANDLE(BRepAdaptor_Curve) *adaptor, const TopoDS_Edge *edge);

/* Boundary may also be an OCCT_HANDLE(BRepAdaptor_Curve). */
OCCT_CAPI OCCT_HANDLE(BRepFill_CurveConstraint) *handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(
    const OCCT_HANDLE(Adaptor3d_Curve) *Boundary, int Tang);

OCCT_CAPI GeomPlate_MakeApprox *new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(
    const OCCT_HANDLE(GeomPlate_Surface) *SurfPlate, double Tol3d, int Nbmax, int dgmax, double dmax, int CritOrder);

OCCT_CAPI OCCT_HANDLE(Geom_BSplineSurface) *handle_Geom_Surface__GeomPlate_MakeApprox__Surface(const GeomPlate_MakeApprox *mApp);

/***********************************************************************************************************************

    Topology: edges, wires, faces, explorers (capi_topology.cpp)

***********************************************************************************************************************/

OCCT_CAPI const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(const OCCT_HANDLE(Geom_Curve) *segment);

OCCT_CAPI const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(
    const OCCT_HANDLE(Geom2d_Curve) *curve, const OCCT_HANDLE(Geom_Surface) *surface);

OCCT_CAPI const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(const OCCT_HANDLE(Geom2d_Curve) *curve);

OCCT_CAPI const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(const gp_Pnt *from, const gp_Pnt *to);

OCCT_CAPI const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom_Curve(const OCCT_HANDLE(Geom_Curve) *curve);

OCCT_CAPI const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(
    const OCCT_HANDLE(Geom2d_Curve) *curve2d, const OCCT_HANDLE(Geom_Surface) *S, double p1, double p2);

OCCT_CAPI const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(
    const OCCT_HANDLE(Geom2d_Curve) *curve2d, const OCCT_HANDLE(Geom_Surface) *S);

OCCT_CAPI const BRepBuilderAPI_MakePolygon *new_BRepBuilderAPI_MakePolygon(void);

OCCT_CAPI void _BRepBuilderAPI_MakePolygon__Add__pt(BRepBuilderAPI_MakePolygon *poly, const gp_Pnt *pt);

OCCT_CAPI const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire(void);

OCCT_CAPI const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeEdge *edge);

OCCT_CAPI void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeWire *wireMaker, BRepBuilderAPI_MakeEdge *edge);

OCCT_CAPI void _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(BRepBuilderAPI_MakeWire *mw, const TopoDS_Wire *wire);

OCCT_CAPI void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *mw, BRepBuilderAPI_MakeWire *mw2);

OCCT_CAPI void _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(BRepBuilderAPI_MakeWire *mw, const TopoDS_Edge *edge);

OCCT_CAPI void _BRepBuilderAPI_MakeWire__Add__TopTools_ListOfShape(BRepBuilderAPI_MakeWire *mw, const TopTools_ListOfShape *listOfShape);

OCCT_CAPI const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1(const TopoDS_Edge *e1);

OCCT_CAPI const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(const TopoDS_Edge *e1, const TopoDS_Edge *e2);

OCCT_CAPI const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(
    const TopoDS_Edge *e1, const TopoDS_Edge *e2, const TopoDS_Edge *e3);

OCCT_CAPI const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3_e4(
    const TopoDS_Edge *e1, const TopoDS_Edge *e2, const TopoDS_Edge *e3, const TopoDS_Edge *e4);

OCCT_CAPI const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire);

/* Borrowed from make_wire. */
OCCT_CAPI const TopoDS_Wire *ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire);

/* Borrowed from make_wire. */
OCCT_CAPI const TopoDS_Shape *ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(BRepBuilderAPI_MakeWire *make_wire);

/* Borrowed: shape itself, viewed as a wire. */
OCCT_CAPI TopoDS_Wire *ref_TopoDS__Wire__TopoDS_Shape(TopoDS_Shape *shape);

/* Borrowed: shape itself, viewed as an edge. */
OCCT_CAPI TopoDS_Edge *ref_TopoDS_Edge__TopoDS_Shape(TopoDS_Shape *shape);

OCCT_CAPI void _TopoDS__Shape__Reverse(TopoDS_Shape *shape);

OCCT_CAPI void _TopoDS_Shape__Free(TopoDS_Shape *shape);

OCCT_CAPI const BRepTools_WireExplorer *new_BRepTools_WireExplorer(void);

OCCT_CAPI void _BRepTools_WireExplorer__Init__TopoDS_Wire(BRepTools_WireExplorer *e, const TopoDS_Wire *W);

OCCT_CAPI void _BRepTools_WireExplorer__Init__BRepBuilderAPI_MakePolygon(BRepTools_WireExplorer *e, BRepBuilderAPI_MakePolygon *W);

OCCT_CAPI int b_BRepTools_WireExplorer__More(const BRepTools_WireExplorer *e);

OCCT_CAPI void _BRepTools_WireExplorer__Next(BRepTools_WireExplorer *e);

OCCT_CAPI const TopoDS_Edge *_BRepTools_WireExplorer__Current(const BRepTools_WireExplorer *e);

OCCT_CAPI TopExp_Explorer *new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(const TopoDS_Shape *S, int ToFind, int ToAvoid);

OCCT_CAPI int _TopExp_Explorer__More(const TopExp_Explorer *explorer);

OCCT_CAPI void _TopExp_Explorer__Next(TopExp_Explorer *explorer);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__TopExp_Explorer__Current(const TopExp_Explorer *explorer);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face__TopExp_Explorer__Current(const TopExp_Explorer *explorer);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face(void);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face__face(const TopoDS_Face *face);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(const TopoDS_Wire *wire);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(const gp_Pln *plane);

OCCT_CAPI TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace(BRepBuilderAPI_MakeFace *face);

OCCT_CAPI TopoDS_Face *TopoDS_Face__BRepBuilderAPI_MakeFace__Face(BRepBuilderAPI_MakeFace *MKF1);

OCCT_CAPI BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace(void);

OCCT_CAPI BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *wire);

OCCT_CAPI BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__s_um_uM_vm_vM_Tol(
    const OCCT_HANDLE(Geom_Surface) *S, double UMin, double UMax, double VMin, double VMax, double TolDegen);

OCCT_CAPI void _BRepBuilderAPI_MakeFace__Init(BRepBuilderAPI_MakeFace *MF, const OCCT_HANDLE(Geom_Surface) *S, int Bound, double TolDegen);

OCCT_CAPI void _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeFace *MF, BRepBuilderAPI_MakeWire *W);

OCCT_CAPI OCCT_HANDLE(Geom_Surface) *handle_Geom_Surface__TopoDS_Face(const TopoDS_Face *face);

OCCT_CAPI gp_Dir *new_gp_Dir__Normal__TopoDS_Face(const TopoDS_Face *aCurrentFace);

OCCT_CAPI TopTools_ListOfShape *new_TopTools_ListOfShape(void);

OCCT_CAPI void _TopTools_ListOfShape__Append__TopoDS_Shape(TopTools_ListOfShape *l, const TopoDS_Shape *shape);

OCCT_CAPI TopoDS_Compound *new_TopoDS_Compound(void);

OCCT_CAPI BRep_Builder *new_BRep_Builder(void);

/* builder may be a BRep_Builder. */
OCCT_CAPI void _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(const TopoDS_Builder *builder, TopoDS_Shape *inThis, const TopoDS_Shape *toAdd);

OCCT_CAPI void _TopoDS_Builder__MakeCompound__TopoDS_Compound(const TopoDS_Builder *b, TopoDS_Compound *c);

OCCT_CAPI void _BRepLib__BuildCurves3d__TopoDS_Shape(const TopoDS_Shape *w1);

OCCT_CAPI OCCT_HANDLE(ShapeExtend_WireData) *new_ShapeExtend_WireData(void);

OCCT_CAPI void _ShapeExtend_WireData__Add__TopoDS_Edge(OCCT_HANDLE(ShapeExtend_WireData) *data, const TopoDS_Edge *edge, int atnum);

OCCT_CAPI void _ShapeExtend_WireData__Add__TopoDS_Wire(OCCT_HANDLE(ShapeExtend_WireData) *data, const TopoDS_Wire *wire, int atnum);

OCCT_CAPI TopoDS_Wire *util_ShapeFix_Wire__Load__ShapeExtend_WireData(const OCCT_HANDLE(ShapeExtend_WireData) *data);

/***********************************************************************************************************************

    Modeling: primitives, sweeps, features, booleans, transforms (capi_modeling.cpp)

***********************************************************************************************************************/

OCCT_CAPI BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__x_y_z(double x, double y, double z);

OCCT_CAPI BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__Ax2_x_y_z(const gp_Ax2 *Axes, double dx, double dy, double dz);

OCCT_CAPI BRepPrimAPI_MakeCylinder *new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(const gp_Ax2 *Axes, double R, double H);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(
    const gp_Ax2 *origin, double radius, double angle1, double angle2);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(const gp_Ax2 *origin, double radius, double height);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H_angle(
    const gp_Ax2 *origin, double R1, double R2, double H, double angle);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(const gp_Ax2 *origin, double R1, double R2, double H);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(const gp_Ax2 *origin, double radius1, double radius2);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(const TopoDS_Face *face, const gp_Vec *normal);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(const TopoDS_Face *face, const gp_Ax1 *ax1);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(const TopoDS_Face *face, const gp_Ax1 *ax1, double angle);

/* shape may be any BRepBuilderAPI_MakeShape subclass (MakeBox, Transform, Cut, MakeFillet, ...). */
OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(BRepBuilderAPI_MakeShape *shape);

OCCT_CAPI BRepFilletAPI_MakeFillet *new_BRepFilletAPI_MakeFillet__TopoDS_Shape(const TopoDS_Shape *body);

OCCT_CAPI void _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(BRepFilletAPI_MakeFillet *make_fillet, double r, const TopoDS_Edge *edge);

OCCT_CAPI BRepOffsetAPI_MakeThickSolid *new_BRepOffsetAPI_MakeThickSolid(void);

OCCT_CAPI void _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(
    BRepOffsetAPI_MakeThickSolid *thick_solid, const TopoDS_Shape *shape, const TopTools_ListOfShape *face_to_remove,
    double thickness, double tol);

OCCT_CAPI BRepOffsetAPI_ThruSections *new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(int isSolid, int ruled, double pres3d);

OCCT_CAPI void _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire *w);

OCCT_CAPI void _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(BRepOffsetAPI_ThruSections *thru_sections, int b);

/* Helix wire (HelixBRep_BuilderHelix) around the Z axis of axes, starting on its X axis.
 * diam1 != diam2 makes a conical spiral; a negative pitch makes a left-handed helix. */
OCCT_CAPI TopoDS_Wire *new_TopoDS_Wire__HelixBRep_BuilderHelix__ax2_diam1_diam2_height_pitch(
    const gp_Ax2 *axes, double diam1, double diam2, double height, double pitch);

/* Solid swept (BRepOffsetAPI_MakePipeShell) by the closed profile along spine. The profile keeps a constant
 * angle with binormal (the helix axis for a helix); its 3D curves are built if missing. */
OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepOffsetAPI_MakePipeShell__spine_profile_binormal(
    const TopoDS_Wire *spine, const TopoDS_Wire *profile, const gp_Dir *binormal);

OCCT_CAPI BRepFeat_MakePipe *new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(
    const TopoDS_Shape *Sbase, const TopoDS_Shape *Pbase, const TopoDS_Face *Skface, const TopoDS_Wire *Spine,
    int Fuse, int Modify);

OCCT_CAPI void _BRepFeat_MakePipe__Perform(BRepFeat_MakePipe *p);

OCCT_CAPI BRepFeat_MakeDPrism *new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(
    const TopoDS_Shape *Sbase, const TopoDS_Face *Pbase, const TopoDS_Face *Skface, double Angle, int Fuse, int Modify);

OCCT_CAPI void _BRepFeat_MakeDPrism__Perform__Height(BRepFeat_MakeDPrism *p, double height);

OCCT_CAPI TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(
    const TopoDS_Shape *shape, const gp_Ax1 *ax1, double Radius, double PFrom, double PTo);

OCCT_CAPI TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(
    const TopoDS_Shape *shape, const gp_Ax1 *ax1, double Radius, double Length);

OCCT_CAPI BRepBuilderAPI_Transform *new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(
    const TopoDS_Shape *w, const gp_Trsf *trsf, int theCopyGeom, int theCopyMesh);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(
    const TopoDS_Shape *shape, const gp_Trsf *gp_trsf, int theCopyGeom);

OCCT_CAPI BRepAlgoAPI_Cut *new_BRepAlgoAPI_Cut__s1_s2(const TopoDS_Shape *result, const TopoDS_Shape *tool);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__BRepAlgoAPI_Cut__TopoDS_Shape_TopTools_ListOfShape(
    const TopoDS_Shape *result, const TopTools_ListOfShape *aLT);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_listrOfShape(const TopoDS_Shape *result, const TopTools_ListOfShape *aLT);

/* Obsolete: prefer the list based variants above. */
OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(const TopoDS_Shape *result, const TopoDS_Shape *tool);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2);

OCCT_CAPI TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_common__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2);

OCCT_CAPI gp_Pnt *new_gp_Pnt__CentreOfMass__TopoDS_Shape(const TopoDS_Shape *myShape);

/* {p1x, p1y, p1z, p2x, p2y, p2z, distance}; distance is -20000000 when no solution.
   Release with occt_free_doubles. */
OCCT_CAPI double *R7_BRepExtrema_DistShapeShape__s1_s2(const TopoDS_Shape *Shape1, const TopoDS_Shape *Shape2);

/***********************************************************************************************************************

    Output: rendering, export, diagnostics (capi_io.cpp, OcctAisHello.cpp)

***********************************************************************************************************************/

/* Renders the shape off-screen into an image file; returns non-zero on success. */
OCCT_CAPI int dumpShape(const TopoDS_Shape *shape, int width, int height, const char *fileName);

OCCT_CAPI void write_step(const TopoDS_Shape *shape, const char *fileName);

OCCT_CAPI void write_stl(const TopoDS_Shape *shape, const char *fileName);

OCCT_CAPI void analyze(const TopoDS_Shape *myShape);

/* Opens an interactive viewer window and blocks until it is closed. */
OCCT_CAPI int visualize(TopoDS_Shape *shape);

#endif /* OCCT_CAPI_H */
