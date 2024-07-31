

void* cMakeBottle(const double, const double, const double);
int visualize(void*);

#define gp_Pnt void
#define gp_Vec void
#define gp_Ax1 void
#define gp_Trsf void
#define Geom_TrimmedCurve void
#define Handle(a) a
#define TopoDS_Edge void
#define TopoDS_Wire void
#define TopoDS_Face void
#define TopoDS_Shape void
#define BRepBuilderAPI_MakeWire void
#define BRepBuilderAPI_Transform void
#define Standard_Real double

gp_Pnt *make_gp_pnt(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);
gp_Vec *make_gp_vec(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);
Standard_Real gp_pnt_x(gp_Pnt *pnt);
Standard_Real gp_pnt_y(gp_Pnt *pnt);
Standard_Real gp_pnt_z(gp_Pnt *pnt);
Handle(Geom_TrimmedCurve)* gc_make_arc_of_circle(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3);
Handle(Geom_TrimmedCurve)* gc_make_segment(gp_Pnt *pnt1, gp_Pnt *pnt2);
const TopoDS_Edge* brep_builderapi_make_edge(Handle(Geom_TrimmedCurve)* segment);
const TopoDS_Wire* brep_builderapi_make_wire_topo_ds_wire(TopoDS_Edge* e1, TopoDS_Edge* e2, TopoDS_Edge* e3);
const TopoDS_Wire* brep_builderapi_make_wire_topo_ds_wire2(BRepBuilderAPI_MakeWire* make_wire);
const BRepBuilderAPI_MakeWire* brep_builderapi_make_wire_new(void);
void brep_builderapi_wire_add(BRepBuilderAPI_MakeWire* mw, TopoDS_Wire* wire);
const gp_Ax1* gp_ox(void);
gp_Trsf* gp_trsf(void);
void gp_trsf_set_mirror(gp_Trsf* trsf, gp_Ax1* ax1);
BRepBuilderAPI_Transform* brep_builderapi_transform(const TopoDS_Wire* w, gp_Trsf* trsf);
const TopoDS_Shape* brep_builderapi_transform_shape(BRepBuilderAPI_Transform *b_rep_transform);
TopoDS_Wire* topo_ds_wire(TopoDS_Shape* shape);
TopoDS_Face* brep_builderapi_make_face_from_wire(TopoDS_Wire* wire);
TopoDS_Face* brep_builderapi_make_face_from_face(TopoDS_Face* face);
TopoDS_Shape* brep_primapi_make_prism(TopoDS_Face* face, gp_Vec* normal);

#define BRepFilletAPI_MakeFillet void
#define TopExp_Explorer void
#define TopAbs_ShapeEnum int
#define bool unsigned int
#define Standard_Boolean unsigned int
#define gp_Dir void
#define gp_Ax2 void
#define BRepPrimAPI_MakeCylinder void
#define Geom_Surface void
#define Geom_Plane void
#define BRepOffsetAPI_MakeThickSolid void
#define TopTools_ListOfShape void
#define Geom_CylindricalSurface void
#define gp_Ax3 void
#define gp_Pnt2d void
#define gp_Dir2d void
#define Geom2d_Ellipse void
#define gp_Ax2d void
#define Standard_True 1
#define Standard_False 0
#define Geom2d_TrimmedCurve void
#define Geom2d_Curve void
#define BRepOffsetAPI_ThruSections void
#define TopoDS_Compound void
#define BRep_Builder void

BRepFilletAPI_MakeFillet* brep_filletapi_make_fillet(TopoDS_Shape* body);

TopExp_Explorer* top_exp_explorer(const TopoDS_Shape* S, int ToFind, int ToAvoid);

bool top_exp_explorer_more(TopExp_Explorer* explorer);

TopoDS_Shape* top_exp_explorer_current(TopExp_Explorer* explorer);

TopoDS_Shape* top_exp_explorer_current_face(TopExp_Explorer* explorer);

void top_exp_explorer_next(TopExp_Explorer* explorer);

TopoDS_Edge* topo_ds_edge(TopoDS_Shape* shape);

void brep_filletapi_make_fillet_add(BRepFilletAPI_MakeFillet* make_fillet, Standard_Real r, TopoDS_Edge* edge);

TopoDS_Shape* brep_filletapi_make_fillet_shape(BRepFilletAPI_MakeFillet* make_fillet);

const gp_Dir* gp_dz(void);

const gp_Ax2* gp_ax2(gp_Pnt* loc, gp_Dir* dir);

BRepPrimAPI_MakeCylinder* brep_primapi_make_cylinder(const gp_Ax2* Axes, const Standard_Real R, const Standard_Real H);

TopoDS_Shape* brep_primapi_make_cylinder_shape(BRepPrimAPI_MakeCylinder* mk);

TopoDS_Shape* brep_algoapi_fuse(TopoDS_Shape* s1, TopoDS_Shape* s2);

Handle(Geom_Surface)* brep_tool_surface(TopoDS_Face* face);

int geom_surface_is_geom_plane(Handle(Geom_Surface)*surface);

Handle(Geom_Plane)* downcast_geom_plane(Handle(Geom_Surface)*surface);

gp_Pnt* geom_plane_location(Handle(Geom_Plane)*plane);


TopTools_ListOfShape* top_tools_list_of_shape(void);
void top_tools_list_of_shape_append(TopTools_ListOfShape* l, TopoDS_Face* face);

BRepOffsetAPI_MakeThickSolid* brep_offset_api_make_thick_solid(void);

void brep_offset_api_make_thick_solid_join(BRepOffsetAPI_MakeThickSolid* thick_solid, TopTools_ListOfShape* face_to_remove, TopoDS_Shape* shape, Standard_Real thickness, Standard_Real tol);

TopoDS_Shape* brep_offset_api_make_thick_solid_shape(BRepOffsetAPI_MakeThickSolid* thick_solid);

Handle(Geom_CylindricalSurface)* geom_cylindrical_surface_create(const gp_Ax3* ax2, const Standard_Real radius);

gp_Pnt2d *make_gp_pnt2d(const Standard_Real theXp, const Standard_Real theYp);

gp_Dir2d *make_gp_dir2d(const Standard_Real theXp, const Standard_Real theYp);
Handle(Geom2d_Ellipse)* geom2d_ellipse_create(const gp_Ax2d* MajorAxis, const Standard_Real MajorRadius, const Standard_Real MinorRadius, const Standard_Boolean Sense);
Handle(Geom2d_TrimmedCurve)* geom2d_trimmed_curve_create(const Handle(Geom2d_Curve)* C, const Standard_Real U1, const Standard_Real U2, const Standard_Boolean Sense, const Standard_Boolean theAdjustPeriodic);
gp_Pnt2d* geom2d_ellipse_value(Handle(Geom2d_Ellipse)* geom2d_ellipse, const Standard_Real U);
Handle(Geom2d_TrimmedCurve)* gce2d_make_segment(const gp_Pnt2d* P1, const gp_Pnt2d* P2);
void brep_lib_build_curves_3d(TopoDS_Wire* w1);
BRepOffsetAPI_ThruSections* brep_tool_thru_sections(const Standard_Boolean isSolid, const Standard_Boolean ruled, const Standard_Real pres3d);
void brep_tool_thru_sections_add_wire(BRepOffsetAPI_ThruSections* thru_sections, const TopoDS_Wire* w);
void brep_tool_thru_sections_check_compatibility(BRepOffsetAPI_ThruSections* thru_sections, const Standard_Boolean b);
TopoDS_Shape* brep_tool_thru_sections_shape(BRepOffsetAPI_ThruSections* thru_sections);
TopoDS_Compound* topods_compound_create(void);
BRep_Builder* brep_builder_create(void);
void brep_builder_make_compound(BRep_Builder* b, TopoDS_Compound* c);
void brep_builder_add(BRep_Builder* b, TopoDS_Compound* c, TopoDS_Shape* s);
TopoDS_Face* topods_face_new(void);
void topods_shape_assignment_operator(TopoDS_Shape** left, TopoDS_Shape* right);
