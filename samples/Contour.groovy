import org.taack.cad.dsl.CadDsl
import org.taack.cad.dsl.CadDslEdge
import org.taack.cad.dsl.CadDslFace
import org.taack.cad.dsl.CadDslWire
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static java.lang.Math.*

/**
 * A closed contour of lines and arcs in a plane, in (u, v) coordinates; face() puts it in 3D, in a plane given by
 * xy(z), xz(y) or yz(x).
 *
 * Used by the samples beside it: cad-dsl finds the classes in the directory of the script.
 */
class Contour {
    private final List<Vec2d> points = []
    // middle of the arc ending at the point of same index, null for a line
    private final List<Vec2d> vias = []

    static Contour start(double u, double v) {
        Contour c = new Contour()
        c.points << new Vec2d(u, v)
        c.vias << null
        c
    }

    /** Rectangle centered on the origin */
    static Contour rectangle(double width, double length) {
        start(width / 2, 0).lineTo(width / 2, length / 2).lineTo(0, length / 2).mirrorQuarters()
    }

    /** Rectangle centered on the origin, its corners rounded */
    static Contour roundedRectangle(double width, double length, double radius) {
        start(width / 2, 0)
                .lineTo(width / 2, length / 2 - radius)
                .arcTo(width / 2 - radius, length / 2, width / 2 - radius, length / 2 - radius)
                .lineTo(0, length / 2)
                .mirrorQuarters()
    }

    // Planes of the contours
    static Closure<Vec> xy(double z) { { Vec2d p -> new Vec(p.x, p.y, z) } }
    static Closure<Vec> xz(double y) { { Vec2d p -> new Vec(p.x, y, p.y) } }
    static Closure<Vec> yz(double x) { { Vec2d p -> new Vec(x, p.x, p.y) } }

    Contour lineTo(double u, double v) {
        points << new Vec2d(u, v)
        vias << null
        this
    }

    /** Arc around (cu, cv) to (u, v), the short way, or the long one with longWay */
    Contour arcTo(double u, double v, double cu, double cv, boolean longWay = false) {
        Vec2d p = points.last()
        double r = hypot(p.x - cu, p.y - cv)
        double a1 = atan2(p.y - cv, p.x - cu)
        double sweep = atan2(v - cv, u - cu) - a1
        while (sweep > PI) sweep -= 2 * PI
        while (sweep <= -PI) sweep += 2 * PI
        if (longWay) sweep -= signum(sweep) * 2 * PI
        double a = a1 + sweep / 2
        points << new Vec2d(u, v)
        vias << new Vec2d(cu + r * cos(a), cv + r * sin(a))
        this
    }

    /** The whole contour, from a quarter going from the +u axis to the +v axis, by symmetry around both axes */
    Contour mirrorQuarters() {
        Contour c = new Contour()
        c.points.addAll(points)
        c.vias.addAll(vias)
        c.append(map { Vec2d p -> new Vec2d(-p.x, p.y) }, true)
        c.append(map { Vec2d p -> new Vec2d(-p.x, -p.y) }, false)
        c.append(map { Vec2d p -> new Vec2d(p.x, -p.y) }, true)
        c
    }

    /** The face of the contour, (u, v) placed in 3D by to3d, with the contours of its holes */
    CadDslFace face(CadDsl dsl, Closure<Vec> to3d, List<Contour> holes = []) {
        CadDslWire wire = dsl.wireFrom(to3d(points[0])) { draw(delegate as CadDslEdge, to3d) }
        holes.each { Contour hole -> wire.move(to3d(hole.points[0])) { hole.draw(delegate as CadDslEdge, to3d) } }
        wire.toFace()
    }

    // the edges of the contour, from its first point
    private void draw(CadDslEdge e, Closure<Vec> to3d) {
        for (int i = 1; i < points.size(); i++) {
            if (vias[i]) e.arc(to3d(points[i]), to3d(vias[i]))
            else e.edge(to3d(points[i]))
        }
        Vec2d last = points.last()
        if (hypot(last.x - points[0].x, last.y - points[0].y) > 1e-9) e.edge(to3d(points[0]))
    }

    private Contour map(Closure<Vec2d> f) {
        Contour c = new Contour()
        c.points.addAll(points.collect(f))
        c.vias.addAll(vias.collect { it ? f(it) : null })
        c
    }

    // the points of other after the first one, backwards if reversed
    private void append(Contour other, boolean reversed) {
        int n = other.points.size()
        for (int i = 1; i < n; i++) {
            if (reversed) {
                points << other.points[n - 1 - i]
                vias << other.vias[n - i]
            } else {
                points << other.points[i]
                vias << other.vias[i]
            }
        }
    }
}
