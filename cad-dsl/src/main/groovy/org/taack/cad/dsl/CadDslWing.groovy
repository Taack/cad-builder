package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

@CompileStatic
class CadDslWing implements CadDslBase {

    /**
     * Declare the next section of the wing; draw it with {@link CadDslWingSection#profile} or {@link CadDslWingSection#naca}.
     * @param leadingEdge   Position of the leading edge, relative to the wing position
     * @param chord         Chord length: the profile is drawn for a unit chord and scaled by it
     * @param twist         Twist in degrees around the quarter chord, positive nose up
     */
    /**
     * Twist of a propeller blade section, in degrees: the angle of a helix of this pitch at this radius.
     * @param pitch     Advance per turn of the propeller
     * @param radius    Radius of the section
     */
    static double pitchTwist(Number pitch, Number radius) {
        Math.toDegrees(Math.atan(pitch.toDouble() / (2 * Math.PI * radius.toDouble())))
    }

    CadDslWingSection section(Vec leadingEdge, Number chord, Number twist = 0) {
        new CadDslWingSection(visitor: visitor, leadingEdge: leadingEdge, chord: chord.toDouble(), twist: twist.toDouble())
    }
}
