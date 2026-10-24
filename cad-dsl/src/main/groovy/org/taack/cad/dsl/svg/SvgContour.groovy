package org.taack.cad.dsl.svg

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec2d

/**
 * A closed contour of an SVG drawing: from start, lines, arcs of circle and cubic Bezier curves.
 */
@CompileStatic
final class SvgContour {

    /** A piece of the contour, from the end of the previous one (or the start) to its end */
    @CompileStatic
    static abstract class Segment {
        final Vec2d to

        Segment(Vec2d to) {
            this.to = to
        }

        abstract Segment map(Affine t)
    }

    @CompileStatic
    static final class Line extends Segment {
        Line(Vec2d to) {
            super(to)
        }

        Segment map(Affine t) {
            new Line(t.apply(to))
        }
    }

    /** Arc of circle through via; only mapped by similarities, which keep it an arc of circle */
    @CompileStatic
    static final class Arc extends Segment {
        final Vec2d via

        Arc(Vec2d via, Vec2d to) {
            super(to)
            this.via = via
        }

        Segment map(Affine t) {
            new Arc(t.apply(via), t.apply(to))
        }
    }

    @CompileStatic
    static final class Cubic extends Segment {
        final Vec2d control1
        final Vec2d control2

        Cubic(Vec2d control1, Vec2d control2, Vec2d to) {
            super(to)
            this.control1 = control1
            this.control2 = control2
        }

        Segment map(Affine t) {
            new Cubic(t.apply(control1), t.apply(control2), t.apply(to))
        }
    }

    final Vec2d start
    final List<Segment> segments

    SvgContour(Vec2d start, List<Segment> segments) {
        this.start = start
        this.segments = Collections.unmodifiableList(segments)
    }

    SvgContour map(Affine t) {
        new SvgContour(t.apply(start), segments.collect { it.map(t) })
    }

    /**
     * Bounding box, exact for lines, arcs and cubic curves: [xMin, yMin, xMax, yMax]
     */
    double[] bounds() {
        double[] b = [start.x, start.y, start.x, start.y] as double[]
        Vec2d from = start
        for (Segment s in segments) {
            add(b, s.to)
            if (s instanceof Arc) addArcExtremes(b, from, (Arc) s)
            else if (s instanceof Cubic) addCubicExtremes(b, from, (Cubic) s)
            from = s.to
        }
        b
    }

    private static void add(double[] b, Vec2d p) {
        b[0] = Math.min(b[0], p.x)
        b[1] = Math.min(b[1], p.y)
        b[2] = Math.max(b[2], p.x)
        b[3] = Math.max(b[3], p.y)
    }

    // the points of the circle at 0, 90, 180 and 270 degrees that the arc passes
    private static void addArcExtremes(double[] b, Vec2d from, Arc arc) {
        Vec2d c = circleCenter(from, arc.via, arc.to)
        if (c == null) return
        double r = Math.hypot(from.x - c.x, from.y - c.y)
        double a0 = Math.atan2(from.y - c.y, from.x - c.x)
        double sweep = sweep(a0, Math.atan2(arc.via.y - c.y, arc.via.x - c.x), Math.atan2(arc.to.y - c.y, arc.to.x - c.x))
        for (int k = 0; k < 4; k++) {
            double a = k * Math.PI / 2
            double d = normalize(a - a0)
            if (sweep < 0) d = d - 2 * Math.PI
            if (sweep > 0 ? d <= sweep : d >= sweep) add(b, new Vec2d(c.x + r * Math.cos(a), c.y + r * Math.sin(a)))
        }
    }

    // where the derivative of the curve is 0, along x and along y
    private static void addCubicExtremes(double[] b, Vec2d p0, Cubic c) {
        for (int axis = 0; axis < 2; axis++) {
            double q0 = coord(p0, axis), q1 = coord(c.control1, axis), q2 = coord(c.control2, axis), q3 = coord(c.to, axis)
            double qa = -q0 + 3 * q1 - 3 * q2 + q3, qb = 2 * (q0 - 2 * q1 + q2), qc = q1 - q0
            List<Double> roots = []
            if (Math.abs(qa) < 1e-12) {
                if (Math.abs(qb) > 1e-12) roots << -qc / qb
            } else {
                double disc = qb * qb - 4 * qa * qc
                if (disc >= 0) {
                    roots << (-qb + Math.sqrt(disc)) / (2 * qa)
                    roots << (-qb - Math.sqrt(disc)) / (2 * qa)
                }
            }
            for (double t in roots) {
                if (t > 0 && t < 1) add(b, cubicPoint(p0, c, t))
            }
        }
    }

    static Vec2d cubicPoint(Vec2d p0, Cubic c, double t) {
        double u = 1 - t
        double w0 = u * u * u, w1 = 3 * u * u * t, w2 = 3 * u * t * t, w3 = t * t * t
        new Vec2d(w0 * p0.x + w1 * c.control1.x + w2 * c.control2.x + w3 * c.to.x,
                w0 * p0.y + w1 * c.control1.y + w2 * c.control2.y + w3 * c.to.y)
    }

    private static double coord(Vec2d p, int axis) {
        axis == 0 ? p.x : p.y
    }

    /** Center of the circle through 3 points, null if they are aligned */
    static Vec2d circleCenter(Vec2d a, Vec2d b, Vec2d c) {
        double d = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y))
        if (Math.abs(d) < 1e-15) return null
        double a2 = a.x * a.x + a.y * a.y, b2 = b.x * b.x + b.y * b.y, c2 = c.x * c.x + c.y * c.y
        new Vec2d((a2 * (b.y - c.y) + b2 * (c.y - a.y) + c2 * (a.y - b.y)) / d,
                (a2 * (c.x - b.x) + b2 * (a.x - c.x) + c2 * (b.x - a.x)) / d)
    }

    // signed sweep from a0 to a2 passing a1
    private static double sweep(double a0, double a1, double a2) {
        double ccw = normalize(a2 - a0)
        normalize(a1 - a0) <= ccw ? ccw : ccw - 2 * Math.PI
    }

    private static double normalize(double a) {
        double r = a % (2 * Math.PI)
        r < 0 ? r + 2 * Math.PI : r
    }
}
