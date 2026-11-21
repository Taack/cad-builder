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

    static void addPoint(Integer id, double x, double y, double z) {
        pointMap.put(id, Point.valueOfPoint(x, y, z))
    }

    static void addPoint(Integer id, List<String> text) {
        pointMap.put(id, Point.valueOfPoint(text[0].toDouble(), text[1].toDouble(), text[2].toDouble()))
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
