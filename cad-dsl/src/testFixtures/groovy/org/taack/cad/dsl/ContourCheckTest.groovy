package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.cad.dsl.geom.Arc2d
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.IOpenShape2d
import org.taack.cad.dsl.geom.Outline
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.checkContours
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class ContourCheckTest {

    private static void square(double x, double y, double side) {
        cd().wireFrom(new Vec(x, y, 0)) {
            edge(new Vec(x + side, y, 0))
            edge(new Vec(x + side, y + side, 0))
            edge(new Vec(x, y + side, 0))
            edge(new Vec(x, y, 0))
        }.toFace().prism(new Vec(0, 0, 2))
    }

    private static List<String> found(Closure build) {
        List<String> found = checkContours(build)
        println found.join('\n')
        found
    }

    @Test
    void "Contour and its offset"() {
        def found = found {
            square(0, 0, 20)
            square(-0.3, -0.3, 20.6)
        }
        assertTrue(found.any { it.contains('offset by 0.3') }, found.toString())
    }

    @Test
    void "Nearly the same contours in parallel planes, and positions they share"() {
        def found = found {
            for (double z in [0d, 10d]) {
                // a plate, its corner cut a bit more on the second one, and the same 4 holes of radii 2 then 1.5
                double cut = z == 0d ? 3d : 3.4d
                def plate = cd().wireFrom(new Vec(0, 0, z)) {
                    edge(new Vec(60 - cut, 0, z))
                    edge(new Vec(60, cut, z))
                    edge(new Vec(60, 40, z))
                    edge(new Vec(0, 40, z))
                    edge(new Vec(0, 0, z))
                }.toFace().prism(new Vec(0, 0, 2))
                plate.cut {
                    for (List<Integer> hole in [[10, 10], [50, 10], [50, 30], [10, 30]])
                        wireFrom(new Vec(hole[0] + (z == 0d ? 2d : 1.5d), hole[1], z)) {
                            arc(new Vec(hole[0] - (z == 0d ? 2d : 1.5d), hole[1], z), new Vec(hole[0], hole[1] + (z == 0d ? 2d : 1.5d), z))
                            arc(new Vec(hole[0] + (z == 0d ? 2d : 1.5d), hole[1], z), new Vec(hole[0], hole[1] - (z == 0d ? 2d : 1.5d), z))
                        }.toFace().prism(new Vec(0, 0, 2))
                }
            }
        }
        assertTrue(found.any { it.contains('2 nearly the same contours') && it.contains('in parallel planes 10.0 mm apart') }, found.toString())
        assertTrue(found.any { it.contains('4 circles centered at the same positions') }, found.toString())
    }

    @Test
    void "Copies in a row"() {
        def found = found {
            square(0, 0, 5)
            square(10, 0, 5)
            square(20, 0, 5)
        }
        assertEquals(1, found.size(), found.toString())
        assertTrue(found[0].contains('3 copies') && found[0].contains('in a row, by (10.0, 0.00, 0.00)'), found.toString())
    }

    @Test
    void "Copies around a center"() {
        def found = found {
            for (int i = 0; i < 6; i++) {
                // a square turned with its position around the origin
                List<Vec> corners = [[28, -2], [32, -2], [32, 2], [28, 2], [28, -2]].collect { List<Integer> c ->
                    double a = Math.toRadians(60d * i)
                    new Vec(c[0] * Math.cos(a) - c[1] * Math.sin(a), c[0] * Math.sin(a) + c[1] * Math.cos(a), 0)
                }
                cd().wireFrom(corners[0]) { corners.drop(1).each { Vec c -> edge(c) } }.toFace().prism(new Vec(0, 0, 2))
            }
        }
        assertTrue(found.any { it.contains('6 copies') && it.contains('every 60.0°') }, found.toString())
    }

    @Test
    void "Arc drawn as edges and teeth along a contour"() {
        def found = found {
            cd().wireFrom(new Vec(10, 0, 0)) {
                for (int i = 1; i <= 36; i++) edge(new Vec(10 * Math.cos(Math.PI * i / 18), 10 * Math.sin(Math.PI * i / 18), 0))
            }.toFace().prism(new Vec(0, 0, 2))
            cd().wireFrom(new Vec(0, 50, 0)) {
                for (int i = 0; i < 8; i++) {
                    edge(new Vec(4 * i + 1, 50, 0))
                    edge(new Vec(4 * i + 1, 52, 0))
                    edge(new Vec(4 * i + 3, 52, 0))
                    edge(new Vec(4 * i + 3, 50, 0))
                }
                edge(new Vec(32, 50, 0))
                edge(new Vec(32, 45, 0))
                edge(new Vec(0, 45, 0))
                edge(new Vec(0, 50, 0))
            }.toFace().prism(new Vec(0, 0, 2))
        }
        assertTrue(found.any { it.contains('an arc or a circle drawn as edges') }, found.toString())
        assertTrue(found.any { it.contains('times the same 4 elements') }, found.toString())
    }

    @Test
    void "Letters drawn as contours"() {
        def found = found {
            // an L, a T and an I side by side, on a plate
            def plate = cd().position(new Vec(-2, -2, -1)) { box(25, 14, 1) }
            plate.fuse {
                wireFrom(new Vec(0, 0, 0)) {
                    edge(new Vec(6, 0, 0)); edge(new Vec(6, 2, 0)); edge(new Vec(2, 2, 0)); edge(new Vec(2, 10, 0))
                    edge(new Vec(0, 10, 0)); edge(new Vec(0, 0, 0))
                }.toFace().prism(new Vec(0, 0, 1))
                wireFrom(new Vec(11, 0, 0)) {
                    edge(new Vec(13, 0, 0)); edge(new Vec(13, 8, 0)); edge(new Vec(16, 8, 0)); edge(new Vec(16, 10, 0))
                    edge(new Vec(8, 10, 0)); edge(new Vec(8, 8, 0)); edge(new Vec(11, 8, 0)); edge(new Vec(11, 0, 0))
                }.toFace().prism(new Vec(0, 0, 1))
                wireFrom(new Vec(19, 0, 0)) {
                    edge(new Vec(21, 0, 0)); edge(new Vec(21, 10, 0)); edge(new Vec(19, 10, 0)); edge(new Vec(19, 0, 0))
                }.toFace().prism(new Vec(0, 0, 1))
            }
        }
        assertTrue(found.any { it.contains('like text') }, found.toString())
    }

    @Test
    void "An outline written once and turned by quarterTurns is no loop to write"() {
        Outline quarter = new Outline().line(10, 0, 0, 1).line(0, 3, 1, 0).line(11, 0, 0, 1).line(0, 5, -1, 0).line(10, 0, 0, 1)
                .fillet(2).line(0, 10, -1, 0).line(5, 0, 0, 1).line(0, 11, -1, 0).line(3, 0, 0, -1)
        Outline whole = quarter.quarterTurns()
        assertEquals([], found { cd().face(Frame.xy(), whole).prism(new Vec(0, 0, 1)) })
        // the same contour written out, as converted from a STEP file
        Vec2d start = whole.start()
        def written = found {
            cd().wireFrom(new Vec(start.x, start.y, 0)) {
                for (IOpenShape2d s in whole.path()) {
                    if (s instanceof Arc2d) arc(new Vec(s.to.x, s.to.y, 0), new Vec(((Arc2d) s).via.x, ((Arc2d) s).via.y, 0))
                    else edge(new Vec(s.to.x, s.to.y, 0))
                }
            }.toFace().prism(new Vec(0, 0, 1))
        }
        assertTrue(written.any { it.contains('4 times the same 10 elements') }, written.toString())
    }

    // The findings of a model written in a script: the lines of the script are known, unlike those of the DSL and its
    // tests, in org.taack.cad
    private static List<String> foundInScript(String model) {
        String script = """
            import org.taack.cad.dsl.geom.*
            import static org.taack.cad.dsl.CadDsl.*
            checkContours { $model }
        """
        List<String> found = (List<String>) new GroovyShell().evaluate(script, 'Model.groovy')
        println found.join('\n')
        found
    }

    @Test
    void "An outline whose elements are added in a loop is no loop to write"() {
        assertEquals([], foundInScript("""
            Outline hexagon = new Outline()
            (0..<6).each { int i -> double a = Math.toRadians(30 + 60 * i); hexagon.circle(10 * Math.cos(a), 10 * Math.sin(a), 3) }
            cd().face(Frame.xy(), hexagon).prism(new Vec(0, 0, 4))
        """))
        // the same outline written out
        def found = foundInScript("""
            Outline hexagon = new Outline()
            hexagon.circle(8.66, 5, 3)
            hexagon.circle(0, 10, 3)
            hexagon.circle(-8.66, 5, 3)
            hexagon.circle(-8.66, -5, 3)
            hexagon.circle(0, -10, 3)
            hexagon.circle(8.66, -5, 3)
            cd().face(Frame.xy(), hexagon).prism(new Vec(0, 0, 4))
        """)
        assertTrue(found.any { it.contains('6 times the same 2 elements') }, found.toString())
    }

    @Test
    void "Nothing in different contours"() {
        def found = found {
            square(0, 0, 20)
            cd().wireFrom(new Vec(40, 0, 0)) {
                edge(new Vec(60, 0, 0))
                arc(new Vec(40, 0, 0), new Vec(50, 10, 0))
            }.toFace().prism(new Vec(0, 0, 2))
        }
        assertEquals([], found)
    }
}
