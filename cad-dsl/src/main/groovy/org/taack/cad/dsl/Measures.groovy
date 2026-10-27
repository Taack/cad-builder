package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.taack.cad.dsl.geom.Vec

/**
 * Measures of a solid, see {@link CadDslSolid#measure}: in mm, mm² and mm³.
 */
@CompileStatic
@TupleConstructor
final class Measures {
    final double volume
    final double area
    /**
     * Center of mass of the volume, the origin for an empty solid
     */
    final Vec center
    /**
     * Corners of the bounding box
     */
    final Vec min, max
    /**
     * Whether OCCT finds the shape valid (BRepCheck_Analyzer)
     */
    final boolean valid

    Vec getSize() {
        max - min
    }

    /**
     * @return The mass in g for a density in g/cm³
     */
    double mass(double density) {
        volume * density / 1000d
    }

    @Override
    String toString() {
        String.format(Locale.ROOT, 'volume %.1f cm³, area %.1f cm², center (%.1f, %.1f, %.1f), box (%.1f, %.1f, %.1f) .. (%.1f, %.1f, %.1f)%s',
                volume / 1000d, area / 100d, center.x, center.y, center.z, min.x, min.y, min.z, max.x, max.y, max.z, valid ? '' : ', NOT VALID')
    }
}
