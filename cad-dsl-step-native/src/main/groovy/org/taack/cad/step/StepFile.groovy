package org.taack.cad.step

import groovy.transform.CompileStatic
import static java.lang.Math.*

@CompileStatic
class StepFile {
    final String fileName
    final String software
    final Date creationDate

    StepFile(String fileName, String software, Date creationDate) {
        this.fileName = fileName
        this.software = software
        this.creationDate = creationDate
    }

    @CompileStatic
    final static class Line {
        private final Integer p1
        private final Integer p2

        private Line(Integer p1, Integer p2) {
            this.p1 = p1
            this.p2 = p2
        }

        static Line valueOfLine(Integer p1, Integer p2) {
            return new Line(p1, p2)
        }

        @Override
        String toString() {
            return "Line{" +
                    "p1=" + p1 +
                    ", p2=" + p2 +
                    '}'
        }
    }

    @CompileStatic
    final static class VertexPoint {
        private final Integer p1

        private VertexPoint(Integer p1) {
            this.p1 = p1
        }

        static VertexPoint valueOfVertexPoint(Integer p1) {
            return new VertexPoint(p1)
        }

        Point getPoint() {
            pointMap[p1]
        }

        @Override
        String toString() {
            return "VertexPoint{" +
                    "p1=" + p1 +
                    ", point=" + point +
                    '}'
        }
    }

    @CompileStatic
    final static class Vector {
        private final Integer p1
        private final double length

        private Vector(Integer p1, double length) {
            this.p1 = p1
            this.length = length
        }

        static Vector valueOfVector(Integer p1, double length) {
            return new Vector(p1, length)
        }

        @Override
        String toString() {
            return "Vector{" +
                    "p1=" + p1 +
                    ", length=" + length +
                    '}'
        }
    }

    @CompileStatic
    final static class EdgeCurve {
        private final Integer vp1
        private final Integer vp2
        private final Integer vp3
        private final Boolean sameSense

        private EdgeCurve(Integer vp1, Integer vp2, Integer vp3, Boolean sameSense) {
            this.vp1 = vp1
            this.vp2 = vp2
            this.vp3 = vp3
            this.sameSense = sameSense
        }

        VertexPoint getVertexPoint1() {
            vertexPointMap[vp1]
        }

        VertexPoint getVertexPoint2() {
            vertexPointMap[vp1]
        }

        VertexPoint getVertexPoint3() {
            vertexPointMap[vp1]
        }

        static EdgeCurve valueOfEdgeCurve(Integer vp1, Integer vp2, Integer vp3, Boolean sameSense) {
            return new EdgeCurve(vp1, vp2, vp3, sameSense)
        }

        @Override
        String toString() {
            return "EdgeCurve{" +
                    "vp1=" + vp1 +
                    ", vp2=" + vp2 +
                    ", vp3=" + vp3 +
                    ", vertexPoint1=" + vertexPoint1 +
                    ", vertexPoint2=" + vertexPoint2 +
                    ", vertexPoint3=" + vertexPoint3 +
                    ", sameSense=" + sameSense +
                    '}'
        }
    }

    @CompileStatic
    final static class Axis2Placement3d {
        private final Integer p1
        private final Integer p2
        private final Integer p3

        private Axis2Placement3d(Integer p1, Integer p2, Integer p3) {
            this.p1 = p1
            this.p2 = p2
            this.p3 = p3
        }

        static Axis2Placement3d valueOfAxis2Placement3d(Integer p1, Integer p2, Integer p3) {
            return new Axis2Placement3d(p1, p2, p3)
        }

        @Override
        String toString() {
            return "Axis2Placement3d{" +
                    "p1=" + p1 +
                    ", p2=" + p2 +
                    ", p3=" + p3 +
                    '}'
        }
    }

    @CompileStatic
    final static class OrientedEdge {
        private final Integer ec
        private final Boolean sameSense

        private OrientedEdge(Integer edgeCurve, Boolean sameSense) {
            this.ec = edgeCurve
            this.sameSense = sameSense
        }

        static OrientedEdge valueOfOrientedEdge(Integer vp1, Boolean sameSense) {
            return new OrientedEdge(vp1, sameSense)
        }

        EdgeCurve getEdgeCurve() {
            edgeCurveMap[ec]
        }

        @Override
        String toString() {
            return "OrientedEdge{" +
                    "vp1=" + ec +
                    ", edgeCurve=" + edgeCurve +
                    ", sameSense=" + sameSense +
                    '}'
        }
    }

    @CompileStatic
    final static class EdgeLoop {
        private final List<Integer> oeList

        private EdgeLoop(List<Integer> oe) {
            this.oeList = oe
        }

        static EdgeLoop valueOfEdgeLoop(List<Integer> orientedEdges) {
            return new EdgeLoop(orientedEdges)
        }

        List<OrientedEdge> getOrientedEdges() {
            oeList.collect {
                orientedEdgeMap[it]
            }
        }

        @Override
        String toString() {
            return "EdgeLoop{" +
                    "oeList=" + oeList +
                    ", orientedEdges=" + orientedEdges +
                    '}'
        }
    }

    @CompileStatic
    final static class Point {
        private final double x
        private final double y
        private final double z

        private Point(double x, double y, double z) {
            this.x = x
            this.y = y
            this.z = z
        }

        private static Point ORIGIN = new Point(0d, 0d, 0d)

        private static boolean isOrigin(double x, double y, double z) {
            return x == 0 && y == 0 && z == 0;
        }

        Point minus(Point other) {
            valueOfPoint(other.x - x, other.y - y, other.z - z)
        }

        double abs(Point other) {
            abs(x - other.x) + abs(y - other.y) + abs(z - other.z)
        }
        Map<Integer, Point> closestPointMap(double tol = 0.0d) {
            pointMap.findAll { abs(it.value) <= tol }
        }

        Map<Integer, Point> symetricalPointMap(Point center = ORIGIN, double tol = 0.0d) {
            pointMap.findAll { (it.value - this).abs(center) <= tol }
        }


        static Point valueOfPoint(double x, double y, double z) {
            // returns a cached instance if it is the origin, or a new instance
            if (isOrigin(x, y, z)) {
                return ORIGIN;
            }
            return new Point(x, y, z);
        }

        @Override
        String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}'
        }
    }

    private static final Map<Integer, Point> pointMap = [:]
    private static final Map<Integer, Point> dirMap = [:]
    private static final Map<Integer, Line> lineMap = [:]
    private static final Map<Integer, Point> colorMap = [:]
    private static final Map<Integer, VertexPoint> vertexPointMap = [:]
    private static final Map<Integer, Vector> vectorMap = [:]
    private static final Map<Integer, EdgeCurve> edgeCurveMap = [:]
    private static final Map<Integer, OrientedEdge> orientedEdgeMap = [:]
    private static final Map<Integer, EdgeLoop> edgeLoopMap = [:]
    private static final Map<Integer, Axis2Placement3d> axis2Placement3dMap = [:]

    static void addPoint(Integer id, double x, double y, double z) {
        pointMap.put(id, Point.valueOfPoint(x, y, z))
    }

    static void addDir(Integer id, double x, double y, double z) {
        dirMap.put(id, Point.valueOfPoint(x, y, z))
    }

    static void addCol(Integer id, double x, double y, double z) {
        colorMap.put(id, Point.valueOfPoint(x, y, z))
    }

    static void addPoint(Integer id, List<String> text) {
        addPoint(id, text[0].toDouble(), text[1].toDouble(), text[2].toDouble())
    }

    static void addDir(Integer id, List<String> text) {
        addDir(id, text[0].toDouble(), text[1].toDouble(), text[2].toDouble())
    }

    static void addCol(Integer id, List<String> text) {
        addCol(id, text[0].toDouble(), text[1].toDouble(), text[2].toDouble())
    }

    static void addLine(Integer id, List<String> text) {
        lineMap.put(id, Line.valueOfLine(text[0].toInteger(), text[0].toInteger()))
    }

    static void addVertexPoint(Integer id, String text) {
        vertexPointMap.put(id, VertexPoint.valueOfVertexPoint(text.toInteger()))
    }

    static void addVector(Integer id, List<String> text) {
        vectorMap.put(id, Vector.valueOfVector(text[0].toInteger(), text[1].toDouble()))
    }

    static void addEdgeCurve(Integer id, List<String> text, boolean sameSense) {
        edgeCurveMap.put(id, EdgeCurve.valueOfEdgeCurve(text[0].toInteger(), text[1].toInteger(), text[2].toInteger(), sameSense))
    }

    static void addAxis2Placement3d(Integer id, List<String> text) {
        axis2Placement3dMap.put(id, Axis2Placement3d.valueOfAxis2Placement3d(text[0].toInteger(), text[1].toInteger(), text[2].toInteger()))
    }

    static void addOrientedEdge(Integer id, String text, boolean sameSense) {
        orientedEdgeMap.put(id, OrientedEdge.valueOfOrientedEdge(text.toInteger(), sameSense))
    }

    static void addEdgeLoop(Integer id, List<String> text) {
        edgeLoopMap.put(id, EdgeLoop.valueOfEdgeLoop(text*.toInteger()))
    }

    @Override
    String toString() {
        return "StepFile{" +
                "fileName='" + fileName + '\'' +
                ", software='" + software + '\'' +
                ", creationDate=" + creationDate + '\n' +
                "pointMap=" + pointMap.size() + '\n' +
                "dirMap=" + dirMap.size() + '\n' +
                "edgeCurveMap=" + edgeCurveMap.size() + '\n' +
                "lineMap=" + lineMap.size() + '\n' +
                "colorMap=" + colorMap.size() + '\n' +
                "vertexPointMap=" + vertexPointMap.size() + '\n' +
                "vectorMap=" + vectorMap.size() + '\n' +
                "axis2Placement3dMap=" + axis2Placement3dMap.size() + '\n' +
                "edgeLoopMap=" + edgeLoopMap.size() + '\n' +
                '}'
    }
}
