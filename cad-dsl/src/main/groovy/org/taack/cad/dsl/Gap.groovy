package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.taack.cad.dsl.geom.Vec

/**
 * A clearance or a creepage distance between two conductors, see {@link Insulation}: its length and its path.
 */
@CompileStatic
@TupleConstructor
final class Gap {
    /** The conductor the path starts from */
    final String from
    /** The conductor it ends on */
    final String to
    /**
     * In mm. With no path, the length the gap is at least: the limit of the search when no path is shorter, infinite
     * when none joins the conductors.
     */
    final double length
    /** From a point of the first conductor to one of the other, through the points where the path bends */
    final List<Vec> path

    /**
     * @return Whether the path was found, else the length is a limit the gap is longer than
     */
    boolean isFound() {
        !path.empty
    }

    @Override
    String toString() {
        String.format('%s to %s: %s%.3f mm', from, to, found ? '' : 'at least ', length)
    }
}
