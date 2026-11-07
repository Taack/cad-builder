package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.taack.cad.dsl.geom.Vec

/**
 * The center line of a path, its edges and arcs, to check that a wire swept along it does not cross itself
 */
@CompileStatic
@PackageScope
class PathCenterLine {
    private static final int MAX_SAMPLES = 200000

    /**
     * An edge (center null) or an arc of the center line
     */
    @CompileStatic
    private static final class Piece {
        final Vec from, to, center, normal
        final double radius, angle, length

        Piece(Vec from, Vec to) {
            this.from = from
            this.to = to
            length = from.distance(to)
        }

        Piece(Vec from, Vec to, Vec center, Vec normal, double angle) {
            this.from = from
            this.to = to
            this.center = center
            this.normal = normal
            this.angle = angle
            radius = from.distance(center)
            length = radius * angle
        }

        Vec at(double t) {
            if (center == null) return from + (to - from) * t
            Vec u = from - center
            double a = angle * t
            center + u * Math.cos(a) + (normal / u) * Math.sin(a)
        }

        /** Unit tangent at t, 0 or 1 */
        Vec tangent(double t) {
            if (center == null) return unit(to - from)
            Vec radial = at(t) - center
            unit(normal / radial)
        }
    }

    private final Vec start
    private final List<Piece> pieces = []
    /** False when the path was drawn with something else than edges and arcs: not checked */
    boolean known = true
    /** The tightest arc */
    double smallestRadius = Double.POSITIVE_INFINITY
    Vec smallestAt

    PathCenterLine(Vec start) {
        this.start = start
    }

    private Vec getEnd() { pieces.empty ? start : pieces.last().to }

    void edge(Vec to) {
        if (to.distance(end) > 1e-9d) pieces << new Piece(end, to)
    }

    void arc(Vec to, Vec via) {
        Vec from = end
        // the circle through the three points: its center in their plane, as far from each
        Vec a = from - to, b = via - to, n = a / b
        double nn = n.cord(n)
        if (nn < 1e-18d) {
            edge(to)
            return
        }
        Vec center = to + ((b * a.cord(a) - a * b.cord(b)) / n) * (0.5d / nn)
        Vec normal = unit(n) // the arc turns around it from 'from' through 'via'
        double angle = angleAround(from - center, to - center, normal)
        Piece piece = new Piece(from, to, center, normal, angle)
        if (piece.radius < smallestRadius) {
            smallestRadius = piece.radius
            smallestAt = piece.at(0.5d)
        }
        pieces << piece
    }

    private static double angleAround(Vec u, Vec w, Vec normal) {
        double a = Math.atan2((u / w).cord(normal), u.cord(w))
        a <= 0d ? a + 2d * Math.PI : a
    }

    private static Vec unit(Vec v) { v * (1d / Math.sqrt(v.cord(v))) }

    /**
     * Where a wire of the radius swept along the line would cross itself, null when it does not: two parts of the line
     * closer than the diameter, at a distance from one another that is the shortest around there. Mitred bends, where
     * the wire meets itself on the bisecting plane, are left out.
     */
    String crossing(double radius) {
        if (!known || pieces.empty) return null
        double total = (double) pieces.sum { Piece p -> p.length }
        double step = Math.max(radius / 4d, total / MAX_SAMPLES)
        List<Vec> points = []
        List<Double> along = []
        List<double[]> mitres = [] // (along, half length of the region left out)
        double s = 0d
        for (int k = 0; k < pieces.size(); k++) {
            Piece p = pieces[k]
            if (k > 0) {
                double cosine = pieces[k - 1].tangent(1d).cord(p.tangent(0d))
                if (cosine < 1d - 1e-9d) {
                    if (cosine < -1d + 1e-9d) return "the path turns back at ${p.from}"
                    double inside = Math.PI - Math.acos(cosine) // angle between the two edges
                    mitres << ([s, radius / Math.tan(inside / 2d) + 2d * radius] as double[])
                }
            }
            int n = Math.max(1, (int) Math.ceil(p.length / step))
            for (int i = 0; i < n; i++) {
                points << p.at((double) i / n)
                along << s + p.length * i / n
            }
            s += p.length
        }
        points << end
        along << s
        boolean closed = end.distance(start) < 1e-6d * Math.max(1d, radius)

        double cell = 2d * radius
        Map<List<Long>, List<Integer>> grid = [:]
        for (int i = 0; i < points.size(); i++) grid.computeIfAbsent(cellOf(points[i], cell)) { [] as List<Integer> } << i
        int last = points.size() - 1
        for (int i = 0; i < points.size(); i++) {
            Vec p = points[i]
            List<Long> c = cellOf(p, cell)
            for (long dx = -1; dx <= 1; dx++) for (long dy = -1; dy <= 1; dy++) for (long dz = -1; dz <= 1; dz++) {
                List<Integer> near = grid[[c[0] + dx, c[1] + dy, c[2] + dz]]
                if (near == null) continue
                for (int j : near) {
                    if (j <= i + 1) continue
                    double d = p.distance(points[j])
                    if (d >= 2d * radius) continue
                    // the closest point of that part of the line, not one on the way from p
                    if (d > p.distance(points[j - 1])) continue
                    if (j < last ? d > p.distance(points[j + 1]) : closed) continue
                    if (mitres.any { double[] m -> Math.abs(along[i] - m[0]) <= m[1] && Math.abs(along[j] - m[0]) <= m[1] }) continue
                    return String.format(Locale.ROOT, '%s and %s, %.3f apart along the path, are %.3f apart, less than the diameter %.3f',
                            p, points[j], along[j] - along[i], d, 2d * radius)
                }
            }
        }
        null
    }

    private static List<Long> cellOf(Vec p, double cell) {
        [(long) Math.floor(p.x / cell), (long) Math.floor(p.y / cell), (long) Math.floor(p.z / cell)]
    }
}
