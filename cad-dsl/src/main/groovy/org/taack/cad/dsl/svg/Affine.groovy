package org.taack.cad.dsl.svg

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec2d

/**
 * Affine transformation of the plane, as an SVG matrix(a b c d e f): x' = a x + c y + e, y' = b x + d y + f.
 */
@CompileStatic
final class Affine {
    static final Affine IDENTITY = new Affine(1, 0, 0, 1, 0, 0)

    final double a, b, c, d, e, f

    Affine(double a, double b, double c, double d, double e, double f) {
        this.a = a
        this.b = b
        this.c = c
        this.d = d
        this.e = e
        this.f = f
    }

    static Affine translation(double tx, double ty) {
        new Affine(1, 0, 0, 1, tx, ty)
    }

    static Affine scale(double sx, double sy) {
        new Affine(sx, 0, 0, sy, 0, 0)
    }

    static Affine rotation(double degrees) {
        double r = Math.toRadians(degrees)
        new Affine(Math.cos(r), Math.sin(r), -Math.sin(r), Math.cos(r), 0, 0)
    }

    Vec2d apply(Vec2d p) {
        new Vec2d(a * p.x + c * p.y + e, b * p.x + d * p.y + f)
    }

    /** This transformation after other */
    Affine multiply(Affine other) {
        new Affine(a * other.a + c * other.b, b * other.a + d * other.b,
                a * other.c + c * other.d, b * other.c + d * other.d,
                a * other.e + c * other.f + e, b * other.e + d * other.f + f)
    }

    /** True if it keeps circles circles: a rotation, a uniform scale, a mirror and a translation */
    boolean isSimilarity() {
        double n1 = a * a + b * b, n2 = c * c + d * d
        Math.abs(n1 - n2) <= 1e-9 * Math.max(n1, n2) && Math.abs(a * c + b * d) <= 1e-9 * Math.max(n1, n2)
    }

    /** Scale of a similarity */
    double scaleFactor() {
        Math.sqrt(Math.abs(a * d - b * c))
    }

    @Override
    String toString() {
        "Affine{$a, $b, $c, $d, $e, $f}"
    }
}
