package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

import static java.lang.Math.*

@CompileStatic
class CadDsl implements CadDslBase {
    static CadDsl cd() {
        new CadDsl(visitor: new CadDslVisitor())
    }

    CadDslWire from(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        new CadDslWire(visitor: visitor)
    }

    CadDslWire2d from(Vec2d pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge2d(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        new CadDslWire2d(visitor: visitor)
    }

    CadDslSolid box(Number length, Number height, Number thickness, Vec direction = new Vec(1)) {
        visitor.visitBox(length, height, thickness, direction)
        new CadDslSolid(visitor: visitor)
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
        visitor.visitSphere(radius, direction)
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Make a Cylinder whose center is the current position
     * @param radius
     * @param height
     * @param direction
     * @return
     */
    CadDslSolid cylinder(Number radius, Number height, Vec direction = new Vec(1)) {
        visitor.visitCylinder(radius, height, direction)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid torus(Number torusRadius, Number ringRadius, Vec direction = new Vec(1)) {
        visitor.visitTorus(torusRadius, ringRadius, direction)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid cut(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCut()
        if (c) {
            c.delegate = this
            c.call()
        }
        visitor.visitCutEnd()
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid fuse(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFuse()
        if (c) {
            c.delegate = this
            c.call()
        }
        visitor.visitFuseEnd()
        new CadDslSolid(visitor: visitor)
    }

    void display(String fileName = null) {
        visitor.display(fileName)
    }
}
