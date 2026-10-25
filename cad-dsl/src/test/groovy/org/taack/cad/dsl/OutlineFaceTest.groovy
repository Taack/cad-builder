package org.taack.cad.dsl

import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Outline
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

/**
 * The visits of face(plane, outline) and arcAround, recorded without a kernel
 */
class OutlineFaceTest {
    private final List<String> visits = []

    private CadDsl dsl() {
        Closure record = { String name -> { Object[] a -> visits << "$name ${a.collect { round(it) }.join(' ')}".toString(); null } }
        ICadDslVisitor v = [visitFrom: record('from'), visitFromEnd: record('end'), visitEdge: record('edge'),
                            visitArc: record('arc'), visitToFace: record('face'), visitTo: record('to'),
                            visitSpline: record('spline')] as ICadDslVisitor
        new CadDsl(visitor: v)
    }

    private static String round(Object o) {
        Closure<String> r = { double d -> String.format(Locale.ROOT, '%.3f', d + 0d).replace('-0.000', '0.000') }
        if (o instanceof Vec) return "(${r(o.x)}, ${r(o.y)}, ${r(o.z)})"
        if (o instanceof Vec2d) return "(${r(o.x)}, ${r(o.y)})"
        if (o instanceof Number) return r((double) o)
        String.valueOf(o)
    }

    @Test
    void "The face of an outline and of its holes, in its plane"() {
        dsl().face(Frame.xz(5), Outline.rectangle(4, 2), Outline.rectangle(2, 1))
        assertEquals(['from (2.000, 5.000, -1.000)', 'edge (2.000, 5.000, 1.000)', 'edge (-2.000, 5.000, 1.000)',
                      'edge (-2.000, 5.000, -1.000)', 'edge (2.000, 5.000, -1.000)', 'end (2.000, 5.000, -1.000)',
                      'from (1.000, 5.000, -0.500)', 'edge (1.000, 5.000, 0.500)', 'edge (-1.000, 5.000, 0.500)',
                      'edge (-1.000, 5.000, -0.500)', 'edge (1.000, 5.000, -0.500)', 'end (1.000, 5.000, -0.500)',
                      'face '], visits)
    }

    @Test
    void "Frames place (u, v) on their planes"() {
        Vec2d p = new Vec2d(1, 2)
        assertEquals('(1.000, 2.000, 3.000)', round(Frame.xy(3).at(p)))
        assertEquals('(1.000, 3.000, 2.000)', round(Frame.xz(3).at(p)))
        assertEquals('(3.000, 1.000, 2.000)', round(Frame.yz(3).at(p)))
    }

    @Test
    void "arcAround goes the short way or the long one"() {
        dsl().wireFrom(new Vec2d(1, 0)) {
            arcAround(new Vec2d(0, 1), new Vec2d())
            arcAround(new Vec2d(1, 0), new Vec2d(), true)
        }
        dsl().wireFrom(new Vec(0, 0, 2)) {
            edge(new Vec(0, 2, 2))
            arcAround(new Vec(0, 0, 4), new Vec(0, 0, 2))
        }
        double h = Math.sqrt(2) / 2
        assertEquals(["arc (0.000, 1.000) (${round(h)}, ${round(h)})".toString(), "arc (1.000, 0.000) (${round(-h)}, ${round(-h)})".toString(),
                      "arc (0.000, 0.000, 4.000) (0.000, ${round(2 * h)}, ${round(2 + 2 * h)})".toString()],
                visits.findAll { it.startsWith('arc') })
    }

    @Test
    void "arcAround needs the last position, as far from the center as its end"() {
        assertThrows(IllegalStateException) { dsl().wireFrom(new Vec2d(1, 0)) { naca('2412'); arcAround(new Vec2d(0, 1), new Vec2d()) } }
        assertThrows(IllegalArgumentException) { dsl().wireFrom(new Vec2d(1, 0)) { arcAround(new Vec2d(0, 2), new Vec2d()) } }
        assertThrows(IllegalArgumentException) { dsl().wireFrom(new Vec2d(1, 0)) { arcAround(new Vec2d(-1, 0), new Vec2d()) } }
    }
}
