package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec2d

@CompileStatic
class CadDslHelix implements CadDslBase {

    /**
     * Sweep a closed profile along the helix, to make a thread or a coil.
     * The profile is drawn in the axial section plane at the start of the helix:
     * x goes away from the axis, y goes along it, and (0, 0) is the start point of the helix.
     * @param from  First point of the profile
     */
    CadDslSolid profile(Vec2d from, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitHelixProfile(from)
        c.delegate = new CadDslEdge2d(visitor: visitor)
        c.call()
        visitor.visitHelixProfileEnd(from)
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Sweep a circle along the helix, to make a spring.
     * @param radius    Radius of the wire
     */
    CadDslSolid wire(Number radius) {
        profile(new Vec2d()) {
            circle(radius)
        }
    }
}
