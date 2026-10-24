package org.taack.cad.dsl.svg

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec2d
import org.w3c.dom.Element
import org.w3c.dom.Node

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Reads the filled shapes of an SVG drawing as closed contours, in its user coordinates (y down), transforms applied.
 *
 * It reads path (all commands), rect (rounded too), circle, ellipse, polygon and polyline, in groups, with their
 * transform attributes. Shapes not filled (fill none) and the content of defs, clipPath, mask, pattern, marker and
 * symbol are left out; strokes are not read, convert them to paths first. Every subpath is closed, as for a fill.
 */
@CompileStatic
final class SvgReader {
    private static final Set<String> SKIPPED = ['defs', 'clipPath', 'mask', 'pattern', 'marker', 'symbol', 'title',
                                                'desc', 'metadata', 'linearGradient', 'radialGradient', 'style'] as Set<String>

    private final List<SvgContour> contours = []

    private SvgReader() {}

    /**
     * @param source An SVG document, the path of an SVG file, or the data of a path (as its d attribute)
     */
    static List<SvgContour> read(String source) {
        SvgReader reader = new SvgReader()
        String s = source.trim()
        if (s.startsWith('<')) reader.document(new ByteArrayInputStream(s.getBytes('UTF-8')))
        else if (s.toLowerCase().endsWith('.svg') || new File(s).isFile()) {
            File file = new File(s)
            if (!file.isFile()) throw new IllegalArgumentException("svg: no file $file")
            file.withInputStream { reader.document(it) }
        } else reader.path(s, Affine.IDENTITY)
        reader.contours
    }

    private void document(InputStream input) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance()
        factory.namespaceAware = true
        // never fetch the DTD or entities a document refers to
        factory.setFeature('http://apache.org/xml/features/nonvalidating/load-external-dtd', false)
        factory.setFeature('http://xml.org/sax/features/external-general-entities', false)
        factory.setFeature('http://xml.org/sax/features/external-parameter-entities', false)
        DocumentBuilder builder = factory.newDocumentBuilder()
        element(builder.parse(input).documentElement, Affine.IDENTITY, true)
    }

    private void element(Element e, Affine parent, boolean parentFilled) {
        String name = e.localName ?: e.tagName
        if (name in SKIPPED || style(e, 'display') == 'none') return
        Affine t = e.hasAttribute('transform') ? parent * transform(e.getAttribute('transform')) : parent
        String fill = style(e, 'fill')
        boolean filled = fill == null ? parentFilled : fill != 'none'
        if (filled) shape(e, name, t)
        for (Node n = e.firstChild; n != null; n = n.nextSibling) {
            if (n instanceof Element) element((Element) n, t, filled)
        }
    }

    private void shape(Element e, String name, Affine t) {
        switch (name) {
            case 'path':
                path(e.getAttribute('d'), t)
                break
            case 'rect':
                double x = length(e, 'x'), y = length(e, 'y'), w = length(e, 'width'), h = length(e, 'height')
                if (w <= 0 || h <= 0) return
                double rx = e.hasAttribute('rx') ? length(e, 'rx') : length(e, 'ry')
                double ry = e.hasAttribute('ry') ? length(e, 'ry') : rx
                rx = Math.min(rx, w / 2)
                ry = Math.min(ry, h / 2)
                PathBuilder p = new PathBuilder(t)
                p.moveTo(x + rx, y)
                p.lineTo(x + w - rx, y)
                if (rx > 0 && ry > 0) p.arcTo(rx, ry, 0, false, true, x + w, y + ry)
                p.lineTo(x + w, y + h - ry)
                if (rx > 0 && ry > 0) p.arcTo(rx, ry, 0, false, true, x + w - rx, y + h)
                p.lineTo(x + rx, y + h)
                if (rx > 0 && ry > 0) p.arcTo(rx, ry, 0, false, true, x, y + h - ry)
                p.lineTo(x, y + ry)
                if (rx > 0 && ry > 0) p.arcTo(rx, ry, 0, false, true, x + rx, y)
                contours.addAll(p.finish())
                break
            case 'circle':
                ellipse(length(e, 'cx'), length(e, 'cy'), length(e, 'r'), length(e, 'r'), t)
                break
            case 'ellipse':
                ellipse(length(e, 'cx'), length(e, 'cy'), length(e, 'rx'), length(e, 'ry'), t)
                break
            case 'polygon':
            case 'polyline':
                Scanner s = new Scanner(e.getAttribute('points'))
                PathBuilder p = new PathBuilder(t)
                boolean first = true
                while (s.hasNumber()) {
                    double x = s.number(), y = s.number()
                    if (first) p.moveTo(x, y) else p.lineTo(x, y)
                    first = false
                }
                contours.addAll(p.finish())
                break
        }
    }

    private void ellipse(double cx, double cy, double rx, double ry, Affine t) {
        if (rx <= 0 || ry <= 0) return
        PathBuilder p = new PathBuilder(t)
        p.moveTo(cx + rx, cy)
        p.arcTo(rx, ry, 0, false, true, cx - rx, cy)
        p.arcTo(rx, ry, 0, false, true, cx + rx, cy)
        contours.addAll(p.finish())
    }

    private void path(String d, Affine t) {
        PathBuilder p = new PathBuilder(t)
        Scanner s = new Scanner(d)
        char command = 'M' as char
        while (s.more()) {
            if (s.hasCommand()) command = s.command()
            else if (!s.hasNumber()) throw new IllegalArgumentException("svg: unexpected '${s.rest()}' in path data")
            boolean relative = Character.isLowerCase(command)
            double x0 = relative ? p.current.x : 0, y0 = relative ? p.current.y : 0
            switch (Character.toUpperCase(command)) {
                case 'M':
                    p.moveTo(x0 + s.number(), y0 + s.number())
                    // pairs after a moveto are linetos
                    command = relative ? 'l' as char : 'L' as char
                    break
                case 'L':
                    p.lineTo(x0 + s.number(), y0 + s.number())
                    break
                case 'H':
                    p.lineTo(x0 + s.number(), p.current.y)
                    break
                case 'V':
                    p.lineTo(p.current.x, y0 + s.number())
                    break
                case 'C':
                    double x1 = x0 + s.number(), y1 = y0 + s.number(), x2 = x0 + s.number(), y2 = y0 + s.number()
                    p.cubicTo(x1, y1, x2, y2, x0 + s.number(), y0 + s.number())
                    break
                case 'S':
                    Vec2d c1 = p.reflectedCubicControl()
                    double x2 = x0 + s.number(), y2 = y0 + s.number()
                    p.cubicTo(c1.x, c1.y, x2, y2, x0 + s.number(), y0 + s.number())
                    break
                case 'Q':
                    double qx = x0 + s.number(), qy = y0 + s.number()
                    p.quadTo(qx, qy, x0 + s.number(), y0 + s.number())
                    break
                case 'T':
                    Vec2d q = p.reflectedQuadControl()
                    p.quadTo(q.x, q.y, x0 + s.number(), y0 + s.number())
                    break
                case 'A':
                    double rx = s.number(), ry = s.number(), phi = s.number()
                    boolean large = s.flag(), sweep = s.flag()
                    p.arcTo(rx, ry, phi, large, sweep, x0 + s.number(), y0 + s.number())
                    break
                case 'Z':
                    p.close()
                    // a command must follow
                    if (s.more() && !s.hasCommand()) throw new IllegalArgumentException("svg: number after z in path data")
                    break
                default:
                    throw new IllegalArgumentException("svg: unknown path command '$command'")
            }
        }
        contours.addAll(p.finish())
    }

    // the value of a property, from its attribute or the style attribute, null if not set
    private static String style(Element e, String property) {
        if (e.hasAttribute('style')) {
            for (String declaration in e.getAttribute('style').split(';')) {
                int colon = declaration.indexOf(':')
                if (colon > 0 && declaration.substring(0, colon).trim() == property) return declaration.substring(colon + 1).trim()
            }
        }
        e.hasAttribute(property) ? e.getAttribute(property).trim() : null
    }

    // a length, its unit (px, mm...) left out
    private static double length(Element e, String attribute) {
        if (!e.hasAttribute(attribute)) return 0
        Scanner s = new Scanner(e.getAttribute(attribute))
        s.hasNumber() ? s.number() : 0
    }

    static Affine transform(String list) {
        Affine t = Affine.IDENTITY
        def m = list =~ /([a-zA-Z]+)\s*\(([^)]*)\)/
        while (m.find()) {
            String name = m.group(1)
            Scanner s = new Scanner(m.group(2))
            List<Double> v = []
            while (s.hasNumber()) v << s.number()
            Affine one
            switch (name) {
                case 'matrix': one = new Affine(v[0], v[1], v[2], v[3], v[4], v[5]); break
                case 'translate': one = Affine.translation(v[0], v.size() > 1 ? v[1] : 0d); break
                case 'scale': one = Affine.scale(v[0], v.size() > 1 ? v[1] : v[0]); break
                case 'rotate':
                    one = Affine.rotation(v[0])
                    if (v.size() > 2) one = Affine.translation(v[1], v[2]) * one * Affine.translation(-v[1], -v[2])
                    break
                case 'skewX': one = new Affine(1, 0, Math.tan(Math.toRadians(v[0])), 1, 0, 0); break
                case 'skewY': one = new Affine(1, Math.tan(Math.toRadians(v[0])), 0, 1, 0, 0); break
                default: throw new IllegalArgumentException("svg: unknown transform $name")
            }
            t = t * one
        }
        t
    }

    /**
     * Numbers, flags and commands of path data and lists of points
     */
    @CompileStatic
    static final class Scanner {
        private final String s
        private int i = 0

        Scanner(String s) {
            this.s = s ?: ''
        }

        private void skip() {
            while (i < s.length() && (Character.isWhitespace(s.charAt(i)) || s.charAt(i) == ',' as char)) i++
        }

        boolean more() {
            skip()
            i < s.length()
        }

        boolean hasCommand() {
            more() && Character.isLetter(s.charAt(i)) && !(s.charAt(i) in ['e' as char, 'E' as char])
        }

        char command() {
            skip()
            s.charAt(i++)
        }

        boolean hasNumber() {
            if (!more()) return false
            char c = s.charAt(i)
            Character.isDigit(c) || c == '-' as char || c == '+' as char || c == '.' as char
        }

        double number() {
            if (!hasNumber()) throw new IllegalArgumentException("svg: number expected at '${rest()}'")
            int start = i
            if (s.charAt(i) == '-' as char || s.charAt(i) == '+' as char) i++
            boolean dot = false
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || (!dot && s.charAt(i) == '.' as char))) {
                if (s.charAt(i) == '.' as char) dot = true
                i++
            }
            if (i < s.length() && (s.charAt(i) == 'e' as char || s.charAt(i) == 'E' as char)) {
                int mark = i++
                if (i < s.length() && (s.charAt(i) == '-' as char || s.charAt(i) == '+' as char)) i++
                if (i < s.length() && Character.isDigit(s.charAt(i))) {
                    while (i < s.length() && Character.isDigit(s.charAt(i))) i++
                } else i = mark
            }
            Double.parseDouble(s.substring(start, i))
        }

        /** An arc flag: a single 0 or 1, possibly glued to what follows */
        boolean flag() {
            skip()
            if (i >= s.length() || !(s.charAt(i) in ['0' as char, '1' as char])) throw new IllegalArgumentException("svg: arc flag expected at '${rest()}'")
            s.charAt(i++) == '1' as char
        }

        String rest() {
            s.substring(Math.min(i, s.length()))
        }
    }

    /**
     * Contours being drawn in local coordinates, mapped by t: arcs of circle are kept when t keeps circles,
     * other arcs become cubic curves
     */
    @CompileStatic
    static final class PathBuilder {
        private final Affine t
        private final List<SvgContour> done = []
        private Vec2d start
        private List<SvgContour.Segment> segments = []
        Vec2d current = new Vec2d(0, 0)
        // control point of the last curve, for S and T
        private Vec2d lastCubicControl
        private Vec2d lastQuadControl

        PathBuilder(Affine t) {
            this.t = t
        }

        void moveTo(double x, double y) {
            endContour()
            start = new Vec2d(x, y)
            current = start
            lastCubicControl = null
            lastQuadControl = null
        }

        void lineTo(double x, double y) {
            Vec2d to = new Vec2d(x, y)
            if (!same(to, current)) segments << (SvgContour.Segment) new SvgContour.Line(to)
            current = to
            lastCubicControl = null
            lastQuadControl = null
        }

        void cubicTo(double x1, double y1, double x2, double y2, double x, double y) {
            Vec2d to = new Vec2d(x, y)
            segments << (SvgContour.Segment) new SvgContour.Cubic(new Vec2d(x1, y1), new Vec2d(x2, y2), to)
            lastCubicControl = new Vec2d(x2, y2)
            lastQuadControl = null
            current = to
        }

        void quadTo(double qx, double qy, double x, double y) {
            Vec2d p0 = current
            // the same curve as a cubic one
            cubicTo(p0.x + 2 / 3d * (qx - p0.x), p0.y + 2 / 3d * (qy - p0.y), x + 2 / 3d * (qx - x), y + 2 / 3d * (qy - y), x, y)
            lastCubicControl = null
            lastQuadControl = new Vec2d(qx, qy)
        }

        Vec2d reflectedCubicControl() {
            lastCubicControl ? new Vec2d(2 * current.x - lastCubicControl.x, 2 * current.y - lastCubicControl.y) : current
        }

        Vec2d reflectedQuadControl() {
            lastQuadControl ? new Vec2d(2 * current.x - lastQuadControl.x, 2 * current.y - lastQuadControl.y) : current
        }

        /** SVG elliptical arc to (x, y), see the SVG 1.1 specification, F.6.5 */
        void arcTo(double rx, double ry, double phiDegrees, boolean large, boolean sweep, double x, double y) {
            Vec2d p1 = current
            if (same(p1, new Vec2d(x, y))) return
            rx = Math.abs(rx)
            ry = Math.abs(ry)
            if (rx == 0 || ry == 0) {
                lineTo(x, y)
                return
            }
            double phi = Math.toRadians(phiDegrees % 360)
            double cos = Math.cos(phi), sin = Math.sin(phi)
            double dx = (p1.x - x) / 2, dy = (p1.y - y) / 2
            double x1 = cos * dx + sin * dy, y1 = -sin * dx + cos * dy
            double lambda = (x1 * x1) / (rx * rx) + (y1 * y1) / (ry * ry)
            if (lambda > 1) {
                rx *= Math.sqrt(lambda)
                ry *= Math.sqrt(lambda)
            }
            double num = rx * rx * ry * ry - rx * rx * y1 * y1 - ry * ry * x1 * x1
            double den = rx * rx * y1 * y1 + ry * ry * x1 * x1
            double coef = (large != sweep ? 1 : -1) * Math.sqrt(Math.max(0d, num / den))
            double cx1 = coef * rx * y1 / ry, cy1 = -coef * ry * x1 / rx
            double cx = cos * cx1 - sin * cy1 + (p1.x + x) / 2, cy = sin * cx1 + cos * cy1 + (p1.y + y) / 2
            double theta1 = Math.atan2((y1 - cy1) / ry, (x1 - cx1) / rx)
            double delta = Math.atan2((-y1 - cy1) / ry, (-x1 - cx1) / rx) - theta1
            if (sweep && delta < 0) delta += 2 * Math.PI
            if (!sweep && delta > 0) delta -= 2 * Math.PI

            Closure<Vec2d> point = { double a ->
                new Vec2d(cx + rx * cos * Math.cos(a) - ry * sin * Math.sin(a), cy + rx * sin * Math.cos(a) + ry * cos * Math.sin(a))
            }
            Vec2d end = new Vec2d(x, y)
            if (Math.abs(rx - ry) <= 1e-9 * Math.max(rx, ry) && t.isSimilarity()) {
                // arcs of circle, at most half a turn each
                int n = Math.max(1, (int) Math.ceil(Math.abs(delta) / Math.PI - 1e-9))
                for (int k = 1; k <= n; k++) {
                    double a0 = theta1 + delta * (k - 1) / n, a1 = theta1 + delta * k / n
                    segments << (SvgContour.Segment) new SvgContour.Arc(point((a0 + a1) / 2), k == n ? end : point(a1))
                }
            } else {
                // cubic curves, at most a quarter of turn each
                int n = Math.max(1, (int) Math.ceil(Math.abs(delta) / (Math.PI / 2) - 1e-9))
                double step = delta / n, k4 = 4 / 3d * Math.tan(step / 4)
                Closure<Vec2d> derivative = { double a ->
                    new Vec2d(-rx * cos * Math.sin(a) - ry * sin * Math.cos(a), -rx * sin * Math.sin(a) + ry * cos * Math.cos(a))
                }
                for (int k = 0; k < n; k++) {
                    double a0 = theta1 + step * k, a1 = a0 + step
                    Vec2d q0 = point(a0), q1 = k == n - 1 ? end : point(a1), d0 = derivative(a0), d1 = derivative(a1)
                    segments << (SvgContour.Segment) new SvgContour.Cubic(new Vec2d(q0.x + k4 * d0.x, q0.y + k4 * d0.y),
                            new Vec2d(q1.x - k4 * d1.x, q1.y - k4 * d1.y), q1)
                }
            }
            current = end
            lastCubicControl = null
            lastQuadControl = null
        }

        void close() {
            if (start != null) {
                Vec2d back = start
                endContour()
                current = back
                start = back
            }
        }

        List<SvgContour> finish() {
            endContour()
            done
        }

        private void endContour() {
            if (start != null && segments) {
                if (!same(current, start)) segments << (SvgContour.Segment) new SvgContour.Line(start)
                done << new SvgContour(start, segments).map(t)
            }
            segments = []
            start = null
        }

        private static boolean same(Vec2d a, Vec2d b) {
            Math.abs(a.x - b.x) <= 1e-12 && Math.abs(a.y - b.y) <= 1e-12
        }
    }
}
