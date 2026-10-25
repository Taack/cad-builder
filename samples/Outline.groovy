import org.taack.cad.dsl.geom.Vec2d

import static java.lang.Math.*

/**
 * A closed outline as it is drawn: arcs of circles and lines, in (u, v) coordinates, running counterclockwise (from
 * +u towards +v), the region on their left. Consecutive circles are joined by their tangent line, a line and a circle
 * meet where they are tangent, two lines at their intersection, and fillet(r) rounds the corner between the elements
 * around it.
 *
 * The radii are signed: > 0 for a convex arc (its center inside the region), < 0 for a concave one. offset(d) moves
 * the outline inward by d, as a draft does to the walls of a molded part, or of a pocket, over a height of
 * d / tan(draft): what is drawn on the plane of the drawing is then found on the plane of a face.
 *
 * Used by the samples beside it, with Contour: cad-dsl finds the classes in the directory of the script.
 */
class Outline {
    // CIRCLE: center c, radius r; LINE: through p, along the unit d; FILLET: radius r, between the elements around it
    private enum Kind { CIRCLE, LINE, FILLET }

    private static class Element {
        Kind kind
        Vec2d c, p, d
        double r
    }

    private final List<Element> elements = []

    Outline circle(double cu, double cv, double r) {
        elements << new Element(kind: Kind.CIRCLE, c: new Vec2d(cu, cv), r: r)
        this
    }

    /** The line through (pu, pv) along (du, dv), the region on its left */
    Outline line(double pu, double pv, double du, double dv) {
        double n = hypot(du, dv)
        elements << new Element(kind: Kind.LINE, p: new Vec2d(pu, pv), d: new Vec2d(du / n, dv / n))
        this
    }

    /** A fillet of radius r (> 0 when it is convex) between the previous element and the next one */
    Outline fillet(double r) {
        elements << new Element(kind: Kind.FILLET, r: r)
        this
    }

    /** The outline moved inward by d (outward when d < 0), its fillets keeping their radius */
    Outline offset(double d) {
        map { Element e ->
            switch (e.kind) {
                case Kind.LINE: return new Element(kind: e.kind, p: e.p + left(e.d) * d, d: e.d)
                case Kind.CIRCLE: return new Element(kind: e.kind, c: e.c, r: e.r - d)
                default: return e
            }
        }
    }

    /** The outline turned by degrees around the origin, counterclockwise */
    Outline rotate(double degrees) {
        double a = toRadians(degrees)
        Closure<Vec2d> turn = { Vec2d q -> q == null ? null : new Vec2d(q.x * cos(a) - q.y * sin(a), q.x * sin(a) + q.y * cos(a)) }
        map { Element e -> new Element(kind: e.kind, c: turn(e.c), p: turn(e.p), d: turn(e.d), r: e.r) }
    }

    /** The outline mirrored by v -> -v, still counterclockwise */
    Outline mirrorV() {
        Closure<Vec2d> flip = { Vec2d q -> q == null ? null : new Vec2d(q.x, -q.y) }
        Outline o = new Outline()
        elements.reverseEach { Element e ->
            o.elements << new Element(kind: e.kind, c: flip(e.c), p: flip(e.p), d: e.d == null ? null : -flip(e.d), r: e.r)
        }
        o
    }

    /** The whole outline, from a quarter going from the +u axis to the +v axis, turned 3 times by 90° */
    Outline quarterTurns() {
        Outline o = new Outline()
        [0, 90, 180, 270].each { int a -> o.elements.addAll(rotate(a).elements) }
        o
    }

    /**
     * The contour of the outline moved inward by inset, its fillets having their radius once moved inward by
     * filletInset: the radius of a fillet made after the draft, at the height where it is measured
     */
    Contour contour(double inset = 0, double filletInset = inset) {
        offset(filletInset).filletsResolved().offset(inset - filletInset).toContour()
    }

    // The fillets made circles
    private Outline filletsResolved() {
        Outline o = new Outline()
        int n = elements.size()
        elements.eachWithIndex { Element e, int i ->
            if (e.kind != Kind.FILLET) {
                o.elements << e
                return
            }
            Element before = elements[(i - 1 + n) % n], after = elements[(i + 1) % n]
            o.elements << new Element(kind: Kind.CIRCLE, c: filletCenter(before, after, e.r), r: e.r)
        }
        o
    }

    private Contour toContour() {
        int n = elements.size()
        // junctions[i]: where element i ends and element i + 1 starts (the ends of their tangent line for 2 circles)
        List<List<Vec2d>> junctions = (0..<n).collect { int i -> junction(elements[i], elements[(i + 1) % n]) }
        Vec2d start = junctions[n - 1][1]
        Contour contour = Contour.start(start.x, start.y)
        Vec2d at = start
        for (int i = 0; i < n; i++) {
            Element e = elements[i]
            Vec2d end = junctions[i][0]
            if (e.kind == Kind.CIRCLE) {
                if (dist(at, end) > 1e-9) contour.arcTo(end.x, end.y, e.c.x, e.c.y, sweep(e, at, end) > PI)
            } else if (dist(at, end) > 1e-9) {
                if (dot(end - at, e.d) < 0) throw new IllegalArgumentException("The fillets around the line through ${e.p} overlap")
                contour.lineTo(end.x, end.y)
            }
            Vec2d next = junctions[i][1]
            if (dist(end, next) > 1e-9) contour.lineTo(next.x, next.y)
            at = next
        }
        contour
    }

    // The angle swept by the arc of e from a to b, counterclockwise for a convex arc, clockwise for a concave one
    private static double sweep(Element e, Vec2d a, Vec2d b) {
        double s = atan2(b.y - e.c.y, b.x - e.c.x) - atan2(a.y - e.c.y, a.x - e.c.x)
        if (e.r < 0) s = -s
        while (s < 0) s += 2 * PI
        s
    }

    // Where a ends and b starts
    private static List<Vec2d> junction(Element a, Element b) {
        if (a.kind == Kind.CIRCLE && b.kind == Kind.CIRCLE) return tangent(a, b)
        if (a.kind == Kind.CIRCLE) return [foot(a.c, b), foot(a.c, b)]
        if (b.kind == Kind.CIRCLE) return [foot(b.c, a), foot(b.c, a)]
        Vec2d x = intersection(a, b)
        [x, x]
    }

    // The tangent line from circle a to circle b, the region on its left: its points are c + r n, n its right normal
    private static List<Vec2d> tangent(Element a, Element b) {
        Vec2d d = b.c - a.c
        double l = hypot(d.x, d.y)
        if (l < 1e-12) return [a.c, a.c]
        double cosT = (a.r - b.r) / l
        if (abs(cosT) > 1 + 1e-9) throw new IllegalArgumentException("No tangent line between the circles of centers ${a.c} and ${b.c}")
        Vec2d u = d * (1 / l)
        // tangent circles, as a fillet and the arc it rounds: they touch on the line of their centers
        if (abs(cosT) > 1 - 1e-9) {
            Vec2d q = a.c + u * (signum(cosT) * a.r)
            return [q, q]
        }
        Vec2d n = u * cosT + right(u) * sqrt(1 - cosT * cosT)
        [a.c + n * a.r, b.c + n * b.r]
    }

    // The center of the fillet of radius r from a to b: on the curves moved inward by r, turning the way of the fillet
    private static Vec2d filletCenter(Element a, Element b, double r) {
        List<Vec2d> candidates = meet(a.kind == Kind.LINE ? a : new Element(kind: Kind.CIRCLE, c: a.c, r: a.r - r),
                b.kind == Kind.LINE ? b : new Element(kind: Kind.CIRCLE, c: b.c, r: b.r - r), r)
        Vec2d best = candidates.find { Vec2d c ->
            Vec2d qa = touch(a, c, r), qb = touch(b, c, r)
            double turn = cross(qa - c, qb - c)
            turn * r > 0 && dot(direction(a, qa), qb - qa) > 0
        }
        if (best == null) throw new IllegalArgumentException("No fillet of radius $r between $a.kind and $b.kind")
        best
    }

    // Where the fillet of center c touches e
    private static Vec2d touch(Element e, Vec2d c, double r) {
        if (e.kind == Kind.LINE) return foot(c, e)
        e.c + (c - e.c) * (e.r / (e.r - r))
    }

    // Direction of travel of e at q
    private static Vec2d direction(Element e, Vec2d q) {
        e.kind == Kind.LINE ? e.d : left((q - e.c) * (1 / e.r))
    }

    // Intersections of two curves (circles, or lines moved inward by r)
    private static List<Vec2d> meet(Element a, Element b, double r) {
        if (a.kind == Kind.LINE && b.kind == Kind.LINE) return [intersection(moved(a, r), moved(b, r))]
        if (a.kind == Kind.LINE) return lineCircle(moved(a, r), b)
        if (b.kind == Kind.LINE) return lineCircle(moved(b, r), a)
        circleCircle(a, b)
    }

    private static Element moved(Element l, double d) {
        new Element(kind: Kind.LINE, p: l.p + left(l.d) * d, d: l.d)
    }

    private static List<Vec2d> lineCircle(Element l, Element c) {
        Vec2d f = foot(c.c, l)
        double h2 = c.r * c.r - dot(f - c.c, f - c.c)
        if (h2 < 0) return []
        double h = sqrt(h2)
        [f + l.d * h, f - l.d * h]
    }

    private static List<Vec2d> circleCircle(Element a, Element b) {
        Vec2d d = b.c - a.c
        double l = hypot(d.x, d.y)
        double x = (l * l + a.r * a.r - b.r * b.r) / (2 * l)
        double h2 = a.r * a.r - x * x
        if (h2 < 0) return []
        Vec2d u = d * (1 / l), m = a.c + u * x
        double h = sqrt(h2)
        [m + left(u) * h, m - left(u) * h]
    }

    private static Vec2d foot(Vec2d q, Element l) {
        l.p + l.d * dot(q - l.p, l.d)
    }

    private static Vec2d intersection(Element a, Element b) {
        double det = cross(a.d, b.d)
        if (abs(det) < 1e-12) throw new IllegalArgumentException("Parallel lines")
        double t = cross(b.p - a.p, b.d) / det
        a.p + a.d * t
    }

    private Outline map(Closure<Element> f) {
        Outline o = new Outline()
        o.elements.addAll(elements.collect(f))
        o
    }

    private static Vec2d left(Vec2d v) { new Vec2d(-v.y, v.x) }

    private static Vec2d right(Vec2d v) { new Vec2d(v.y, -v.x) }

    private static double cross(Vec2d a, Vec2d b) { a.x * b.y - a.y * b.x }

    private static double dot(Vec2d a, Vec2d b) { a.x * b.x + a.y * b.y }

    private static double dist(Vec2d a, Vec2d b) { hypot(a.x - b.x, a.y - b.y) }
}
