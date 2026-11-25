package org.taack.cad.dsl.kernel

import org.taack.cad.dsl.Gap

/**
 * The paths between groups of solids a kernel finds, for {@link org.taack.cad.dsl.Insulation}: the groups are numbered
 * from 0, the insulating solids are in group -1. The gaps it returns have no names.
 */
interface InsulationPaths {
    /**
     * @return The shortest path through the air between the groups, around all the solids, or, with no path, the
     * length limit when none is shorter, infinite when none joins them
     */
    Gap clearance(int from, int to, double limit)

    /**
     * @return The shortest path along the surfaces of the insulating solids between the groups, crossing the gaps
     * narrower than groove through the air, or no path, as {@link #clearance}
     */
    Gap creepage(int from, int to, double groove, double limit)
}
