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

OcctExternSym::OcctExternSym() {
}

extern "C" gp_Pnt *gp_pnt_new(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    return new gp_Pnt(theXp, theYp, theZp);
}

extern "C" void gp_pnt_delete(gp_Pnt *pnt) {
    delete pnt;
}

extern "C" gp_Vec *gp_vec_new(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp) {
    return new gp_Vec(theXp, theYp, theZp);
}

extern "C" void gp_vec_delete(gp_Vec *pnt) {
    delete pnt;
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

extern "C" Handle(Geom_TrimmedCurve) *gc_make_arc_of_circle(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3) {
    return new Handle(Geom_TrimmedCurve)(GC_MakeArcOfCircle(*pnt1, *pnt2, *pnt3).Value());
}

extern "C" void gc_delete_arc_of_circle(Handle(Geom_TrimmedCurve)* ptr) {
    delete ptr;
}

extern "C" Handle(Geom_TrimmedCurve) *gc_make_segment(gp_Pnt *pnt1, gp_Pnt *pnt2) {
    return new Handle(Geom_TrimmedCurve)(GC_MakeSegment(*pnt1, *pnt2).Value());
}

extern "C" void gc_delete_segment(Handle(Geom_TrimmedCurve)* ptr) {
    delete ptr;
}

extern "C" const TopoDS_Edge *brep_builderapi_make_edge(Handle(Geom_TrimmedCurve) &segment) {
    return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(segment).Edge());
}

extern "C" void brep_builderapi_delete_edge(TopoDS_Edge *ptr) {
    delete ptr;
}

extern "C" const BRepBuilderAPI_MakeEdge *brep_builderapi_make_edge_from_pts(gp_Pnt& from, gp_Pnt& to) {
    return new BRepBuilderAPI_MakeEdge(from, to);
}

extern "C" const BRepBuilderAPI_MakeWire *brep_builderapi_makewire_new() {
    return new BRepBuilderAPI_MakeWire();
}

extern "C" void brep_builderapi_make_wire_add(BRepBuilderAPI_MakeWire& wireMaker, BRepBuilderAPI_MakeEdge& edge) {
    wireMaker.Add(edge);
}

extern "C" const TopoDS_Wire *brep_builderapi_make_wire_topo_ds_wire(TopoDS_Edge e1, TopoDS_Edge e2, TopoDS_Edge e3) {
    return new TopoDS_Wire(BRepBuilderAPI_MakeWire(e1, e2, e3).Wire());
}

extern "C" const TopoDS_Wire &brep_builderapi_make_wire_topo_ds_wire2(BRepBuilderAPI_MakeWire &make_wire) {
    return make_wire.Wire();
}

extern "C" const BRepBuilderAPI_MakeWire *brep_builderapi_make_wire_new() {
    return new BRepBuilderAPI_MakeWire();
}

extern "C" void brep_builderapi_wire_add(BRepBuilderAPI_MakeWire &mw, TopoDS_Wire &wire) {
    mw.Add(wire);
}

extern "C" void brep_builderapi_wire_add_edge(BRepBuilderAPI_MakeWire &mw, TopoDS_Edge &edge) {
    mw.Add(edge);
}

extern "C" void brep_builderapi_wire_add_makeedge(BRepBuilderAPI_MakeWire &mw, BRepBuilderAPI_MakeEdge &edge) {
    mw.Add(edge);
}

extern "C" void brep_builderapi_wire_add_Listofshape(BRepBuilderAPI_MakeWire &mw, TopTools_ListOfShape &listOfShape) {
    mw.Add(listOfShape);
}

extern "C" const gp_Ax1 &gp_ox() {
    return gp::OX();
}

extern "C" gp_Trsf *gp_trsf() {
    return new gp_Trsf();
}

extern "C" void gp_trsf_set_mirror(gp_Trsf *trsf, gp_Ax1 *ax1) {
    trsf->SetMirror(*ax1);
}

extern "C" BRepBuilderAPI_Transform *brep_builderapi_transform(const TopoDS_Wire *w, gp_Trsf *trsf) {
    return new BRepBuilderAPI_Transform(*w, *trsf);
}


// extern "C" const TopoDS_Shape &brep_builderapi_transform_shape(BRepBuilderAPI_Transform *b_rep_transform) {
//     return b_rep_transform->Shape();
// }

extern "C" TopoDS_Wire &topo_ds_wire(TopoDS_Shape &shape) {
    return TopoDS::Wire(shape);
}

extern "C" TopoDS_Face *brep_builderapi_make_face_from_wire(TopoDS_Wire &wire) {
    return new TopoDS_Face(BRepBuilderAPI_MakeFace(wire).Face());
}

extern "C" TopoDS_Face *brep_builderapi_make_face_from_makewire(BRepBuilderAPI_MakeWire &wire) {
    return new TopoDS_Face(BRepBuilderAPI_MakeFace(wire).Face());
}

extern "C" TopoDS_Face *brep_builderapi_make_face_from_face(TopoDS_Face &face) {
    return new TopoDS_Face(BRepBuilderAPI_MakeFace(face).Face());
}

extern "C" TopoDS_Face *topods_face_new() {
    return new TopoDS_Face();
}

extern "C" TopoDS_Shape *brep_primapi_make_prism(TopoDS_Face &face, gp_Vec &normal) {
    return new TopoDS_Shape(BRepPrimAPI_MakePrism(face, normal));
}

extern "C" BRepFilletAPI_MakeFillet *brep_filletapi_make_fillet(TopoDS_Shape &body) {
    return new BRepFilletAPI_MakeFillet(body);
}

TopAbs_ShapeEnum TopAbs_ShapeEnumFromOrdinal(int ordinal) {
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

extern "C" TopExp_Explorer *top_exp_explorer(const TopoDS_Shape &S, const /*TopAbs_ShapeEnum*/int ToFind,
                                             const int ToAvoid = TopAbs_SHAPE) {
    return new TopExp_Explorer(S, TopAbs_ShapeEnumFromOrdinal(ToFind), TopAbs_ShapeEnumFromOrdinal(ToAvoid));
}

extern "C" bool top_exp_explorer_more(TopExp_Explorer &explorer) {
    return explorer.More();
}

extern "C" TopoDS_Shape *top_exp_explorer_current(TopExp_Explorer &explorer) {
    return new TopoDS_Shape(explorer.Current());
}

extern "C" TopoDS_Shape *top_exp_explorer_current_face(TopExp_Explorer &explorer) {
    return new TopoDS_Face(TopoDS::Face(explorer.Current()));
}

extern "C" void top_exp_explorer_next(TopExp_Explorer &explorer) {
    return explorer.Next();
}

extern "C" TopoDS_Edge &topo_ds_edge(TopoDS_Shape &shape) {
    return TopoDS::Edge(shape);
}

extern "C" void brep_filletapi_make_fillet_add(BRepFilletAPI_MakeFillet &make_fillet, Standard_Real r,
                                               TopoDS_Edge &edge) {
    return make_fillet.Add(r, edge);
}

// extern "C" TopoDS_Shape *brep_filletapi_make_fillet_shape(BRepFilletAPI_MakeFillet &make_fillet) {
//     return new TopoDS_Shape(make_fillet.Shape());
// }

extern "C" const gp_Dir *gp_dz() {
    return new gp_Dir(gp::DZ());
}

extern "C" const gp_Ax2 *gp_ax2(gp_Pnt &loc, gp_Dir &dir) {
    return new gp_Ax2(loc, dir);
}

extern "C" const gp_Ax2 *gp_ax2_dz() {
    return new gp_Ax2(gp_Pnt(0.0, 0.0, 0.0), gp::DZ());
}

extern "C" BRepPrimAPI_MakeCylinder *brep_primapi_make_cylinder(const gp_Ax2 &Axes, const Standard_Real R,
                                                                const Standard_Real H) {
    return new BRepPrimAPI_MakeCylinder(Axes, R, H);
}

extern "C" BRepPrimAPI_MakeBox *brep_primapi_make_box(const Standard_Real x, const Standard_Real y,
                                                      const Standard_Real z) {
    return new BRepPrimAPI_MakeBox(x, y, z);
}

extern "C" TopoDS_Shape *brep_builderapi_make_shape(BRepBuilderAPI_MakeShape &mk) {
    return new TopoDS_Shape(mk.Shape());
}

extern "C" Handle(Geom_Surface) *brep_tool_surface(TopoDS_Face &face) {
    return new Handle(Geom_Surface)(BRep_Tool::Surface(face));
}

extern "C" int geom_surface_is_geom_plane(Handle(Geom_Surface) &surface) {
    return surface->DynamicType() == STANDARD_TYPE(Geom_Plane) ? 1 : 0;
}

extern "C" Handle(Geom_Plane) *downcast_geom_plane(Handle(Geom_Surface) &surface) {
    return new Handle(Geom_Plane)(Handle(Geom_Plane)::DownCast(surface));
}

extern "C" gp_Pnt *geom_plane_location(Handle(Geom_Plane) &plane) {
    return new gp_Pnt(plane->Location());
}

extern "C" TopTools_ListOfShape *top_tools_list_of_shape() {
    return new TopTools_ListOfShape();
}

extern "C" void top_tools_list_of_shape_delete(TopTools_ListOfShape* ptr) {
    delete ptr;
}

extern "C" void top_tools_list_of_shape_append(TopTools_ListOfShape *l, TopoDS_Shape &face) {
    l->Append(face);
}

extern "C" void top_tools_list_of_shape_append_edge(TopTools_ListOfShape *l, TopoDS_Edge &face) {
    l->Append(face);
}

extern "C" void top_tools_list_of_shape_append_makeedge(TopTools_ListOfShape *l, BRepBuilderAPI_MakeEdge &face) {
    l->Append(face);
}

extern "C" BRepOffsetAPI_MakeThickSolid *brep_offset_api_make_thick_solid() {
    return new BRepOffsetAPI_MakeThickSolid();
}

extern "C" void brep_offset_api_make_thick_solid_join(BRepOffsetAPI_MakeThickSolid *thick_solid, TopoDS_Shape *shape,
                                                      const TopTools_ListOfShape *face_to_remove,
                                                      Standard_Real thickness, Standard_Real tol) {
    thick_solid->MakeThickSolidByJoin(*shape, *face_to_remove, thickness, tol);
}

//
// extern "C" TopoDS_Shape *brep_offset_api_make_thick_solid_shape(BRepOffsetAPI_MakeThickSolid *thick_solid) {
//     return new TopoDS_Shape(thick_solid->Shape());
// }

extern "C" Handle(Geom_CylindricalSurface) *geom_cylindrical_surface_create(
    const gp_Ax3 &ax2, const Standard_Real radius) {
    return new Handle(Geom_CylindricalSurface)(new Geom_CylindricalSurface(ax2, radius));
}

extern "C" gp_Pnt2d *make_gp_pnt2d(const Standard_Real theXp, const Standard_Real theYp) {
    return new gp_Pnt2d(theXp, theYp);
}

extern "C" gp_Dir2d *make_gp_dir2d(const Standard_Real theXp, const Standard_Real theYp) {
    return new gp_Dir2d(theXp, theYp);
}

extern "C" Handle(Geom2d_Ellipse) *geom2d_ellipse_create(const gp_Ax2d &MajorAxis, const Standard_Real MajorRadius,
                                                         const Standard_Real MinorRadius,
                                                         const Standard_Boolean Sense = Standard_True) {
    return new Handle(Geom2d_Ellipse)(new Geom2d_Ellipse(MajorAxis, MajorRadius, MinorRadius, Sense));
}

extern "C" Handle(Geom2d_TrimmedCurve) *geom2d_trimmed_curve_create(const Handle(Geom2d_Curve) &C,
                                                                    const Standard_Real U1, const Standard_Real U2,
                                                                    const Standard_Boolean Sense = Standard_True,
                                                                    const Standard_Boolean theAdjustPeriodic =
                                                                        Standard_True) {
    return new Handle(Geom2d_TrimmedCurve)(new Geom2d_TrimmedCurve(C, U1, U2, Sense, theAdjustPeriodic));
}

extern "C" gp_Pnt2d *geom2d_ellipse_value(Handle(Geom2d_Ellipse) &geom2d_ellipse, const Standard_Real U) {
    return new gp_Pnt2d(geom2d_ellipse->Value(U));
}

extern "C" Handle(Geom2d_TrimmedCurve) *gce2d_make_segment(const gp_Pnt2d &P1, const gp_Pnt2d &P2) {
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeSegment(P1, P2));
}

extern "C" void brep_lib_build_curves_3d(TopoDS_Wire &w1) {
    BRepLib::BuildCurves3d(w1);
}

extern "C" BRepOffsetAPI_ThruSections *brep_tool_thru_sections(const Standard_Boolean isSolid = Standard_False,
                                                               const Standard_Boolean ruled = Standard_False,
                                                               const Standard_Real pres3d = 1.0e-06) {
    return new BRepOffsetAPI_ThruSections(isSolid, ruled, pres3d);
}

extern "C" void brep_tool_thru_sections_add_wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire &w) {
    thru_sections->AddWire(w);
}

extern "C" void brep_tool_thru_sections_check_compatibility(BRepOffsetAPI_ThruSections *thru_sections,
                                                            const Standard_Boolean b) {
    thru_sections->CheckCompatibility(b);
}

// extern "C" TopoDS_Shape *brep_tool_thru_sections_shape(BRepOffsetAPI_ThruSections *thru_sections) {
//     return new TopoDS_Shape(thru_sections->Shape());
// }

extern "C" TopoDS_Compound *topods_compound_create() {
    return new TopoDS_Compound();
}

extern "C" BRep_Builder *brep_builder_create() {
    return new BRep_Builder();
}

extern "C" void brep_builder_make_compound(BRep_Builder &b, TopoDS_Compound &c) {
    b.MakeCompound(c);
}

extern "C" void brep_builder_add(BRep_Builder &b, TopoDS_Compound &c, TopoDS_Shape &s) {
    b.Add(c, s);
}

extern "C" bool dumpShape(const TopoDS_Shape &shape, const Standard_Integer width, const Standard_Integer height,
                          const char *fileName) {
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

extern "C" TopoDS_Shape *make_hole(const TopoDS_Shape &shape, const gp_Ax1 &ax1, const Standard_Real Radius,
                                   const Standard_Real PFrom, const Standard_Real PTo) {
    std::cout << "PFrom = " << PFrom << "PTo = " << PTo << " " << (PFrom > PTo) << std::endl;
    BRepFeat_MakeCylindricalHole makeCylindrical;
    makeCylindrical.Init(shape, ax1);
    if (PFrom > PTo)
        makeCylindrical.Perform(Radius);
    else
        makeCylindrical.Perform(Radius, PFrom, PTo);
    return new TopoDS_Shape(makeCylindrical.Shape());
}

extern "C" TopoDS_Shape *make_hole_blind(const TopoDS_Shape &shape, const gp_Ax1 &ax1, const Standard_Real Radius,
                                         const Standard_Real Length) {
    std::cout << "Length = " << Length << std::endl;
    BRepFeat_MakeCylindricalHole makeCylindrical;
    makeCylindrical.Init(shape, ax1);
    makeCylindrical.PerformBlind(Radius, Length);
    makeCylindrical.Build();
    return new TopoDS_Shape(makeCylindrical.Shape());
}

extern "C" gp_Ax1 *gp_ax1_new(const gp_Pnt &theP, const gp_Dir &theV) {
    return new gp_Ax1(theP, theV);
}

extern "C" gp_Dir *gp_dir_new(const Standard_Real theXv, const Standard_Real theYv, const Standard_Real theZv) {
    return new gp_Dir(theXv, theYv, theZv);
}

extern "C" gp_Dir *gp_dir_normal_to_face(const TopoDS_Face &aCurrentFace) {
    Standard_Real umin, umax, vmin, vmax;
    BRepTools::UVBounds(aCurrentFace, umin, umax, vmin, vmax);
    Handle(Geom_Surface) aSurface = BRep_Tool::Surface(aCurrentFace);
    GeomLProp_SLProps props(aSurface, (umin + umax) / 2, (vmin + vmax) / 2, 1, 0.01);
    gp_Dir n = props.Normal();
    std::cout << "gp_dir_normal_to_face: umin = " << umin << " umax = " << umax << " vmin = " << vmin << " vmax = " <<
        vmax << " n.X " << n.X() << " n.Y " << n.Y() << " n.Z " << n.Z() << std::endl;
    return new gp_Dir(n);
}

extern "C" gp_Pnt *gp_pnt_center_of_mass(const TopoDS_Shape &myShape) {
    GProp_GProps massProps;
    BRepGProp::SurfaceProperties(myShape, massProps);
    return new gp_Pnt(massProps.CentreOfMass());
}


extern "C" Standard_Real gp_dir_x(gp_Dir *dir) {
    return dir->X();
}

extern "C" Standard_Real gp_dir_y(gp_Dir *dir) {
    return dir->Y();
}

extern "C" Standard_Real gp_dir_z(gp_Pnt *dir) {
    return dir->Z();
}


extern "C" TopoDS_Shape *brep_builderapi_make_shere(const gp_Ax2 &origin, const Standard_Real radius,
                                                    const Standard_Real angle1, const Standard_Real angle2) {
    return new TopoDS_Shape(BRepPrimAPI_MakeSphere(origin,
                                                   radius,
                                                   angle1,
                                                   angle2));
}

extern "C" TopoDS_Shape *brep_builderapi_make_cylinder(const gp_Ax2 &origin, const Standard_Real radius,
                                                       const Standard_Real height) {
    return new TopoDS_Shape(BRepPrimAPI_MakeCylinder(origin,
                                                     radius,
                                                     height).Shape());
}

extern "C" gp_Trsf *gp_trsf_new() {
    return new gp_Trsf();
}

extern "C" void gp_trsf_set_translation(gp_Trsf &gp_trsf, const gp_Vec &translation) {
    return gp_trsf.SetTranslation(translation);
}

extern "C" TopoDS_Shape *brep_builderapi_transform_shape(const TopoDS_Shape &shape, const gp_Trsf &gp_trsf,
                                                         const Standard_Boolean theCopyGeom) {
    return new TopoDS_Shape(BRepBuilderAPI_Transform(shape, gp_trsf, theCopyGeom).Shape());
}

extern "C" TopoDS_Shape *brep_algoapi_cut_ds_shape(TopoDS_Shape &result, TopoDS_Shape &tool) {
    const auto cut = new BRepAlgoAPI_Cut(result, tool);
    cut->Build();
    cut->SimplifyResult();
    return new TopoDS_Shape(cut->Shape());
}

extern "C" TopoDS_Shape *brep_algoapi_fuse(TopoDS_Shape &s1, TopoDS_Shape &s2) {
    const auto fuse = new BRepAlgoAPI_Fuse(s1, s2);
    fuse->Build();
    fuse->SimplifyResult();
    return new TopoDS_Shape(fuse->Shape());
}

extern "C" TopoDS_Shape *brep_algoapi_cut(TopoDS_Shape &result, TopTools_ListOfShape &aLT) {
    Standard_Boolean bRunParallel;
    Standard_Real aFuzzyValue;
    BRepAlgoAPI_Cut aBuilder;
    // perpare the arguments
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

extern "C" TopoDS_Shape *brep_primapi_make_thorus(const gp_Ax2 &origin, const Standard_Real radius1,
                                                  const Standard_Real radius2) {
    return new TopoDS_Shape(BRepPrimAPI_MakeTorus(origin,
                                                  radius1,
                                                  radius2).Shape());
}

extern "C" void analyze(const TopoDS_Shape &myShape) {
    BRepCheck_Analyzer checker(myShape);
    if (checker.IsValid()) {
        std::cout << "Valide Shaoe " << std::endl << std::flush;
        return;
    }
    std::cout << "Not valid Shaoe " << std::endl << std::flush;
    Handle(BRepCheck_Result) r = checker.Result(myShape);
    if (r->IsBlind())
        std::cout << "Shape is Blind" << std::endl;
    if (r->IsMinimum())
        std::cout << "Shape is Min" << std::endl;
    //
    // BRepCheck_HListOfStatus aSL = r->Status();
    // for(BRepCheck_ListIteratorOfListOfStatus it = aSL.(); it != aSL.end(); ++it)
    // {
    //     std::cout << "Status " << it.Index() << std::endl;
    //     std::cout << "Shape " << it.Index() << std::endl;
    // }
}
//
// extern "C" TopTools_ListOfShape *toptools_listofshape_new() {
//     return new TopTools_ListOfShape();
// }
//
// extern "C" void toptools_listofshape_append(TopTools_ListOfShape &ls, const TopoDS_Shape &myShape) {
//     ls.Append(myShape);
// }

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


extern "C" TopoDS_Shape* brep_primapi_makerevol(TopoDS_Face& face, gp_Ax1& ax1) {
    return new TopoDS_Shape(BRepPrimAPI_MakeRevol(face, ax1).Shape());
}

extern "C" void deleteVoid(void *ptr) {
    delete ptr;
}


extern "C" gp_Circ2d* gp_circ2d_new(gp_Ax2d &ax2d, Standard_Real theRadius) {
    return new gp_Circ2d(ax2d, theRadius);
}

extern "C" Handle(Geom2d_Circle)* gce2d_makecircle(gp_Circ2d &ptr) {
    return new Handle(Geom2d_Circle)(GCE2d_MakeCircle(ptr));
}

extern "C" Geom2dAPI_InterCurveCurve* geom2dapi_intercurvecurve_new(const Handle(Geom2d_Curve)& C1, const Handle(Geom2d_Curve)& C2) {
    return new Geom2dAPI_InterCurveCurve(C1, C2);
}

extern "C" Standard_Integer geom2dapi_intercurvecurve_nbpoints(const Geom2dAPI_InterCurveCurve &inter_curve_curve) {
    return inter_curve_curve.NbPoints();
}

extern "C" Handle(Geom2d_TrimmedCurve)* gce2d_makearcofcircle(gp_Circ2d& circ2d, gp_Pnt2d& p1, gp_Pnt2d& p2) {
    return new Handle(Geom2d_TrimmedCurve)(GCE2d_MakeArcOfCircle(circ2d, p1, p2));
}

extern "C" void geom2d_trimmedcurve_mirror(Geom2d_TrimmedCurve& curve, gp_Ax2d& ax2d) {
    curve.Mirror(ax2d);
}

extern "C" void geom2d_trimmedcurve_reverse(Geom2d_TrimmedCurve& curve) {
    curve.Reverse();
}

extern "C" gp_Pnt2d* geom2d_trimmedcurve_endpoint(Geom2d_TrimmedCurve& curve) {
    return new gp_Pnt2d(curve.EndPoint());
}

extern "C" gp_Pnt2d* geom2d_trimmedcurve_startpoint(Geom2d_TrimmedCurve& curve) {
    return new gp_Pnt2d(curve.StartPoint());
}
