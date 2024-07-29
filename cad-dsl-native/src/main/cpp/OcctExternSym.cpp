//
// Created by auo on 29/07/24.
//

#include "OcctExternSym.h"

#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_Transform.hxx>
#include <BRepPrimAPI_MakePrism.hxx>
#include <GC_MakeArcOfCircle.hxx>
#include <GC_MakeSegment.hxx>
#include <Geom_TrimmedCurve.hxx>
#include <Standard_Handle.hxx>
#include <gp_Pnt.hxx>
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