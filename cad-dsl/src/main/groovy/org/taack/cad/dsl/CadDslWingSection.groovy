package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

@CompileStatic
class CadDslWingSection implements CadDslBase {
    Vec leadingEdge
    double chord
    double twist

    /**
     * Draw a closed profile for a unit chord: x goes from the leading edge (0) to the trailing edge (1),
     * y is the thickness direction.
     * @param from  First point of the profile
     */
    CadDslWing profile(Vec2d from, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitWingSection(leadingEdge, chord, twist, from)
        c.delegate = new CadDslEdge2d(visitor: visitor, at2d: from)
        c.call()
        visitor.visitWingSectionEnd(from)
        new CadDslWing(visitor: visitor)
    }

    /**
     * Use a NACA 4-digit airfoil as profile.
     * @param code  4 digits, e.g. "2412": 2% camber at 40% of the chord, 12% thick
     */
    CadDslWing naca(String code) {
        profile(new Vec2d(1, 0)) {
            naca(code)
        }
    }
}
