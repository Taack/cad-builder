package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * A path drawn in absolute coordinates, to sweep a section along
 */
@CompileStatic
class CadDslPath implements CadDslBase {
    /**
     * The points it was drawn through, null when it was drawn with edges and arcs
     */
    private List<Vec> points
    private PathCenterLine line

    /**
     * Radius of the tightest rounded bend, infinite without any
     */
    double getSmallestBend() { line.smallestRadius }

    static CadDslPath drawn(ICadDslVisitor visitor, Vec from, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c) {
        PathCenterLine line = new PathCenterLine(from)
        visitor.visitPath(from)
        c.delegate = new CadDslPathEdge(visitor: visitor, at3d: from, line: line)
        c.call()
        visitor.visitPathEnd(from)
        new CadDslPath(visitor: visitor, line: line)
    }

    static CadDslPath through(ICadDslVisitor visitor, List<Vec> points, List<? extends Number> bendRadii) {
        if (points.size() < 2) throw new IllegalArgumentException("path: ${points.size()} point, at least two needed")
        if (bendRadii.size() != points.size()) throw new IllegalArgumentException("path: ${bendRadii.size()} radii for ${points.size()} points")
        CadDslPath path = drawn(visitor, points.first()) {
            CadDslEdge edges = (CadDslEdge) getDelegate()
            Vec at = points.first()
            double previousTangent = 0d
            for (int i = 1; i < points.size() - 1; i++) {
                Vec p = points[i]
                double radius = bendRadii[i].toDouble()
                Vec u1 = unit(points[i - 1], p, i), u2 = unit(points[i + 1], p, i)
                double cosine = u1.cord(u2)
                if (radius <= 0d || cosine < -1d + 1e-12d) { // mitred, or no bend
                    edges.edge(p)
                    at = p
                    previousTangent = 0d
                    continue
                }
                double half = Math.acos(Math.min(1d, cosine)) / 2d, tangent = radius / Math.tan(half)
                double length = points[i - 1].distance(p)
                if (previousTangent + tangent > length + 1e-6d * Math.max(1d, length)) throw new IllegalArgumentException(
                        "path: the bends at points ${i - 1} and $i overlap, ${previousTangent + tangent} along their edge of ${length}")
                Vec bisector = u1 + u2
                Vec from = p + u1 * tangent, to = p + u2 * tangent
                Vec center = p + bisector * (radius / Math.sin(half) / bisector.distance(new Vec()))
                if (from.distance(at) > 1e-6d * Math.max(1d, radius)) edges.edge(from) // two bends may meet: a half circle
                edges.arcAround(to, center)
                at = to
                previousTangent = tangent
            }
            Vec last = points.last()
            double length = points[points.size() - 2].distance(last)
            if (previousTangent > length + 1e-6d * Math.max(1d, length)) throw new IllegalArgumentException(
                    "path: the bend at point ${points.size() - 2} goes beyond the end, ${previousTangent} along its edge of ${length}")
            if (last.distance(at) > 1e-6d) edges.edge(last)
        }
        path.points = points
        path
    }

    private static Vec unit(Vec to, Vec from, int i) {
        double length = to.distance(from)
        if (length < 1e-9d) throw new IllegalArgumentException("path: point $i is repeated")
        (to - from) * (1d / length)
    }

    /**
     * The same path through its points, each bend rounded as much as its edges allow: the arcs of two bends take up to
     * 49 % of the edge between them each, and an edge at an end is not shared.
     * @param maxRadius Radius of the bends where the edges are long enough
     */
    CadDslPath rounded(Number maxRadius = Double.POSITIVE_INFINITY) {
        if (points == null) throw new IllegalStateException('rounded(): only for a path through points, path(List<Vec>)')
        List<Double> radii = [0d]
        for (int i = 1; i < points.size() - 1; i++) {
            Vec a = points[i] - points[i - 1], b = points[i + 1] - points[i]
            double la = a.distance(new Vec()), lb = b.distance(new Vec())
            double turn = Math.acos(Math.max(-1d, Math.min(1d, a.cord(b) / la / lb)))
            // an end edge is not shared with another bend
            double room = Math.min(i == 1 ? la / 0.49d * 0.98d : la, i == points.size() - 2 ? lb / 0.49d * 0.98d : lb)
            radii << (turn < 1e-9d ? 0d : Math.min(maxRadius.toDouble(), 0.49d * room / Math.tan(turn / 2d)))
        }
        radii << 0d
        through(visitor, points, radii)
    }

    /**
     * Sweep a circle along the path, square to it at its start, to make a tube, a cable or a bent rod. The bends
     * between edges that are not tangent are mitred: draw them with arcs, or give the path the radius of its bends, to
     * round them.
     * @param radius Radius of the wire, smaller than the radius of the rounded bends
     * @throws IllegalArgumentException when the wire would cross itself: two parts of the path closer than its diameter
     */
    CadDslSolid wire(Number radius) {
        double r = radius.toDouble()
        if (r >= line.smallestRadius * (1d - 1e-9d)) throw new IllegalArgumentException("wire($radius): not thinner than the tightest bend of the path, of radius ${line.smallestRadius} at ${line.smallestAt}")
        String crossing = line.crossing(r)
        if (crossing != null) throw new IllegalArgumentException("wire($radius): the wire would cross itself, $crossing")
        visitor.visitPathWire(r)
        new CadDslSolid(visitor: visitor)
    }
}
