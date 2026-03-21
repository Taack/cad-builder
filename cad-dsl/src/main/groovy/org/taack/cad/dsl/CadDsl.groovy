package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static java.lang.Math.*

@CompileStatic
class CadDsl {
    static CadDsl cd() {
        new CadDsl()
    }

    CadDsl from(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) c = null) {

    }

    CadDsl from(Vec2d pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) c = null) {

    }

    CadDslWire toWire() {

    }

    CadDslSolid box(Number length, Number height, Number thickness) {

    }

    /**
     * Make a Sphere whose center is the current position
     *
     * @param radius    Sphere radius
     * @param direction coordinate system for the construction of the sphere
     * @param angleFrom first angle defining a spherical segment
     * @param angleTo   second angle defining a spherical segment
     * @return
     */
    CadDslSolid sphere(Number radius, Vec direction = new Vec(1), Number angleFrom = 0, Number angleTo = 2 * PI) {

    }

    /**
     * Make a Cylinder whose center is the current position
     * @param radius
     * @param height
     * @param direction
     * @return
     */
    CadDslSolid cylinder(Number radius, Number height, Vec direction = new Vec(1)) {

    }

    CadDslSolid torus(Number torusRadius, Number ringRadius, Vec direction = new Vec(1)) {

    }

    CadDslSolid cut(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) c = null) {

    }

    CadDslSolid fuse(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) c = null) {

    }
}
