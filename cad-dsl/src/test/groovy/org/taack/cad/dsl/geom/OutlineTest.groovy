package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class OutlineTest {
    private static void assertVec(double x, double y, Vec2d actual) {
        assertEquals(x, actual.x, 1e-9, "x of $actual")
        assertEquals(y, actual.y, 1e-9, "y of $actual")
    }

    private static void assertEdge(double x, double y, IOpenShape2d s) {
        assertTrue(s instanceof Edge2d, "$s is an edge")
        assertVec(x, y, s.to)
    }

    private static void assertArc(double x, double y, double viaX, double viaY, IOpenShape2d s) {
        assertTrue(s instanceof Arc2d, "$s is an arc")
        assertVec(x, y, s.to)
        assertVec(viaX, viaY, ((Arc2d) s).via)
    }

    @Test
    void "Lines meet at their intersections"() {
        Outline o = Outline.rectangle(4, 2)
        assertVec(2, -1, o.start())
        List<IOpenShape2d> path = o.path()
        assertEquals(4, path.size())
        assertEdge(2, 1, path[0])
        assertEdge(-2, 1, path[1])
        assertEdge(-2, -1, path[2])
        assertEdge(2, -1, path[3])
    }

    @Test
    void "Fillets are tangent to the lines they round"() {
        Outline o = Outline.roundedRectangle(20, 10, 2)
        assertVec(10, -3, o.start())
        List<IOpenShape2d> path = o.path()
        assertEquals(8, path.size())
        assertEdge(10, 3, path[0])
        double h = 2 / Math.sqrt(2)
        assertArc(8, 5, 8 + h, 3 + h, path[1])
        assertEdge(-8, 5, path[2])
        assertArc(10, -3, 8 + h, -3 - h, path[7])
    }

    @Test
    void "Consecutive circles are joined by their tangent line"() {
        List<IOpenShape2d> path = new Outline().circle(0, 0, 10).circle(30, 0, 10).path()
        assertEquals(4, path.size())
        assertArc(0, -10, -10, 0, path[0])
        assertEdge(30, -10, path[1])
        assertArc(30, 10, 40, 0, path[2])
        assertEdge(0, 10, path[3])
    }

    @Test
    void "A concave arc turns clockwise"() {
        // a square notched at its +u side by a circle of radius 2 centered on it, the corners of the notch convex
        List<IOpenShape2d> path = new Outline().line(5, 0, 0, 1).fillet(1).circle(5, 0, -2).fillet(1)
                .line(5, 0, 0, 1).line(0, 5, -1, 0).line(-5, 0, 0, -1).line(0, -5, 1, 0).path()
        Arc2d notch = (Arc2d) path.find { it instanceof Arc2d && Math.abs(((Arc2d) it).via.y) < 1e-9 }
        assertVec(3, 0, notch.via)
    }

    @Test
    void "A single circle is two half turns"() {
        Outline o = new Outline().circle(1, 2, 3)
        assertVec(4, 2, o.start())
        List<IOpenShape2d> path = o.path()
        assertArc(-2, 2, 1, 5, path[0])
        assertArc(4, 2, 1, -1, path[1])
    }

    @Test
    void "Offset moves the outline inward"() {
        List<IOpenShape2d> path = Outline.rectangle(4, 2).offset(0.5).path()
        assertEdge(1.5, 0.5, path[0])
        assertEdge(-1.5, 0.5, path[1])
    }

    @Test
    void "Inset keeps the radius of the fillets at their own inset"() {
        // fillets of radius 3 once moved inward by 1, then moved inward by 1 more: radius 2, around (6, 6)
        List<IOpenShape2d> path = Outline.roundedRectangle(20, 20, 3).inset(2, 1).path()
        double h = 2 / Math.sqrt(2)
        assertEdge(8, 6, path[0])
        assertArc(6, 8, 6 + h, 6 + h, path[1])
    }

    @Test
    void "Rotations and mirrors keep the outline counterclockwise"() {
        List<IOpenShape2d> turned = Outline.rectangle(4, 2).rotate(90).path()
        assertEdge(-1, 2, turned[0])
        List<IOpenShape2d> mirrored = new Outline().line(0, 0, 1, 0).line(1, 0, -1, 1).line(0, 0, 0, -1).mirrorV().path()
        assertEdge(0, -1, mirrored[0])
        assertEdge(1, 0, mirrored[1])
        assertEdge(0, 0, mirrored[2])
        // across the lines v = 2 and u = 2: the axis itself is given, not twice its value
        Outline triangle = new Outline().line(0, 0, 1, 0).line(1, 0, -1, 1).line(0, 0, 0, -1)
        List<IOpenShape2d> acrossV = triangle.mirrorV(2).path()
        assertEdge(0, 3, acrossV[0])
        assertEdge(1, 4, acrossV[1])
        assertEdge(0, 4, acrossV[2])
        List<IOpenShape2d> acrossU = triangle.mirrorU(2).path()
        assertEdge(4, 1, acrossU[0])
        assertEdge(3, 0, acrossU[1])
        assertEdge(4, 0, acrossU[2])
        List<IOpenShape2d> quarters = new Outline().circle(3, 3, 1).quarterTurns().path()
        assertEquals(8, quarters.size())
    }

    @Test
    void "Impossible outlines are reported"() {
        assertThrows(IllegalArgumentException) { new Outline().line(0, 0, 1, 0).line(0, 1, 1, 0).line(0, 0, 0, 1).path() }
        assertThrows(IllegalArgumentException) { Outline.roundedRectangle(4, 4, 3).path() }
        assertThrows(IllegalArgumentException) { new Outline().circle(0, 0, 1).circle(1, 0, 5).path() }
    }
}
