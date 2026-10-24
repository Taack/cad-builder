package org.taack.cad.dsl.svg

import groovy.transform.CompileStatic
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions.Align
import org.taack.cad.dsl.TextOptions.VAlign

/**
 * An SVG drawing in model units: its y axis up, from the point given by the alignment options.
 */
@CompileStatic
final class SvgDrawing {
    private SvgDrawing() {}

    /**
     * @param source  See {@link SvgReader#read}
     * @param width   Width of the drawing (of its bounding box) in model units
     * @return The contours, relative to the point chosen by align and valign on the bounding box
     */
    static List<SvgContour> fit(String source, double width, SvgOptions options) {
        List<SvgContour> contours = SvgReader.read(source)
        if (!contours) throw new IllegalArgumentException('svg: no filled shape in the drawing')
        double[] b = null
        for (SvgContour c in contours) {
            double[] cb = c.bounds()
            b = b == null ? cb : [Math.min(b[0], cb[0]), Math.min(b[1], cb[1]), Math.max(b[2], cb[2]), Math.max(b[3], cb[3])] as double[]
        }
        if (b[2] - b[0] <= 0) throw new IllegalArgumentException('svg: the drawing has no width')
        double scale = width / (b[2] - b[0])
        double x = options.align == Align.LEFT ? b[0] : options.align == Align.RIGHT ? b[2] : (b[0] + b[2]) / 2
        // y is down in SVG: its bottom is the largest y
        double y = options.valign == VAlign.BOTTOM ? b[3] : options.valign == VAlign.TOP ? b[1] : (b[1] + b[3]) / 2
        Affine toModel = Affine.scale(scale, -scale) * Affine.translation(-x, -y)
        contours.collect { it.map(toModel) }
    }
}
