package org.taack.cad.step

import groovy.transform.CompileStatic

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

    @Override
    String toString() {
        return "StepFile{" +
                "fileName='" + fileName + '\'' +
                ", software='" + software + '\'' +
                ", creationDate=" + creationDate +
                pointMap +
                '}'
    }
}
