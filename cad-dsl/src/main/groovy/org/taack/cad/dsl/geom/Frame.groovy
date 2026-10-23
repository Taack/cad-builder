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
