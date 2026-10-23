package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Degree of freedom left by a joint between two connectors: an angle around z (revolute), or a distance along z
 * (slider). Its value is fixed, follows a law of the time, or another joint through a gear.
 */
@CompileStatic
class CadDslJoint {
    enum Kind {
        REVOLUTE('angle'), SLIDER('distance')

        final String value

        Kind(String value) {
            this.value = value
        }
    }

    final Kind kind
    final double initial
    private Closure law
    // Joint driving this one through a gear
    private CadDslJoint gearedTo

    CadDslJoint(Kind kind, double initial) {
        this.kind = kind
        this.initial = initial
    }

    /**
     * @return The angle in degrees, or the distance, t seconds into the animation
     */
    double valueAt(double t) {
        law ? (law.call(t) as Number).toDouble() : initial
    }

    Trsf trsfAt(double t) {
        double v = valueAt(t)
        kind == Kind.REVOLUTE ? Trsf.rotation(Vec.vZ, v) : Trsf.translation(new Vec(0, 0, v))
    }

    /**
     * @param law Value from the time in seconds
     */
    void drive(Closure law) {
        if (gearedTo) throw new IllegalStateException("This joint is driven by a gear, its ${kind.value} cannot be set")
        this.law = law
    }

    /**
     * Follow driver: value = ratio * driver value
     */
    void gear(CadDslJoint driver, double ratio) {
        if (law) throw new IllegalStateException("This joint ${kind.value} already has a law, it cannot follow a gear")
        for (CadDslJoint j = driver; j; j = j.gearedTo)
            if (j.is(this)) throw new IllegalArgumentException("Gear loop: a joint cannot drive itself")
        gearedTo = driver
        law = { double t -> ratio * driver.valueAt(t) }
    }
}
