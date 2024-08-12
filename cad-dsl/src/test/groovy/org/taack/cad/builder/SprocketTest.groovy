package org.taack.cad.builder

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.CadBuilder
import org.taack.cad.dsl.Vec
import org.taack.cad.dsl.Vec2d

import static org.taack.cad.dsl.CadBuilder.cb

import groovy.transform.CompileStatic

@CompileStatic
class SprocketTest {

    // The basic inputs needed to build a sprocket
    BigDecimal roller_diameter = 10.2
    BigDecimal pitch = 15.875
    BigInteger num_teeth = 40
    BigDecimal chain_width = 6.35

    // Dimensions derived from the provided inputs
    BigDecimal roller_radius = roller_diameter / 2.0;
    BigDecimal tooth_angle = (2 * Math.PI) / num_teeth;
    BigDecimal pitch_circle_diameter = pitch / Math.sin(tooth_angle.toDouble() / 2.0);
    BigDecimal pitch_circle_radius = pitch_circle_diameter / 2.0;

    BigDecimal roller_contact_angle_min =
            (Math.PI * 120 / 180) - ((Math.PI / 2) / num_teeth);
    BigDecimal roller_contact_angle_max =
            (Math.PI * 140 / 180) - ((Math.PI / 2) / num_teeth);
    BigDecimal roller_contact_angle =
            (roller_contact_angle_min + roller_contact_angle_max) / 2.0;

    BigDecimal tooth_radius_min = 0.505 * roller_diameter;
    BigDecimal tooth_radius_max =
            tooth_radius_min + (0.069 * Math.pow(roller_diameter.toDouble(), 1.0d / 3.0));
    BigDecimal tooth_radius = (tooth_radius_min + tooth_radius_max) / 2.0;

    BigDecimal profile_radius = 0.12 * roller_diameter * (num_teeth + 2);
    BigDecimal top_diameter =
            pitch_circle_diameter + ((1 - (1.6 / num_teeth)) * pitch) - roller_diameter;
    BigDecimal top_radius = top_diameter / 2.0;

    BigDecimal thickness = chain_width * 0.95;

// Center hole data
    BigDecimal center_radius = 125.0 / 2.0;

// Mounting hole data
    BigInteger mounting_hole_count = 6;
    BigDecimal mounting_radius = 153.0 / 2.0;
    BigDecimal hole_radius = 8.5 / 2.0;

/*
 * Build a single tooth
 */
CadBuilder buildTooth() {
    // Create a 2D arc to form the base of the tooth
    cb().sketch {
    }
    // Determine the center of the profile circle

    // Construct the profile circle

    // Construct the outer circle

    // Calculate the intersection point(s) of the profile circle
    // and the outer circle.  If there are two points, pick the one closest
    // to the center of the profile circle

    // Trim the profile circle and mirror

    // Calculate the outermost point

    // and use it to create the third arc

    // Mirror and reverse the three arcs

    // Replace the two outer arcs with a single one

    // Create an arc for the inside of the wedge

    //Convert the 2D arcs and two extra lines to 3D edges

    // Combine the edges in a wire

    // Convert the wire into a face

    // Finally, extrude the face

}

    @Test
    void "Build Tooth"() {

    }
}
