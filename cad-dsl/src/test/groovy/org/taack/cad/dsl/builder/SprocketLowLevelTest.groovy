package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.occt.NativeLib as nl

import java.lang.foreign.MemorySegment

// https://algotopia.com/contents/opencascade/opencascade_sprocket
@CompileStatic
class SprocketLowLevelTest {

    // The basic inputs needed to build a sprocket
    double roller_diameter = 10.2
    double pitch = 15.875
    BigInteger num_teeth = 40
    double chain_width = 6.35

    // Dimensions derived from the provided inputs
    double roller_radius = roller_diameter / 2.0
    double tooth_angle = (2 * Math.PI) / num_teeth
    double pitch_circle_diameter = pitch / Math.sin(tooth_angle.toDouble() / 2.0)
    double pitch_circle_radius = pitch_circle_diameter / 2.0

    double roller_contact_angle_min =
            (Math.PI * 120 / 180) - ((Math.PI / 2) / num_teeth)
    double roller_contact_angle_max =
            (Math.PI * 140 / 180) - ((Math.PI / 2) / num_teeth)
    double roller_contact_angle =
            (roller_contact_angle_min + roller_contact_angle_max) / 2.0

    double tooth_radius_min = 0.505 * roller_diameter
    double tooth_radius_max =
            tooth_radius_min + (0.069 * Math.pow(roller_diameter.toDouble(), 1.0d / 3.0))
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


    MemorySegment buildTooth() {

        println "Create a 2D arc to form the base of the tooth"
        Vec2d base_center = new Vec2d(pitch_circle_radius + (tooth_radius - roller_radius), 0)
        def base_circle = nl.gp_circ2d_new(nl.gp_ax_2d_new_pt_dir(base_center.toGpPnt2d(), nl.gp_dir_2d_new()),
                tooth_radius)
        def trimmed_base = nl.gce2d_makearcofcircle_from_angles(base_circle, Math.PI - (roller_contact_angle / 2.0), Math.PI)
        nl.geom2d_trimmedcurve_reverse(trimmed_base)
        def p0 = nl.geom2d_trimmedcurve_startpoint(trimmed_base)
        def p1 = nl.geom2d_trimmedcurve_endpoint(trimmed_base)

        println "Determine the center of the profile circle"
        double x_distance = Math.cos(roller_contact_angle / 2d) * (profile_radius + tooth_radius)
        double y_distance = Math.sin(roller_contact_angle / 2d) * (profile_radius + tooth_radius)
        def profile_center = new Vec2d(pitch_circle_radius - x_distance, y_distance).toGpPnt2d()

        println "Construct the profile circle"
        def profile_circle = nl.gp_circ2d_new(nl.gp_ax_2d_new_pt_dir(profile_center, nl.gp_dir_2d_new()),
                nl.gp_Pnt2d_distance(profile_center, p1))
        def geom_profile_circle = nl.gce2d_makecircle(profile_circle)

        println "Construct the outer circle"
        def outer_circle = nl.gp_circ2d_new(nl.gp_ax_2d_new_pt_dir(new Vec2d(0, 0).toGpPnt2d(), nl.gp_dir_2d_new()),
                top_radius)
        def geom_outer_circle = nl.gce2d_makecircle(outer_circle)

        println """\
        Calculate the intersection point(s) of the profile circle
        and the outer circle.  If there are two points, pick the one closest
        to the center of the profile circle""".stripIndent()

        def inter = nl.geom2dapi_intercurvecurve_new(geom_profile_circle, geom_outer_circle)
        int num_points = nl.geom2dapi_intercurvecurve_nbpoints(inter)

        def p2
        if (num_points == 2) {
            if (nl.gp_Pnt2d_distance(p1, nl.geom2dapi_intercurvecurve_point(inter, 1)) < nl.gp_Pnt2d_distance(p1, nl.geom2dapi_intercurvecurve_point(inter, 2))) {
                p2 = nl.geom2dapi_intercurvecurve_point(inter, 1)
            } else {
                p2 = nl.geom2dapi_intercurvecurve_point(inter, 2)
            }
        } else if (num_points == 1) {
            p2 = nl.geom2dapi_intercurvecurve_point(inter, 1)
        } else throw new Exception("Too many intersection points between curves")

        println "Trim the profile circle and mirror"
        def trimmed_profile = nl.gce2d_makearcofcircle(profile_circle, p1, p2)

        println "Calculate the outermost point"
        Vec2d vP3 = new Vec2d(Math.cos(tooth_angle / 2d) * top_radius, Math.sin(tooth_angle / 2d) * top_radius)

        println "and use it to create the third arc"
        def trimmed_outer = nl.gce2d_makearcofcircle(profile_circle, p2, vP3.toGpPnt2d())

        println "Mirror and reverse the three arcs"
        def mirror_axis = nl.gp_ax_2d_new_pt_dir(new Vec2d().toGpPnt2d(), new Vec2d(1, 0).rotate(tooth_angle / 2.0d).toGpDir2d())
        def mirror_base = nl.geom2d_geometry_copy(trimmed_base)
        def mirror_profile = nl.geom2d_geometry_copy(trimmed_profile)
        def mirror_outer = nl.geom2d_geometry_copy(trimmed_outer)
        nl.geom2d_trimmedcurve_mirror(mirror_base, mirror_axis)
        nl.geom2d_trimmedcurve_mirror(mirror_profile, mirror_axis)
        nl.geom2d_trimmedcurve_mirror(mirror_outer, mirror_axis)
        nl.geom2d_trimmedcurve_reverse(mirror_base)
        nl.geom2d_trimmedcurve_reverse(mirror_profile)
        nl.geom2d_trimmedcurve_reverse(mirror_outer)

        println "Replace the two outer arcs with a single one"
        def outer_start = nl.geom2d_trimmedcurve_startpoint(trimmed_outer)
        def outer_mid = nl.geom2d_trimmedcurve_endpoint(trimmed_outer)
        def outer_end = nl.geom2d_trimmedcurve_endpoint(mirror_outer)
        def outer_arc = nl.gce2d_makearcofcircle_from_points(outer_start, outer_mid, outer_end)

        println "Create an arc for the inside of the wedge"
        def inner_circle = nl.gp_circ2d_new(nl.gp_ax2(new Vec2d().toGpPnt2d(), new Vec2d(1, 0).toGpDir2d()), top_radius - roller_diameter)
        Vec2d innerStartVec2d = new Vec2d(top_radius - roller_diameter, 0)
        def inner_arc = nl.gce2d_makearcofcircle_from_point_angle(inner_circle, innerStartVec2d.toGpPnt2d(), tooth_angle)
        nl.geom2d_trimmedcurve_reverse(inner_arc)

        println "Convert the 2D arcs and two extra lines to 3D edges"
        def plane = nl.plane_create_pt_dir(new Vec().toGpPnt(), new Vec(0, 0, 1).toGpDir())
        def arc1 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(trimmed_base, plane)
        def arc2 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(trimmed_profile, plane)
        def arc3 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(outer_arc, plane)
        def arc4 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(mirror_profile, plane)
        def arc5 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(mirror_base, plane)

        def p4 = nl.geom2d_trimmedcurve_endpoint(mirror_base)
        Vec2d p4v2d = Vec2d.fromAPnt(p4)
        def p5 = nl.geom2d_trimmedcurve_startpoint(inner_arc)
        Vec2d p5v2d = Vec2d.fromAPnt(p5)
        def lin1 = nl.brep_builderapi_make_edge_from_pts(new Vec(p4v2d.x, p4v2d.y, 0).toGpPnt(), new Vec(p5v2d.x, p5v2d.y, 0).toGpPnt())
        def arc6 = nl.brep_builderapi_make_edge_from_curve nl.geomapi_2dto3d(inner_arc, plane)
        def p6 = nl.geom2d_trimmedcurve_endpoint(inner_arc)
        Vec2d p6v2d = Vec2d.fromAPnt(p6)
        Vec2d p0v2d = Vec2d.fromAPnt(p0)
        def lin2 = nl.brep_builderapi_make_edge_from_pts(new Vec(p6v2d.x, p6v2d.y, 0).toGpPnt(), new Vec(p0v2d.x, p0v2d.y, 0).toGpPnt())

        println "Combine the edges in a wire"
        def wire = nl.brep_builderapi_makewire_new_edge(arc1)
        nl.brep_builderapi_make_wire_add(wire, arc2)
        nl.brep_builderapi_make_wire_add(wire, arc3)
        nl.brep_builderapi_make_wire_add(wire, arc4)
        nl.brep_builderapi_make_wire_add(wire, arc5)
        nl.brep_builderapi_make_wire_add(wire, lin1)
        nl.brep_builderapi_make_wire_add(wire, arc6)
        nl.brep_builderapi_make_wire_add(wire, lin2)

        println "Convert the wire into a face"
        def face = nl.brep_builderapi_make_face_from_makewire(wire)

        println "Finally, extrude the face"
        def wedge = nl.brep_primapi_make_prism(face, new Vec(0, 0, thickness).toGpVec())

        nl.visualize(face)

        return nl.brep_builderapi_make_shape_Shape(wedge)
    }


    @Test
    void "Build Tooth"() {
        buildTooth()
    }
}
