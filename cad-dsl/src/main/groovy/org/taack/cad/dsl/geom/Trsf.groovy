package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import static java.lang.Math.*

/**
 * Rigid transformation (rotation then translation), as the 3x4 matrix
 * | a11 a12 a13 a14 |
 * | a21 a22 a23 a24 |
 * | a31 a32 a33 a34 |
 */
@CompileStatic
final class Trsf {
    private final double[] m

    final static Trsf IDENTITY = new Trsf([1, 0, 0, 0,
                                           0, 1, 0, 0,
                                           0, 0, 1, 0] as double[])

    private Trsf(double[] m) {
        this.m = m
    }

    static Trsf translation(Vec v) {
        new Trsf([1, 0, 0, v.x,
                  0, 1, 0, v.y,
                  0, 0, 1, v.z] as double[])
    }

    /**
     * @param axis    Rotation axis direction
     * @param degrees Angle, counterclockwise looking against axis
     * @param around  A point of the rotation axis
     */
    static Trsf rotation(Vec axis, double degrees, Vec around = new Vec()) {
        double n = sqrt(axis.x * axis.x + axis.y * axis.y + axis.z * axis.z)
        if (n == 0) throw new IllegalArgumentException("Null rotation axis")
        double x = axis.x / n, y = axis.y / n, z = axis.z / n
        double a = toRadians(degrees)
        double c = cos(a), s = sin(a), t = 1 - c
        // Rodrigues' rotation formula
        Trsf r = new Trsf([t * x * x + c, t * x * y - s * z, t * x * z + s * y, 0,
                           t * x * y + s * z, t * y * y + c, t * y * z - s * x, 0,
                           t * x * z - s * y, t * y * z + s * x, t * z * z + c, 0] as double[])
        translation(around) * r * translation(-around)
    }

    /**
     * @return The transformation applying other, then this one
     */
    Trsf multiply(Trsf other) {
        double[] o = other.m
        double[] r = new double[12]
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                double v = m[4 * i] * o[j] + m[4 * i + 1] * o[4 + j] + m[4 * i + 2] * o[8 + j]
                r[4 * i + j] = j == 3 ? v + m[4 * i + 3] : v
            }
        }
        new Trsf(r)
    }

    /**
     * @return The transformation undoing this one
     */
    Trsf inverse() {
        // transposed rotation, then the opposite translation rotated back
        double[] r = [m[0], m[4], m[8], 0,
                      m[1], m[5], m[9], 0,
                      m[2], m[6], m[10], 0] as double[]
        for (int i = 0; i < 3; i++) r[4 * i + 3] = -(r[4 * i] * m[3] + r[4 * i + 1] * m[7] + r[4 * i + 2] * m[11])
        new Trsf(r)
    }

    /**
     * @return The transformation from the coordinates of the frame (origin, x, y = z ^ x, z) to the global ones
     */
    static Trsf ofFrame(Vec origin, Vec x, Vec z) {
        Vec y = z / x
        new Trsf([x.x, y.x, z.x, origin.x,
                  x.y, y.y, z.y, origin.y,
                  x.z, y.z, z.z, origin.z] as double[])
    }

    /**
     * @return The image of direction d (the translation is not applied)
     */
    Vec applyDir(Vec d) {
        new Vec(m[0] * d.x + m[1] * d.y + m[2] * d.z,
                m[4] * d.x + m[5] * d.y + m[6] * d.z,
                m[8] * d.x + m[9] * d.y + m[10] * d.z)
    }

    Vec apply(Vec p) {
        new Vec(m[0] * p.x + m[1] * p.y + m[2] * p.z + m[3],
                m[4] * p.x + m[5] * p.y + m[6] * p.z + m[7],
                m[8] * p.x + m[9] * p.y + m[10] * p.z + m[11])
    }

    /**
     * @return a11, a12, a13, a14, a21, ... a34
     */
    double[] values() {
        m.clone()
    }

    @Override
    String toString() {
        "Trsf$m"
    }
}
