package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.taack.cad.dsl.geom.Vec

/**
 * The shortest distance between two solids, see {@link CadDslSolid#closest}, and where it is: straight, through
 * whatever stands between them.
 */
@CompileStatic
@TupleConstructor
final class Closest {
    /** In mm, 0 when the solids touch or overlap */
    final double distance
    /** The closest point of the solid closest was called on */
    final Vec from
    /** The closest point of the other solid */
    final Vec to

    @Override
    String toString() {
        String.format('%.3f mm from %s to %s', distance, from, to)
    }
}
