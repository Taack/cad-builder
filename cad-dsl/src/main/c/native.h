/*
Replacements from Cpp code to C header source:
123456789012345
 \{[^}]*[^ ]\}
 *
123456789012
extern \"C\"

*
*

),]*



*/

int visualize(void*);

#define gp_Dir2d void
#define gp_Pln void
#define gp_Pnt void
#define gp_Vec void
#define gp_Vec2d void
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
#define gp_Ax2d void
#define gp_Pnt2d void
#define Geom_Curve void
#define Geom2d_Curve void
#define Geom_Surface void
#define BRepBuilderAPI_MakeShape void
#define Standard_Real double
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
#define BRepPrimAPI_MakeBox void
#define BRepPrimAPI_MakeShape void
#define Standard_Integer int
#define BRepBuilderAPI_MakeEdge void
#define TopTools_ListOfShape void
#define BRepBuilderAPI_MakeEdge void
#define gp_Circ2d void
#define Geom2d_Circle void
#define Geom2dAPI_InterCurveCurve void
#define Geom2d_Geometry void
#define Geom2d_Conic void
#define TopoDS_Builder void

/*

    2D

*/

gp_Pnt2d *new_gp_Pnt2d__x_y(const Standard_Real theXp, const Standard_Real theYp);

gp_Pnt2d *new_gp_Pnt2d__Geom2d_Conic__Value__u(Handle(Geom2d_Conic) *geom2d_conic, const Standard_Real U);

gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(const Handle(Geom2d_TrimmedCurve)* curve);

gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(const Handle(Geom2d_TrimmedCurve)* curve);

gp_Vec2d *new_gp_Vec2d__x_y(const Standard_Real theXp, const Standard_Real theYp);

gp_Ax2d *new_gp_Ax2d();

gp_Ax2d *new_gp_Ax2d__pt_dir(const gp_Pnt2d *theP, const gp_Dir2d *theV);

gp_Dir2d *new_gp_Dir2d();

gp_Dir2d *new_gp_Dir2d__x_y(const Standard_Real theXp, const Standard_Real theYp);

gp_Circ2d* new_gp_Circ2d__ax2d_r(gp_Ax2d *ax2d, Standard_Real theRadius);

Standard_Real gp_Pnt2d__Distance__p1_p2(const gp_Pnt2d *theOne, const gp_Pnt2d *theOther);

Standard_Real gp_Pnt2d__X(gp_Pnt2d *pnt);

Standard_Real gp_Pnt2d__Y(gp_Pnt2d *pnt);


Standard_Real gp_Dir2d__X(gp_Dir2d *dir);

Standard_Real gp_Dir2d__Y(gp_Dir2d *dir);

Handle(Geom2d_Ellipse) *handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(const gp_Ax2d *MajorAxis, const Standard_Real MajorRadius,
                                                         const Standard_Real MinorRadius,
                                                         const Standard_Boolean Sense);

Handle(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__curve_u1_u2(const Handle(Geom2d_Curve) *C,
                                                                    const Standard_Real U1, const Standard_Real U2,
                                                                    const Standard_Boolean Sense,
                                                                    const Standard_Boolean theAdjustPeriodic);

Handle(Geom2d_TrimmedCurve) *handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(const gp_Pnt2d *P1, const gp_Pnt2d *P2);

Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(gp_Circ2d* circ2d, gp_Pnt2d* p1, gp_Pnt2d* p2);

Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_ang1_ang2(gp_Circ2d* circ2d, Standard_Real angle1, Standard_Real angle2);

Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__p1_p2_p3(gp_Pnt2d* pt1, gp_Pnt2d* pt2, gp_Pnt2d* pt3);

Handle(Geom2d_TrimmedCurve)* handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_ang(gp_Circ2d* circ2d, gp_Pnt2d* pt1, Standard_Real angle1);

void _Geom2d_TrimmedCurve__Mirror__ax2(Handle(Geom2d_TrimmedCurve)* curve, gp_Ax2d* ax2d);

void _Geom2d_TrimmedCurve__Reverse(Handle(Geom2d_TrimmedCurve)* curve);

Handle(Geom2d_Circle)* handle_Geom2d_Circle__GCE2d_MakeCircle__cir2d(gp_Circ2d *cir2d);

Geom2dAPI_InterCurveCurve* new_Geom2dAPI_InterCurveCurve__curve1_curve2(const Handle(Geom2d_Curve)* C1, const Handle(Geom2d_Curve)* C2);

Standard_Integer int_Geom2dAPI_InterCurveCurve__NbPoints(const Geom2dAPI_InterCurveCurve *inter_curve_curve);

 Handle(Geom2d_Geometry)*  handle_Geom2d_Geometry__Copy(const Handle(Geom2d_Geometry) *toCpy);

 Handle(Geom_Curve)* handle_Geom_Curve__GeomAPI_To3d__curve_plan(Handle(Geom2d_Curve) *curve, gp_Pln *plan);

/***********************************************************************************************************************

    3D

***********************************************************************************************************************/

gp_Pnt * new_gp_Pnt__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);

void delete_gp_Pnt(gp_Pnt *pnt);

gp_Vec *new_gp_Vec__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);

void delete_gp_Vec(gp_Vec *pnt);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_vtangente_p2(gp_Pnt *pnt1, gp_Vec *tan, gp_Pnt *pnt3);

void delete_handle_Geom_TrimmedCurve(Handle(Geom_TrimmedCurve)* ptr);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(gp_Pnt *pnt1, gp_Pnt *pnt2);

const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve) *segment);

const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve_Geom_Surface(Handle(Geom2d_Curve) *curve, const Handle(Geom_Surface) *surface);

void delete_TopoDS_Edge(TopoDS_Edge *ptr);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(gp_Pnt* from, gp_Pnt* to);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve)* curve);

const BRepBuilderAPI_MakeWire * new_BRepBuilderAPI_MakeWire();

const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeEdge* edge);

void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeWire* wireMaker, BRepBuilderAPI_MakeEdge* edge);

const TopoDS_Wire * new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(TopoDS_Edge* e1, TopoDS_Edge* e2, TopoDS_Edge* e3);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(TopoDS_Edge* e1, TopoDS_Edge* e2);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3_e4(TopoDS_Edge* e1, TopoDS_Edge* e2, TopoDS_Edge* e3, TopoDS_Edge* e4);

const TopoDS_Wire *ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire);

void _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(BRepBuilderAPI_MakeWire *mw, TopoDS_Wire *wire);

void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *mw, BRepBuilderAPI_MakeWire *mw2);

void _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(BRepBuilderAPI_MakeWire *mw, TopoDS_Edge *edge);

void _BRepBuilderAPI_MakeWire__Add__TopTools_ListOfShape(BRepBuilderAPI_MakeWire *mw, TopTools_ListOfShape *listOfShape);

const gp_Ax1 *gp__OX();

gp_Trsf *new_gp_Trsf();

void _gp_Trsf__SetMirror__gp_Ax1(gp_Trsf *trsf, gp_Ax1 *ax1);

void _gp_Trsf__SetTranslation__gp_Vec(gp_Trsf *gp_trsf, const gp_Vec *translation);

BRepBuilderAPI_Transform *new_BRepBuilderAPI_Transform__TopoDS_Wire_gp_Trsf(const TopoDS_Wire *w, gp_Trsf *trsf);

TopoDS_Wire *ref_TopoDS__Wire__TopoDS_Shape(TopoDS_Shape *shape);

TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(TopoDS_Wire *wire);

TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gp_Pln *plane);

TopoDS_Face *new_TopoDS_Face();

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(TopoDS_Face *face, gp_Vec *normal);

BRepFilletAPI_MakeFillet * new_BRepFilletAPI_MakeFillet__TopoDS_Shape(TopoDS_Shape *body);

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

TopExp_Explorer *new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(const TopoDS_Shape *S, const /*TopAbs_ShapeEnum*/int ToFind,
                                             const int ToAvoid);

bool _TopExp_Explorer__More(TopExp_Explorer *explorer);

TopoDS_Shape *new_TopoDS_Shape__TopExp_Explorer__Current(TopExp_Explorer *explorer);

TopoDS_Face * new_TopoDS_Face__TopExp_Explorer__Current(TopExp_Explorer *explorer);

void _TopExp_Explorer__Next(TopExp_Explorer *explorer);

TopoDS_Edge *ref_TopoDS_Edge__TopoDS_Shape(TopoDS_Shape *shape);

void _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(BRepFilletAPI_MakeFillet *make_fillet, Standard_Real r,
                                               TopoDS_Edge *edge);

const gp_Dir *new_gp_Dir_DZ();

gp_Dir *new_gp_Dir__x_y_z(const Standard_Real theXv, const Standard_Real theYv, const Standard_Real theZv);

gp_Dir *new_gp_Dir__Normal__TopoDS_Face(const TopoDS_Face *aCurrentFace);


const gp_Ax2 * new_gp_Ax2__gp_Pnt_gp_Dir(gp_Pnt *loc, gp_Dir *dir);

const gp_Ax2 *new_gp_Ax2_DZ();

BRepPrimAPI_MakeCylinder *new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(const gp_Ax2 *Axes, const Standard_Real R,
                                                                const Standard_Real H);

BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__x_y_z(const Standard_Real x, const Standard_Real y,
                                                      const Standard_Real z);

Handle(Geom_Surface) *handle_Geom_Surface__TopoDS_Face(TopoDS_Face *face);

Standard_Integer int_Geom_Surface__is__Geom_Plane(Handle(Geom_Surface) *surface);

Handle(Geom_Plane) *handle_Geom_Plane__handle_Geom_Surface(Handle(Geom_Surface) *surface);

gp_Pnt *new_gp_Pnt__Geom_Plane(Handle(Geom_Plane) *plane);

TopTools_ListOfShape *new_TopTools_ListOfShape();

void delete_TopTools_ListOfShape(TopTools_ListOfShape* ptr);

void _TopTools_ListOfShape__Append__TopoDS_Shape(TopTools_ListOfShape *l, TopoDS_Shape *face);

BRepOffsetAPI_MakeThickSolid *new_BRepOffsetAPI_MakeThickSolid();

void _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(
                                                      BRepOffsetAPI_MakeThickSolid *thick_solid, TopoDS_Shape *shape,
                                                      const TopTools_ListOfShape *face_to_remove,
                                                      Standard_Real thickness, Standard_Real tol);

Handle(Geom_CylindricalSurface) *handle_Geom_CylindricalSurface__ax2_radius(
    const gp_Ax3 *ax2, const Standard_Real radius);

void _BRepLib__BuildCurves3d__TopoDS_Shape(const TopoDS_Shape *w1);

BRepOffsetAPI_ThruSections *new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(const Standard_Boolean isSolid,
                                                               const Standard_Boolean ruled,
                                                               const Standard_Real pres3d);

void _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire *w);

void _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(BRepOffsetAPI_ThruSections *thru_sections,
                                                            const Standard_Boolean b);

TopoDS_Compound *new_TopoDS_Compound();

BRep_Builder *new_BRep_Builder();

void _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(TopoDS_Builder *builder, TopoDS_Shape *inThis, TopoDS_Shape *toAdd);

void _TopoDS_Builder__MakeCompound__TopoDS_Compound(TopoDS_Builder *b, TopoDS_Compound *c);

gp_Ax1 *new_gp_Ax1__p_dir(const gp_Pnt *theP, const gp_Dir *theV);

gp_Pnt *new_gp_Pnt__CentreOfMass__TopoDS_Shape(const TopoDS_Shape *myShape);

Standard_Real gp_Pnt__X(gp_Pnt *pnt);

Standard_Real gp_Pnt__Y(gp_Pnt *pnt);

Standard_Real gp_Pnt__Z(gp_Pnt *pnt);

Standard_Real gp_Dir__X(gp_Dir *dir);

Standard_Real gp_Dir__Y(gp_Dir *dir);

Standard_Real gp_Dir__Z(gp_Pnt *dir);

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(const gp_Ax2 *origin, const Standard_Real radius,
                                                    const Standard_Real angle1, const Standard_Real angle2);

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(const gp_Ax2 *origin, const Standard_Real radius,
                                                       const Standard_Real height);

TopoDS_Shape *new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape__gp_Trsf_bCopy(const TopoDS_Shape *shape, const gp_Trsf *gp_trsf,
                                                         const Standard_Boolean theCopyGeom);


TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(const gp_Ax2 *origin, const Standard_Real radius1,
                                                  const Standard_Real radius2);


TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(TopoDS_Face* face, gp_Ax1* ax1);

void deleteVoid(void *ptr);

gp_Pln* new_gp_Pln__x_y_z_d(const Standard_Real x, const Standard_Real y, const Standard_Real z, const Standard_Real d);

gp_Pln* new_gp_Pln__pt_dir(const gp_Pnt* pt, const gp_Dir* dir);

TopoDS_Shape* new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(BRepBuilderAPI_MakeShape *shape);

/*

Composed

*/
TopoDS_Shape *brep_algoapi_cut_ds_shape(TopoDS_Shape *result, TopoDS_Shape *tool);

TopoDS_Shape *brep_algoapi_fuse(TopoDS_Shape *s1, TopoDS_Shape *s2);

TopoDS_Shape *brep_algoapi_cut(TopoDS_Shape *result, TopTools_ListOfShape *aLT);


TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(const TopoDS_Shape *shape, const gp_Ax1 *ax1, const Standard_Real Radius,
                                   const Standard_Real PFrom, const Standard_Real PTo);

TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(const TopoDS_Shape *shape, const gp_Ax1 *ax1, const Standard_Real Radius,
                                         const Standard_Real Length);


bool dumpShape(const TopoDS_Shape *shape, const Standard_Integer width, const Standard_Integer height,
                          const char *fileName);

void write_step(const TopoDS_Shape* shape, const char *fileName);

void write_stl(const TopoDS_Shape* shape, const char *fileName);

void analyze(const TopoDS_Shape *myShape);

