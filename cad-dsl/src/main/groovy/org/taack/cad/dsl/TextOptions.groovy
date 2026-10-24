package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * Settings of a text, see {@link CadDslEdge2d#text(Map, String, Number)}.
 */
@CompileStatic
final class TextOptions {
    static final String DEFAULT_FONT = 'sans-serif'

    enum Align { LEFT, CENTER, RIGHT }

    enum VAlign { BOTTOM, CENTER, TOP }

    private static final Set<String> NAMES = ['font', 'bold', 'italic', 'align', 'valign', 'angle', 'direction', 'normal'] as Set<String>

    /** Font name (a family, or an alias such as sans-serif, serif, monospace), or the path of a font file */
    final String font
    final boolean bold
    final boolean italic
    /** Where the last position is along the text */
    final Align align
    /** Where the last position is across the text: BOTTOM is the baseline */
    final VAlign valign
    /** On a face: angle of the text from the u direction, in degrees, counterclockwise seen from outside */
    final double angle
    /** In a 3D wire: direction along the text, and normal of its plane, the side it reads from */
    final Vec direction
    final Vec normal

    TextOptions(Map<String, ?> options = [:]) {
        Set<String> unknown = options.keySet() - NAMES
        if (unknown) throw new IllegalArgumentException("text: unknown options $unknown, expected some of $NAMES")
        font = options.font ?: DEFAULT_FONT
        bold = options.bold ?: false
        italic = options.italic ?: false
        align = options.align ? Align.valueOf(options.align.toString().toUpperCase()) : Align.LEFT
        valign = options.valign ? VAlign.valueOf(options.valign.toString().toUpperCase()) : VAlign.BOTTOM
        angle = options.angle ? (options.angle as Number).doubleValue() : 0d
        direction = (Vec) options.direction ?: Vec.vX
        normal = (Vec) options.normal ?: Vec.vZ
    }

    @Override
    String toString() {
        "TextOptions{font=$font, bold=$bold, italic=$italic, align=$align, valign=$valign, angle=$angle, direction=$direction, normal=$normal}"
    }
}
