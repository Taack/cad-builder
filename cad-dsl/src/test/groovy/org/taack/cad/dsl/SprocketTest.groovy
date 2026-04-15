package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.helper.Circle2dConstruct
import org.taack.cad.dsl.helper.CurveIntersection2d
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.geom.ArcOfCircle2d
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.ITrimmable2d

import java.lang.foreign.MemorySegment

import static java.lang.Math.*
import static org.taack.cad.dsl.CadDsl.cd
import static org.taack.occt.NativeLib.*

// mirrorProfile
@CompileStatic
class SprocketTest {

    // The basic inputs needed to build a sprocket
    double roller_diameter = 10.2
    double pitch = 15.875
    BigInteger num_teeth = 40
    double chain_width = 6.35

    // Dimensions derived from the provided inputs
    double roller_radius = roller_diameter / 2.0
    double tooth_angle = (2.0d * PI) / num_teeth.toDouble()
    double pitch_circle_diameter = pitch / sin(tooth_angle.toDouble() / 2.0)
    double pitch_circle_radius = pitch_circle_diameter / 2.0

    double roller_contact_angle_min =
            (PI * 120 / 180) - ((PI / 2) / num_teeth)
    double roller_contact_angle_max =
            (PI * 140 / 180) - ((PI / 2) / num_teeth)
    double roller_contact_angle =
            (roller_contact_angle_min + roller_contact_angle_max) / 2.0

    double tooth_radius_min = 0.505 * roller_diameter
    double tooth_radius_max =
            tooth_radius_min + (0.069 * pow(roller_diameter.toDouble(), 1.0d / 3.0))
    double tooth_radius = (tooth_radius_min + tooth_radius_max) / 2.0

    double profile_radius = 0.12 * roller_diameter * (num_teeth + 2)
    double top_diameter =
            pitch_circle_diameter + ((1 - (1.6 / num_teeth)) * pitch) - roller_diameter
    double top_radius = top_diameter / 2.0

    double thickness = chain_width * 0.95

    // Center hole data
    double center_radius = 125.0 / 2.0

    // Mounting hole data
    BigInteger mounting_hole_count = 6
    double mounting_radius = 153.0 / 2.0
    double hole_radius = 8.5 / 2.0

    /**
     * Build a single tooth
     */
    CadDsl buildTooth() {

        println "Create a 2D arc to form the base of the tooth"
        Vec2d base_center = new Vec2d(pitch_circle_radius + (tooth_radius - roller_radius), 0)
        Vec2d p0v2d
        Vec2d p1
        cd().wireFrom(base_center) {
            Circle2d baseConstruct = circle(tooth_radius)
            ArcOfCircle2d trimmedBase = trimmed(baseConstruct, PI - (roller_contact_angle / 2.0d), PI, true)
            p0v2d = trimmedBase.start
            p1 = trimmedBase.end

            println "Determine the center of the profile circle p0: $p0v2d, p1: $p1"
            double x_distance = cos(roller_contact_angle / 2d) * (profile_radius + tooth_radius)
            double y_distance = sin(roller_contact_angle / 2d) * (profile_radius + tooth_radius)
            Vec2d profileCenter = new Vec2d(pitch_circle_radius - x_distance, y_distance)

            println "Construct the profile circle"
            to profileCenter
            Circle2d profileConstruct = circle(p1.distance(profileCenter))

            println "Construct the outer circle"
            to new Vec2d()
            Circle2d outerConstruct = circle(top_radius)

            println """\
            Calculate the intersection point(s) of the profile circle
            and the outer circle.  If there are two points, pick the one closest
            to the center of the profile circle""".stripIndent()
            CurveIntersection2d inter = new CurveIntersection2d(profileConstruct, outerConstruct)
            Vec2d p2
            if (inter.results.size() == 2)
                if (p1.distance(inter.results[0]) < p1.distance(inter.results[1])) p2 = inter.results[0]
                else p2 = inter.results[1]
            else if (inter.results.size() == 1) p2 = inter.results[1]
            else throw new Exception("Too many intersection points between curves")

            println "Trim the profile circle and mirror"
            ArcOfCircle2d trimmedProfile = trimmed(profileConstruct, p1, p2)

            println "Calculate the outermost point"
            Vec2d vP3 = new Vec2d(cos(tooth_angle / 2d) * top_radius, sin(tooth_angle / 2d) * top_radius)

            println "and use it to create the third arc"
            ArcOfCircle2d trimmedOuter = trimmed(outerConstruct, p2, vP3)

            println "Mirror and reverse the three arcs"
            Vec2d dirMirror = new Vec2d(1, 0).rotate(tooth_angle / 2.0d)
            ITrimmable2d mirrorOuter = mirror(trimmedOuter, new Vec2d(), dirMirror)

            println "Replace the two outer arcs with a single one"
            to trimmedOuter.start
            arc(mirrorOuter.end, trimmedOuter.end)
            ITrimmable2d mirrorProfile = mirror(trimmedProfile, new Vec2d(), dirMirror)
            ITrimmable2d mirrorBase = mirror(trimmedBase, new Vec2d(), dirMirror)

            println "Create an arc for the inside of the wedge"
            to new Vec2d()
            Circle2d innerCircleConstruct = circle(top_radius - roller_diameter)
            Vec2d innerStartVec2d = new Vec2d(top_radius - roller_diameter, 0)
            ArcOfCircle2d innerArc = trimmed(innerCircleConstruct, innerStartVec2d, tooth_angle, true)

            removeFromConstruction(innerArc)

            println "Convert the 2D arcs and two extra lines to 3D edges"
            Vec2d p4v2d = mirrorBase.end
            Vec2d p5v2d = innerArc.start
            println "p4v2d = $p4v2d, p5v2d = $p5v2d"
            to p4v2d
            edge(p5v2d)
            addToConstruction(innerArc)
            Vec2d p6v2d = innerArc.end
            to p6v2d
            edge(p0v2d)

            removeFromConstruction(baseConstruct, profileConstruct, outerConstruct, trimmedOuter, mirrorOuter, innerCircleConstruct)

        }.toFace().prism(thickness).toCadDsl()
    }

    /**
     * Round off the edge of the single tooth
     */
    CadDsl roundTooth(CadDsl wedge) {
        double round_x = 2.6
        double round_z = 0.06 * pitch
        double round_radius = pitch

        println "Determine where the circle used for rounding has to start and stop"
        Vec2d p2d_1v = new Vec2d(top_radius - round_x, 0)
        Vec2d p2d_2v = new Vec2d(top_radius, round_z)

        wedge.cut {
            wireFrom(new Vec()) {
                println "Construct the rounding circle"
                Circle2dConstruct circle2dConstruct = new Circle2dConstruct(p2d_1v, p2d_2v, round_radius)
                Circle2d c1 = circle2dConstruct.circle2d1()
                Circle2d c2 = circle2dConstruct.circle2d2()
                Circle2d round_circle

                println "round_circle_2d_1; ${c1.pos}"
                if (c1.pos.y >= 0) round_circle = c1
                else round_circle = c2

                println "Calculate extra points used to construct lines"
                Vec p1v = new Vec(p2d_1v.x, 0, p2d_1v.y)
                Vec p2v = new Vec(p2d_2v.x, 0, p2d_2v.y)
                Vec p3v = new Vec(p2d_2v.x + 1, 0, p2d_2v.y)
                Vec p4v = new Vec(p2d_2v.x + 1, 0, p2d_1v.y - 1)
                Vec p5v = new Vec(p2d_1v.x, 0, p2d_1v.y - 1)

                println "Convert the arc and four extra lines into 3D edges"
                to p1v
                adapt3d trimmed(round_circle, p2d_1v, p2d_2v), new Vec(0, -1, 0), new Vec(1, 0, 0)
                edge p3v
                edge p4v
                edge p5v
                edge p1v
            }.toFace().revolution(tooth_angle).display()
        }.display().toCadDsl()
    }

    /**
     * Round off the edge of the single tooth
     */
    CadDsl roundTooth2d(CadDsl wedge) {
        double round_x = 2.6
        double round_z = 0.06 * pitch
        double round_radius = pitch

        println "Determine where the circle used for rounding has to start and stop"
        Vec2d p2d_1v = new Vec2d(top_radius - round_x, 0)
        Vec2d p2d_2v = new Vec2d(top_radius, round_z)

        wedge.fuse {
            wireFrom(new Vec2d()) {
                println "Construct the rounding circle"
                Circle2dConstruct circle2dConstruct = new Circle2dConstruct(p2d_1v, p2d_2v, round_radius)
                Circle2d c1 = circle2dConstruct.circle2d1()
                Circle2d c2 = circle2dConstruct.circle2d2()
                Circle2d round_circle

                println "round_circle_2d_1; ${c1.pos}"
                if (c1.pos.y >= 0) round_circle = c1
                else round_circle = c2

                println "Calculate extra points used to construct lines"
                Vec2d p3v2d = new Vec2d(p2d_2v.x + 1, p2d_2v.y)
                Vec2d p4v2d = new Vec2d(p2d_2v.x + 1, p2d_1v.y - 1)
                Vec2d p5v2d = new Vec2d(p2d_1v.x, p2d_1v.y - 1)

                println "Convert the arc and four extra lines into 3D edges"
                to p2d_1v
                trimmed(round_circle, p2d_1v, p2d_2v)
                edge p3v2d
                edge p4v2d
                edge p5v2d
                edge p2d_1v
            }.toFace().revolution(new Vec(), new Vec(0, 1, 0), tooth_angle).display()
        }.display().toCadDsl()
    }

    /**
     * Round off the edge of the single tooth
     */
//    MemorySegment roundTooth(MemorySegment wedge) {
//        double round_x = 2.6
//        double round_z = 0.06 * pitch
//        double round_radius = pitch
//
//        println "Determine where the circle used for rounding has to start and stop"
//        Vec2d p2d_1v = new Vec2d(top_radius - round_x, 0)
//        def p2d_1 = p2d_1v.toGpPnt2d()
//        Vec2d p2d_2v = new Vec2d(top_radius, round_z)
//        def p2d_2 = p2d_2v.toGpPnt2d()
//
//        println "Construct the rounding circle"
//        def round_circle = new_GccAna_Circ2d2TanRad__p2d1_p2d2_roundRadius(p2d_1, p2d_2, round_radius, 0.01d)
//        if (i_GccAna_Circ2d2TanRad__NbSolutions(round_circle) != 2)
//            throw new RuntimeException()
//
//        def round_circle_2d_1 = ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(round_circle, 1)
//        def round_circle_2d_2 = ref_gp_Circ2d__GccAna_Circ2d2TanRad__ThisSolution__index(round_circle, 2)
//        MemorySegment round_circle_2d
//        Vec2d roundP1 = Vec2d.fromAPnt ref_gp_Pnt2d__gp_Ax22d__Location(ref_Position__gp_Circ2d__Position(round_circle_2d_1))
//
//        println "round_circle_2d_1; ${roundP1.y}"
//        if (roundP1.y >= 0)
//            round_circle_2d = round_circle_2d_1
//        else
//            round_circle_2d = round_circle_2d_2
//
//        println "Remove the arc used for rounding"
//        def trimmed_circle = handle_Geom2d_TrimmedCurve__GCE2d_MakeArcOfCircle__cir2d_p1_p2(round_circle_2d, p2d_1, p2d_2)
//
//        println "Calculate extra points used to construct lines"
//        Vec p1v = new Vec(p2d_1v.x, 0, p2d_1v.y)
//        def p1 = p1v.toGpPnt()
//        Vec p2v = new Vec(p2d_2v.x, 0, p2d_2v.y)
//        def p2 = p2v.toGpPnt()
//        Vec p3v = new Vec(p2d_2v.x + 1, 0, p2d_2v.y)
//        def p3 = p3v.toGpPnt()
//        Vec p4v = new Vec(p2d_2v.x + 1, 0, p2d_1v.y - 1)
//        def p4 = p4v.toGpPnt()
//        Vec p5v = new Vec(p2d_1v.x, 0, p2d_1v.y - 1)
//        def p5 = p5v.toGpPnt()
//
//        println "Convert the arc and four extra lines into 3D edges"
//        def plane = new_gp_Pln__gp_Ax3(new_gp_Ax3__p_dN_dX(new Vec().toGpPnt(), new Vec(0, -1, 0).toGpDir(), new Vec(1, 0, 0).toGpDir()))
//        def arc1 = new_BRepBuilderAPI_MakeEdge__Geom_Curve(handle_Geom_Curve__GeomAPI_To3d__Geom2d_Curve_gp_Pln(trimmed_circle, plane))
//        def lin1 = new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(p2, p3)
//        def lin2 = new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(p3, p4)
//        def lin3 = new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(p4, p5)
//        def lin4 = new_BRepBuilderAPI_MakeEdge__ptFrom_ptTo(p5, p1)
//
//        println "Make a wire composed of the edges"
//        def round_wire = new_BRepBuilderAPI_MakeWire__BRepBuilderAPI_MakeEdge(arc1)
//        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(round_wire, lin1)
//        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(round_wire, lin2)
//        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(round_wire, lin3)
//        _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeEdge(round_wire, lin4)
//
//        println "Turn the wire into a face"
//        def round_face = new_TopoDS_Face__BRepBuilderAPI_MakeFace(new_BRepBuilderAPI_MakeFace__BRepBuilderAPI_MakeWire(round_wire))
//
//        println "Revolve the face around the Z axis over the tooth angle"
//        def rounding_cut_1 = new_TopoDS_Shape__BRepPrimAPI_MakeRevol__TopoDS_Face_gp_Ax1_ang(round_face, new_gp_Ax1__p_dir(new Vec().toGpPnt(), new Vec(1).toGpDir()), tooth_angle)
//
//        // Construct a mirrored copy of the first cutting shape
//        def mirror = new_gp_Trsf()
//        _gp_Trsf__SetMirror__gp_Ax2(mirror, new_gp_Ax2__gp_Pnt_gp_Dir(new Vec().toGpPnt(), new Vec(1).toGpDir()))
//        def mirrored_cut_1 = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(rounding_cut_1, mirror, 0)
//
//        println "and translate it so that it ends up on the other side of the wedge"
//        def translate = new_gp_Trsf()
//        _gp_Trsf__SetTranslation__gp_Vec(translate, new Vec(0, 0, thickness).toGpVec())
//
//        def rounding_cut_2 = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(mirrored_cut_1, translate, 0)
//
//        println "Cut the wedge using the first and second cutting shape"
//        def cut_1 = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(wedge, rounding_cut_1)
//
//        def cut_2 = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(cut_1, rounding_cut_2)
//        cut_2
//    }
    /**
     * Copy a single tooth to form a complete sprocket
     * This is done in two stages to speed up the fusing
     * @param base_shape
     * @return
     */
    MemorySegment cloneTooth(MemorySegment base_shape) {
        def clone = new_gp_Trsf()
        println "TopoDS_Shape aggregated_shape = base_shape"
        def grouped_shape = base_shape

        println "Find a divisor, between 1 and 8, for the number_of teeth"
        int multiplier = 1
        int max_multiplier = 1

        for (int current_multiplier = 1;
             current_multiplier <= 8;
             current_multiplier++) {
            if ((num_teeth % multiplier) == 0)
                max_multiplier = current_multiplier
        }

        multiplier = max_multiplier
        def gp_OZ = new_gp_Ax1__p_dir(new Vec().toGpPnt(), new Vec(1).toGpDir())
        for (int i = 1; i < multiplier; i++) {
            _gp_Trsf__SetRotation__gp_Vec(clone, gp_OZ, -i * tooth_angle)

            def rotated_shape = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(base_shape, clone, 1)
            grouped_shape = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(grouped_shape, rotated_shape)
            println i
        }

        def aggregated_shape = grouped_shape

        println "Rotate the basic tooth and fuse together"
        for (int i = 1; i < num_teeth / multiplier; i++) {
            _gp_Trsf__SetRotation__gp_Vec(clone, gp_OZ, -i * multiplier * tooth_angle)
            def rotated_shape = new_TopoDS_Shape__BRepBuilderAPI_Transform__Shape_gp_Trsf_bCopy(grouped_shape, clone, 1)
            aggregated_shape = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(aggregated_shape, rotated_shape)
            println i
        }

        println "Fuse a disc to fill in the center"
        def cylinder = new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(new_gp_Ax2_DZ(),
                top_radius - roller_diameter,
                thickness)
        new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(aggregated_shape, cylinder)
    }

    /**
     * Create a hole in the center of the sprocket
     * @param shape
     * @return
     */
    MemorySegment centerHole(MemorySegment shape) {
        def cylinder = new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(new_gp_Ax2_DZ(), center_radius, thickness)
        new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(shape, cylinder)
    }

    /**
     * Provide chamfered mounting holes
     * @param shape
     * @return
     */
    MemorySegment mountingHoles(MemorySegment base) {
        def result = base
        for (int i = 0; i < mounting_hole_count; i++) {
            Vec centerVec = new Vec(
                    cos(i * PI / 3) * mounting_radius,
                    sin(i * PI / 3) * mounting_radius,
                    0.0)
            MemorySegment center_axis = new_gp_Ax2__gp_Pnt_gp_Dir(centerVec.toGpPnt(), new Vec(1).toGpDir())
            def cylinder = new_TopoDS_Shape__BRepPrimAPI_MakeCylinder__gp_Ax2_radius_height(center_axis, hole_radius, thickness)

            result = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(result, cylinder)

            def cone = new_TopoDS_Shape__BRepPrimAPI_MakeCone__gp_Ax2_R1_R2_H(
                    center_axis,
                    (hole_radius + thickness / 2.0d),
                    hole_radius, thickness / 2.0d)
            result = new_TopoDS_Shape__bBRepAlgoAPI_Cut__s1_s2(result, cone)
        }

        return result

    }

    @Test
    void "Build Tooth"() {
//        buildTooth()
        CadDsl tooth = buildTooth()
        roundTooth(tooth)
//        def manyTooth = cloneTooth(roundTooth)
//        def manyToothWithCenterHole = centerHole(manyTooth)
//        visualize mountingHoles(manyToothWithCenterHole)
    }
}
