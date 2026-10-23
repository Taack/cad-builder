//
// C API: modeling (primitives, sweeps, features, fillets, offsets, booleans, transforms, measures).
//

#include "capi_internal.hxx"

#include <cmath>

#include <BRepAlgoAPI_Common.hxx>
#include <BRepAlgoAPI_Cut.hxx>
#include <BRepAlgoAPI_Fuse.hxx>
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
    std::cout << "PFrom = " << PFrom << "PTo = " << PTo << " " << (PFrom > PTo) << std::endl;
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
    std::cout << "Length = " << Length << std::endl;
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
        std::cout << "aLS.Size()" << aLS.Size() << std::endl;
        std::cout << "aLT.Size()" << aLT->Size() << std::endl;

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

extern "C" TopoDS_Shape *new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(const TopoDS_Shape *result, const TopoDS_Shape *tool) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepAlgoAPI_Cut(*result, *tool).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepAlgoAPI_Fuse(*s1, *s2).Shape()); });
}

extern "C" TopoDS_Shape *new_TopoDS_Shape__brep_algoapi_common__s1_s2(const TopoDS_Shape *s1, const TopoDS_Shape *s2) {
    TRACE("OBSOLETE");
    return guard(__func__, [&] { return new TopoDS_Shape(BRepAlgoAPI_Common(*s1, *s2).Shape()); });
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
