//
// C API: topology (edges, wires, faces, explorers, compounds, wire healing).
//

#include "capi_internal.hxx"

#include <BRepAdaptor_Surface.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeEdge2d.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_MakePolygon.hxx>
#include <BRepBuilderAPI_MakeVertex.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepExtrema_DistShapeShape.hxx>
#include <BRepGProp.hxx>
#include <BRepCheck_Wire.hxx>
#include <BRepLib.hxx>
#include <BRepTools.hxx>
#include <BRepTools_WireExplorer.hxx>
#include <BRep_Builder.hxx>
#include <BRep_Tool.hxx>
#include <Geom2d_Curve.hxx>
#include <GeomLProp_SLProps.hxx>
#include <Geom_Curve.hxx>
#include <GProp_GProps.hxx>
#include <Geom_Surface.hxx>
#include <Precision.hxx>
#include <ShapeAnalysis_Wire.hxx>
#include <ShapeExtend_WireData.hxx>
#include <ShapeFix_Face.hxx>
#include <ShapeFix_ShapeTolerance.hxx>
#include <ShapeFix_Wire.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Compound.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Vertex.hxx>
#include <TopoDS_Wire.hxx>
#include <gp_Dir.hxx>
#include <gp_Cylinder.hxx>
#include <gp_Pln.hxx>

#include <cmath>

/*

    Edges

*/

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(const occ::handle<Geom_Curve> *segment) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(*segment).Edge()); });
}

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(
    const occ::handle<Geom2d_Curve> *curve, const occ::handle<Geom_Surface> *surface) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Edge(BRepBuilderAPI_MakeEdge(*curve, *surface).Edge()); });
}

extern "C" const TopoDS_Edge *new_TopoDS_Edge__BRepBuilderAPI_MakeEdge2d__Geom2d_Curve(const occ::handle<Geom2d_Curve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Edge(BRepBuilderAPI_MakeEdge2d(*curve).Edge()); });
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(const gp_Pnt *from, const gp_Pnt *to) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeEdge(*from, *to); });
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom_Curve(const occ::handle<Geom_Curve> *curve) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeEdge(*curve); });
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface_p1_p2(
    const occ::handle<Geom2d_Curve> *curve2d, const occ::handle<Geom_Surface> *S, double p1, double p2) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeEdge(*curve2d, *S, p1, p2); });
}

extern "C" const BRepBuilderAPI_MakeEdge *new_BRepBuilderAPI_MakeEdge__Geom2d_Curve_Geom_Surface(
    const occ::handle<Geom2d_Curve> *curve2d, const occ::handle<Geom_Surface> *S) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeEdge(*curve2d, *S); });
}

extern "C" const BRepBuilderAPI_MakePolygon *new_BRepBuilderAPI_MakePolygon(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakePolygon(); });
}

extern "C" void _BRepBuilderAPI_MakePolygon__Add__pt(BRepBuilderAPI_MakePolygon *poly, const gp_Pnt *pt) {
    TRACE("");
    guard(__func__, [&] { poly->Add(*pt); });
}

/*

    Wires

*/

extern "C" const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeWire(); });
}

extern "C" const BRepBuilderAPI_MakeWire *new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeEdge *edge) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeWire(edge->Edge()); });
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(BRepBuilderAPI_MakeWire *wireMaker, BRepBuilderAPI_MakeEdge *edge) {
    TRACE("");
    guard(__func__, [&] {
        try {
            wireMaker->Add(edge->Edge());
        } catch (const StdFail_NotDone &) {
            switch (wireMaker->Error()) {
                case BRepBuilderAPI_WireDone:
                    TRACE("BRepBuilderAPI_WireDone");
                    break;
                case BRepBuilderAPI_EmptyWire:
                    TRACE("BRepBuilderAPI_EmptyWire");
                    break;
                case BRepBuilderAPI_DisconnectedWire:
                    TRACE("BRepBuilderAPI_DisconnectedWire");
                    break;
                case BRepBuilderAPI_NonManifoldWire:
                    TRACE("BRepBuilderAPI_NonManifoldWire");
                    break;
            }
            throw;
        }
    });
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(BRepBuilderAPI_MakeWire *mw, const TopoDS_Wire *wire) {
    TRACE("");
    guard(__func__, [&] { mw->Add(*wire); });
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *mw, BRepBuilderAPI_MakeWire *mw2) {
    TRACE("");
    guard(__func__, [&] { mw->Add(mw2->Wire()); });
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopoDS_Edge(BRepBuilderAPI_MakeWire *mw, const TopoDS_Edge *edge) {
    TRACE("");
    guard(__func__, [&] { mw->Add(*edge); });
}

extern "C" void _BRepBuilderAPI_MakeWire__Add__TopTools_ListOfShape(BRepBuilderAPI_MakeWire *mw, const TopTools_ListOfShape *listOfShape) {
    TRACE("");
    guard(__func__, [&] { mw->Add(*listOfShape); });
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1(const TopoDS_Edge *e1) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Wire(BRepBuilderAPI_MakeWire(*e1).Wire()); });
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(const TopoDS_Edge *e1, const TopoDS_Edge *e2) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Wire(BRepBuilderAPI_MakeWire(*e1, *e2).Wire()); });
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(
    const TopoDS_Edge *e1, const TopoDS_Edge *e2, const TopoDS_Edge *e3) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Wire(BRepBuilderAPI_MakeWire(*e1, *e2, *e3).Wire()); });
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3_e4(
    const TopoDS_Edge *e1, const TopoDS_Edge *e2, const TopoDS_Edge *e3, const TopoDS_Edge *e4) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Wire(BRepBuilderAPI_MakeWire(*e1, *e2, *e3, *e4).Wire()); });
}

extern "C" const TopoDS_Wire *new_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Wire(make_wire->Wire()); });
}

extern "C" const TopoDS_Wire *ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(BRepBuilderAPI_MakeWire *make_wire) {
    TRACE("");
    return guard(__func__, [&] { return &make_wire->Wire(); });
}

extern "C" const TopoDS_Shape *ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(BRepBuilderAPI_MakeWire *make_wire) {
    TRACE("");
    return guard(__func__, [&] { return &make_wire->Shape(); });
}

extern "C" TopoDS_Wire *ref_TopoDS__Wire__TopoDS_Shape(TopoDS_Shape *shape) {
    TRACE("");
    return guard(__func__, [&] { return &TopoDS::Wire(*shape); });
}

extern "C" TopoDS_Edge *ref_TopoDS_Edge__TopoDS_Shape(TopoDS_Shape *shape) {
    TRACE("");
    return guard(__func__, [&] { return &TopoDS::Edge(*shape); });
}

extern "C" void _TopoDS__Shape__Reverse(TopoDS_Shape *shape) {
    TRACE("");
    shape->Reverse();
}

extern "C" void _TopoDS_Shape__Free(TopoDS_Shape *shape) {
    TRACE("");
    shape->Free(true);
}

/*

    Explorers

*/

extern "C" const BRepTools_WireExplorer *new_BRepTools_WireExplorer(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRepTools_WireExplorer(); });
}

extern "C" void _BRepTools_WireExplorer__Init__TopoDS_Wire(BRepTools_WireExplorer *e, const TopoDS_Wire *W) {
    TRACE("");
    guard(__func__, [&] { e->Init(*W); });
}

extern "C" void _BRepTools_WireExplorer__Init__BRepBuilderAPI_MakePolygon(BRepTools_WireExplorer *e, BRepBuilderAPI_MakePolygon *W) {
    TRACE("");
    guard(__func__, [&] { e->Init(W->Wire()); });
}

extern "C" int b_BRepTools_WireExplorer__More(const BRepTools_WireExplorer *e) {
    TRACE("");
    return e->More() ? 1 : 0;
}

extern "C" void _BRepTools_WireExplorer__Next(BRepTools_WireExplorer *e) {
    TRACE("");
    guard(__func__, [&] { e->Next(); });
}

extern "C" const TopoDS_Edge *_BRepTools_WireExplorer__Current(const BRepTools_WireExplorer *e) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Edge(e->Current()); });
}

extern "C" TopExp_Explorer *new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(const TopoDS_Shape *S, int ToFind, int ToAvoid) {
    TRACE("");
    return guard(__func__, [&] {
        return new TopExp_Explorer(*S,
                                   static_cast<TopAbs_ShapeEnum>(TopAbs_ShapeEnumFromOrdinal(ToFind)),
                                   static_cast<TopAbs_ShapeEnum>(TopAbs_ShapeEnumFromOrdinal(ToAvoid)));
    });
}

extern "C" int _TopExp_Explorer__More(const TopExp_Explorer *explorer) {
    TRACE("");
    return explorer->More() ? 1 : 0;
}

extern "C" void _TopExp_Explorer__Next(TopExp_Explorer *explorer) {
    TRACE("");
    guard(__func__, [&] { explorer->Next(); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__TopExp_Explorer__Current(const TopExp_Explorer *explorer) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(explorer->Current()); });
}

extern "C" TopoDS_Face *new_TopoDS_Face__TopExp_Explorer__Current(const TopExp_Explorer *explorer) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(TopoDS::Face(explorer->Current())); });
}

/*

    Faces

*/

static void wire_check1(const TopoDS_Wire &wire) {
    if (!wire.Closed()) {
        gp_Pln plane;
        TopoDS_Face face(BRepBuilderAPI_MakeFace(plane).Face());
        ShapeAnalysis_Wire analyze(wire, face, Precision::PConfusion());
        analyze.Perform();
        TRACE2(std::ostringstream{} << "Precision " << analyze.Precision());
        int ne = analyze.NbEdges();
        for (int i = 0; i < ne; i++) {
            if (analyze.CheckConnected(i)) {
                TRACE2(std::ostringstream{} << "CheckConnected TRUE for " << i);
            } else {
                TRACE2(std::ostringstream{} << "CheckConnected FALSE for " << i);
                if (analyze.StatusConnected(ShapeExtend_OK)) TRACE("CheckConnected Status ShapeExtend_OK");
                if (analyze.StatusConnected(ShapeExtend_DONE)) TRACE("CheckConnected Status ShapeExtend_DONE");
                if (analyze.StatusConnected(ShapeExtend_DONE1)) TRACE("CheckConnected Status ShapeExtend_DONE1");
                if (analyze.StatusConnected(ShapeExtend_DONE2)) TRACE("CheckConnected Status ShapeExtend_DONE2");
                if (analyze.StatusConnected(ShapeExtend_FAIL)) TRACE("CheckConnected Status ShapeExtend_FAIL");
                if (analyze.StatusConnected(ShapeExtend_FAIL1)) TRACE("CheckConnected Status ShapeExtend_FAIL1");
                if (analyze.StatusConnected(ShapeExtend_FAIL2)) TRACE("CheckConnected Status ShapeExtend_FAIL2");
            }
            if (analyze.CheckGap2d(i)) {
                TRACE2(std::ostringstream{} << "CheckGap2d TRUE for " << i);
            } else {
                TRACE2(std::ostringstream{} << "CheckGap2d FALSE for " << i);
                if (analyze.StatusGaps2d(ShapeExtend_OK)) TRACE("CheckGap2d Status ShapeExtend_OK");
                if (analyze.StatusGaps2d(ShapeExtend_DONE)) TRACE("CheckGap2d Status ShapeExtend_DONE");
                if (analyze.StatusGaps2d(ShapeExtend_FAIL)) TRACE("CheckGap2d Status ShapeExtend_FAIL");
            }
            if (analyze.CheckLacking(i)) {
                TRACE2(std::ostringstream{} << "CheckLacking TRUE for " << i);
            } else {
                TRACE2(std::ostringstream{} << "CheckLacking FALSE for " << i);
                if (analyze.StatusLacking(ShapeExtend_OK)) TRACE("CheckLacking Status ShapeExtend_OK");
                if (analyze.StatusLacking(ShapeExtend_DONE)) TRACE("CheckLacking Status ShapeExtend_DONE");
                if (analyze.StatusLacking(ShapeExtend_DONE1)) TRACE("CheckLacking Status ShapeExtend_DONE1");
                if (analyze.StatusLacking(ShapeExtend_DONE2)) TRACE("CheckLacking Status ShapeExtend_DONE2");
                if (analyze.StatusLacking(ShapeExtend_FAIL)) TRACE("CheckLacking Status ShapeExtend_FAIL");
            }
        }
        TRACE2(std::ostringstream{} << "MinDistance2d " << analyze.MinDistance2d());
        TRACE2(std::ostringstream{} << "MaxDistance2d " << analyze.MaxDistance2d());
        TRACE2(std::ostringstream{} << "MinDistance3d " << analyze.MinDistance3d());
        TRACE2(std::ostringstream{} << "MaxDistance3d " << analyze.MaxDistance3d());
    }
}

static void wire_check2(const TopoDS_Wire &wire) {
    BRepCheck_Wire checker(wire);
    BRepCheck_Status closedStatus = checker.Closed();
    TRACE2(std::ostringstream{} << "closedStatus " << closedStatus);

    if (closedStatus == BRepCheck_NoError) TRACE("BRepCheck_NoError => Closed");
    if (closedStatus == BRepCheck_NotConnected) TRACE("BRepCheck_NotConnected => wire is not topologically closed");
    if (closedStatus == BRepCheck_RedundantEdge) TRACE("BRepCheck_RedundantEdge => edge is in wire more than 3 times or in case of 2 occurrences if not with FORWARD and REVERSED orientation.");
    if (closedStatus == BRepCheck_UnorientableShape) TRACE("BRepCheck_UnorientableShape => ???");
    if (closedStatus == BRepCheck_NotClosed) TRACE("BRepCheck_NotClosed => ???");
    if (closedStatus == BRepCheck_CheckFail) TRACE("BRepCheck_CheckFail => ???");
}

extern "C" TopoDS_Face *new_TopoDS_Face(void) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(); });
}

extern "C" TopoDS_Face *new_TopoDS_Face__face(const TopoDS_Face *face) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(*face); });
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(const TopoDS_Wire *wire) {
    TRACE(wire->IsNull() ? "NULL" : "NOT NULL");
    TRACE(wire->Closed() ? "Closed" : "NOT Closed");
    TRACE(wire->Infinite() ? "Infinite" : "NOT Infinite");
    TRACE(wire->Convex() ? "Convex" : "NOT Convex");
    return guard(__func__, [&] {
        wire_check2(*wire);
        wire_check1(*wire);
        return new TopoDS_Face(BRepBuilderAPI_MakeFace(*wire).Face());
    });
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace__gp_Pln(const gp_Pln *plane) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(BRepBuilderAPI_MakeFace(*plane).Face()); });
}

extern "C" TopoDS_Face *new_TopoDS_Face__BRepBuilderAPI_MakeFace(BRepBuilderAPI_MakeFace *face) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(face->Face()); });
}

extern "C" TopoDS_Face *TopoDS_Face__BRepBuilderAPI_MakeFace__Face(BRepBuilderAPI_MakeFace *MKF1) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Face(MKF1->Face()); });
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeFace(); });
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeWire *wire) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeFace(wire->Wire()); });
}

extern "C" BRepBuilderAPI_MakeFace *new_BRepBuilderAPI_MakeFace__s_um_uM_vm_vM_Tol(
    const occ::handle<Geom_Surface> *S, double UMin, double UMax, double VMin, double VMax, double TolDegen) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_MakeFace(*S, UMin, UMax, VMin, VMax, TolDegen); });
}

extern "C" TopoDS_Face *new_TopoDS_Face__ShapeFix_Face__surface_wire_precision_maxTol(
    const occ::handle<Geom_Surface> *S, const TopoDS_Wire *W, double Precision, double MaxTolerance) {
    TRACE("");
    return guard(__func__, [&] {
        // Not checked inside: the edges of W have no curve on S until ShapeFix_Face projects them
        BRepBuilderAPI_MakeFace makeFace(*S, *W, false);
        if (!makeFace.IsDone()) throw Standard_Failure("BRepBuilderAPI_MakeFace failed");
        ShapeFix_Face fix(makeFace.Face());
        fix.SetPrecision(Precision);
        fix.SetMaxTolerance(MaxTolerance);
        fix.Perform();
        if (fix.Status(ShapeExtend_FAIL)) throw Standard_Failure("ShapeFix_Face failed");
        // Perform leaves a single wire as drawn, which may bound the outside of the face
        fix.FixOrientation();
        return new TopoDS_Face(fix.Face());
    });
}

extern "C" void _BRepBuilderAPI_MakeFace__Init(BRepBuilderAPI_MakeFace *MF, const occ::handle<Geom_Surface> *S, int Bound, double TolDegen) {
    TRACE("");
    guard(__func__, [&] { MF->Init(*S, to_bool(Bound), TolDegen); });
}

extern "C" void _BRepBuilderAPI_MakeFace__Add__BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeFace *MF, BRepBuilderAPI_MakeWire *W) {
    TRACE("");
    guard(__func__, [&] { MF->Add(W->Wire()); });
}

extern "C" occ::handle<Geom_Surface> *handle_Geom_Surface__TopoDS_Face(const TopoDS_Face *face) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<Geom_Surface>(BRep_Tool::Surface(*face)); });
}

extern "C" double *R6_TopoDS_Shape__PlanarFace__dir_hasPos_pos(const TopoDS_Shape *shape, double dx, double dy, double dz, int hasPos, double px, double py, double pz) {
    TRACE("");
    return guard(__func__, [&]() -> double * {
        const gp_Dir dir(dx, dy, dz);
        const gp_Pnt pos(px, py, pz);
        const TopoDS_Vertex posVertex = BRepBuilderAPI_MakeVertex(pos);
        bool found = false;
        double best = 0;
        gp_Pnt origin;
        gp_Dir normal;
        for (TopExp_Explorer ex(*shape, TopAbs_FACE); ex.More(); ex.Next()) {
            const TopoDS_Face &face = TopoDS::Face(ex.Current());
            BRepAdaptor_Surface surface(face);
            if (surface.GetType() != GeomAbs_Plane) continue;
            gp_Pln plane = surface.Plane();
            gp_Dir n = plane.Axis().Direction();
            if (face.Orientation() == TopAbs_REVERSED) n.Reverse();
            if (n.Dot(dir) < std::cos(M_PI / 180)) continue;

            GProp_GProps props;
            BRepGProp::SurfaceProperties(face, props);
            gp_Pnt centre = props.CentreOfMass();
            // the farthest along dir, or the closest to pos
            double score;
            if (to_bool(hasPos)) {
                BRepExtrema_DistShapeShape distance(posVertex, face);
                if (!distance.IsDone()) continue;
                score = -distance.Value();
            } else score = gp_Vec(centre.XYZ()).Dot(gp_Vec(dir));
            if (found && score <= best) continue;
            found = true;
            best = score;
            normal = n;
            origin = to_bool(hasPos) ? pos.Translated(-gp_Vec(n) * gp_Vec(n).Dot(gp_Vec(plane.Location(), pos))) : centre;
        }
        if (!found) return nullptr;
        return new double[6]{origin.X(), origin.Y(), origin.Z(), normal.X(), normal.Y(), normal.Z()};
    });
}

extern "C" double *R6_TopoDS_Shape__CylindricalFaceAxis__hasPos_pos(const TopoDS_Shape *shape, int hasPos, double px, double py, double pz) {
    TRACE("");
    return guard(__func__, [&]() -> double * {
        const gp_Pnt pos(px, py, pz);
        const TopoDS_Vertex posVertex = BRepBuilderAPI_MakeVertex(pos);
        bool found = false;
        double best = 0;
        gp_Pnt origin;
        gp_Dir axis;
        for (TopExp_Explorer ex(*shape, TopAbs_FACE); ex.More(); ex.Next()) {
            const TopoDS_Face &face = TopoDS::Face(ex.Current());
            BRepAdaptor_Surface surface(face);
            if (surface.GetType() != GeomAbs_Cylinder) continue;
            gp_Cylinder cylinder = surface.Cylinder();

            // the largest radius, or the closest to pos
            double score;
            if (to_bool(hasPos)) {
                BRepExtrema_DistShapeShape distance(posVertex, face);
                if (!distance.IsDone()) continue;
                score = -distance.Value();
            } else score = cylinder.Radius();
            if (found && score <= best) continue;
            found = true;
            best = score;

            // a direction independent of the surface parametrization
            const gp_Ax1 &a = cylinder.Axis();
            gp_Dir d = a.Direction();
            const double eps = 1e-9;
            if (d.Z() < -eps || (std::abs(d.Z()) <= eps && (d.Y() < -eps || (std::abs(d.Y()) <= eps && d.X() < 0))))
                d.Reverse();
            axis = d;
            if (to_bool(hasPos)) {
                origin = a.Location().Translated(gp_Vec(d) * gp_Vec(a.Location(), pos).Dot(gp_Vec(d)));
            } else {
                // V runs along the axis of a cylindrical surface: start at the face end the lowest along d
                double umin, umax, vmin, vmax;
                BRepTools::UVBounds(face, umin, umax, vmin, vmax);
                const gp_Ax3 &p = cylinder.Position();
                gp_Pnt e1 = p.Location().Translated(gp_Vec(p.Direction()) * vmin);
                gp_Pnt e2 = p.Location().Translated(gp_Vec(p.Direction()) * vmax);
                origin = gp_Vec(e1.XYZ()).Dot(gp_Vec(d)) <= gp_Vec(e2.XYZ()).Dot(gp_Vec(d)) ? e1 : e2;
            }
        }
        if (!found) return nullptr;
        return new double[6]{origin.X(), origin.Y(), origin.Z(), axis.X(), axis.Y(), axis.Z()};
    });
}

extern "C" gp_Dir *new_gp_Dir__Normal__TopoDS_Face(const TopoDS_Face *aCurrentFace) {
    TRACE("");
    return guard(__func__, [&] {
        double umin, umax, vmin, vmax;
        BRepTools::UVBounds(*aCurrentFace, umin, umax, vmin, vmax);
        occ::handle<Geom_Surface> aSurface = BRep_Tool::Surface(*aCurrentFace);
        GeomLProp_SLProps props(aSurface, (umin + umax) / 2, (vmin + vmax) / 2, 1, 0.01);
        gp_Dir n = props.Normal();
        std::cout << "gp_dir_normal_to_face: umin = " << umin << " umax = " << umax << " vmin = " << vmin << " vmax = " <<
            vmax << " n.X " << n.X() << " n.Y " << n.Y() << " n.Z " << n.Z() << std::endl;
        return new gp_Dir(n);
    });
}

/*

    Shape lists and compounds

*/

extern "C" TopTools_ListOfShape *new_TopTools_ListOfShape(void) {
    TRACE("");
    return guard(__func__, [&] { return new TopTools_ListOfShape(); });
}

extern "C" void _TopTools_ListOfShape__Append__TopoDS_Shape(TopTools_ListOfShape *l, const TopoDS_Shape *shape) {
    TRACE("");
    guard(__func__, [&] { l->Append(*shape); });
}

extern "C" TopoDS_Compound *new_TopoDS_Compound(void) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Compound(); });
}

extern "C" BRep_Builder *new_BRep_Builder(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRep_Builder(); });
}

extern "C" void _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(const TopoDS_Builder *builder, TopoDS_Shape *inThis, const TopoDS_Shape *toAdd) {
    TRACE("");
    guard(__func__, [&] { builder->Add(*inThis, *toAdd); });
}

extern "C" void _TopoDS_Builder__MakeCompound__TopoDS_Compound(const TopoDS_Builder *b, TopoDS_Compound *c) {
    TRACE("");
    guard(__func__, [&] { b->MakeCompound(*c); });
}

extern "C" void _BRepLib__BuildCurves3d__TopoDS_Shape(const TopoDS_Shape *w1) {
    TRACE("");
    guard(__func__, [&] { BRepLib::BuildCurves3d(*w1); });
}

/*

    Wire healing

*/

extern "C" occ::handle<ShapeExtend_WireData> *new_ShapeExtend_WireData(void) {
    TRACE("");
    return guard(__func__, [&] { return new occ::handle<ShapeExtend_WireData>(new ShapeExtend_WireData()); });
}

extern "C" void _ShapeExtend_WireData__Add__TopoDS_Edge(occ::handle<ShapeExtend_WireData> *data, const TopoDS_Edge *edge, int atnum) {
    TRACE("");
    guard(__func__, [&] { (*data)->Add(*edge, atnum); });
}

extern "C" void _ShapeExtend_WireData__Add__TopoDS_Wire(occ::handle<ShapeExtend_WireData> *data, const TopoDS_Wire *wire, int atnum) {
    TRACE("");
    guard(__func__, [&] { (*data)->Add(*wire, atnum); });
}

extern "C" TopoDS_Wire *util_ShapeFix_Wire__Load__ShapeExtend_WireData(const occ::handle<ShapeExtend_WireData> *data) {
    TRACE("");
    return guard(__func__, [&] {
        BRepBuilderAPI_MakeWire aMakeWire;
        occ::handle<ShapeFix_Wire> sfw = new ShapeFix_Wire();
        ShapeFix_ShapeTolerance FTol;

        sfw->Load(*data);
        sfw->Perform();
        //Reorder edges is very important
        sfw->FixReorder();
        sfw->SetMaxTolerance(0.01);
        sfw->ClosedWireMode() = true;
        sfw->FixConnected(1.e-3);
        sfw->FixClosed(1.e-3);
        for (int i = 1; i <= sfw->NbEdges(); i++) {
            TopoDS_Edge Edge = sfw->WireData()->Edge(i);
            FTol.SetTolerance(Edge, 0.01, TopAbs_VERTEX);
            aMakeWire.Add(Edge);
        }

        return new TopoDS_Wire(aMakeWire.Wire());
    });
}
