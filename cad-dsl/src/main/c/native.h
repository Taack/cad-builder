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

//int visualize(void*);
//int visualize2(void*);
int visualize3(void*d);

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
#define ShapeExtend_WireData void
#define Geom_SurfaceOfLinearExtrusion void
#define Geom_Ellipse void
#define Geom_SurfaceOfRevolution void
#define GccAna_Circ2d2TanRad void
#define gp_Ax22d void
#define BRepAlgoAPI_Cut void
#define BRepBuilderAPI_MakeFace void
#define BRepBuilderAPI_MakePolygon void
#define BRepTools_WireExplorer void
#define GeomPlate_BuildPlateSurface void
#define BRepAdaptor_Curve void
#define BRepFill_CurveConstraint void
#define Adaptor3d_Curve void
#define GeomPlate_PointConstraint void
#define GeomPlate_MakeApprox void
#define GeomPlate_Surface void
#define Geom2d_Line void
#define TColgp_Array1OfPnt void
#define Geom_BezierCurve void
#define BRepFeat_MakePipe void
#define BRepFeat_MakeDPrism void
#define Geom_Line void
#define GeomAPI_ExtremaCurveSurface void
#define Graphic3d_Text void
#define Standard_ShortReal float

/*

    2D

*/

gp_Pnt2d *new_gp_Pnt2d__x_y(const Standard_Real theXp, const Standard_Real theYp);

gp_Pnt2d *new_gp_Pnt2d__Geom2d_Curve__Value__u(Handle(Geom2d_Conic) *geom2d_conic, const Standard_Real U);

gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(const Handle(Geom2d_TrimmedCurve)* curve);

gp_Pnt2d* new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(const Handle(Geom2d_TrimmedCurve)* curve);

gp_Vec2d *new_gp_Vec2d__x_y(const Standard_Real theXp, const Standard_Real theYp);

gp_Ax2d *new_gp_Ax2d(void);

gp_Ax2d *new_gp_Ax2d__pt_dir(const gp_Pnt2d *theP, const gp_Dir2d *theV);

gp_Dir2d *new_gp_Dir2d(void);

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

Handle(Geom_Line)* handle_Geom_Line__ax1(const gp_Ax1 *A1);

GeomAPI_ExtremaCurveSurface* new_GeomAPI_ExtremaCurveSurface__curve_surface(const Handle(Geom_Curve) *Curve, const Handle(Geom_Surface) *Surface);

Standard_Integer i_GeomAPI_ExtremaCurveSurface__NbExtrema(GeomAPI_ExtremaCurveSurface* extrema);

Standard_Real r_GeomAPI_ExtremaCurveSurface__Distance__index(GeomAPI_ExtremaCurveSurface* extrema, const Standard_Integer Index);

Standard_Real* R6_GeomAPI_ExtremaCurveSurface__NbExtrema(GeomAPI_ExtremaCurveSurface* extrema, const Standard_Integer Index);

Standard_Integer int_Geom2dAPI_InterCurveCurve__NbPoints(const Geom2dAPI_InterCurveCurve *inter_curve_curve);

gp_Pnt2d* new_gp_Pnt2d__Geom2dAPI_InterCurveCurve__Point__i(const Geom2dAPI_InterCurveCurve *inter_curve_curve, const Standard_Integer index);

Handle(Geom2d_Geometry)*  handle_Geom2d_Geometry__Copy(const Handle(Geom2d_Geometry) *toCpy);

Handle(Geom_Curve)* handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(Handle(Geom2d_Curve) *curve, gp_Pln *plan);

Handle(Geom2d_Line) *handle_Geom2d_Line__GCE2d_MakeLine__p1_p2(gp_Pnt2d* p1, gp_Pnt2d* p2);
/***********************************************************************************************************************

    3D

***********************************************************************************************************************/

gp_Pnt * new_gp_Pnt__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);

void delete_gp_Pnt(gp_Pnt *pnt);

gp_Vec *new_gp_Vec__x_y_z(const Standard_Real theXp, const Standard_Real theYp, const Standard_Real theZp);

void delete_gp_Vec(gp_Vec *pnt);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__Geom_Curve_u1_u2(const Handle(Geom_Curve) *C, const Standard_Real U1, const Standard_Real U2);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(gp_Pnt *pnt1, gp_Pnt *pnt2, gp_Pnt *pnt3);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_vtangente_p2(gp_Pnt *pnt1, gp_Vec *tan, gp_Pnt *pnt3);

void delete_handle_Geom_TrimmedCurve(Handle(Geom_TrimmedCurve)* ptr);

Handle(Geom_TrimmedCurve) *handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(gp_Pnt *pnt1, gp_Pnt *pnt2);

const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve) *segment);

const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(Handle(Geom2d_Curve) *curve, const Handle(Geom_Surface) *surface);

const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(Handle(Geom2d_Curve) *curve);

void delete_TopoDS_Edge(TopoDS_Edge *ptr);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(gp_Pnt* from, gp_Pnt* to);

const BRepBuilderAPI_MakePolygon *new_BRepBuilderAPI_MakePolygon(void);

void _BRepBuilderAPI_MakePolygon__Add__pt(BRepBuilderAPI_MakePolygon* poly, gp_Pnt* pt);

const BRepTools_WireExplorer *new_BRepTools_WireExplorer(void);

void _BRepTools_WireExplorer__Init__TopoDS_Wire(BRepTools_WireExplorer *e, const TopoDS_Wire *W);

void _BRepTools_WireExplorer__Init__BRepBuilderAPI_MakePolygon(BRepTools_WireExplorer *e, const BRepBuilderAPI_MakePolygon *W);

Standard_Boolean b_BRepTools_WireExplorer__More(BRepTools_WireExplorer *e);

const void _BRepTools_WireExplorer__Next(BRepTools_WireExplorer *e);

const Handle(TopoDS_Edge)* _BRepTools_WireExplorer__Current(BRepTools_WireExplorer *e);

GeomPlate_BuildPlateSurface* new_GeomPlate_BuildPlateSurface__degree_NbPt_NbIter(const Standard_Integer	Degree, const Standard_Integer	NbPtsOnCur, const Standard_Integer	NbIter);

Handle(GeomPlate_PointConstraint)* handle_GeomPlate_PointConstraint__gp_Pnt_order(gp_Pnt *p, Standard_Integer order);

void _GeomPlate_BuildPlateSurface__Add__Cont(GeomPlate_BuildPlateSurface* s, const Handle(GeomPlate_PointConstraint) *Cont);

void _GeomPlate_BuildPlateSurface__Add__BRepFill_CurveConstraint(GeomPlate_BuildPlateSurface* s, const Handle(BRepFill_CurveConstraint) *Cont);

void _GeomPlate_BuildPlateSurface__Perform(GeomPlate_BuildPlateSurface* s);

Handle(GeomPlate_Surface)* handle_GeomPlate_Surface__GeomPlate_BuildPlateSurface__Surface(GeomPlate_BuildPlateSurface *s);

Standard_Real r_GeomPlate_BuildPlateSurface__G0Error(GeomPlate_BuildPlateSurface *s);

Standard_Real* r4_GeomPlate_Surface__Bounds(GeomPlate_Surface *s);

Handle(BRepAdaptor_Curve) *handle_BRepAdaptor_Curve(void);

void _BRepAdaptor_Curve__Initialize(BRepAdaptor_Curve* adaptor, TopoDS_Edge *edge);

Handle(BRepFill_CurveConstraint)* handle_BRepFill_CurveConstraint__Adaptor3d_Curve_Tang(const Handle(Adaptor3d_Curve) *Boundary, const Standard_Integer Tang);

GeomPlate_MakeApprox* new_GeomPlate_MakeApprox__SurfPlate_Tol3d_Nbmax_dgmax_dmax_CritOrder(const Handle(GeomPlate_Surface) *SurfPlate, const Standard_Real Tol3d, const Standard_Integer Nbmax, const Standard_Integer dgmax, const Standard_Real dmax, const Standard_Integer CritOrder);

Handle(Geom_Surface) *handle_Geom_Surface__GeomPlate_MakeApprox__Surface(GeomPlate_MakeApprox *mApp);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom_Curve(Handle(Geom_Curve)* curve);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(const Handle(Geom2d_Curve)* curve2d, const Handle(Geom_Surface)* S, const Standard_Real	p1,const Standard_Real	p2);

const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(const Handle(Geom2d_Curve)* curve2d, const Handle(Geom_Surface)* S);

const BRepBuilderAPI_MakeWire * new_BRepBuilderAPI_MakeWire(void);

const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeEdge* edge);

void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeWire* wireMaker, BRepBuilderAPI_MakeEdge* edge);

const TopoDS_Wire * new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(TopoDS_Edge* e1, TopoDS_Edge* e2, TopoDS_Edge* e3);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(TopoDS_Edge* e1, TopoDS_Edge* e2);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1(TopoDS_Edge* e1);

BRepFeat_MakePipe *new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(const TopoDS_Shape *Sbase, const TopoDS_Shape *Pbase, const TopoDS_Face *Skface, const TopoDS_Wire *Spine, const Standard_Integer Fuse, const Standard_Boolean Modify);

void _BRepFeat_MakePipe__Perform(BRepFeat_MakePipe *p);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3_e4(TopoDS_Edge* e1, TopoDS_Edge* e2, TopoDS_Edge* e3, TopoDS_Edge* e4);

const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire);

const TopoDS_Wire *ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire);

const TopoDS_Shape *ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(BRepBuilderAPI_MakeWire *make_wire);

void _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(BRepBuilderAPI_MakeWire *mw, TopoDS_Wire *wire);

void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *mw, BRepBuilderAPI_MakeWire *mw2);

void _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(BRepBuilderAPI_MakeWire *mw, TopoDS_Edge *edge);

void _BRepBuilderAPI_MakeWire__Add__TopTools_ListOfShape(BRepBuilderAPI_MakeWire *mw, TopTools_ListOfShape *listOfShape);

const gp_Ax1 *gp__OX(void);

gp_Trsf *new_gp_Trsf(void);

void _gp_Trsf__SetMirror__gp_Ax1(gp_Trsf *trsf, gp_Ax1 *ax1);

void _gp_Trsf__SetMirror__gp_Ax2(gp_Trsf *trsf, gp_Ax2 *ax2);

void _gp_Trsf__SetTranslation__gp_Vec(gp_Trsf *gp_trsf, const gp_Vec *translation);

void _gp_Trsf__SetRotation__gp_Vec(gp_Trsf *gp_trsf, const gp_Ax1 *ax1, Standard_Real angle);

BRepBuilderAPI_Transform *new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(const TopoDS_Shape *w, gp_Trsf *trsf, const Standard_Boolean theCopyGeom, const Standard_Boolean theCopyMesh);

TopoDS_Wire *ref_TopoDS__Wire__TopoDS_Shape(TopoDS_Shape *shape);

void _TopoDS__Shape__Reverse(TopoDS_Shape *shape);

TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(TopoDS_Wire *wire);

BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *wire);

BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__s_um_uM_vm_vM_Tol(const Handle(Geom_Surface) *S, const Standard_Real UMin, const Standard_Real UMax, const Standard_Real VMin, const Standard_Real VMax, const Standard_Real TolDegen);

BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace(void);

TopoDS_Face *TopoDS_Face__BRepBuilderAPI_MakeFace__Face(BRepBuilderAPI_MakeFace *MKF1);

void _BRepBuilderAPI_MakeFace__Init(BRepBuilderAPI_MakeFace *MF, const Handle(Geom_Surface) *S, const Standard_Boolean Bound, const Standard_Real	TolDegen );

void _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeFace *MF, BRepBuilderAPI_MakeWire *W);

TColgp_Array1OfPnt* new_TColgp_Array1OfPnt__Low_Up(const Standard_Integer 	Low, const Standard_Integer 	Up);

void _TColgp_Array1OfPnt__Ar_Pt_Indx(TColgp_Array1OfPnt* ar, gp_Pnt* pt, Standard_Integer indx);

Handle(Geom_BezierCurve) *handle_Geom_BezierCurve__TColgp_Array1OfPnt(TColgp_Array1OfPnt* CurvePoles);

TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace(BRepBuilderAPI_MakeFace *face);

TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(gp_Pln *plane);

TopoDS_Face *new_TopoDS_Face(void);

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(TopoDS_Face *face, gp_Vec *normal);

BRepFeat_MakeDPrism *new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(const TopoDS_Shape *Sbase, const TopoDS_Face *Pbase, const TopoDS_Face *Skface, const Standard_Real Angle, const Standard_Integer Fuse, const Standard_Boolean Modify);

void _BRepFeat_MakeDPrism__Perform__Height(BRepFeat_MakeDPrism *p, Standard_Real height);

BRepFilletAPI_MakeFillet * new_BRepFilletAPI_MakeFillet__TopoDS_Shape(TopoDS_Shape *body);

TopAbs_ShapeEnum TopAbs_ShapeEnumFromOrdinal(int ordinal);

TopExp_Explorer *new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(const TopoDS_Shape *S, const /*TopAbs_ShapeEnum*/int ToFind,
                                             const int ToAvoid);

bool _TopExp_Explorer__More(TopExp_Explorer *explorer);

TopoDS_Shape *new_TopoDS_Shape__TopExp_Explorer__Current(TopExp_Explorer *explorer);

TopoDS_Face * new_TopoDS_Face__TopExp_Explorer__Current(TopExp_Explorer *explorer);

void _TopExp_Explorer__Next(TopExp_Explorer *explorer);

TopoDS_Edge *ref_TopoDS_Edge__TopoDS_Shape(TopoDS_Shape *shape);

void _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(BRepFilletAPI_MakeFillet *make_fillet, Standard_Real r,
                                               TopoDS_Edge *edge);

const gp_Dir *new_gp_Dir_DZ(void);

gp_Dir *new_gp_Dir__x_y_z(const Standard_Real theXv, const Standard_Real theYv, const Standard_Real theZv);

gp_Dir *new_gp_Dir__Normal__TopoDS_Face(const TopoDS_Face *aCurrentFace);


const gp_Ax2 * new_gp_Ax2__gp_Pnt_gp_Dir(gp_Pnt *loc, gp_Dir *dir);

const gp_Ax2 * new_gp_Ax2__gp_Pnt_gp_Dir_Normal(gp_Pnt *loc, gp_Dir *dir, gp_Dir *n);

const gp_Ax3 * new_gp_Ax3__p_dN_dX(gp_Pnt *loc, gp_Dir *dirN, gp_Dir *dirX);

const gp_Ax2 *new_gp_Ax2_DZ(void);

BRepPrimAPI_MakeCylinder *new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(const gp_Ax2 *Axes, const Standard_Real R,
                                                                const Standard_Real H);

BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__x_y_z(const Standard_Real x, const Standard_Real y,
                                                      const Standard_Real z);

BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__Ax2_x_y_z(const gp_Ax2 *Axes, const Standard_Real dx, const Standard_Real dy, const Standard_Real dz);

Handle(Geom_Surface) *handle_Geom_Surface__TopoDS_Face(TopoDS_Face *face);

Handle(Geom_Plane) *handle_Geom_Plan__gp_Pln(gp_Pln *pln);

Standard_Integer int_Geom_Surface__is__Geom_Plane(Handle(Geom_Surface) *surface);

Handle(Geom_Plane) *handle_Geom_Plane__handle_Geom_Surface(Handle(Geom_Surface) *surface);

gp_Pnt *new_gp_Pnt__Geom_Plane(Handle(Geom_Plane) *plane);

TopTools_ListOfShape *new_TopTools_ListOfShape(void);

void delete_TopTools_ListOfShape(TopTools_ListOfShape* ptr);

void _TopTools_ListOfShape__Append__TopoDS_Shape(TopTools_ListOfShape *l, TopoDS_Shape *face);

BRepOffsetAPI_MakeThickSolid *new_BRepOffsetAPI_MakeThickSolid(void);

void _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(
                                                      BRepOffsetAPI_MakeThickSolid *thick_solid, TopoDS_Shape *shape,
                                                      const TopTools_ListOfShape *face_to_remove,
                                                      Standard_Real thickness, Standard_Real tol);



Handle(Geom_CylindricalSurface) *handle_Geom_CylindricalSurface__ax2_radius(
    const gp_Ax3 *ax2, const Standard_Real radius);

Handle(Geom_SurfaceOfLinearExtrusion) *handle_Geom_SurfaceOfLinearExtrusion__Geom_Curve_gp_Dir(
    const Handle(Geom_Curve) *C, const gp_Dir *V);

Handle(Geom_SurfaceOfRevolution) *handle_Geom_SurfaceOfRevolution__Geom_Curve_gp_Ax1(
    const Handle(Geom_Curve) *C, const gp_Ax1 *V);

Handle(Geom_Ellipse) *handle_Geom_Ellipse__gp_Ax2_rM_rm(const gp_Ax2 *A2, const Standard_Real MajorRadius, const Standard_Real MinorRadius);

void* R4_Geom_Surface__Bounds(const Geom_Surface *S);

gp_Pnt* gp_Pnt__Geom_Surface__Value(const Handle(Geom_Surface) *S, const Standard_Real U, const Standard_Real V);

void _BRepLib__BuildCurves3d__TopoDS_Shape(const TopoDS_Shape *w1);

BRepOffsetAPI_ThruSections *new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(const Standard_Boolean isSolid,
                                                               const Standard_Boolean ruled,
                                                               const Standard_Real pres3d);

void _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire *w);

void _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(BRepOffsetAPI_ThruSections *thru_sections,
                                                            const Standard_Boolean b);

TopoDS_Compound *new_TopoDS_Compound(void);

BRep_Builder *new_BRep_Builder(void);

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

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H_angle(const gp_Ax2 *origin, const Standard_Real R1, const Standard_Real R2, const Standard_Real H, const Standard_Real angle);

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(const gp_Ax2 *origin, const Standard_Real R1, const Standard_Real R2, const Standard_Real H);

TopoDS_Shape *new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(const TopoDS_Shape *shape, const gp_Trsf *gp_trsf,
                                                         const Standard_Boolean theCopyGeom);


TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(const gp_Ax2 *origin, const Standard_Real radius1,
                                                  const Standard_Real radius2);


TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(TopoDS_Face* face, gp_Ax1* ax1);

TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(TopoDS_Face* face, gp_Ax1* ax1, Standard_Real angle);

void _TopoDS_Shape__Free(TopoDS_Face* face);

TopoDS_Face *new_TopoDS_Face__face(TopoDS_Face* face);

gp_Pln* new_gp_Pln__x_y_z_d(const Standard_Real x, const Standard_Real y, const Standard_Real z, const Standard_Real d);

gp_Pln* new_gp_Pln__pt_dir(const gp_Pnt* pt, const gp_Dir* dir);

gp_Pln* new_gp_Pln__gp_Ax3(const gp_Ax3* ax3);

TopoDS_Shape* new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(BRepBuilderAPI_MakeShape *shape);

ShapeExtend_WireData* new_ShapeExtend_WireData(void);

void _ShapeExtend_WireData__Add__TopoDS_Edge(ShapeExtend_WireData *data, const TopoDS_Edge *edge, const Standard_Integer atnum);

void _ShapeExtend_WireData__Add__TopoDS_Wire(ShapeExtend_WireData *data, const TopoDS_Wire *edge, const Standard_Integer atnum);

Handle(TopoDS_Wire)* util_ShapeFix_Wire__Load__ShapeExtend_WireData(ShapeExtend_WireData *data);

GccAna_Circ2d2TanRad *new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(const gp_Pnt2d *Point1, const gp_Pnt2d *Point2, const Standard_Real Radius, const Standard_Real Tolerance);

Standard_Integer i_GccAna_Circ2d2TanRad__NbSolutions(GccAna_Circ2d2TanRad* circ2d2TanRad);

gp_Circ2d* ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(GccAna_Circ2d2TanRad* circ2d2TanRad, Standard_Integer numStartingAt1);

const gp_Ax22d *ref_Position__gp_Circ2d__Position(gp_Circ2d *cir2d);

const gp_Pnt2d *ref_gp_Pnt2d__gp_Ax22d__Location(gp_Ax22d *ax22d);

BRepAlgoAPI_Cut *new_BRepAlgoAPI_Cut__s1_s2(TopoDS_Shape *result, TopoDS_Shape *tool);

Standard_Real *R7_BRepExtrema_DistShapeShape__s1_s2(const TopoDS_Shape *Shape1, const TopoDS_Shape *Shape2);

Graphic3d_Text* new_Graphic3d_Text__text_height_pos(const char *theText, const Standard_ShortReal theHeight, const gp_Pnt *thePoint);

/*

Composed

*/
TopoDS_Shape *new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(TopoDS_Shape *result, TopoDS_Shape *tool);

TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(TopoDS_Shape *s1, TopoDS_Shape *s2);

TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_listrOfShape(TopoDS_Shape *result, TopTools_ListOfShape *aLT);

TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_common__s1_s2(TopoDS_Shape *s1, TopoDS_Shape *s2);

TopoDS_Shape *new_TopoDS_Shape__BRepAlgoAPI_Cut__TopoDS_Shape_TopTools_ListOfShape(TopoDS_Shape *result, TopTools_ListOfShape *aLT);


TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(const TopoDS_Shape *shape, const gp_Ax1 *ax1, const Standard_Real Radius,
                                   const Standard_Real PFrom, const Standard_Real PTo);

TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(const TopoDS_Shape *shape, const gp_Ax1 *ax1, const Standard_Real Radius,
                                         const Standard_Real Length);


bool dumpShape(const TopoDS_Shape *shape, const Standard_Integer width, const Standard_Integer height,
                          const char *fileName);

void write_step(const TopoDS_Shape* shape, const char *fileName);

void write_stl(const TopoDS_Shape* shape, const char *fileName);

void analyze(const TopoDS_Shape *myShape);

