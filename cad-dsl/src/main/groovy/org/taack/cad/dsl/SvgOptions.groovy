package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.TextOptions.Align
import org.taack.cad.dsl.TextOptions.VAlign
import org.taack.cad.dsl.geom.Vec

/**
 * Settings of an SVG drawing, see {@link CadDslEdge2d#svg(Map, String, Number)}.
 */
@CompileStatic
final class SvgOptions {
    private static final Set<String> NAMES = ['align', 'valign', 'angle', 'direction', 'normal'] as Set<String>

    /** Where the last position is across the drawing, from its left to its right */
    final Align align
    /** Where the last position is from the bottom to the top of the drawing */
    final VAlign valign
    /** On a face: angle of the drawing from the u direction, in degrees, counterclockwise seen from outside */
    final double angle
    /** In a 3D wire: direction of the x axis of the drawing, and normal of its plane, the side it reads from */
    final Vec direction
    final Vec normal

    SvgOptions(Map<String, ?> options = [:]) {
        Set<String> unknown = options.keySet() - NAMES
        if (unknown) throw new IllegalArgumentException("svg: unknown options $unknown, expected some of $NAMES")
        align = options.align ? Align.valueOf(options.align.toString().toUpperCase()) : Align.LEFT
        valign = options.valign ? VAlign.valueOf(options.valign.toString().toUpperCase()) : VAlign.BOTTOM
        angle = options.angle ? (options.angle as Number).doubleValue() : 0d
        direction = (Vec) options.direction ?: Vec.vX
        normal = (Vec) options.normal ?: Vec.vZ
    }

    @Override
    String toString() {
        "SvgOptions{align=$align, valign=$valign, angle=$angle, direction=$direction, normal=$normal}"
    }
}
