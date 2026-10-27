package org.taack.cad.dsl.check

import groovy.transform.CompileStatic

/**
 * A contour drawn by a model: its edges, arcs and splines as polylines, in 3D, or on a face for a 2D one (z = 0).
 */
@CompileStatic
final class Contour {
    // Polyline step of the arcs, so that the tangents at their ends are right to the degree
    private static final double ARC_STEP = Math.toRadians(1d)

    /**
     * An edge, an arc or a spline, as the polyline from the end of the previous one
     */
    static final class Element {
        final String kind
        final List<double[]> points
        double length

        Element(String kind, List<double[]> points) {
            this.kind = kind
            this.points = points
        }
    }

    /**
     * file:line of the code drawing it
     */
    final String source
    final boolean in3d
    /**
     * Ids of the model (cd()) and of the wire (wireFrom, face) drawing it: the contours of a wire make a face with holes
     */
    final int model, wire
    final double[] start
    final List<Element> elements = []

    Contour(String source, boolean in3d, int model, int wire, double[] start) {
        this.source = source
        this.in3d = in3d
        this.model = model
        this.wire = wire
        this.start = start
    }

    double[] getEnd() {
        elements ? elements.last().points.last() : start
    }

    void edge(double[] to) {
        elements << new Element('edge', [to])
    }

    void arc(double[] via, double[] to) {
        elements << new Element('arc', arcPoints(end, via, to))
    }

    void spline(List<double[]> through) {
        elements << new Element('spline', through)
    }

    boolean isClosed() {
        dist(start, end) < 1e-3d
    }

    /**
     * @return The points of the contour, the start then each point of its elements
     */
    List<double[]> polyline() {
        List<double[]> all = [start]
        for (Element e in elements) all.addAll(e.points)
        all
    }

    private static List<double[]> arcPoints(double[] a, double[] b, double[] c) {
        double[] ab = sub(b, a), ac = sub(c, a), n = cross(ab, ac)
        double n2 = dot(n, n)
        if (n2 < 1e-12d) return [c] // aligned: a straight edge
        double[] center = add(a, scale(add(scale(cross(n, ab), dot(ac, ac)), scale(cross(ac, n), dot(ab, ab))), 1d / (2d * n2)))
        double[] u = sub(a, center)
        double r = norm(u)
        u = scale(u, 1d / r)
        double[] v = cross(unit(n), u)
        double[] cc = sub(c, center)
        double sweep = Math.atan2(dot(cc, v), dot(cc, u))
        if (sweep <= 0d) sweep += 2d * Math.PI
        int count = Math.max(2, (int) Math.ceil(sweep / ARC_STEP))
        List<double[]> points = []
        for (int i = 1; i < count; i++) {
            double t = sweep * i / count
            points << add(center, add(scale(u, r * Math.cos(t)), scale(v, r * Math.sin(t))))
        }
        points << c
        points
    }

    static double[] sub(double[] a, double[] b) { [a[0] - b[0], a[1] - b[1], a[2] - b[2]] as double[] }

    static double[] add(double[] a, double[] b) { [a[0] + b[0], a[1] + b[1], a[2] + b[2]] as double[] }

    static double[] scale(double[] a, double f) { [a[0] * f, a[1] * f, a[2] * f] as double[] }

    static double dot(double[] a, double[] b) { a[0] * b[0] + a[1] * b[1] + a[2] * b[2] }

    static double[] cross(double[] a, double[] b) {
        [a[1] * b[2] - a[2] * b[1], a[2] * b[0] - a[0] * b[2], a[0] * b[1] - a[1] * b[0]] as double[]
    }

    static double norm(double[] a) { Math.sqrt(dot(a, a)) }

    static double dist(double[] a, double[] b) { norm(sub(a, b)) }

    static double[] unit(double[] a) {
        double n = norm(a)
        n < 1e-12d ? a : scale(a, 1d / n)
    }
}
