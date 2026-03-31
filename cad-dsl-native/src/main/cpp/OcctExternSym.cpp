//
// Created by auo on 29/07/24.
//

#include "OcctExternSym.h"

#include <AIS_InteractiveContext.hxx>
#include <AIS_Shape.hxx>
#include <Aspect_DisplayConnection.hxx>
#include <Aspect_NeutralWindow.hxx>
#include <BRepAlgoAPI_Cut.hxx>
#include <BRepAlgoAPI_Fuse.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_Transform.hxx>
#include <BRepCheck_Analyzer.hxx>
#include <BRepFeat_MakeCylindricalHole.hxx>
#include <BRepFilletAPI_MakeFillet.hxx>
#include <BRepGProp.hxx>
#include <BRepLib.hxx>
#include <BRepOffsetAPI_MakeThickSolid.hxx>
#include <BRepOffsetAPI_ThruSections.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <BRepPrimAPI_MakeCylinder.hxx>
#include <BRepPrimAPI_MakePrism.hxx>
#include <BRepPrimAPI_MakeRevol.hxx>
#include <BRepPrimAPI_MakeSphere.hxx>
#include <BRepPrimAPI_MakeTorus.hxx>
#include <BRepTools.hxx>
#include <GCE2d_MakeArcOfCircle.hxx>
#include <GCE2d_MakeCircle.hxx>
#include <GCE2d_MakeSegment.hxx>
#include <GC_MakeArcOfCircle.hxx>
#include <GC_MakeSegment.hxx>
#include <GProp_GProps.hxx>
#include <Geom2dAPI_InterCurveCurve.hxx>
#include <Geom2d_Circle.hxx>
#include <Geom2d_Ellipse.hxx>
#include <Geom2d_TrimmedCurve.hxx>
#include <GeomLProp_SLProps.hxx>
#include <Geom_CylindricalSurface.hxx>
#include <Geom_Plane.hxx>
#include <Geom_TrimmedCurve.hxx>
#include <Image_AlienPixMap.hxx>
#include <OpenGl_GraphicDriver.hxx>
#include <STEPCAFControl_Writer.hxx>
#include <Standard_Handle.hxx>
#include <StlAPI_Writer.hxx>
#include <TDocStd_Document.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Edge.hxx>
#include <V3d_View.hxx>
#include <V3d_Viewer.hxx>
#include <XCAFApp_Application.hxx>
#include <XCAFDoc_DocumentTool.hxx>
#include <XCAFDoc_ShapeTool.hxx>
#include <Xw_Window.hxx>
#include <gp_Pnt.hxx>
#include <GeomAPI.hxx>
#include <ShapeExtend_Status.hxx>
#include <ShapeExtend_WireData.hxx>
#include <ShapeFix_Wire.hxx>
#include <ShapeFix_ShapeTolerance.hxx>
#include <StdFail_NotDone.hxx>
#include <Geom_SurfaceOfLinearExtrusion.hxx>
#include <Geom_Ellipse.hxx>
#include <Geom_SurfaceOfRevolution.hxx>
#include <GccAna_Circ2d2TanRad.hxx>
#include <gp_Ax22d.hxx>
#include <BRepPrimAPI_MakeCone.hxx>
#include <BRepBuilderAPI_MakePolygon.hxx>
#include <GeomPlate_BuildPlateSurface.hxx>
#include <BRepTools_WireExplorer.hxx>
#include <BRepAdaptor_Curve.hxx>
#include <BRepFill_CurveConstraint.hxx>
#include <GeomPlate_MakeApprox.hxx>
#include <GeomPlate_Surface.hxx>
#include <Geom2d_Line.hxx>
#include <GCE2d_MakeLine.hxx>
#include <Geom_BezierCurve.hxx>
#include <BRepFeat_MakePipe.hxx>
#include <BRepFeat_MakeDPrism.hxx>
#include <BRepBuilderAPI_MakeEdge2d.hxx>
#include <BRepAlgoAPI_Common.hxx>

#include <string>       // std::string
#include <sstream>      // std::ostringstream

#define TRACE2(message) TRACE_IMPL2(__FILE__, __LINE__, __PRETTY_FUNCTION__, message)
void TRACE_IMPL2(const char *file, int line, const char *function, std::ostringstream message) {
    std::cout << /*file << " : " <<*/ line << " : " << function << " : " << message.str() << "\n";
    std::cout.flush();
}

#define TRACE(message) TRACE_IMPL(__FILE__, __LINE__, __PRETTY_FUNCTION__, message)
void TRACE_IMPL(const char *file, int line, const char *function, const char *message) {
    std::cout << /*file << " : " <<*/ line << " : " << function << " : " << message << "\n";
    std::cout.flush();
}


OcctExternSym::OcctExternSym() {
}

/*

    2D

*/

extern "C" gp_Pnt2d *new_gp_Pnt2d__x_y(const Standard_Real theXp, const Standard_Real theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return new gp_Pnt2d(theXp, theYp);
}

extern "C" gp_Pnt2d *new_gp_Pnt2d__Geom2d_Curve__Value__u(Handle(Geom2d_Curve) &geom2d_curve, const Standard_Real U) {
    TRACE("");
    return new gp_Pnt2d(geom2d_curve->Value(U));
}

extern "C" gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(const Handle(Geom2d_TrimmedCurve)& curve) {
    TRACE("");
    return new gp_Pnt2d(curve->EndPoint());
}

extern "C" gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(const Handle(Geom2d_TrimmedCurve)& curve) {
    TRACE("");
    return new gp_Pnt2d(curve->StartPoint());
}

extern "C" gp_Vec2d *new_gp_Vec2d__x_y(const Standard_Real theXp, const Standard_Real theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return new gp_Vec2d(theXp, theYp);
}

extern "C" gp_Ax2d *new_gp_Ax2d() {
    TRACE("");
    return new gp_Ax2d();
}

extern "C" gp_Ax2d *new_gp_Ax2d__pt_dir(const gp_Pnt2d &theP, const gp_Dir2d &theV) {
    TRACE2(std::ostringstream{} << " theP " << &theP << " theV " << &theV);
    return new gp_Ax2d(theP, theV);
}

extern "C" gp_Dir2d *new_gp_Dir2d() {
    TRACE("");
    return new gp_Dir2d();
}

extern "C" gp_Dir2d *new_gp_Dir2d__x_y(const Standard_Real theXp, const Standard_Real theYp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp);
    return new gp_Dir2d(theXp, theYp);
}

extern "C" gp_Circ2d* new_gp_Circ2d__ax2d_r(gp_Ax2d &ax2d, Standard_Real theRadius) {
    TRACE2(std::ostringstream{} << " ax2d " << &ax2d << " theRadius " << theRadius);
    return new gp_Circ2d(ax2d, theRadius);
}

extern "C" Standard_Real gp_Pnt2d__Distance__p1_p2(const gp_Pnt2d &theOne, const gp_Pnt2d &theOther) {
    TRACE("");
    return theOne.Distance(theOther);
}

extern "C" Standard_Real gp_Pnt2d__X(gp_Pnt2d *pnt) {
    TRACE("");
    return pnt->X();
}

extern "C" Standard_Real gp_Pnt2d__Y(gp_Pnt2d *pnt) {
    TRACE("");
    return pnt->Y();
}


extern "C" Standard_Real gp_Dir2d__X(gp_Dir2d *dir) {
    TRACE("");
    return dir->X();
}

extern "C" Standard_Real gp_Dir2d__Y(gp_Dir2d *dir) {
    TRACE("");
    return dir->Y();
}

extern "C" Handle(Geom2d_Ellipse) *handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(const gp_Ax2d &MajorAxis, const Standard_Real MajorRadius,
                                                         const Standard_Real MinorRadius,
                                                         const Standard_Boolean Sense = Standard_True) {
    TRACE("");
    return new Handle(Geom2d_Ellipse)(new Geom2d_Ellipse(MajorAxis, MajorRadius, MinorRadius, Sense));
}

extern "C" Handle(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__curve_u1_u2(const Handle(Geom2d_Curve) &C,
                                                                    const Standard_Real U1, const Standard_Real U2,
                                                                    const Standard_Boolean Sense = Standard_True,
                                                                    const Standard_Boolean theAdjustPeriodic =
                                                                        Standard_True) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(new Geom2d_TrimmedCurve(C, U1, U2, Sense, theAdjustPeriodic));
}

extern "C" Handle(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(const gp_Pnt2d &P1, const gp_Pnt2d &P2) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeSegment(P1, P2).Value());
}

extern "C" Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(gp_Circ2d& circ2d, gp_Pnt2d& p1, gp_Pnt2d& p2) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeArcOfCircle(circ2d, p1, p2).Value());
}

extern "C" Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(gp_Circ2d& circ2d, Standard_Real angle1, Standard_Real angle2) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeArcOfCircle(circ2d, angle1, angle2).Value());
}

extern "C" Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(gp_Pnt2d& pt1, gp_Pnt2d& pt2, gp_Pnt2d& pt3) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeArcOfCircle(pt1, pt2, pt3).Value());
}

extern "C" Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(gp_Circ2d& circ2d, gp_Pnt2d& pt1, Standard_Real angle1) {
    TRACE("");
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeArcOfCircle(circ2d, pt1, angle1).Value());
}

extern "C" void _Geom2d_TrimmedCurve__Mirror__ax2(Handle(Geom2d_TrimmedCurve)& curve, gp_Ax2d& ax2d) {
    TRACE("");
    curve->Mirror(ax2d);
}

extern "C" void _Geom2d_TrimmedCurve__Reverse(Handle(Geom2d_TrimmedCurve)& curve) {
    TRACE("");
    curve->Reverse();
}

extern "C" Handle(Geom2d_Circle)* handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(gp_Circ2d &cir2d) {
    TRACE("");
    return new Handle(Geom2d_Circle)(GCE2d_MakeCircle(cir2d));
}

extern "C" Geom2dAPI_InterCurveCurve* new_Geom2dAPI_InterCurveCurve__curve1_curve2(const Handle(Geom2d_Curve)& C1, const Handle(Geom2d_Curve)& C2) {
    TRACE("");
    return new Geom2dAPI_InterCurveCurve(C1, C2);
}

extern "C" Standard_Integer int_Geom2dAPI_InterCurveCurve__NbPoints(const Geom2dAPI_InterCurveCurve &inter_curve_curve) {
    TRACE("");
    return inter_curve_curve.NbPoints();
}

extern "C" gp_Pnt2d* new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(const Geom2dAPI_InterCurveCurve &inter_curve_curve, const Standard_Integer index) {
    TRACE("");
    return new gp_Pnt2d(inter_curve_curve.Point(index));
}

extern "C"  Handle(Geom2d_Geometry)*  handle_Geom2d_Geometry__Copy(const Handle(Geom2d_Geometry) &toCpy) {
    TRACE("");
    return new Handle(Geom2d_Geometry)(toCpy->Copy());
}

extern "C"  Handle(Geom_Curve)* handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(Handle(Geom2d_Curve) &curve, gp_Pln &plan) {
    TRACE("");
    try {
        return new Handle(Geom_Curve)(GeomAPI::To3d(curve, plan).get());
    } catch(Standard_ConstructionError &e) {
        TRACE(e.GetMessageString());
        throw e;
    }
}

extern "C" Handle(Geom2d_Line) *handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(gp_Pnt2d& p1, gp_Pnt2d& p2) {
    TRACE("");
    return new Handle(Geom2d_Line)(GCE2d_MakeLine(p1,p2).Value());
}

/***********************************************************************************************************************

    3D

***********************************************************************************************************************/

extern "C" gp_Pnt * new_gp_Pnt__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp << " theZp " << theZp);
    return new gp_Pnt(theXp, theYp, theZp);
}

extern "C" void delete_gp_Pnt(gp_Pnt *pnt) {
    TRACE("");
    delete pnt;
}

extern "C" gp_Vec *new_gp_Vec__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    TRACE2(std::ostringstream{} << " theXp " << theXp << " theYp " << theYp << " theZp " << theZp);
    return new gp_Vec(theXp, theYp, theZp);
}

extern "C" void delete_gp_Vec(gp_Vec *pnt) {
    TRACE("");
    delete pnt;
}

extern "C" Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(const Handle(Geom_Curve) &C, const Standard_Real U1, const Standard_Real U2) {
    TRACE("");
    return new Handle(Geom_TrimmedCurve)(new Geom_TrimmedCurve(C, U1, U2));
}

extern "C" Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3) {
    TRACE("");
    return new Handle(Geom_TrimmedCurve)(GC_MakeArcOfCircle(*pnt1, *pnt2, *pnt3).Value());
}

extern "C" Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_vtangente_p2(gp_Pnt *pnt1, gp_Vec *tan, gp_Pnt *pnt3) {
    TRACE("");
    return new Handle(Geom_TrimmedCurve)(GC_MakeArcOfCircle(*pnt1, *tan, *pnt3).Value());
}

extern "C" void delete_handle_Geom_TrimmedCurve(Handle(Geom_TrimmedCurve)* ptr) {
    TRACE("");
    delete ptr;
}

extern "C" Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(gp_Pnt *pnt1, gp_Pnt *pnt2) {
    TRACE("");
    return new Handle(Geom_TrimmedCurve)(GC_MakeSegment(*pnt1, *pnt2).Value());
}

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve) &segment) {
    TRACE("");
    return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(segment).Edge());
}

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(Handle(Geom2d_Curve) &curve, const Handle(Geom_Surface) &surface) {
    TRACE("");
    return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(curve, surface).Edge());
}

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(Handle(Geom2d_Curve) &curve) {
    TRACE("");
    return new TopoDS_Edge(BRepBuilderAPI_MakeEdge2d(curve).Edge());
}

extern "C" void delete_TopoDS_Edge(TopoDS_Edge *ptr) {
    TRACE("");
    delete ptr;
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(gp_Pnt& from, gp_Pnt& to) {
    TRACE("");
    return new BRepBuilderAPI_MakeEdge(from, to);
}

extern "C" const BRepBuilderAPI_MakePolygon *new_BRepBuilderAPI_MakePolygon(void) {
    TRACE("");
    return new BRepBuilderAPI_MakePolygon();
}

extern "C" const void _BRepBuilderAPI_MakePolygon__Add__pt(BRepBuilderAPI_MakePolygon& poly, gp_Pnt& pt) {
    TRACE("");
    poly.Add(pt);
}

extern "C" const BRepTools_WireExplorer *new_BRepTools_WireExplorer(void) {
    TRACE("");
    return new BRepTools_WireExplorer();
}

extern "C" void _BRepTools_WireExplorer__Init__TopoDS_Wire(BRepTools_WireExplorer &e, const TopoDS_Wire &W) {
    TRACE("");
    return e.Init(W);
}

extern "C" void _BRepTools_WireExplorer__Init__BRepBuilderAPI_MakePolygon(BRepTools_WireExplorer &e, BRepBuilderAPI_MakePolygon &W) {
    TRACE("");
    return e.Init(W.Wire());
}

extern "C" Standard_Boolean b_BRepTools_WireExplorer__More(BRepTools_WireExplorer &e) {
    TRACE("");
    return e.More();
}

extern "C" const void _BRepTools_WireExplorer__Next(BRepTools_WireExplorer &e) {
    TRACE("");
    e.Next();
}

extern "C" const TopoDS_Edge* _BRepTools_WireExplorer__Current(BRepTools_WireExplorer &e) {
    TRACE("");
    return new TopoDS_Edge(e.Current());
}

extern "C" GeomPlate_BuildPlateSurface* new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(const Standard_Integer	Degree, const Standard_Integer	NbPtsOnCur, const Standard_Integer	NbIter) {
    TRACE("");
    return new GeomPlate_BuildPlateSurface(Degree, NbPtsOnCur, NbIter);
}

extern "C" Handle(GeomPlate_PointConstraint)* handle_GeomPlate_PointConstraint__gp_Pnt_order(gp_Pnt &p, Standard_Integer order) {
    TRACE("");
    return new Handle(GeomPlate_PointConstraint)(new GeomPlate_PointConstraint(p, order));
}

extern "C" void _GeomPlate_BuildPlateSurface__Add__Cont(GeomPlate_BuildPlateSurface& s, const Handle(GeomPlate_PointConstraint) &Cont) {
    TRACE("");
    s.Add(Cont);
}

extern "C" void _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(GeomPlate_BuildPlateSurface& s, const Handle(BRepFill_CurveConstraint) &Cont) {
    TRACE("");
    s.Add(Cont);
}

extern "C" void _GeomPlate_BuildPlateSurface__Perform(GeomPlate_BuildPlateSurface& s) {
    TRACE("");
    try {
        s.Perform();
    } catch(Standard_RangeError& e) {
        TRACE(e.GetMessageString());
        throw e;
    } catch(Standard_ConstructionError& e) {
        TRACE(e.GetMessageString());
        throw e;
    }
}

extern "C" Handle(GeomPlate_Surface)* handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface(GeomPlate_BuildPlateSurface &s) {
    TRACE("");
    return new  Handle(GeomPlate_Surface)(s.Surface());
}

extern "C" Standard_Real r_GeomPlate_BuildPlateSurface__G0Error(GeomPlate_BuildPlateSurface &s) {
    TRACE("");
    return s.G0Error();
}

extern "C" Standard_Real* r4_GeomPlate_Surface__Bounds(Handle(GeomPlate_Surface) &s) {
    TRACE("");
    Standard_Real U1;
    Standard_Real U2;
    Standard_Real V1;
    Standard_Real V2;

    s->Bounds(U1, U2, V1, V2);
    Standard_Real* res = new Standard_Real[4] {U1, U2, V1, V2};
    return res;
}

extern "C" Handle(BRepAdaptor_Curve) *handle_BRepAdaptor_Curve(void) {
    TRACE("");
    return new Handle(BRepAdaptor_Curve)(new BRepAdaptor_Curve());
}

extern "C" void _BRepAdaptor_Curve__Initialize(Handle(BRepAdaptor_Curve)& adaptor, TopoDS_Edge &edge) {
    TRACE("");
    adaptor->Initialize(edge);
}

extern "C" Handle(BRepFill_CurveConstraint)* handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(const Handle(Adaptor3d_Curve) &	Boundary, const Standard_Integer	Tang) {
    TRACE("");
    return new Handle(BRepFill_CurveConstraint)(new BRepFill_CurveConstraint(Boundary, Tang));
}

extern "C" GeomPlate_MakeApprox* new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(const Handle(GeomPlate_Surface) &SurfPlate, const Standard_Real Tol3d, const Standard_Integer Nbmax, const Standard_Integer dgmax, const Standard_Real dmax, const Standard_Integer CritOrder) {
    TRACE("");
    return new GeomPlate_MakeApprox(SurfPlate, Tol3d, Nbmax, dgmax, dmax, CritOrder);
}

extern "C" Handle(Geom_BSplineSurface) *handle_Geom_Surface__GeomPlate_MakeApprox__Surface(GeomPlate_MakeApprox &mApp) {
    TRACE("");
    return new Handle(Geom_BSplineSurface)(mApp.Surface());
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve)& curve) {
    TRACE("");
    return new BRepBuilderAPI_MakeEdge(curve);
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(const Handle(Geom2d_Curve)& curve2d, const Handle(Geom_Surface)& S, const Standard_Real	p1,const Standard_Real	p2) {
    TRACE("");
    return new BRepBuilderAPI_MakeEdge(curve2d, S, p1, p2);
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(const Handle(Geom2d_Curve)& curve2d, const Handle(Geom_Surface)& S) {
    TRACE("");
    return new BRepBuilderAPI_MakeEdge(curve2d, S);
}

extern "C" const BRepBuilderAPI_MakeWire * new_BRepBuilderAPI_MakeWire() {
    TRACE("");
    return new BRepBuilderAPI_MakeWire();
}

extern "C" const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeEdge& edge) {
    TRACE("");
    return new BRepBuilderAPI_MakeWire(edge);
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeWire& wireMaker, BRepBuilderAPI_MakeEdge& edge) {
    TRACE("");

    try {
        wireMaker.Add(edge);
    } catch(StdFail_NotDone &e) {
        TRACE(e.GetMessageString());
        BRepBuilderAPI_WireError error = wireMaker.Error();
        switch(error) {
            case BRepBuilderAPI_WireDone:
                TRACE("BRepBuilderAPI_WireDone");
                break;
            case BRepBuilderAPI_EmptyWire:
                TRACE("BRepBuilderAPI_EmptyWire");
                break;
            case BRepBuilderAPI_DisconnectedWire :
                TRACE("BRepBuilderAPI_DisconnectedWire ");
                break;
            case BRepBuilderAPI_NonManifoldWire:
                TRACE("BRepBuilderAPI_NonManifoldWire");
                break;

        }
        throw e;
    }
}

extern "C" const TopoDS_Wire * new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(TopoDS_Edge& e1, TopoDS_Edge& e2, TopoDS_Edge& e3) {
    TRACE("");
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1, e2, e3).Wire());
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(TopoDS_Edge& e1, TopoDS_Edge& e2) {
    TRACE("");
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1, e2).Wire());
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1(TopoDS_Edge& e1) {
    TRACE("");
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1).Wire());
}

extern "C" BRepFeat_MakePipe *new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(const TopoDS_Shape &Sbase, const TopoDS_Shape &Pbase, const TopoDS_Face &Skface, const TopoDS_Wire &Spine, const Standard_Integer Fuse, const Standard_Boolean Modify) {
    TRACE("");
    return new BRepFeat_MakePipe(Sbase, Pbase, Skface, Spine, Fuse, Modify);
}

extern "C" void _BRepFeat_MakePipe__Perform(BRepFeat_MakePipe *p) {
    TRACE("");
    p->Perform();
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3_e4(TopoDS_Edge& e1, TopoDS_Edge& e2, TopoDS_Edge& e3, TopoDS_Edge& e4) {
    TRACE("");
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1, e2, e3, e4).Wire());
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire &make_wire) {
    TRACE("");
    return new TopoDS_Wire(make_wire.Wire());
}

extern "C" const TopoDS_Wire &ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire &make_wire) {
    TRACE("");
    return make_wire.Wire();
}

extern "C" const TopoDS_Shape &ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(BRepBuilderAPI_MakeWire &make_wire) {
    TRACE("");
    return make_wire.Shape();
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(BRepBuilderAPI_MakeWire &mw, TopoDS_Wire &wire) {
    TRACE("");
    mw.Add(wire);
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire &mw, BRepBuilderAPI_MakeWire &mw2) {
    TRACE("");
    mw.Add(mw2);
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(BRepBuilderAPI_MakeWire &mw, TopoDS_Edge &edge) {
    TRACE("");
    mw.Add(edge);
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopTools_ListOfShape(BRepBuilderAPI_MakeWire &mw, TopTools_ListOfShape &listOfShape) {
    TRACE("");
    mw.Add(listOfShape);
}

extern "C" const gp_Ax1 &gp__OX() {
    TRACE("");
    return gp::OX();
}

extern "C" gp_Trsf *new_gp_Trsf() {
    TRACE("");
    return new gp_Trsf();
}

extern "C" void _gp_Trsf__SetMirror__gp_Ax1(gp_Trsf *trsf, gp_Ax1 *ax1) {
    TRACE("");
    trsf->SetMirror(*ax1);
}

extern "C" void _gp_Trsf__SetMirror__gp_Ax2(gp_Trsf *trsf, gp_Ax2 *ax2) {
    TRACE("");
    trsf->SetMirror(*ax2);
}

extern "C" void _gp_Trsf__SetTranslation__gp_Vec(gp_Trsf &gp_trsf, const gp_Vec &translation) {
    TRACE("");
    return gp_trsf.SetTranslation(translation);
}

extern "C" void _gp_Trsf__SetRotation__gp_Vec(gp_Trsf &gp_trsf, const gp_Ax1 &ax1, Standard_Real angle) {
    TRACE("");
    return gp_trsf.SetRotation(ax1, angle);
}

extern "C" BRepBuilderAPI_Transform *new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(const TopoDS_Shape &w, gp_Trsf &trsf, const Standard_Boolean theCopyGeom, const Standard_Boolean theCopyMesh) {
    TRACE("");
    return new BRepBuilderAPI_Transform(w, trsf, theCopyGeom, theCopyMesh);
}

extern "C" TopoDS_Wire &ref_TopoDS__Wire__TopoDS_Shape(TopoDS_Shape &shape) {
    TRACE("");
    return TopoDS::Wire(shape);
}

extern "C" void _TopoDS__Shape__Reverse(TopoDS_Shape &shape) {
    TRACE("");
    return shape.Reverse();
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(TopoDS_Wire &wire) {
    TRACE(wire.IsNull() ? "NULL" : "NOT NULL");
    TRACE(wire.Closed() ? "Closed" : "NOT Closed");
    TRACE(wire.Infinite() ? "Infinite" : "NOT Infinite");
    TRACE(wire.Convex() ? "Convex" : "NOT Convex");
//    return new TopoDS_Face(wire);
//    return new TopoDS_Face(BRepBuilderAPI_MakeFace(wire).Face());
    BRepBuilderAPI_MakeFace* mf;
    try {
        mf = new BRepBuilderAPI_MakeFace(wire);
    } catch(Standard_NullObject &e) {
        TRACE("Standard_NullObject");
        TRACE(e.GetMessageString());
        throw e;
    }
    TRACE("mf");
    const TopoDS_Face & face = mf->Face();
    TRACE("face");
    return new TopoDS_Face(face);
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire &wire) {
    TRACE("");
    return new BRepBuilderAPI_MakeFace(wire.Wire());
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__s_um_uM_vm_vM_Tol(const Handle(Geom_Surface) &S, const Standard_Real UMin, const Standard_Real UMax, const Standard_Real VMin, const Standard_Real VMax, const Standard_Real TolDegen) {
    TRACE("");
    return new BRepBuilderAPI_MakeFace(S, UMin, UMax, VMin, VMax, TolDegen);
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace(void) {
    TRACE("");
    return new BRepBuilderAPI_MakeFace();
}

extern "C" TopoDS_Face *TopoDS_Face__BRepBuilderAPI_MakeFace__Face(BRepBuilderAPI_MakeFace &MKF1) {
    TRACE("");
    return new TopoDS_Face(MKF1.Face());
}

extern "C" void _BRepBuilderAPI_MakeFace__Init(BRepBuilderAPI_MakeFace *MF, const Handle(Geom_Surface) &	S, const Standard_Boolean Bound, const Standard_Real	TolDegen ) {
    TRACE("");
    MF->Init(S, Bound, TolDegen);
}

extern "C" void _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeFace *MF, BRepBuilderAPI_MakeWire &W) {
    TRACE("");
    try {
        MF->Add(W.Wire());
    } catch (StdFail_NotDone& e) {
        TRACE(e.GetMessageString());
        throw e;
    }
}

extern "C" TColgp_Array1OfPnt* new_TColgp_Array1OfPnt__Low_Up(const Standard_Integer 	Low, const Standard_Integer 	Up) {
    TRACE("");
    return new TColgp_Array1OfPnt(Low, Up);
}
extern "C" void _TColgp_Array1OfPnt__Ar_Pt_Indx(TColgp_Array1OfPnt& ar, gp_Pnt& pt, Standard_Integer indx) {
    TRACE("");
    ar(indx) = pt;
}

extern "C" Handle(Geom_BezierCurve) *handle_Geom_BezierCurve__TColgp_Array1OfPnt(TColgp_Array1OfPnt& CurvePoles) {
    TRACE("");
    return new Handle(Geom_BezierCurve)(new Geom_BezierCurve(CurvePoles));
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace(BRepBuilderAPI_MakeFace &face) {
    TRACE("");
    return new TopoDS_Face(face.Face());
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gp_Pln &plane) {
    TRACE("");
    return new TopoDS_Face(BRepBuilderAPI_MakeFace(plane).Face());
}

extern "C" TopoDS_Face *new_TopoDS_Face() {
    TRACE("");
    return new TopoDS_Face();
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(TopoDS_Face &face, gp_Vec &normal) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakePrism(face, normal));
}

extern "C" BRepFeat_MakeDPrism *new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(const TopoDS_Shape &Sbase, const TopoDS_Face &Pbase, const TopoDS_Face &Skface, const Standard_Real Angle, const Standard_Integer Fuse, const Standard_Boolean Modify) {
    TRACE("");
    return new BRepFeat_MakeDPrism(Sbase, Pbase, Skface, Angle, Fuse, Modify);
}

extern "C" void _BRepFeat_MakeDPrism__Perform__Height(BRepFeat_MakeDPrism &p, Standard_Real height) {
    TRACE("");
    p.Perform(height);
}

extern "C" BRepFilletAPI_MakeFillet * new_BRepFilletAPI_MakeFillet__TopoDS_Shape(TopoDS_Shape &body) {
    TRACE("");
    return new BRepFilletAPI_MakeFillet(body);
}

extern "C" TopAbs_ShapeEnum TopAbs_ShapeEnumFromOrdinal(int ordinal) {
    TRACE("");
    switch (ordinal) {
    case 0:
        return TopAbs_COMPOUND;
    case 1:
        return TopAbs_COMPSOLID;
    case 2:
        return TopAbs_SOLID;
    case 3:
        return TopAbs_SHELL;
    case 4:
        return TopAbs_FACE;
    case 5:
        return TopAbs_WIRE;
    case 6:
        return TopAbs_EDGE;
    case 7:
        return TopAbs_VERTEX;
    case 8:
        return TopAbs_SHAPE;
    default:
        return TopAbs_SHAPE;
    }
}

extern "C" TopExp_Explorer *new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(const TopoDS_Shape &S, const /*TopAbs_ShapeEnum*/int ToFind,
                                             const int ToAvoid = TopAbs_SHAPE) {
    TRACE("");
    return new TopExp_Explorer(S, TopAbs_ShapeEnumFromOrdinal(ToFind), TopAbs_ShapeEnumFromOrdinal(ToAvoid));
}

extern "C" bool _TopExp_Explorer__More(TopExp_Explorer &explorer) {
    TRACE("");
    return explorer.More();
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__TopExp_Explorer__Current(TopExp_Explorer &explorer) {
    TRACE("");
    return new TopoDS_Shape(explorer.Current());
}

extern "C" TopoDS_Face * new_TopoDS_Face__TopExp_Explorer__Current(TopExp_Explorer &explorer) {
    TRACE("");
    return new TopoDS_Face(TopoDS::Face(explorer.Current()));
}

extern "C" void _TopExp_Explorer__Next(TopExp_Explorer &explorer) {
    TRACE("");
    return explorer.Next();
}

extern "C" TopoDS_Edge &ref_TopoDS_Edge__TopoDS_Shape(TopoDS_Shape &shape) {
    TRACE("");
    try {
        return TopoDS::Edge(shape);
    } catch (Standard_TypeMismatch& e) {
        TRACE("Standard_TypeMismatch");
        throw e;
    } catch (StdFail_NotDone& e) {
        TRACE("StdFail_NotDone");
        throw e;
    }
}



extern "C" void _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(BRepFilletAPI_MakeFillet &make_fillet, Standard_Real r,
                                               TopoDS_Edge &edge) {
    TRACE("");
    return make_fillet.Add(r, edge);
}

extern "C" const gp_Dir *new_gp_Dir_DZ() {
    TRACE("");
    return new gp_Dir(gp::DZ());
}

extern "C" gp_Dir *new_gp_Dir__x_y_z(const Standard_Real theXv, const Standard_Real theYv, const Standard_Real theZv) {
    TRACE2(std::ostringstream{} << " theXv " << theXv << " theYv " << theYv << " theZv " << theZv);
    return new gp_Dir(theXv, theYv, theZv);
}

extern "C" gp_Dir *new_gp_Dir__Normal__TopoDS_Face(const TopoDS_Face &aCurrentFace) {
    TRACE("");
    Standard_Real umin, umax, vmin, vmax;
    BRepTools::UVBounds(aCurrentFace, umin, umax, vmin, vmax);
    Handle(Geom_Surface) aSurface = BRep_Tool::Surface(aCurrentFace);
    GeomLProp_SLProps props(aSurface, (umin + umax) / 2, (vmin + vmax) / 2, 1, 0.01);
    gp_Dir n = props.Normal();
    std::cout << "gp_dir_normal_to_face: umin = " << umin << " umax = " << umax << " vmin = " << vmin << " vmax = " <<
        vmax << " n.X " << n.X() << " n.Y " << n.Y() << " n.Z " << n.Z() << std::endl;
    return new gp_Dir(n);
}


extern "C" const gp_Ax2 * new_gp_Ax2__gp_Pnt_gp_Dir(gp_Pnt &loc, gp_Dir &dir) {
    TRACE("");
    return new gp_Ax2(loc, dir);
}

extern "C" const gp_Ax2 * new_gp_Ax2__gp_Pnt_gp_Dir_Normal(gp_Pnt &loc, gp_Dir &dir, gp_Dir &normal) {
    TRACE("");
    return new gp_Ax2(loc, dir, normal);
}

extern "C" const gp_Ax3 * new_gp_Ax3__p_dN_dX(gp_Pnt &loc, gp_Dir &dirN, gp_Dir &dirX) {
    TRACE("");
    return new gp_Ax3(loc, dirN, dirX);
}

extern "C" const gp_Ax2 *new_gp_Ax2_DZ() {
    TRACE("");
    return new gp_Ax2(gp_Pnt(0.0, 0.0, 0.0), gp::DZ());
}

extern "C" BRepPrimAPI_MakeCylinder *new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(const gp_Ax2 &Axes, const Standard_Real R,
                                                                const Standard_Real H) {
    TRACE("");
    return new BRepPrimAPI_MakeCylinder(Axes, R, H);
}

extern "C" BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__x_y_z(const Standard_Real x, const Standard_Real y,
                                                      const Standard_Real z) {
    TRACE("");
    return new BRepPrimAPI_MakeBox(x, y, z);
}

extern "C" BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__Ax2_x_y_z(const gp_Ax2 &Axes, const Standard_Real dx, const Standard_Real dy, const Standard_Real dz) {
    TRACE("");
    return new BRepPrimAPI_MakeBox(Axes, dx, dy, dz);
}

extern "C" Handle(Geom_Surface) *handle_Geom_Surface__TopoDS_Face(TopoDS_Face &face) {
    TRACE("");
    return new Handle(Geom_Surface)(BRep_Tool::Surface(face));
}

extern "C" Handle(Geom_Plane) *handle_Geom_Plan__gp_Pln(gp_Pln &pln) {
    TRACE("");
    return new Handle(Geom_Plane)(new Geom_Plane(pln));
}

extern "C" Standard_Integer int_Geom_Surface__is__Geom_Plane(Handle(Geom_Surface) &surface) {
    TRACE("");
    return surface->DynamicType() == STANDARD_TYPE(Geom_Plane) ? 1 : 0;
}

extern "C" Handle(Geom_Plane) *handle_Geom_Plane__handle_Geom_Surface(Handle(Geom_Surface) &surface) {
    TRACE("");
    return new Handle(Geom_Plane)(Handle(Geom_Plane)::DownCast(surface));
}

extern "C" gp_Pnt *new_gp_Pnt__Geom_Plane(Handle(Geom_Plane) &plane) {
    TRACE("");
    return new gp_Pnt(plane->Location());
}

extern "C" TopTools_ListOfShape *new_TopTools_ListOfShape() {
    TRACE("");
    return new TopTools_ListOfShape();
}

extern "C" void delete_TopTools_ListOfShape(TopTools_ListOfShape* ptr) {
    TRACE("");
    delete ptr;
}

extern "C" void _TopTools_ListOfShape__Append__TopoDS_Shape(TopTools_ListOfShape *l, TopoDS_Shape &face) {
    TRACE("");
    l->Append(face);
}

extern "C" BRepOffsetAPI_MakeThickSolid *new_BRepOffsetAPI_MakeThickSolid() {
    TRACE("");
    return new BRepOffsetAPI_MakeThickSolid();
}

extern "C" void _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(
                                                      BRepOffsetAPI_MakeThickSolid *thick_solid, TopoDS_Shape *shape,
                                                      const TopTools_ListOfShape *face_to_remove,
                                                      Standard_Real thickness, Standard_Real tol) {
    TRACE("");
    thick_solid->MakeThickSolidByJoin(*shape, *face_to_remove, thickness, tol);
}

extern "C" Handle(Geom_CylindricalSurface) *handle_Geom_CylindricalSurface__ax2_radius(
    const gp_Ax3 &ax2, const Standard_Real radius) {
    TRACE("");
    return new Handle(Geom_CylindricalSurface)(new Geom_CylindricalSurface(ax2, radius));
}

extern "C" Handle(Geom_SurfaceOfLinearExtrusion) *handle_Geom_SurfaceOfLinearExtrusion__Geom_Curve_gp_Dir(
    const Handle(Geom_Curve) &C, const gp_Dir &V) {
    TRACE("");
    return new Handle(Geom_SurfaceOfLinearExtrusion)(new Geom_SurfaceOfLinearExtrusion(C, V));
}

extern "C" Handle(Geom_SurfaceOfRevolution) *handle_Geom_SurfaceOfRevolution__Geom_Curve_gp_Ax1(
    const Handle(Geom_Curve) &C, const gp_Ax1 &V) {
    TRACE("");
    Geom_SurfaceOfRevolution* surface = new Geom_SurfaceOfRevolution(C, V);
    if (surface->IsVPeriodic()) TRACE("IsVPeriodic true");
    if (surface->IsUPeriodic()) TRACE("IsUPeriodic true");
    if (surface->IsUClosed()) TRACE("IsUClosed true");
    if (surface->IsVClosed()) TRACE("IsVClosed true");
    return new Handle(Geom_SurfaceOfRevolution)(surface);
}

extern "C" Handle(Geom_Ellipse) *handle_Geom_Ellipse__gp_Ax2_rM_rm(const gp_Ax2 &A2, const Standard_Real MajorRadius, const Standard_Real MinorRadius) {
    TRACE("");
    return new Handle(Geom_Ellipse)(new Geom_Ellipse(A2, MajorRadius, MinorRadius));
}

extern "C" Standard_Real* R4_Geom_Surface__Bounds(const Handle(Geom_Surface) &S) {
    TRACE("");
    Standard_Real U1;
    Standard_Real U2;
    Standard_Real V1;
    Standard_Real V2;

    S->Bounds(U1, U2, V1, V2);
    Standard_Real* res = new Standard_Real[4] {U1, U2, V1, V2};
    return res;
}

extern "C" gp_Pnt* gp_Pnt__Geom_Surface__Value(const Handle(Geom_Surface) &S, const Standard_Real U, const Standard_Real V) {
    TRACE("");
    return new gp_Pnt(S->Value(U, V));
}

extern "C" void _BRepLib__BuildCurves3d__TopoDS_Shape(const TopoDS_Shape &w1) {
    TRACE("");
    BRepLib::BuildCurves3d(w1);
}

extern "C" BRepOffsetAPI_ThruSections *new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(const Standard_Boolean isSolid = Standard_False,
                                                               const Standard_Boolean ruled = Standard_False,
                                                               const Standard_Real pres3d = 1.0e-06) {
    TRACE("");
    return new BRepOffsetAPI_ThruSections(isSolid, ruled, pres3d);
}

extern "C" void _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire &w) {
    TRACE("");
    thru_sections->AddWire(w);
}

extern "C" void _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(BRepOffsetAPI_ThruSections *thru_sections,
                                                            const Standard_Boolean b) {
    TRACE("");
    thru_sections->CheckCompatibility(b);
}

extern "C" TopoDS_Compound *new_TopoDS_Compound() {
    TRACE("");
    return new TopoDS_Compound();
}

extern "C" BRep_Builder *new_BRep_Builder() {
    TRACE("");
    return new BRep_Builder();
}

extern "C" void _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(TopoDS_Builder &builder, TopoDS_Shape &inThis, TopoDS_Shape &toAdd) {
    TRACE("");
    builder.Add(inThis, toAdd);
}

extern "C" void _TopoDS_Builder__MakeCompound__TopoDS_Compound(TopoDS_Builder &b, TopoDS_Compound &c) {
    TRACE("");
    b.MakeCompound(c);
}

extern "C" gp_Ax1 *new_gp_Ax1__p_dir(const gp_Pnt &theP, const gp_Dir &theV) {
    TRACE("");
    return new gp_Ax1(theP, theV);
}

extern "C" gp_Pnt *new_gp_Pnt__CentreOfMass__TopoDS_Shape(const TopoDS_Shape &myShape) {
    TRACE("");
    GProp_GProps massProps;
    BRepGProp::SurfaceProperties(myShape, massProps);
    return new gp_Pnt(massProps.CentreOfMass());
}

extern "C" Standard_Real gp_Pnt__X(gp_Pnt *pnt) {
    TRACE("");
    return pnt->X();
}

extern "C" Standard_Real gp_Pnt__Y(gp_Pnt *pnt) {
    TRACE("");
    return pnt->Y();
}

extern "C" Standard_Real gp_Pnt__Z(gp_Pnt *pnt) {
    TRACE("");
    return pnt->Z();
}

extern "C" Standard_Real gp_Dir__X(gp_Dir *dir) {
    TRACE("");
    return dir->X();
}

extern "C" Standard_Real gp_Dir__Y(gp_Dir *dir) {
    TRACE("");
    return dir->Y();
}

extern "C" Standard_Real gp_Dir__Z(gp_Pnt *dir) {
    TRACE("");
    return dir->Z();
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(const gp_Ax2 &origin, const Standard_Real radius,
                                                    const Standard_Real angle1, const Standard_Real angle2) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeSphere(origin,
                                                   radius,
                                                   angle1,
                                                   angle2));
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(const gp_Ax2 &origin, const Standard_Real radius,
                                                       const Standard_Real height) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeCylinder(origin,
                                                     radius,
                                                     height));
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H_angle(const gp_Ax2 &origin, const Standard_Real R1, const Standard_Real R2, const Standard_Real H, const Standard_Real angle) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeCone(origin, R1, R2, H, angle));
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(const gp_Ax2 &origin, const Standard_Real R1, const Standard_Real R2, const Standard_Real H) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeCone(origin, R1, R2, H));
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(const TopoDS_Shape &shape, const gp_Trsf &gp_trsf,
                                                         const Standard_Boolean theCopyGeom) {
    TRACE("");
    return new TopoDS_Shape(BRepBuilderAPI_Transform(shape, gp_trsf, theCopyGeom).Shape());
//    return BRepBuilderAPI_Transform(shape, gp_trsf, theCopyGeom).Shape();
}


extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(const gp_Ax2 &origin, const Standard_Real radius1,
                                                  const Standard_Real radius2) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeTorus(origin,
                                                  radius1,
                                                  radius2).Shape());
//    return BRepPrimAPI_MakeTorus(origin,radius1,radius2).Shape();
}


extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(TopoDS_Face& face, gp_Ax1& ax1) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeRevol(face, ax1).Shape());
//    return BRepPrimAPI_MakeRevol(face, ax1).Shape();
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(TopoDS_Face& face, gp_Ax1& ax1, Standard_Real angle) {
    TRACE("");
    return new TopoDS_Shape(BRepPrimAPI_MakeRevol(face, ax1, angle).Shape());
}

extern "C" void _TopoDS_Shape__Free(TopoDS_Face& face) {
    TRACE("");
    return face.Free(Standard_True);
}

extern "C" TopoDS_Face *new_TopoDS_Face__face(TopoDS_Face& face) {
    TRACE("");
    return new TopoDS_Face(face);
}

extern "C" gp_Pln* new_gp_Pln__x_y_z_d(const Standard_Real x, const Standard_Real y, const Standard_Real z, const Standard_Real d) {
    TRACE("");
    return new gp_Pln(x, y, z, d);
}

extern "C" gp_Pln* new_gp_Pln__pt_dir(const gp_Pnt& pt, const gp_Dir& dir) {
    TRACE("");
    return new gp_Pln(pt, dir);
}

extern "C" gp_Pln* new_gp_Pln__gp_Ax3(const gp_Ax3& ax3) {
    TRACE("");
    return new gp_Pln(ax3);
}

extern "C" TopoDS_Shape* new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(BRepBuilderAPI_MakeShape &shape) {
    TRACE("");
    try {
        return new TopoDS_Shape(shape.Shape());
    } catch (StdFail_NotDone& e) {
        TRACE(e.GetMessageString());
        throw e;
    }

//    return shape.Shape();
}

extern "C" Handle(ShapeExtend_WireData)* new_ShapeExtend_WireData() {
    TRACE("");
    return new Handle(ShapeExtend_WireData)(new ShapeExtend_WireData());
}

extern "C" void _ShapeExtend_WireData__Add__TopoDS_Edge(ShapeExtend_WireData &data, const TopoDS_Edge &edge, const Standard_Integer atnum=0) {
    TRACE("");
    data.Add(edge, atnum);
}

extern "C" void _ShapeExtend_WireData__Add__TopoDS_Wire(ShapeExtend_WireData &data, const TopoDS_Wire &edge, const Standard_Integer atnum=0) {
    TRACE("");
    data.Add(edge, atnum);
}

extern "C" TopoDS_Wire* util_ShapeFix_Wire__Load__ShapeExtend_WireData(const Handle(ShapeExtend_WireData) &data) {
    TRACE("");
    BRepBuilderAPI_MakeWire aMakeWire;
    ShapeFix_Wire* sfw = new ShapeFix_Wire();
    ShapeFix_ShapeTolerance FTol;

    sfw->Load(data);
    sfw->Perform();
    //Reorder edges is very important
    sfw->FixReorder();
    sfw->SetMaxTolerance(0.01);
    sfw->ClosedWireMode() = Standard_True;
    sfw->FixConnected(1.e-3);
    sfw->FixClosed(1.e-3);
    for (int i = 1; i <= sfw->NbEdges(); i ++) {
        TopoDS_Edge Edge = sfw->WireData()->Edge(i);
        FTol.SetTolerance(Edge, 0.01, TopAbs_VERTEX);
        aMakeWire.Add(Edge);
    }

    TopoDS_Wire aWire = aMakeWire.Wire();
    return new TopoDS_Wire(aWire);
}

extern "C" GccAna_Circ2d2TanRad *new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(const gp_Pnt2d &Point1, const gp_Pnt2d &Point2, const Standard_Real Radius, const Standard_Real Tolerance) {
    return new GccAna_Circ2d2TanRad(Point1, Point2, Radius, Tolerance);
}

extern "C" Standard_Integer i_GccAna_Circ2d2TanRad__NbSolutions(GccAna_Circ2d2TanRad& circ2d2TanRad) {
    return circ2d2TanRad.NbSolutions();
}

extern "C" gp_Circ2d* ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(GccAna_Circ2d2TanRad* circ2d2TanRad, Standard_Integer numStartingAt1) {
    return new gp_Circ2d(circ2d2TanRad->ThisSolution(numStartingAt1));
}

extern "C" const gp_Ax22d &ref_Position__gp_Circ2d__Position(gp_Circ2d &cir2d) {
    return cir2d.Position();
}

extern "C" const gp_Pnt2d &ref_gp_Pnt2d__gp_Ax22d__Location(gp_Ax22d &ax22d) {
    return ax22d.Location();
}

extern "C" BRepAlgoAPI_Cut *new_BRepAlgoAPI_Cut__s1_s2(TopoDS_Shape &result, TopoDS_Shape &tool) {
    BRepAlgoAPI_Cut* cut = new BRepAlgoAPI_Cut(result, tool);
    cut->Build();
    cut->SimplifyResult();
    return cut;
}

/*

Composed

*/
extern "C" TopoDS_Shape *new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(TopoDS_Shape &result, TopoDS_Shape &tool) {
    TRACE("");
    const auto cut = new BRepAlgoAPI_Cut(result, tool);
//    cut->Build();
//    cut->SimplifyResult();
    return new TopoDS_Shape(cut->Shape());
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(TopoDS_Shape &s1, TopoDS_Shape &s2) {
    TRACE("");
    const auto fuse = new BRepAlgoAPI_Fuse(s1, s2);
//    fuse->Build();
//    fuse->SimplifyResult();
    return new TopoDS_Shape(fuse->Shape());
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_common__s1_s2(TopoDS_Shape &s1, TopoDS_Shape &s2) {
    TRACE("");
    const auto common = new BRepAlgoAPI_Common(s1, s2);
//    fuse->Build();
//    fuse->SimplifyResult();
    return new TopoDS_Shape(common->Shape());
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepAlgoAPI_Cut__TopoDS_Shape_TopTools_ListOfShape(TopoDS_Shape &result, TopTools_ListOfShape &aLT) {
    TRACE("");
    Standard_Boolean bRunParallel;
    Standard_Real aFuzzyValue;
    BRepAlgoAPI_Cut aBuilder;
    // prepare the arguments
    TopTools_ListOfShape aLS;
    aLS.Append(result);
    //
    bRunParallel = Standard_False;
    aFuzzyValue = 2.1e-5;
    //
    // set the arguments
    std::cout << "aLS.Size()" << aLS.Size() << std::endl;
    std::cout << "aLT.Size()" << aLT.Size() << std::endl;
    aBuilder.SetArguments(aLS);
    aBuilder.SetTools(aLT);
    //
    // Set options for the algorithm
    // setting options for this algorithm is similar to setting options for GF algorithm (see "GF Usage" chapter)

    // Set parallel processing mode (default is false)
    aBuilder.SetRunParallel(bRunParallel);
    // Set Fuzzy value (default is Precision::Confusion())
    aBuilder.SetFuzzyValue(aFuzzyValue);
    // Set safe processing mode (default is false)
    Standard_Boolean bSafeMode = Standard_True;
    aBuilder.SetNonDestructive(bSafeMode);
    // Set Gluing mode for coinciding arguments (default is off)
    BOPAlgo_GlueEnum aGlue = BOPAlgo_GlueShift;
    aBuilder.SetGlue(aGlue);
    // Disabling/Enabling the check for inverted solids (default is true)
    Standard_Boolean bCheckInverted = Standard_False;
    aBuilder.SetCheckInverted(bCheckInverted);
    // Set OBB usage (default is false)
    Standard_Boolean bUseOBB = Standard_True;
    aBuilder.SetUseOBB(bUseOBB);

    // run the algorithm
    aBuilder.Build();
    aBuilder.SimplifyResult();
    //
    // result of the operation aR
    const TopoDS_Shape &aR = aBuilder.Shape();
    return new TopoDS_Shape(aR);
}


extern "C" TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(const TopoDS_Shape &shape, const gp_Ax1 &ax1, const Standard_Real Radius,
                                   const Standard_Real PFrom, const Standard_Real PTo) {
    TRACE("");
    std::cout << "PFrom = " << PFrom << "PTo = " << PTo << " " << (PFrom > PTo) << std::endl;
    BRepFeat_MakeCylindricalHole makeCylindrical;
    makeCylindrical.Init(shape, ax1);
    if (PFrom > PTo)
        makeCylindrical.Perform(Radius);
    else
        makeCylindrical.Perform(Radius, PFrom, PTo);
    return new TopoDS_Shape(makeCylindrical.Shape());
}

extern "C" TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(const TopoDS_Shape &shape, const gp_Ax1 &ax1, const Standard_Real Radius,
                                         const Standard_Real Length) {
    TRACE("");
    std::cout << "Length = " << Length << std::endl;
    BRepFeat_MakeCylindricalHole makeCylindrical;
    makeCylindrical.Init(shape, ax1);
    makeCylindrical.PerformBlind(Radius, Length);
    makeCylindrical.Build();
    return new TopoDS_Shape(makeCylindrical.Shape());
}


extern "C" bool dumpShape(const TopoDS_Shape &shape, const Standard_Integer width, const Standard_Integer height,
                          const char *fileName) {
    TRACE("");
    Handle(Aspect_DisplayConnection)
        _displayConnection = new Aspect_DisplayConnection();

    // don't waste the time waiting for VSync when window is not displayed on the screen
    OpenGl_Caps _caps;
    _caps.buffersNoSwap = true;

    Handle(OpenGl_GraphicDriver)
        _graphicDriver = new OpenGl_GraphicDriver(_displayConnection, false);
    //
    _graphicDriver->ChangeOptions() = _caps;
    _graphicDriver->InitContext();

    //* Viewer setup
    Handle(V3d_Viewer) _viewer = new V3d_Viewer(_graphicDriver);
    Quantity_Color bgColor(255. / 255, 255. / 255, 255. / 255, Quantity_TOC_RGB);
    _viewer->SetDefaultBackgroundColor(bgColor);
    _viewer->SetDefaultLights();
    _viewer->SetLightOn();

    Handle(V3d_View)
        _view = new V3d_View(_viewer, _viewer->DefaultTypeOfView());

#ifdef _WIN32
  /* Window - create a so called "virtual" WNT window that is a pure WNT window
     redefined to be never shown. */
  Handle(WNT_WClass) _wClass = new WNT_WClass("GW3D_Class", (void*) DefWindowProcW,
    CS_VREDRAW | CS_HREDRAW, 0, 0,
    ::LoadCursor(NULL, IDC_ARROW));

  Handle(WNT_Window) _win = new WNT_Window("",
    _wClass,
    WS_POPUP,
    0, 0,
    width, height,
    Quantity_NOC_BLACK);

  _win->SetVirtual(true);
  _view->SetWindow(_win);

#else // Linux
    Handle(Xw_Window) _win = new Xw_Window(_graphicDriver->GetDisplayConnection(),
                                           "",
                                           0, 0,
                                           width, height);
    _win->SetVirtual(true);
    _view->SetWindow(_win);
#endif

    //* View setup
    _view->SetWindow(_win);
    _view->SetComputedMode(false);
    _view->SetProj(V3d_XposYnegZpos);
    _view->AutoZFit();

    //* AIS context
    Handle(AIS_InteractiveContext) _context = new AIS_InteractiveContext(_viewer);
    _context->SetDisplayMode(AIS_Shaded, false);
    _context->DefaultDrawer()->SetFaceBoundaryDraw(true);

    // Render immediate structures into back buffer rather than front.
    _view->View()->SetImmediateModeDrawToFront(false);

    //* Dump
    Handle(Image_AlienPixMap) pixmap = new Image_AlienPixMap;
    Quantity_Color _shapeColor(200. / 255, 200. / 255, 200. / 255, Quantity_TOC_RGB);

    Handle(AIS_Shape) _shapePrs = new AIS_Shape(shape);
    _shapePrs->SetColor(_shapeColor);
    _context->Display(_shapePrs, false);
    _view->FitAll(0.1, true);

    _view->ToPixMap(*pixmap, width, height);
    return pixmap->Save(fileName);
}

extern "C" void write_step(const TopoDS_Shape& shape, const char *fileName)
{
    // Create document
    Handle(TDocStd_Document) aDoc;
    Handle(XCAFApp_Application) anApp = XCAFApp_Application::GetApplication();
    anApp->NewDocument("MDTV-XCAF",aDoc);

    // Create label and add our shape
    Handle (XCAFDoc_ShapeTool) myShapeTool =
            XCAFDoc_DocumentTool::ShapeTool(aDoc->Main());
    TDF_Label aLabel = myShapeTool->NewShape();
    myShapeTool->SetShape(aLabel, shape);

    // Write as STEP file
    STEPCAFControl_Writer *myWriter = new STEPCAFControl_Writer();
    myWriter->Perform(aDoc, fileName);
}

extern "C" void write_stl(const TopoDS_Shape& shape, const char *fileName)
{
    // Write as STL
    StlAPI_Writer stl_writer;
    // stl_writer.SetDeflection(0.001);
    // stl_writer.RelativeMode() = false;
    stl_writer.Write(shape, fileName);
}

extern "C" void analyze(const TopoDS_Shape &myShape) {
    TRACE("");
    BRepCheck_Analyzer checker(myShape);
    if (checker.IsValid()) {
    TRACE("");
        std::cout << "Valide Shaoe " << std::endl << std::flush;
        return;
    }
    std::cout << "Not valid Shaoe " << std::endl << std::flush;
    Handle(BRepCheck_Result) r = checker.Result(myShape);
    if (r->IsBlind())
        std::cout << "Shape is Blind" << std::endl;
    if (r->IsMinimum())
        std::cout << "Shape is Min" << std::endl;
}


