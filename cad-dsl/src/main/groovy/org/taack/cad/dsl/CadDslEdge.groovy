package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.svg.SvgDrawing

@CompileStatic
class CadDslEdge extends CadDslEdge2d {
    /**
     * The last position, where {@link #arcAround(Vec, Vec, boolean)} starts: null when it is not known
     */
    Vec at3d
    /**
     * Displace new position on this face.
     * @param to    Move, starting from the old position
     */
    void move(Vec to) {
        visitor.visitMove(to)
    }

    /**
     * Move to new position on this face.
     * @param to    New start positon
     */
    void to(Vec to) {
        visitor.visitTo(to)
        at3d = to
    }

    /**
     * Draw an Edge from the last position to the "to" position (which will become the last position).
     * @param to    End of the Edge
     */
    void edge(Vec to) {
        visitor.visitEdge(to)
        at3d = to
    }

    void adapt3d(ITrimmable2d trimmedCurve, Vec dirX, Vec dirY) {
        visitor.visitAdapt3d(trimmedCurve, dirX, dirY)
    }

    /**
     * Draw an arc from the last position to the "to" position (which will become the last position)
     * passing to the via position.
     * @param to    End of the arc
     * @param via   Middle point of the arc
     */
    void arc(Vec to, Vec via) {
        visitor.visitArc(to, via)
        at3d = to
    }

    /**
     * Draw an arc of circle around center from the last position to the "to" position (which will become the last
     * position), in the plane of the three points, the short way, or the long one. The last position is the start of
     * the wire, or the one left by the last edge, arc or to.
     * @param to      End of the arc, as far from the center as the last position: within 0.001, or 0.1% beyond a radius
     *                of 1, the arc passing at their mean distance
     * @param longWay The arc going more than half a turn
     */
    void arcAround(Vec to, Vec center, boolean longWay = false) {
        if (at3d == null) throw new IllegalStateException("arcAround($to, $center): the last position is not known, draw an edge or an arc, or call to() first")
        Vec u = at3d - center, w = to - center
        double r1 = at3d.distance(center), r2 = to.distance(center), r = (r1 + r2) / 2, bisector = (u + w).distance(new Vec())
        if (Math.abs(r1 - r2) > ARC_TOLERANCE * Math.max(1d, r)) throw new IllegalArgumentException("arcAround($to, $center): its ends $at3d and $to are not as far from the center, $r1 and $r2")
        if (bisector < 1e-9 * Math.max(1d, r)) throw new IllegalArgumentException("arcAround($to, $center): half a turn, in no given plane: draw it with arc(to, via)")
        arc(to, center + (u + w) * ((longWay ? -r : r) / bisector))
    }

    /**
     * Draw the contours of a line of text at the last position, in absolute coordinates.
     * @param options as for a text on a face (font, bold, italic, align, valign), with direction: along the text
     *                (default X) and normal: of its plane, the side it reads from (default Z)
     * @param content Text, in one line
     * @param size    Size of the font, in model units
     */
    @Override
    CadDslEdge2d text(Map<String, ?> options = [:], String content, Number size) {
        visitor.visitText3d(content, size.toDouble(), new TextOptions(options))
        this
    }

    /**
     * Draw the contours of the filled shapes of an SVG drawing at the last position, in absolute coordinates.
     * @param options as on a face (align, valign), with direction: of the x axis of the drawing (default X) and
     *                normal: of its plane, the side it reads from (default Z)
     * @param source  An SVG document, the path of an SVG file, or the data of a path (as its d attribute)
     * @param width   Width of the drawing (of its bounding box), in model units
     */
    @Override
    CadDslEdge2d svg(Map<String, ?> options = [:], String source, Number width) {
        SvgOptions o = new SvgOptions(options)
        visitor.visitSvg3d(SvgDrawing.fit(source, width.toDouble(), o), o)
        this
    }
}
