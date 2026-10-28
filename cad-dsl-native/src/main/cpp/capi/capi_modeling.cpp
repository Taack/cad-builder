//
// C API: modeling (primitives, sweeps, features, fillets, offsets, booleans, transforms, measures).
//

#include "capi_internal.hxx"

#include <cmath>
#include <stdexcept>

#include <BRepAlgoAPI_Common.hxx>
#include <BRepAlgoAPI_Cut.hxx>
#include <BRepAlgoAPI_Fuse.hxx>
#include <BRepCheck_Analyzer.hxx>
#include <BRepBndLib.hxx>
#include <Bnd_Box.hxx>
#include <BRepOffsetAPI_MakeOffset.hxx>
#include <BRepAdaptor_CompCurve.hxx>
#include <BRepAdaptor_Surface.hxx>
#include <BRepBuilderAPI_MakeEdge.hxx>
#include <BRepBuilderAPI_MakeWire.hxx>
#include <BRepBuilderAPI_MakeFace.hxx>
#include <BRepBuilderAPI_Transform.hxx>
#include <BRepExtrema_DistShapeShape.hxx>
#include <BRepFeat_MakeCylindricalHole.hxx>
#include <BRepFeat_MakeDPrism.hxx>
#include <BRepFeat_MakePipe.hxx>
#include <BRepFilletAPI_MakeFillet.hxx>
#include <BRepGProp.hxx>
#include <BRepLib.hxx>
#include <BRepOffsetAPI_MakePipeShell.hxx>
#include <BRepOffsetAPI_MakeThickSolid.hxx>
#include <BRepOffsetAPI_ThruSections.hxx>
#include <BRepPrimAPI_MakeBox.hxx>
#include <BRepPrimAPI_MakeCone.hxx>
#include <BRepPrimAPI_MakeCylinder.hxx>
#include <BRepPrimAPI_MakePrism.hxx>
#include <BRepPrimAPI_MakeRevol.hxx>
#include <BRepPrimAPI_MakeSphere.hxx>
#include <BRepPrimAPI_MakeTorus.hxx>
#include <BRepTools.hxx>
#include <LocOpe_DPrism.hxx>
#include <Precision.hxx>
#include <ShapeAnalysis.hxx>
#include <TopExp_Explorer.hxx>
#include <TopoDS_Iterator.hxx>
#include <BRep_Builder.hxx>
#include <TopoDS_Compound.hxx>
#include <GProp_GProps.hxx>
#include <HelixBRep_BuilderHelix.hxx>
#include <Standard_Failure.hxx>
#include <TopoDS.hxx>
#include <TopoDS_Edge.hxx>
#include <TopoDS_Face.hxx>
#include <TopoDS_Wire.hxx>
#include <gp_Ax1.hxx>
#include <gp_Ax2.hxx>
#include <gp_Ax3.hxx>
#include <gp_Circ.hxx>
#include <gp_Pln.hxx>
#include <gp_Trsf.hxx>
#include <gp_Vec.hxx>

/*

    Primitives and sweeps

*/

extern "C" BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__x_y_z(double x, double y, double z) {
    TRACE("");
    return guard(__func__, [&] { return new BRepPrimAPI_MakeBox(x, y, z); });
}

extern "C" BRepPrimAPI_MakeBox *new_BRepPrimAPI_MakeBox__Ax2_x_y_z(const gp_Ax2 *Axes, double dx, double dy, double dz) {
    TRACE("");
    return guard(__func__, [&] { return new BRepPrimAPI_MakeBox(*Axes, dx, dy, dz); });
}

extern "C" BRepPrimAPI_MakeCylinder *new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(const gp_Ax2 *Axes, double R, double H) {
    TRACE("");
    return guard(__func__, [&] { return new BRepPrimAPI_MakeCylinder(*Axes, R, H); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeSphere__gp_Ax2_radius_a1_a2(
    const gp_Ax2 *origin, double radius, double angle1, double angle2) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeSphere(*origin, radius, angle1, angle2).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(const gp_Ax2 *origin, double radius, double height) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeCylinder(*origin, radius, height).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H_angle(
    const gp_Ax2 *origin, double R1, double R2, double H, double angle) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeCone(*origin, R1, R2, H, angle).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(const gp_Ax2 *origin, double R1, double R2, double H) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeCone(*origin, R1, R2, H).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeTorus__gp_Ax2_r1_r2(const gp_Ax2 *origin, double radius1, double radius2) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeTorus(*origin, radius1, radius2).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(const TopoDS_Face *face, const gp_Vec *normal) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakePrism(*face, *normal).Shape()); });
}

// The face of wire in plane, wire as its outer bound
static TopoDS_Face planar_face(const gp_Pln &plane, const TopoDS_Wire &wire) {
    TopoDS_Face face = BRepBuilderAPI_MakeFace(plane, wire, true).Face();
    if (!ShapeAnalysis::IsOuterBound(face)) face = BRepBuilderAPI_MakeFace(plane, TopoDS::Wire(wire.Reversed()), true).Face();
    return face;
}

// The solid of LocOpe_DPrism for face, or a null shape when it fails
static TopoDS_Shape dprism(const TopoDS_Face &face, double height, double angle) {
    try {
        // its length is along the walls
        LocOpe_DPrism prism(face, height / std::cos(angle), angle);
        if (prism.IsDone()) {
            TopExp_Explorer solid(prism.Shape(), TopAbs_SOLID);
            if (solid.More() && BRepCheck_Analyzer(solid.Current()).IsValid()) return solid.Current();
        }
    } catch (const Standard_Failure &) {
    }
    return TopoDS_Shape();
}

// The prism of the face of wire along the normal of plane, height long (backward when negative), narrowing by
// height * tan(angle): by LocOpe_DPrism, which fails on some contours, then on the face turned a little around the
// normal (and the solid turned back), else as a ruled loft from wire to its offset
static TopoDS_Shape drafted_solid(const gp_Pln &plane, const TopoDS_Wire &wire, double height, double angle) {
    TopoDS_Face face = planar_face(plane, wire);
    TopoDS_Shape solid = dprism(face, height, angle);
    for (double turn : {1e-5, -1e-5, 1e-4, -1e-4}) {
        if (!solid.IsNull()) return solid;
        gp_Trsf rotation;
        rotation.SetRotation(plane.Axis(), turn);
        TopoDS_Shape turned = dprism(TopoDS::Face(BRepBuilderAPI_Transform(face, rotation, true).Shape()), height, angle);
        if (!turned.IsNull()) solid = BRepBuilderAPI_Transform(turned, rotation.Inverted(), true).Shape();
    }
    if (!solid.IsNull()) return solid;
    BRepOffsetAPI_MakeOffset offset(face, GeomAbs_Intersection);
    offset.Perform(-height * std::tan(angle));
    if (!offset.IsDone()) throw Standard_Failure("Cannot offset the contour of a prism with a draft");
    gp_Trsf move;
    move.SetTranslation(gp_Vec(plane.Axis().Direction()) * height);
    TopExp_Explorer top(BRepBuilderAPI_Transform(offset.Shape(), move).Shape(), TopAbs_WIRE);
    BRepOffsetAPI_ThruSections loft(true, true);
    loft.AddWire(BRepTools::OuterWire(face));
    loft.AddWire(TopoDS::Wire(top.Current()));
    loft.Build();
    if (!loft.IsDone()) throw Standard_Failure("Cannot make the prism with a draft");
    return loft.Shape();
}

// The prism with a draft of a planar face
static TopoDS_Shape drafted_prism(const TopoDS_Face &face, const gp_Vec &dir, double draft) {
    BRepAdaptor_Surface surface(face, false);
    if (surface.GetType() != GeomAbs_Plane) throw Standard_Failure("A prism with a draft needs a planar face");
    gp_Pln plane = surface.Plane();
    gp_Vec normal(plane.Axis().Direction());
    if (dir.Magnitude() < Precision::Confusion() || !dir.IsParallel(normal, Precision::Angular()))
        throw Standard_Failure("A prism with a draft goes along the normal of its face");
    // LocOpe_DPrism ignores the holes of the face: they are cut from the outside, as prisms widening
    double height = dir.Dot(normal);
    double angle = height > 0 ? draft : -draft;
    // the contours drawn on a face may have only their 2D curves
    TopoDS_Face spine = face;
    BRepLib::BuildCurves3d(spine);
    TopoDS_Wire outer = BRepTools::OuterWire(spine);
    TopoDS_Shape result = drafted_solid(plane, outer, height, angle);
    for (TopExp_Explorer wire(spine, TopAbs_WIRE); wire.More(); wire.Next()) {
        if (wire.Current().IsSame(outer)) continue;
        BRepAlgoAPI_Cut cut(result, drafted_solid(plane, TopoDS::Wire(wire.Current()), height, -angle));
        if (!cut.IsDone()) throw Standard_Failure("Cannot cut a hole of the face from the prism with a draft");
        result = cut.Shape();
    }
    return result;
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__LocOpe_DPrism__TopoDS_Shape_gp_Vec_draft(const TopoDS_Shape *faces, const gp_Vec *dir, double draft) {
    TRACE("");
    return guard(__func__, [&] {
        TopoDS_Compound prisms;
        BRep_Builder builder;
        builder.MakeCompound(prisms);
        int count = 0;
        for (TopExp_Explorer face(*faces, TopAbs_FACE); face.More(); face.Next(), count++)
            builder.Add(prisms, drafted_prism(TopoDS::Face(face.Current()), *dir, draft));
        if (count == 0) throw Standard_Failure("No face for the prism with a draft");
        return new TopoDS_Shape(count == 1 ? TopoDS_Iterator(prisms).Value() : TopoDS_Shape(prisms));
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1(const TopoDS_Face *face, const gp_Ax1 *ax1) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeRevol(*face, *ax1).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(const TopoDS_Face *face, const gp_Ax1 *ax1, double angle) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepPrimAPI_MakeRevol(*face, *ax1, angle).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(BRepBuilderAPI_MakeShape *shape) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(shape->Shape()); });
}

/*

    Fillets, offsets, lofts

*/

extern "C" BRepFilletAPI_MakeFillet *new_BRepFilletAPI_MakeFillet__TopoDS_Shape(const TopoDS_Shape *body) {
    TRACE("");
    return guard(__func__, [&] { return new BRepFilletAPI_MakeFillet(*body); });
}

extern "C" void _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(BRepFilletAPI_MakeFillet *make_fillet, double r, const TopoDS_Edge *edge) {
    TRACE("");
    guard(__func__, [&] { make_fillet->Add(r, *edge); });
}

extern "C" BRepOffsetAPI_MakeThickSolid *new_BRepOffsetAPI_MakeThickSolid(void) {
    TRACE("");
    return guard(__func__, [&] { return new BRepOffsetAPI_MakeThickSolid(); });
}

extern "C" void _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(
    BRepOffsetAPI_MakeThickSolid *thick_solid, const TopoDS_Shape *shape, const TopTools_ListOfShape *face_to_remove,
    double thickness, double tol) {
    TRACE("");
    guard(__func__, [&] { thick_solid->MakeThickSolidByJoin(*shape, *face_to_remove, thickness, tol); });
}

extern "C" BRepOffsetAPI_ThruSections *new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(int isSolid, int ruled, double pres3d) {
    TRACE("");
    return guard(__func__, [&] { return new BRepOffsetAPI_ThruSections(to_bool(isSolid), to_bool(ruled), pres3d); });
}

extern "C" void _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(BRepOffsetAPI_ThruSections *thru_sections, const TopoDS_Wire *w) {
    TRACE("");
    guard(__func__, [&] { thru_sections->AddWire(*w); });
}

extern "C" void _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(BRepOffsetAPI_ThruSections *thru_sections, int b) {
    TRACE("");
    guard(__func__, [&] { thru_sections->CheckCompatibility(to_bool(b)); });
}

extern "C" TopoDS_Wire *new_TopoDS_Wire__HelixBRep_BuilderHelix__ax2_diam1_diam2_height_pitch(
    const gp_Ax2 *axes, double diam1, double diam2, double height, double pitch) {
    TRACE("");
    return guard(__func__, [&] {
        NCollection_Array1<double> heights(1, 1), pitches(1, 1);
        NCollection_Array1<bool> isPitches(1, 1);
        heights(1) = height;
        pitches(1) = std::abs(pitch);
        isPitches(1) = true;

        // The builder only takes positive pitches: the hand comes from the frame, right-handed when it is direct
        gp_Ax3 frame(*axes);
        if (pitch < 0) frame.YReverse();

        HelixBRep_BuilderHelix builder;
        if (diam1 == diam2)
            builder.SetParameters(frame, diam1, heights, pitches, isPitches);
        else
            builder.SetParameters(frame, diam1, diam2, heights, pitches, isPitches);
        builder.Perform();
        if (builder.ErrorStatus() != 0)
            throw Standard_Failure(("HelixBRep_BuilderHelix error status " + std::to_string(builder.ErrorStatus())).c_str());
        return new TopoDS_Wire(TopoDS::Wire(builder.Shape()));
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepOffsetAPI_MakePipeShell__spine_profile_binormal(
    const TopoDS_Wire *spine, const TopoDS_Wire *profile, const gp_Dir *binormal) {
    TRACE("");
    return guard(__func__, [&] {
        // Profiles drawn on a plane only have 2D curves
        BRepLib::BuildCurves3d(*profile);

        BRepOffsetAPI_MakePipeShell pipe(*spine);
        pipe.SetMode(*binormal);
        pipe.Add(*profile);
        pipe.Build();
        if (!pipe.IsDone() || !pipe.MakeSolid())
            throw Standard_Failure("BRepOffsetAPI_MakePipeShell failed");
        return new TopoDS_Shape(pipe.Shape());
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepOffsetAPI_MakePipeShell__spine_radius(
    const TopoDS_Wire *spine, double radius) {
    TRACE("");
    return guard(__func__, [&] {
        // A circle square to the spine at its start
        BRepAdaptor_CompCurve curve(*spine);
        gp_Pnt start;
        gp_Vec tangent;
        curve.D1(curve.FirstParameter(), start, tangent);
        gp_Circ circle(gp_Ax2(start, gp_Dir(tangent)), radius);
        TopoDS_Wire profile = BRepBuilderAPI_MakeWire(BRepBuilderAPI_MakeEdge(circle).Edge()).Wire();

        // The section keeps square to the spine (corrected Frenet), mitred on the bends between edges that are not
        // tangent: round corners fail on a polyline sampling a curve (NCollection_Array1 out of range)
        BRepOffsetAPI_MakePipeShell pipe(*spine);
        pipe.SetMode(false);
        pipe.SetTransitionMode(BRepBuilderAPI_RightCorner);
        pipe.Add(profile);
        pipe.Build();
        if (!pipe.IsDone() || !pipe.MakeSolid())
            throw Standard_Failure("BRepOffsetAPI_MakePipeShell failed");
        return new TopoDS_Shape(pipe.Shape());
    });
}

/*

    Features

*/

extern "C" BRepFeat_MakePipe *new_BRepFeat_MakePipe__Sbase_Pbase_Skface_Spine_Fuse_Modify(
    const TopoDS_Shape *Sbase, const TopoDS_Shape *Pbase, const TopoDS_Face *Skface, const TopoDS_Wire *Spine,
    int Fuse, int Modify) {
    TRACE("");
    return guard(__func__, [&] { return new BRepFeat_MakePipe(*Sbase, *Pbase, *Skface, *Spine, Fuse, to_bool(Modify)); });
}

extern "C" void _BRepFeat_MakePipe__Perform(BRepFeat_MakePipe *p) {
    TRACE("");
    guard(__func__, [&] { p->Perform(); });
}

extern "C" BRepFeat_MakeDPrism *new_BRepFeat_MakeDPrism__Sbase_Pbase_Skface_Angle_Fuse_Modify(
    const TopoDS_Shape *Sbase, const TopoDS_Face *Pbase, const TopoDS_Face *Skface, double Angle, int Fuse, int Modify) {
    TRACE("");
    return guard(__func__, [&] { return new BRepFeat_MakeDPrism(*Sbase, *Pbase, *Skface, Angle, Fuse, to_bool(Modify)); });
}

extern "C" void _BRepFeat_MakeDPrism__Perform__Height(BRepFeat_MakeDPrism *p, double height) {
    TRACE("");
    guard(__func__, [&] { p->Perform(height); });
}

extern "C" TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_ptFrom_ptTo(
    const TopoDS_Shape *shape, const gp_Ax1 *ax1, double Radius, double PFrom, double PTo) {
    TRACE("");
    TRACE2(std::ostringstream{} << "PFrom = " << PFrom << " PTo = " << PTo);
    return guard(__func__, [&] {
        BRepFeat_MakeCylindricalHole makeCylindrical;
        makeCylindrical.Init(*shape, *ax1);
        if (PFrom > PTo)
            makeCylindrical.Perform(Radius);
        else
            makeCylindrical.Perform(Radius, PFrom, PTo);
        return new TopoDS_Shape(makeCylindrical.Shape());
    });
}

extern "C" TopoDS_Shape *ptrTopoDS_Shape__BRepFeat_MakeCylindricalHole__Perform__TopoDS_Shape_gp_Ax1_r_l(
    const TopoDS_Shape *shape, const gp_Ax1 *ax1, double Radius, double Length) {
    TRACE("");
    TRACE2(std::ostringstream{} << "Length = " << Length);
    return guard(__func__, [&] {
        BRepFeat_MakeCylindricalHole makeCylindrical;
        makeCylindrical.Init(*shape, *ax1);
        makeCylindrical.PerformBlind(Radius, Length);
        makeCylindrical.Build();
        return new TopoDS_Shape(makeCylindrical.Shape());
    });
}

/*

    Transforms

*/

extern "C" BRepBuilderAPI_Transform *new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(
    const TopoDS_Shape *w, const gp_Trsf *trsf, int theCopyGeom, int theCopyMesh) {
    TRACE("");
    return guard(__func__, [&] { return new BRepBuilderAPI_Transform(*w, *trsf, to_bool(theCopyGeom), to_bool(theCopyMesh)); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(
    const TopoDS_Shape *shape, const gp_Trsf *gp_trsf, int theCopyGeom) {
    TRACE("");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepBuilderAPI_Transform(*shape, *gp_trsf, to_bool(theCopyGeom)).Shape()); });
}

/*

    Booleans

*/

extern "C" BRepAlgoAPI_Cut *new_BRepAlgoAPI_Cut__s1_s2(const TopoDS_Shape *result, const TopoDS_Shape *tool) {
    TRACE("");
    return guard(__func__, [&] {
        auto *cut = new BRepAlgoAPI_Cut(*result, *tool);
        cut->Build();
        cut->SimplifyResult();
        return cut;
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__BRepAlgoAPI_Cut__TopoDS_Shape_TopTools_ListOfShape(
    const TopoDS_Shape *result, const TopTools_ListOfShape *aLT) {
    TRACE("");
    return guard(__func__, [&] {
        TopTools_ListOfShape aLS;
        aLS.Append(*result);
        TRACE2(std::ostringstream{} << "aLS.Size() " << aLS.Size() << " aLT.Size() " << aLT->Size());

        BRepAlgoAPI_Cut aBuilder;
        aBuilder.SetArguments(aLS);
        aBuilder.SetTools(*aLT);
        aBuilder.SetRunParallel(false);
        aBuilder.Build();
        aBuilder.SimplifyResult();
        return new TopoDS_Shape(aBuilder.Shape());
    });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_listrOfShape(const TopoDS_Shape *result, const TopTools_ListOfShape *aLT) {
    TRACE("");
    return guard(__func__, [&] {
        TopTools_ListOfShape aLS;
        aLS.Append(*result);

        BRepAlgoAPI_Fuse aBuilder;
        aBuilder.SetArguments(aLS);
        aBuilder.SetTools(*aLT);
        aBuilder.SetRunParallel(false);
        // Fuzzy value left to its default (Precision::Confusion())
        aBuilder.SetNonDestructive(false);
        // No gluing: it skips the intersection of overlapping solids, which stay apart in the result
        aBuilder.SetCheckInverted(false);
        aBuilder.SetUseOBB(true);
        aBuilder.Build();
        aBuilder.SimplifyResult();
        return new TopoDS_Shape(aBuilder.Shape());
    });
}

// The result of a boolean operation, or the errors of the algorithm as an exception instead of a broken shape
template<class Operation>
static TopoDS_Shape *boolean_result(const TopoDS_Shape &s1, const TopoDS_Shape &s2) {
    Operation operation(s1, s2);
    if (operation.HasErrors()) {
        std::ostringstream errors;
        operation.DumpErrors(errors);
        throw std::runtime_error(errors.str());
    }
    return new TopoDS_Shape(operation.Shape());
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(const TopoDS_Shape *result, const TopoDS_Shape *tool) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return boolean_result<BRepAlgoAPI_Cut>(*result, *tool); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return boolean_result<BRepAlgoAPI_Fuse>(*s1, *s2); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_common__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return boolean_result<BRepAlgoAPI_Common>(*s1, *s2); });
}

/*

    Measures

*/

extern "C" gp_Pnt *new_gp_Pnt__CentreOfMass__TopoDS_Shape(const TopoDS_Shape *myShape) {
    TRACE("");
    return guard(__func__, [&] {
        GProp_GProps massProps;
        BRepGProp::SurfaceProperties(*myShape, massProps);
        return new gp_Pnt(massProps.CentreOfMass());
    });
}

extern "C" double *R12_shape_measures(const TopoDS_Shape *shape) {
    TRACE("");
    return guard(__func__, [&]() -> double * {
        // adaptive integration: the fixed Gauss order of the default is wrong by percents on dense B-spline faces;
        // 1e-3 is the largest adaptive tolerance, finer ones are several times slower for a few mm³ more
        GProp_GProps volume, surface;
        BRepGProp::VolumeProperties(*shape, volume, 1e-3);
        BRepGProp::SurfaceProperties(*shape, surface, 1e-3);
        gp_Pnt center = volume.Mass() != 0.0 ? volume.CentreOfMass() : gp_Pnt();
        Bnd_Box box;
        BRepBndLib::AddOptimal(*shape, box, false, false);
        double x1 = 0, y1 = 0, z1 = 0, x2 = 0, y2 = 0, z2 = 0;
        if (!box.IsVoid()) box.Get(x1, y1, z1, x2, y2, z2);
        double valid = BRepCheck_Analyzer(*shape).IsValid() ? 1.0 : 0.0;
        return new double[12]{volume.Mass(), surface.Mass(), center.X(), center.Y(), center.Z(), x1, y1, z1, x2, y2, z2, valid};
    });
}

extern "C" double *R7_BRepExtrema_DistShapeShape__s1_s2(const TopoDS_Shape *Shape1, const TopoDS_Shape *Shape2) {
    TRACE("");
    double *res = guard(__func__, [&]() -> double * {
        BRepExtrema_DistShapeShape dist(*Shape1, *Shape2, 0.01);
        if (!dist.IsDone()) TRACE("NOT DONE");
        if (!dist.Perform()) TRACE("NOT PERFORMED");

        int nbSol = dist.NbSolution();
        TRACE2(std::ostringstream{} << "nbSol" << nbSol);
        if (nbSol <= 0) {
            return nullptr;
        }
        const gp_Pnt pS1 = dist.PointOnShape1(1);
        const gp_Pnt pS2 = dist.PointOnShape2(1);
        double d = dist.Value();
        TRACE2(std::ostringstream{} << "d: " << d);
        return new double[7]{pS1.X(), pS1.Y(), pS1.Z(), pS2.X(), pS2.Y(), pS2.Z(), d};
    });
    return res ? res : new double[7]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -20000000.0};
}
