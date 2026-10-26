package org.example.parts

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslSolid
import org.taack.cad.dsl.geom.Vec

import static org.taack.cad.dsl.CadDsl.cd

/**
 * A plate drilled in its corners, from (0, 0, 0) to (length, width, thickness)
 */
@CompileStatic
class Plate {
    static CadDslSolid drilled(double length, double width, double thickness, double holeDiameter) {
        double inset = holeDiameter * 1.5
        List<Vec> holes = [new Vec(inset, inset, 0), new Vec(length - inset, inset, 0),
                           new Vec(inset, width - inset, 0), new Vec(length - inset, width - inset, 0)]
        cd().box(length, width, thickness).cut {
            holes.each { Vec hole ->
                position(hole) {
                    cylinder(holeDiameter / 2, thickness)
                }
            }
        }
    }
}
