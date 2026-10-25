package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.geom.ArcOfCircle2d
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Ellipse2d
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.IConstruction
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Naca
import org.taack.cad.dsl.svg.SvgDrawing

/**
 * Manage Drawing in face, directly or via construction closures
 */
@CompileStatic
class CadDslEdge2d implements CadDslBase {
    /**
     * The last position, where {@link #arcAround} starts: null when it is not known
     */
    Vec2d at2d

    // How far from the center the ends of an arcAround may be, beyond a radius of 1 relative to it
    protected static final double ARC_TOLERANCE = 1e-3

    /**
     * Displace new position on this face.
     * @param to Move, starting from the old position
     */
    void move(Vec2d to) {
        visitor.visitMove(to)
        at2d = at2d == null ? null : at2d + to
    }

    /**
     * Displace new position on this face.
     * @param to Move, starting from the old position
     */
    void move(Number x, Number y) {
        move(new Vec2d(x, y))
    }

    /**
     * Move to new position on this face.
     * @param to New start positon
     */
    void to(Vec2d to) {
        visitor.visitTo(to)
        at2d = to
    }

    /**
     * Move to new position on this face.
     * @param to New start positon
     */
    void to(Number x, Number y) {
        to(new Vec2d(x, y))
    }

    /**
     * Draw an Edge from the last position to the "to" position (which will become the last position).
     * @param to End of the Edge
     */
    CadDslEdge2d edge(Vec2d to) {
        visitor.visitEdge(to)
        at2d = to
        this
    }

    /**
     * Draw an arc from the last position to the "to" position (which will become the last position)
     * passing to the via position.
     * @param to End of the arc
     * @param via Middle point of the arc
     */
    CadDslEdge2d arc(Vec2d to, Vec2d via) {
        visitor.visitArc(to, via)
        at2d = to
        this
    }

    /**
     * Draw an arc of circle around center from the last position to the "to" position (which will become the last
     * position), the short way, or the long one. The last position is the start of the wire, or the one left by the
     * last edge, arc, spline, to or move.
     * @param to      End of the arc, as far from the center as the last position: within 0.001, or 0.1% beyond a radius
     *                of 1, the arc passing at their mean distance
     * @param longWay The arc going more than half a turn
     */
    CadDslEdge2d arcAround(Vec2d to, Vec2d center, boolean longWay = false) {
        if (at2d == null) throw new IllegalStateException("arcAround($to, $center): the last position is not known, draw an edge, an arc or a spline, or call to() first")
        Vec2d u = at2d - center, w = to - center
        double r1 = u.distance(new Vec2d()), r2 = w.distance(new Vec2d()), r = (r1 + r2) / 2, bisector = (u + w).distance(new Vec2d())
        if (Math.abs(r1 - r2) > ARC_TOLERANCE * Math.max(1d, r)) throw new IllegalArgumentException("arcAround($to, $center): its ends $at2d and $to are not as far from the center, $r1 and $r2")
        if (bisector < 1e-9 * Math.max(1d, r)) throw new IllegalArgumentException("arcAround($to, $center): half a turn, either way: draw it with arc(to, via)")
        arc(to, center + (u + w) * ((longWay ? -r : r) / bisector))
    }

    /**
     * Draw a smooth curve from the last position through the given points (the last one becomes the last position).
     * @param through Points the curve goes through
     */
    CadDslEdge2d spline(Vec2d... through) {
        visitor.visitSpline(through.toList())
        if (through) at2d = through.last()
        this
    }

    /**
     * Draw a closed NACA 4-digit airfoil for a unit chord, from the trailing edge (1, 0) around the leading edge (0, 0).
     * @param code      4 digits, e.g. "2412": 2% camber at 40% of the chord, 12% thick
     * @param nbPoints  Points per surface
     */
    CadDslEdge2d naca(String code, int nbPoints = 40) {
        visitor.visitTo(new Vec2d(1, 0))
        visitor.visitSpline(Naca.points(code, nbPoints))
        at2d = null
        this
    }

    /**
     * Draw a closed circle at the last position
     * @param diameter Diameter of the circle
     */
    Circle2d circle(Number radius, boolean reverse = false) {
        visitor.visitCircle2d(radius.toDouble(), reverse)
    }

    /**
     * Draw the contours of a line of text at the last position, along the u direction of the face, read from
     * outside it. Its letters are closed contours, their holes found as for any contours drawn inside others.
     * @param options font: a font name (default sans-serif) or the path of a font file; bold, italic;
     *                align: left (default), center or right; valign: bottom (default, the baseline), center or top;
     *                angle: from the u direction, in degrees, counterclockwise seen from outside
     * @param content Text, in one line
     * @param size    Size of the font, in model units
     */
    CadDslEdge2d text(Map<String, ?> options = [:], String content, Number size) {
        visitor.visitText(content, size.toDouble(), new TextOptions(options))
        this
    }

    /**
     * Draw the contours of the filled shapes of an SVG drawing at the last position, its x axis along the u direction
     * of the face, read from outside it. Holes are found as for any contours drawn inside others (the even-odd rule).
     * @param options align: left (default), center or right; valign: bottom (default), center or top, the last
     *                position on the bounding box of the drawing; angle: from the u direction, in degrees,
     *                counterclockwise seen from outside
     * @param source  An SVG document, the path of an SVG file, or the data of a path (as its d attribute)
     * @param width   Width of the drawing (of its bounding box), in model units
     */
    CadDslEdge2d svg(Map<String, ?> options = [:], String source, Number width) {
        SvgOptions o = new SvgOptions(options)
        visitor.visitSvg(SvgDrawing.fit(source, width.toDouble(), o), o)
        this
    }

    /**
     * Draw a closed circle at the last position
     * @param diameter Diameter of the circle
     */
    Ellipse2d ellipse(Vec2d dir, Number majDia, Number minDia) {
        visitor.visitEllipse2d(dir, majDia.toDouble(), minDia.toDouble())
    }

//    /**
//     * Draw a closed rectangle whose center is the last position
//     * @param sX Size in X coords
//     * @param sY Size in Y coords
//     * @param c If not null, convert edges of this rectangle into construction points
//     */
//    void rect(Number sX, Number sY, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
////        else {
//        double sXd = sX.toDouble()
//        double sYd = sY.toDouble()
//        visitor.visitMove new Vec2d(sXd / 2, sYd / 2)
//        edge(new Vec2d(sXd / 2, -sYd / 2))
//        edge(new Vec2d(-sXd / 2, -sYd / 2))
//        edge(new Vec2d(-sXd / 2, sYd / 2))
//        edge(new Vec2d(sXd / 2, sYd / 2))
////        if (c) visitor.visitConstruct2d(c)
//
////        }
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(sXd / 2, -sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, -sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
//
////            visitor.visitEdge(sX, sY, c)
//    }

    void closedWire(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitClosedWire()
        c.delegate = this
        c.call()
        visitor.visitClosedWireEnd()
    }

    ArcOfCircle2d trimmed(Circle2d curve, Number from, Number to, boolean reverse = false) {
        visitor.visitTrimmed(curve, from.toDouble(), to.toDouble(), reverse)
    }

    ArcOfCircle2d trimmed(Circle2d curve, Vec2d from, Vec2d to, boolean reverse = false) {
        visitor.visitTrimmed(curve, from, to, reverse)
    }

    ArcOfCircle2d trimmed(Circle2d curve, Vec2d from, Number to, boolean reverse = false) {
        visitor.visitTrimmed(curve, from, to.toDouble(), reverse)
    }

    ITrimmable2d trimmed(IClosedShape2d curve, Number from, Number to, boolean reverse = false) {
        visitor.visitTrimmed(curve, from.toDouble(), to.toDouble(), reverse)
    }

    ITrimmable2d mirror(ITrimmable2d curve, Vec2d pt, Vec2d dir) {
        visitor.visitMirror(curve, pt, dir)
    }

    void removeFromConstruction(IConstruction... toRemove) {
        visitor.visitRemoveFromConstruction(toRemove)
    }

    void addToConstruction(IConstruction... toAdd) {
        visitor.visitAddToConstruction(toAdd)
    }
}
