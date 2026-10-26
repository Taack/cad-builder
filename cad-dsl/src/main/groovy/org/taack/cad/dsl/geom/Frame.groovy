package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import static java.lang.Math.*

/**
 * Orthonormal frame: an origin, a normal z and an x direction perpendicular to it.
 */
@CompileStatic
final class Frame {
    final Vec origin
    final Vec x
    final Vec z

    /**
     * @param x Projected on the plane normal to z; when omitted, the global X (or Y when z is close to X) projected
     */
    Frame(Vec origin, Vec z, Vec x = null) {
        this.origin = origin
        this.z = unit(z)
        Vec ref = x ?: (abs(this.z.x) < 0.9 ? Vec.vX : Vec.vY)
        Vec projected = ref - this.z * ref.cord(this.z)
        if (projected.cord(projected) < 1e-18) throw new IllegalArgumentException("Frame x direction $x is along z $z")
        this.x = unit(projected)
    }

    /**
     * Plane z = height, (u, v) along (X, Y), facing +Z
     */
    static Frame xy(double height = 0) {
        new Frame(new Vec(0, 0, height), Vec.vZ, Vec.vX)
    }

    /**
     * Plane y = height, (u, v) along (X, Z), facing -Y
     */
    static Frame xz(double height = 0) {
        new Frame(new Vec(0, height, 0), -Vec.vY, Vec.vX)
    }

    /**
     * Plane x = height, (u, v) along (Y, Z), facing +X
     */
    static Frame yz(double height = 0) {
        new Frame(new Vec(height, 0, 0), Vec.vX, Vec.vY)
    }

    /**
     * Frame of the plane through point, of normal z: its origin the global origin projected on the plane, its x the
     * default one (the global X projected, or Y when z is close to X), as on a face selected by topZ(), butX() ...
     */
    static Frame ofPlane(Vec point, Vec z) {
        Vec n = unit(z)
        new Frame(n * point.cord(n), n)
    }

    /**
     * The y direction, z × x
     */
    Vec getY() {
        z / x
    }

    /**
     * The point of the plane of the frame at (u, v), along x and y
     */
    Vec at(Vec2d p) {
        origin + x * p.x + y * p.y
    }

    Trsf toTrsf() {
        Trsf.ofFrame(origin, x, z)
    }

    Frame transformed(Trsf t) {
        new Frame(t.apply(origin), t.applyDir(z), t.applyDir(x))
    }

    private static Vec unit(Vec v) {
        double n = sqrt(v.cord(v))
        if (n == 0) throw new IllegalArgumentException("Null direction")
        v * (1 / n)
    }

    @Override
    String toString() {
        "Frame{origin=$origin, z=$z, x=$x}"
    }
}
