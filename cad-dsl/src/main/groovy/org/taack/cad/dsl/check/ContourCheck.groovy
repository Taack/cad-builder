package org.taack.cad.dsl.check

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernel
import org.taack.cad.dsl.kernel.CadKernels

import static org.taack.cad.dsl.check.Contour.*

/**
 * Finds in the contours drawn by a model what could be written shorter:
 * <ul>
 * <li>contours close to one another, maybe in parallel planes: the same, an offset of it or nearly the same, to draw
 * once, with an offset or with parameters;</li>
 * <li>circles at the same positions in several models: holes, pins, screws, whose positions go in a constant;</li>
 * <li>copies of a contour, moved, turned or mirrored, and those in a row or around a center: a method, a loop;</li>
 * <li>edges repeating along a contour, or making an arc: a loop, an arc;</li>
 * <li>small contours in a row, of different shapes: text, to draw with text().</li>
 * </ul>
 */
@CompileStatic
final class ContourCheck {
    private static final double ANGLE = Math.toRadians(1d)

    final double tolerance
    // distance under which the shapes are the same
    private static final double SAME = 0.01d
    private final List<Contour> contours
    private final List<Shape> shapes

    private ContourCheck(List<Contour> contours, double tolerance) {
        this.contours = contours
        this.tolerance = tolerance
        shapes = contours.collect { Contour c -> new Shape(c, tolerance / 2d) }
    }

    /**
     * Builds the models made in build, recording their contours, and finds what could be written shorter.
     * @param tolerance Distance in mm under which two contours are close
     * @return The findings, each one starting with file:line of the code drawing the contours
     */
    static List<String> run(double tolerance = 1d, Closure build) {
        CadKernel kernel = CadKernels.get()
        def recorder = new ContourRecorder(kernel)
        CadKernels.set(recorder)
        try {
            build.call()
        } finally {
            CadKernels.set(kernel)
        }
        check(recorder.contours, tolerance)
    }

    static List<String> check(List<Contour> contours, double tolerance = 1d) {
        def check = new ContourCheck(contours, tolerance)
        check.closeContours() + check.sharedPositions() + check.copies() + check.repeatedEdges() + check.text()
    }

    /**
     * A contour sampled for the comparisons
     */
    private static final class Shape {
        final Contour contour
        final List<double[]> points
        final double[] center, min = [Double.MAX_VALUE] * 3 as double[], max = [-Double.MAX_VALUE] * 3 as double[]
        final double[] normal
        final double length, step
        // kind, length, sweep, turn of each element
        final List<double[]> steps = []
        final List<String> kinds = []

        Shape(Contour contour, double step) {
            this.contour = contour
            this.step = step
            List<double[]> line = contour.polyline()
            points = resample(line, step)
            // the center of the line, each segment weighing its length
            double total = 0d
            double[] sum = [0d, 0d, 0d] as double[], n = [0d, 0d, 0d] as double[]
            for (int i = 1; i < line.size(); i++) {
                double l = dist(line[i - 1], line[i])
                total += l
                sum = add(sum, scale(add(line[i - 1], line[i]), l / 2d))
            }
            length = total
            center = total > 0d ? scale(sum, 1d / total) : line.first()
            for (double[] q in points) for (int k = 0; k < 3; k++) {
                min[k] = Math.min(min[k], q[k])
                max[k] = Math.max(max[k], q[k])
            }
            for (int i = 0; i < line.size(); i++) n = add(n, cross(line[i], line[(i + 1) % line.size()]))
            normal = norm(n) > 1e-9d ? unit(n) : null
            double[] before = contour.closed ? tangentOut(contour.elements.last(), previous(contour.elements.size() - 1)) : null
            for (int i = 0; i < contour.elements.size(); i++) {
                def e = contour.elements[i]
                double[] from = previous(i)
                double elementLength = 0d, prior = 0d
                double[] at = from
                for (double[] q in e.points) {
                    elementLength += dist(at, q)
                    at = q
                }
                double[] tIn = unit(sub(e.points.first(), from)), tOut = tangentOut(e, from)
                double sweep = e.kind == 'edge' ? 0d : angle(tIn, tOut)
                double turn = before ? angle(before, tIn) : 0d
                steps << ([elementLength, sweep, turn] as double[])
                kinds << e.kind
                before = tOut
            }
        }

        /**
         * @return Whether the contour is a circle, made of arcs at the same distance from its center
         */
        boolean isCircle() {
            contour.closed && kinds.every { it == 'arc' } && points.every { double[] p -> Math.abs(dist(p, center) - dist(points.first(), center)) < 1e-3d }
        }

        private double[] previous(int i) {
            i == 0 ? contour.start : contour.elements[i - 1].points.last()
        }

        private static double[] tangentOut(Contour.Element e, double[] from) {
            List<double[]> p = e.points
            unit(sub(p.last(), p.size() > 1 ? p[p.size() - 2] : from))
        }

        // signed with the normal of the contour when it has one
        private double angle(double[] a, double[] b) {
            double cos = Math.max(-1d, Math.min(1d, dot(a, b)))
            double angle = Math.acos(cos)
            normal && dot(cross(a, b), normal) < 0d ? -angle : angle
        }

        double[] getSize() { sub(max, min) }

        // the largest size: the height of most letters, the width of the others
        double getHeight() {
            Math.max(size[0], Math.max(size[1], size[2]))
        }

        boolean near(Shape other, double margin, double[] shift = [0d, 0d, 0d] as double[]) {
            (0..2).every { int k -> min[k] - margin <= other.max[k] + shift[k] && other.min[k] + shift[k] - margin <= max[k] }
        }

        /**
         * @return Distance from the plane of this contour to the one of other, when they are parallel, else null
         */
        Double planeGap(Shape other) {
            if (normal == null || other.normal == null || Math.abs(Math.abs(dot(normal, other.normal)) - 1d) > 1e-6d) return null
            dot(sub(other.center, center), normal)
        }

        private static List<double[]> resample(List<double[]> line, double step) {
            List<double[]> out = [line.first()]
            for (int i = 1; i < line.size(); i++) {
                double[] a = line[i - 1], b = line[i]
                int count = Math.max(1, (int) Math.ceil(dist(a, b) / step))
                for (int k = 1; k <= count; k++) out << add(a, scale(sub(b, a), k / (double) count))
            }
            out
        }
    }

    /**
     * Segments of a polyline in cells of a grid, for the nearest one within a distance
     */
    private static final class Grid {
        final double cell
        final Map<List<Long>, List<double[][]>> cells = [:]

        Grid(List<double[]> line, double[] shift, double cell) {
            this.cell = cell
            for (int i = 1; i < line.size(); i++) {
                double[][] segment = [add(line[i - 1], shift), add(line[i], shift)] as double[][]
                Set<List<Long>> keys = [key(segment[0]), key(segment[1])] as Set
                for (List<Long> k in keys) cells.computeIfAbsent(k) { [] } << segment
            }
        }

        private List<Long> key(double[] p, int dx = 0, int dy = 0, int dz = 0) {
            [(long) Math.floor(p[0] / cell) + dx, (long) Math.floor(p[1] / cell) + dy, (long) Math.floor(p[2] / cell) + dz]
        }

        // the distance to the nearest segment, if less than the cell, else MAX_VALUE
        double nearest(double[] p) {
            double best = Double.MAX_VALUE
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++)
                for (double[][] s in cells.get(key(p, dx, dy, dz)) ?: []) best = Math.min(best, toSegment(p, s[0], s[1]))
            best <= cell ? best : Double.MAX_VALUE
        }

        private static double toSegment(double[] p, double[] a, double[] b) {
            double[] ab = sub(b, a)
            double l2 = dot(ab, ab)
            double t = l2 < 1e-18d ? 0d : Math.max(0d, Math.min(1d, dot(sub(p, a), ab) / l2))
            dist(p, add(a, scale(ab, t)))
        }
    }

    /**
     * @return {smallest, largest} distance of the points of a to b moved by shift, null when one is farther than limit
     */
    private static double[] distances(Shape a, Shape b, double[] shift, double limit) {
        // a segment within limit has its ends in the cells around
        def grid = new Grid(b.points, shift, limit + b.step)
        double low = Double.MAX_VALUE, high = 0d
        for (double[] p in a.points) {
            double d = grid.nearest(p)
            if (d > limit) return null
            low = Math.min(low, d)
            high = Math.max(high, d)
        }
        [low, high] as double[]
    }

    // {smallest, largest} distance, both ways
    private static double[] hausdorff(Shape a, Shape b, double[] shift, double limit) {
        double[] ab = distances(a, b, shift, limit)
        if (ab == null) return null
        double[] ba = distances(b, a, scale(shift, -1d), limit)
        ba == null ? null : [Math.min(ab[0], ba[0]), Math.max(ab[1], ba[1])] as double[]
    }

    private static String sources(Collection<Shape> group) {
        group.collect { Shape s -> s.contour.source }.unique().join(', ')
    }

    private static String mm(double v) { String.format(Locale.ROOT, '%.3g', Math.abs(v) < 1e-9d ? 0d : v) }

    private static String vec(double[] v) { "(${mm(v[0])}, ${mm(v[1])}, ${mm(v[2])})" }

    /**
     * Contours of different code, drawn at a distance under the tolerance one of the other
     */
    List<String> closeContours() {
        // the families of close contours, and the largest distance in each one
        Map<Shape, Shape> family = [:]
        Map<Shape, Double> apart = [:]
        // {smallest, largest} distance of the last close pair of each family
        Map<Shape, double[]> pair = [:]
        Closure<Shape> root
        root = { Shape s -> family[s] == null || family[s].is(s) ? s : root(family[s]) }
        for (int i = 0; i < shapes.size(); i++) for (int j = i + 1; j < shapes.size(); j++) {
            Shape a = shapes[i], b = shapes[j]
            // the outline and the holes of a face; circles are short to draw
            if (!a.contour.in3d || !b.contour.in3d || a.contour.wire == b.contour.wire || (a.circle && b.circle)) continue
            // in parallel planes, b is compared in the plane of a; small contours are close for less
            Double gap = a.planeGap(b)
            double[] shift = gap == null ? [0d, 0d, 0d] as double[] : scale(a.normal, -gap)
            double limit = Math.min(tolerance, 0.05d * Math.min(a.height, b.height))
            if (!a.near(b, limit, shift)) continue
            double[] d = hausdorff(a, b, shift, limit)
            if (d == null) continue
            Shape ra = root(a), rb = root(b)
            if (!ra.is(rb)) family[rb] = ra
            apart[ra] = [apart[ra] ?: 0d, apart[rb] ?: 0d, d[1]].max()
            pair[ra] = d
        }
        List<String> found = []
        shapes.findAll { Shape s -> family.containsKey(s) || family.containsValue(s) }.groupBy { Shape s -> root(s) }.each { Shape r, List<Shape> close ->
            closeFamilies << (close as Set<Shape>)
            double most = apart[r] ?: 0d
            Shape first = close.first()
            List<Double> gaps = close.collect { Shape s -> first.planeGap(s) }.findAll { it != null } as List<Double>
            String planes = gaps.size() == close.size() && gaps.any { Math.abs(it) > SAME } ?
                    " in parallel planes ${mm(gaps.max() - gaps.min())} mm apart at most" : ''
            double[] d = pair[r]
            // an offset is as far everywhere, but at the sharp corners
            String what = most < SAME ? "the same contour ${close.size()} times$planes, draw it once" :
                    close.size() == 2 && d[0] > 0.7d * d[1] ? "a contour and its offset by ${mm(d[0])} mm$planes, draw it once with an offset" :
                    "${close.size()} nearly the same contours, ${mm(most)} mm apart at most$planes, draw them with one method and parameters"
            found << "${sources(close)}: $what".toString()
        }
        found
    }

    // the families of close contours, which copies() does not report again
    private final List<Set<Shape>> closeFamilies = []

    /**
     * Circles of several models, centered at the same positions in parallel planes
     */
    List<String> sharedPositions() {
        List<Shape> circles = shapes.findAll { Shape s -> s.circle && s.normal && s.contour.in3d }
        Map<Integer, List<Shape>> byModel = circles.groupBy { Shape s -> s.contour.model }
        List<Integer> models = byModel.keySet().sort()
        List<String> found = []
        Set<String> seen = [] as Set
        for (int i = 0; i < models.size(); i++) for (int j = i + 1; j < models.size(); j++) {
            List<Shape> shared = []
            for (Shape a in byModel[models[i]]) {
                Shape b = byModel[models[j]].find { Shape o ->
                    Double gap = a.planeGap(o)
                    gap != null && dist(add(o.center, scale(a.normal, -gap)), a.center) < SAME * 10d
                }
                if (b) shared.addAll([a, b])
            }
            int count = (int) (shared.size() / 2)
            // circles around one axis, as those of screws and nuts, are not positions to share
            int positions = (0..<count).collect { int k ->
                Shape a = shared[2 * k]
                double[] inPlane = sub(a.center, scale(a.normal, dot(a.center, a.normal)))
                inPlane.collect { double v -> Math.round(v * 100d) }
            }.unique().size()
            String key = shared.collect { Shape s -> s.contour.source }.sort().join()
            if (positions >= 3 && seen.add(key))
                found << "${sources(shared)}: ${count} circles centered at the same positions in two models, put the positions in a constant".toString()
        }
        found
    }

    /**
     * Copies of a contour moved, turned or mirrored, and the rows and circles they make
     */
    List<String> copies() {
        List<String> found = []
        List<List<Shape>> groups = []
        for (Shape s in shapes) {
            if (s.contour.elements.size() < 2) continue
            List<Shape> group = groups.find { List<Shape> g -> congruent(g.first(), s) }
            if (group) group << s
            else groups << [s]
        }
        for (List<Shape> group in groups) {
            // copies drawn by the same line are already in a loop
            if (closeFamilies.any { Set<Shape> f -> f.containsAll(group) }) continue
            List<String> lines = group.collect { Shape s -> s.contour.source }.unique()
            if (group.size() < 2 || (lines.size() < 2 && lines[0] != '?')) continue
            String pattern = row(group) ?: circle(group)
            if (!pattern && group.first().circle) continue
            pattern = pattern ?: 'draw it in a method, placed by its position'
            found << "${sources(group)}: ${group.size()} copies of a contour of ${group.first().contour.elements.size()} elements, $pattern".toString()
        }
        found
    }

    private boolean congruent(Shape a, Shape b) {
        int n = a.steps.size()
        if (n != b.steps.size() || Math.abs(a.length - b.length) > SAME * n) return false
        if (hausdorff(a, b, sub(a.center, b.center), SAME * 5d) != null) return true
        // turned or mirrored: the same elements, maybe from another one when closed
        for (int shift = 0; shift < (a.contour.closed ? n : 1); shift++) {
            boolean match = (0..<n).every { int i -> sameStep(a, i, b, (i + shift) % n, 1d) }
            boolean mirrored = !match && (0..<n).every { int i -> sameStep(a, i, b, (i + shift) % n, -1d) }
            if (match || mirrored) return true
        }
        false
    }

    private boolean sameStep(Shape a, int i, Shape b, int j, double sign) {
        double[] p = a.steps[i], q = b.steps[j]
        // the turn into the first element of an open contour is unknown
        boolean turn = (a.contour.closed && b.contour.closed) || (i > 0 && j > 0)
        a.kinds[i] == b.kinds[j] && Math.abs(p[0] - q[0]) <= SAME * 2d && Math.abs(p[1] - sign * q[1]) <= ANGLE &&
                (!turn || Math.abs(p[2] - sign * q[2]) <= ANGLE)
    }

    private String row(List<Shape> group) {
        if (group.size() < 3) return null
        Shape first = group.first()
        Shape last = group.max { Shape s -> dist(s.center, first.center) }
        double[] dir = unit(sub(last.center, first.center))
        List<Shape> sorted = group.sort(false) { Shape s -> dot(sub(s.center, first.center), dir) }
        double[] step = sub(sorted[1].center, sorted[0].center)
        for (int i = 1; i < sorted.size(); i++)
            if (dist(sub(sorted[i].center, sorted[i - 1].center), step) > SAME * 5d) return null
        "in a row, by ${vec(step)}: a loop"
    }

    private String circle(List<Shape> group) {
        if (group.size() < 3) return null
        double[] a = group[0].center, b = group[(int) (group.size() / 3)].center, c = group[(int) (2 * group.size() / 3)].center
        double[] ab = sub(b, a), ac = sub(c, a), n = cross(ab, ac)
        double n2 = dot(n, n)
        if (n2 < 1e-9d) return null
        double[] center = add(a, scale(add(scale(cross(n, ab), dot(ac, ac)), scale(cross(ac, n), dot(ab, ab))), 1d / (2d * n2)))
        double r = dist(a, center)
        if (group.any { Shape s -> Math.abs(dist(s.center, center) - r) > SAME * 5d || Math.abs(dot(sub(s.center, center), unit(n))) > SAME * 5d })
            return null
        double[] u = unit(sub(a, center)), v = cross(unit(n), u)
        List<Double> angles = group.collect { Shape s ->
            double[] d = sub(s.center, center)
            double t = Math.toDegrees(Math.atan2(dot(d, v), dot(d, u)))
            t < -1e-6d ? t + 360d : t
        }.sort() as List<Double>
        double stepAngle = angles[1] - angles[0]
        for (int i = 1; i < angles.size(); i++) if (Math.abs(angles[i] - angles[i - 1] - stepAngle) > 0.1d) return null
        "around ${vec(center)}, axis ${vec(unit(n))}, every ${mm(stepAngle)}°: circularPattern or a loop"
    }

    /**
     * Elements repeating along a contour: a loop, or an arc drawn as edges
     */
    List<String> repeatedEdges() {
        List<String> found = []
        Set<String> seen = [] as Set
        for (Shape s in shapes) {
            int n = s.steps.size()
            if (n < 6 || !seen.add("${s.contour.source}:${s.steps.collect { it[0].round(2) }}".toString())) continue
            for (int period = 1; period <= n / 3; period++) {
                int best = 0, run = 0, bestEnd = 0
                for (int i = 0; i < (s.contour.closed ? 2 * n : n - period); i++) {
                    run = sameStep(s, i % n, s, (i + period) % n, 1d) ? run + 1 : 0
                    if (run > best) {
                        best = Math.min(run, n - period)
                        bestEnd = i % n
                    }
                }
                int repeats = (int) ((best + period) / period)
                // a rounded rectangle repeats 2 elements 4 times
                if (repeats < 3 || (repeats < 5 && period < 3) || best + period < 6) continue
                double[] step = s.steps[bestEnd]
                if (period == 1 && s.kinds[bestEnd] == 'edge' && Math.abs(step[2]) < Math.toRadians(30d))
                    found << "${s.contour.source}: ${best + 1} edges of ${mm(step[0])} mm turning by ${mm(Math.toDegrees(step[2]))}°, an arc or a circle drawn as edges".toString()
                else if (period > 1 || s.kinds[bestEnd] != 'edge')
                    found << "${s.contour.source}: ${repeats} times the same ${period} elements along a contour of $n, a loop".toString()
                break
            }
        }
        found
    }

    /**
     * Small closed contours side by side in a plane, of several shapes: letters
     */
    List<String> text() {
        List<Shape> small = shapes.findAll { Shape s ->
            s.contour.closed && s.normal && s.height < 40d && (s.steps.size() >= 4 || 'spline' in s.kinds)
        }
        List<List<Shape>> rows = []
        for (Shape s in small) {
            List<List<Shape>> joined = rows.findAll { List<Shape> r -> r.any { Shape o -> sideBySide(o, s) } }
            List<Shape> row = [s]
            for (List<Shape> r in joined) {
                row.addAll(r)
                rows.remove(r)
            }
            rows << row
        }
        // letters are faces, with their holes: o, a, e...
        rows.findAll { List<Shape> r -> r.collect { Shape s -> s.contour.wire }.unique().size() >= 3 && distinctShapes(r) >= 3 }.collect { List<Shape> r ->
            double height = r.collect { Shape s -> s.height }.max()
            "${sources(r)}: ${r.size()} small contours side by side, ${mm(height)} mm high, like text: draw it with text()".toString()
        }
    }

    private static boolean sideBySide(Shape a, Shape b) {
        // the letters of a text are in a model, in a plane
        if (a.contour.model != b.contour.model || Math.abs(Math.abs(dot(a.normal, b.normal)) - 1d) > 1e-3d ||
                Math.abs(dot(sub(b.center, a.center), a.normal)) > 0.05d) return false
        double h = Math.max(a.height, b.height)
        Math.min(a.height, b.height) > h / 4d && a.near(b, h * 0.8d)
    }

    private int distinctShapes(List<Shape> row) {
        List<Shape> kinds = []
        for (Shape s in row) if (!kinds.any { Shape k -> congruent(k, s) }) kinds << s
        kinds.size()
    }
}
