//
// Created by auo on 29/07/24.
//

#include "OcctExternSym.h"

#include <BRepAlgoAPI_Fuse.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_Transform.hxx>
#include <BRepFilletAPI_MakeFillet.hxx>
#include <BRepLib.hxx>
#include <BRepOffsetAPI_MakeThickSolid.hxx>
#include <BRepOffsetAPI_ThruSections.hxx>
#include <BRepPrimAPI_MakeCylinder.hxx>
#include <BRepPrimAPI_MakePrism.hxx>
#include <GCE2d_MakeSegment.hxx>
#include <GC_MakeArcOfCircle.hxx>
#include <GC_MakeSegment.hxx>
#include <Geom2d_Ellipse.hxx>
#include <Geom2d_TrimmedCurve.hxx>
#include <Geom_CylindricalSurface.hxx>
#include <Geom_Plane.hxx>
#include <Geom_TrimmedCurve.hxx>
#include <Standard_Handle.hxx>
#include <gp_Pnt.hxx>
#include <TopExp.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Edge.hxx>

OcctExternSym::OcctExternSym() {
}

extern "C" gp_Pnt *make_gp_pnt(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    return new gp_Pnt(theXp, theYp, theZp);
}

extern "C" gp_Vec *make_gp_vec(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    return new gp_Vec(theXp, theYp, theZp);
}

extern "C" Standard_Real gp_pnt_x(gp_Pnt *pnt) {
    return pnt->X();
}

extern "C" Standard_Real gp_pnt_y(gp_Pnt *pnt) {
    return pnt->Y();
}

extern "C" Standard_Real gp_pnt_z(gp_Pnt *pnt) {
    return pnt->Z();
}

extern "C" Handle(Geom_TrimmedCurve)* gc_make_arc_of_circle(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3) {
    return new Handle(Geom_TrimmedCurve)(GC_MakeArcOfCircle(*pnt1, *pnt2, *pnt3).Value());
}

extern "C" Handle(Geom_TrimmedCurve)* gc_make_segment(gp_Pnt *pnt1, gp_Pnt *pnt2) {
    return new Handle(Geom_TrimmedCurve)(GC_MakeSegment(*pnt1, *pnt2).Value());
}

extern "C" const TopoDS_Edge* brep_builderapi_make_edge(Handle(Geom_TrimmedCurve)& segment) {
    return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(segment).Edge());
}

extern "C" const TopoDS_Wire* brep_builderapi_make_wire_topo_ds_wire(TopoDS_Edge e1, TopoDS_Edge e2, TopoDS_Edge e3) {
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1, e2, e3).Wire());
}

extern "C" const TopoDS_Wire& brep_builderapi_make_wire_topo_ds_wire2(BRepBuilderAPI_MakeWire& make_wire) {
    return make_wire.Wire();
}

extern "C" const BRepBuilderAPI_MakeWire* brep_builderapi_make_wire_new() {
    return new BRepBuilderAPI_MakeWire();
}

extern "C" void brep_builderapi_wire_add(BRepBuilderAPI_MakeWire& mw, TopoDS_Wire& wire) {
    return mw.Add(wire);
}

extern "C" const gp_Ax1& gp_ox() {
    return gp::OX();
}

extern "C" gp_Trsf* gp_trsf() {
    return new gp_Trsf();
}

extern "C" void gp_trsf_set_mirror(gp_Trsf* trsf, gp_Ax1* ax1) {
    trsf->SetMirror(*ax1);
}

extern "C" BRepBuilderAPI_Transform* brep_builderapi_transform(const TopoDS_Wire* w, gp_Trsf* trsf) {
    return new BRepBuilderAPI_Transform(*w, *trsf);
}


extern "C" const TopoDS_Shape& brep_builderapi_transform_shape(BRepBuilderAPI_Transform *b_rep_transform) {
    return b_rep_transform->Shape();
}

extern "C" TopoDS_Wire& topo_ds_wire(TopoDS_Shape& shape) {
    return TopoDS::Wire(shape);
}

extern "C" TopoDS_Face* brep_builderapi_make_face(TopoDS_Wire& wire) {
    return new TopoDS_Face(BRepBuilderAPI_MakeFace(wire));
}

extern "C" TopoDS_Shape* brep_primapi_make_prism(TopoDS_Face& face, gp_Vec& normal) {
    return new TopoDS_Shape(BRepPrimAPI_MakePrism(face, normal));
}


extern "C" BRepFilletAPI_MakeFillet* brep_filletapi_make_fillet(TopoDS_Shape& body) {
    return new BRepFilletAPI_MakeFillet(body);
}

extern "C" TopExp_Explorer* top_exp_explorer(const TopoDS_Shape& S, const TopAbs_ShapeEnum ToFind, const TopAbs_ShapeEnum ToAvoid = TopAbs_SHAPE) {
    return new TopExp_Explorer(S, ToFind, ToAvoid);
}

extern "C" bool top_exp_explorer_more(TopExp_Explorer& explorer) {
    return explorer.More();
}

extern "C" TopoDS_Shape* top_exp_explorer_current(TopExp_Explorer& explorer) {
    return new TopoDS_Shape(explorer.Current());
}

extern "C" void top_exp_explorer_next(TopExp_Explorer& explorer) {
    return explorer.Next();
}

extern "C" TopoDS_Edge& topo_ds_edge(TopoDS_Shape& shape) {
    return TopoDS::Edge(shape);
}

extern "C" void brep_filletapi_make_fillet_add(BRepFilletAPI_MakeFillet& make_fillet, Standard_Real r, TopoDS_Edge& edge) {
    return make_fillet.Add(r, edge);
}

extern "C" TopoDS_Shape* brep_filletapi_make_fillet_shape(BRepFilletAPI_MakeFillet& make_fillet) {
    return new TopoDS_Shape(make_fillet.Shape());
}

extern "C" const gp_Dir* gp_dz() {
    return new gp_Dir(gp::DZ());
}

extern "C" const gp_Ax2* gp_ax2(gp_Pnt& loc, gp_Dir& dir) {
    return new gp_Ax2(loc, dir);
}

extern "C" BRepPrimAPI_MakeCylinder* brep_primapi_make_cylinder(const gp_Ax2& Axes, const Standard_Real R, const Standard_Real H) {
    return new BRepPrimAPI_MakeCylinder(Axes, R, H);
}

extern "C" TopoDS_Shape* brep_primapi_make_cylinder_shape(BRepPrimAPI_MakeCylinder& mk) {
    return new TopoDS_Shape(mk.Shape());
}

extern "C" TopoDS_Shape* brep_algoapi_fuse(TopoDS_Shape& s1, TopoDS_Shape& s2) {
    return new TopoDS_Shape(BRepAlgoAPI_Fuse(s1, s2));
}

extern "C" Handle(Geom_Surface)* brep_tool_surface(TopoDS_Face& face) {
    return new Handle(Geom_Surface)(BRep_Tool::Surface(face));
}

extern "C" bool geom_surface_is_geom_plane(Handle(Geom_Surface)&surface) {
    return surface->DynamicType() == STANDARD_TYPE(Geom_Plane);
}

extern "C" Handle(Geom_Plane)* downcast_geom_plane(Handle(Geom_Surface)&surface) {
    return new Handle(Geom_Plane)(Handle(Geom_Plane)::DownCast(surface));
}

extern "C" gp_Pnt* geom_plane_location(Handle(Geom_Plane)&plane) {
    return new gp_Pnt(plane->Location());
}

extern "C" BRepOffsetAPI_MakeThickSolid* brep_offset_api_make_thick_solid() {
    return new BRepOffsetAPI_MakeThickSolid();
}

extern "C" void brep_offset_api_make_thick_solid_join(BRepOffsetAPI_MakeThickSolid& thick_solid, TopTools_ListOfShape& face_to_remove, TopoDS_Shape& shape, Standard_Real thickness, Standard_Real tol) {
    thick_solid.MakeThickSolidByJoin(shape, face_to_remove, thickness, tol);
}

extern "C" TopoDS_Shape* brep_offset_api_make_thick_solid_shape(BRepOffsetAPI_MakeThickSolid& thick_solid) {
    return new TopoDS_Shape(thick_solid.Shape());
}

extern "C" Handle(Geom_CylindricalSurface)* geom_cylindrical_surface_create(const gp_Ax3& ax2, const Standard_Real radius) {
    return new Handle(Geom_CylindricalSurface)(new Geom_CylindricalSurface(ax2, radius));
}

extern "C" gp_Pnt2d *make_gp_pnt2d(const Standard_Real theXp, const Standard_Real theYp) {
    return new gp_Pnt2d(theXp, theYp);
}

extern "C" gp_Dir2d *make_gp_dir2d(const Standard_Real theXp, const Standard_Real theYp) {
    return new gp_Dir2d(theXp, theYp);
}

extern "C" Handle(Geom2d_Ellipse)* geom2d_ellipse_create(const gp_Ax2d& MajorAxis, const Standard_Real MajorRadius, const Standard_Real MinorRadius, const Standard_Boolean Sense = Standard_True) {
    return new Handle(Geom2d_Ellipse)(new Geom2d_Ellipse(MajorAxis, MajorRadius, MinorRadius, Sense));
}

extern "C" Handle(Geom2d_TrimmedCurve)* geom2d_trimmed_curve_create(const Handle(Geom2d_Curve)& C, const Standard_Real U1, const Standard_Real U2, const Standard_Boolean Sense = Standard_True, const Standard_Boolean theAdjustPeriodic = Standard_True) {
    return new Handle(Geom2d_TrimmedCurve)(new Geom2d_TrimmedCurve(C, U1, U2, Sense, theAdjustPeriodic));
}

extern "C" gp_Pnt2d* geom2d_ellipse_value(Handle(Geom2d_Ellipse)& geom2d_ellipse, const Standard_Real U) {
    return new gp_Pnt2d(geom2d_ellipse->Value(U));
}

extern "C" Handle(Geom2d_TrimmedCurve)* gce2d_make_segment(const gp_Pnt2d& P1, const gp_Pnt2d& P2) {
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeSegment(P1, P2));
}

extern "C" void brep_lib_build_curves_3d(TopoDS_Wire& w1) {
    BRepLib::BuildCurves3d(w1);
}

extern "C" BRepOffsetAPI_ThruSections* brep_tool_thru_sections(const Standard_Boolean isSolid = Standard_False, const Standard_Boolean ruled = Standard_False, const Standard_Real pres3d = 1.0e-06) {
    return new BRepOffsetAPI_ThruSections(isSolid, ruled, pres3d);
}

extern "C" void brep_tool_thru_sections_add_wire(BRepOffsetAPI_ThruSections* thru_sections, const TopoDS_Wire& w) {
    thru_sections->AddWire(w);
}

extern "C" void brep_tool_thru_sections_check_compatibility(BRepOffsetAPI_ThruSections* thru_sections, const Standard_Boolean b) {
    thru_sections->CheckCompatibility(b);
}

extern "C" TopoDS_Shape* brep_tool_thru_sections_shape(BRepOffsetAPI_ThruSections* thru_sections) {
    return new TopoDS_Shape(thru_sections->Shape());
}

extern "C" TopoDS_Compound* topods_compound_create() {
    return new TopoDS_Compound();
}

extern "C" BRep_Builder* brep_builder_create() {
    return new BRep_Builder();
}

extern "C" void brep_builder_make_compound(BRep_Builder& b, TopoDS_Compound& c) {
    b.MakeCompound(c);
}

extern "C" void brep_builder_add(BRep_Builder& b, TopoDS_Compound& c, TopoDS_Shape& s) {
    b.Add(c, s);
}
