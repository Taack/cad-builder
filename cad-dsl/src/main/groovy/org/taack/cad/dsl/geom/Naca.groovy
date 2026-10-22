package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import static java.lang.Math.*

/**
 * NACA 4-digit airfoil, with a closed trailing edge, for a unit chord: the leading edge is (0, 0),
 * the trailing edge (1, 0).
 */
@CompileStatic
final class Naca {
    private Naca() {}

    /**
     * Points from the trailing edge, over the upper surface to the leading edge, then under the lower
     * surface back to the trailing edge. The first point (trailing edge) is left out.
     * @param code      4 digits, e.g. "2412": 2% camber at 40% of the chord, 12% thick
     * @param nbPoints  Points per surface, packed near the leading edge
     */
    static List<Vec2d> points(String code, int nbPoints = 40) {
        if (!(code ==~ /\d{4}/)) throw new IllegalArgumentException("NACA 4-digit code expected, got '$code'")
        double m = (code[0] as int) / 100d
        double p = (code[1] as int) / 10d
        double t = (code[2..3] as int) / 100d

        List<Vec2d> upper = []
        List<Vec2d> lower = []
        for (int i = 0; i <= nbPoints; i++) {
            double x = (1 - cos(PI * i / nbPoints)) / 2
            double yt = 5 * t * (0.2969 * sqrt(x) - 0.1260 * x - 0.3516 * x * x + 0.2843 * x * x * x - 0.1036 * x * x * x * x)
            double yc = 0
            double dyc = 0
            if (m > 0 && p > 0) {
                double q = x < p ? p : 1 - p
                yc = m / (q * q) * (x < p ? 2 * p * x - x * x : 1 - 2 * p + 2 * p * x - x * x)
                dyc = 2 * m / (q * q) * (p - x)
            }
            double theta = atan(dyc)
            upper << new Vec2d(x - yt * sin(theta), yc + yt * cos(theta))
            lower << new Vec2d(x + yt * sin(theta), yc - yt * cos(theta))
        }
        // upper: trailing edge excluded, leading edge kept once
        upper.reverse().subList(1, upper.size()) + lower.subList(1, lower.size())
    }
}
