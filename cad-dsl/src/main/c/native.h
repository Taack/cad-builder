

void* cMakeBottle(const double, const double, const double);
int pfff(void*);

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
TopoDS_Face* brep_builderapi_make_face(TopoDS_Wire* wire);
TopoDS_Shape* brep_primapi_make_prism(TopoDS_Face* face, gp_Vec* normal);