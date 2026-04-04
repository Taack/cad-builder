package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.builder.Vec

import static org.taack.cad.dsl.CadDsl.cd
import static java.lang.Math.*

@CompileStatic
class SelectorTest {
    final double stud_spacing = 8
    final double stud_diameter = 5
    final double stud_radius = stud_diameter / 2
    final double plate_height = 3.2
    final double block_height = stud_spacing * 8 / 5
    final double wall_thickness = (stud_spacing - stud_diameter) / 2
    final double stud_height = block_height / 3 - wall_thickness
    final double cyl_outer_diam = sqrt(2) * stud_spacing - stud_diameter
    final double cyl_thickness = (cyl_outer_diam - stud_diameter) / 2
    final double block_length = stud_spacing * 4


    @Test
    void "Basic Box and Studs"() {
        cd().box(block_length, block_length, block_height).butZ().hollowedSolid(-wall_thickness).topZ().wireFrom() {
            to(stud_spacing, stud_spacing)
            circle(stud_diameter)
            move(2 * stud_spacing, 0)
            circle(stud_diameter)
            move(0, 2 * stud_spacing)
            circle(stud_diameter)
            move(-2 * stud_spacing, 0)
            circle(stud_diameter)
        }.toFace()
                .prism(stud_height)
                .butZ(new Vec(block_length / 2, block_length / 2, block_height - 2 * wall_thickness))
                .wireFrom() {
                    circle(stud_diameter + wall_thickness)
                    circle(stud_diameter, true)
                }.toFace().prism(block_height - wall_thickness).display()

    }

    @Test
    void "Basic Box and Studs2"() {
        cd().box(2 * block_length, block_length, block_height).butZ().hollowedSolid(-wall_thickness).topZ().wireFrom() {
            to(stud_spacing, stud_spacing)
            circle(stud_diameter)
            move(2 * stud_spacing, 0)
            circle(stud_diameter)
            move(0, 2 * stud_spacing)
            circle(stud_diameter)
            move(-2 * stud_spacing, 0)
            circle(stud_diameter)
            to(5 * stud_spacing, stud_spacing)
            circle(stud_diameter)
            move(2 * stud_spacing, 0)
            circle(stud_diameter)
            move(0, 2 * stud_spacing)
            circle(stud_diameter)
            move(-2 * stud_spacing, 0)
            circle(stud_diameter)
        }.toFace()
                .prism(stud_height)
                .butZ(new Vec(block_length / 2, block_length / 2, block_height - 2 * wall_thickness))
                .wireFrom() {
                    to(2 * stud_spacing, 2 * stud_spacing)
                    circle(stud_diameter + wall_thickness)
                    circle(stud_diameter, true)
                    to(6 * stud_spacing, 2 * stud_spacing)
                    circle(stud_diameter + wall_thickness)
                    circle(stud_diameter, true)
                }.toFace()
                .prism(block_height - wall_thickness)
                .note("coucou", 20.0, new Vec(20))
//                .display()
    }
}