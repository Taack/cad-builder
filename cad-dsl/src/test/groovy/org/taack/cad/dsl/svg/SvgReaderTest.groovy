package org.taack.cad.dsl.svg

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.geom.Vec2d

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class SvgReaderTest {
    @TempDir
    File dir

    private static void assertPoint(double x, double y, Vec2d p) {
        assertEquals(x, p.x, 1e-9, "$p")
        assertEquals(y, p.y, 1e-9, "$p")
    }

    private static void assertBounds(List<Double> expected, double[] actual) {
        for (int i = 0; i < 4; i++) assertEquals(expected[i], actual[i], 1e-9, "${actual.toList()}")
    }

    @Test
    void "Path commands, absolute and relative, closed contours"() {
        List<SvgContour> contours = SvgReader.read('M0,0 h10 V10 l-10,0 z m3 3 4 0 0 4 -4 0z')
        assertEquals(2, contours.size())
        SvgContour square = contours[0]
        assertPoint(0, 0, square.start)
        assertEquals([10d, 10d, 0d, 0d], square.segments*.to*.x)
        assertEquals([0d, 10d, 10d, 0d], square.segments*.to*.y)
        assertTrue(square.segments.every { it instanceof SvgContour.Line })
        // the pairs after m are linetos, relative to the start of the subpath after z
        SvgContour inner = contours[1]
        assertPoint(3, 3, inner.start)
        assertPoint(7, 3, inner.segments[0].to)
        assertPoint(3, 3, inner.segments.last().to)
    }

    @Test
    void "Unclosed subpaths are closed, as for a fill"() {
        List<SvgContour> contours = SvgReader.read('M0 0L10 0L10 10')
        assertEquals(1, contours.size())
        assertPoint(0, 0, contours[0].segments.last().to)
        assertEquals(3, contours[0].segments.size())
    }

    @Test
    void "Compact numbers and arc flags"() {
        // 0.5.5 is 0.5 then .5, -1e1 an exponent, the flags of the arc glued
        List<SvgContour> contours = SvgReader.read('M0.5.5L-1e1 0.5a5 5 0 01 10 0z')
        SvgContour c = contours[0]
        assertPoint(0.5, 0.5, c.start)
        assertPoint(-10, 0.5, c.segments[0].to)
        assertTrue(c.segments[1] instanceof SvgContour.Arc)
        assertPoint(0, 0.5, c.segments[1].to)
    }

    @Test
    void "Smooth and quadratic curves"() {
        SvgContour c = SvgReader.read('M0 0 C0 10 10 10 10 0 S20 -10 20 0 Q25 10 30 0 T40 0 Z')[0]
        SvgContour.Cubic s = (SvgContour.Cubic) c.segments[1]
        // the first control point of S mirrors the last one of C
        assertPoint(10, -10, s.control1)
        SvgContour.Cubic q = (SvgContour.Cubic) c.segments[2]
        assertPoint(20 + 2 / 3d * 5, 2 / 3d * 10, q.control1)
        SvgContour.Cubic t = (SvgContour.Cubic) c.segments[3]
        // T mirrors the control point of Q: (35, -10)
        assertPoint(30 + 2 / 3d * 5, -2 / 3d * 10, t.control1)
        assertPoint(40, 0, t.to)
    }

    @Test
    void "Circle as two arcs, exact bounds"() {
        SvgContour c = SvgReader.read('<svg xmlns="http://www.w3.org/2000/svg"><circle cx="5" cy="5" r="5"/></svg>')[0]
        assertEquals(2, c.segments.size())
        assertTrue(c.segments.every { it instanceof SvgContour.Arc })
        assertBounds([0d, 0d, 10d, 10d], c.bounds())
    }

    @Test
    void "Ellipses and skewed circles become cubic curves"() {
        SvgContour e = SvgReader.read('<svg xmlns="http://www.w3.org/2000/svg"><ellipse cx="0" cy="0" rx="4" ry="2"/></svg>')[0]
        assertTrue(e.segments.every { it instanceof SvgContour.Cubic })
        assertBounds([-4d, -2d, 4d, 2d], e.bounds())
        SvgContour skewed = SvgReader.read('<svg xmlns="http://www.w3.org/2000/svg"><circle r="1" transform="scale(2 1)"/></svg>')[0]
        assertTrue(skewed.segments.every { it instanceof SvgContour.Cubic })
        assertBounds([-2d, -1d, 2d, 1d], skewed.bounds())
    }

    @Test
    void "Rounded rectangle, polygon, transforms of groups"() {
        List<SvgContour> contours = SvgReader.read('''<svg xmlns="http://www.w3.org/2000/svg">
            <g transform="translate(100 0)">
                <rect x="0" y="0" width="10" height="6" rx="2"/>
                <g transform="rotate(90)"><polygon points="0,0 4,0 4,2"/></g>
            </g>
        </svg>''')
        assertEquals(2, contours.size())
        assertBounds([100d, 0d, 110d, 6d], contours[0].bounds())
        assertEquals(8, contours[0].segments.size())
        // rotated a quarter of turn, then translated
        assertPoint(100, 4, contours[1].segments[0].to)
    }

    @Test
    void "Shapes not filled and definitions are left out"() {
        List<SvgContour> contours = SvgReader.read('''<?xml version="1.0"?>
            <!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd">
            <svg xmlns="http://www.w3.org/2000/svg">
                <defs><rect width="5" height="5"/></defs>
                <rect width="5" height="5" fill="none" stroke="black"/>
                <g style="fill:none"><circle r="3"/><circle r="2" style="fill: red"/></g>
                <rect width="1" height="1" style="display:none"/>
            </svg>''')
        assertEquals(1, contours.size())
        assertBounds([-2d, -2d, 2d, 2d], contours[0].bounds())
    }

    @Test
    void "SVG file"() {
        File file = new File(dir, 'square.svg')
        file.text = '<svg xmlns="http://www.w3.org/2000/svg"><path d="M0 0H2V2H0Z"/></svg>'
        assertEquals(1, SvgReader.read(file.path).size())
        assertThrows(IllegalArgumentException) { SvgReader.read(new File(dir, 'none.svg').path) }
    }

    @Test
    void "Fit to a width, y up, aligned on the bounding box"() {
        // 20 x 10 in SVG units, from (10, 10)
        String rect = '<svg xmlns="http://www.w3.org/2000/svg"><rect x="10" y="10" width="20" height="10"/></svg>'
        // from its bottom left corner by default
        assertBounds([0d, 0d, 4d, 2d], SvgDrawing.fit(rect, 4, new SvgOptions())[0].bounds())
        assertBounds([-2d, -1d, 2d, 1d], SvgDrawing.fit(rect, 4, new SvgOptions(align: 'center', valign: 'center'))[0].bounds())
        assertBounds([-4d, -2d, 0d, 0d], SvgDrawing.fit(rect, 4, new SvgOptions(align: 'right', valign: 'top'))[0].bounds())
        // the top of the SVG drawing is up
        SvgContour triangle = SvgDrawing.fit('M0 0 L10 0 L0 10 Z', 10, new SvgOptions())[0]
        assertPoint(0, 10, triangle.start)
    }

    @Test
    void "Errors"() {
        assertThrows(IllegalArgumentException) { SvgReader.read('M0 0 L1') }
        assertThrows(IllegalArgumentException) { SvgReader.read('M0 0 X1 1') }
        assertThrows(IllegalArgumentException) { new SvgOptions(size: 3) }
        assertThrows(IllegalArgumentException) { SvgDrawing.fit('<svg xmlns="http://www.w3.org/2000/svg"/>', 1, new SvgOptions()) }
    }
}
