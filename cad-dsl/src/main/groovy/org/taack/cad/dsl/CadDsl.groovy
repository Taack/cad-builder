package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static java.lang.Math.*

@CompileStatic
class CadDsl extends CadDslSolid implements CadDslBase {
    static CadDsl cd() {

        new CadDsl(visitor: new CadDslVisitor())
    }

    CadDslWire wireFrom(Vec pos, @DelegatesTo(value = CadDslEdge, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        new CadDslWire(visitor: visitor)
    }

    CadDslWire2d wireFrom(Vec2d pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge2d(visitor: visitor)
            c.call()
        }
        visitor.visitFromEnd(pos)
        new CadDslWire2d(visitor: visitor)
    }

    CadDsl position(Vec pos, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = this
            c.call()
        }
        visitor.visitFromEnd(pos)

        this
    }

    CadDsl direction(Vec axis, Vec normal = null) {
        visitor.visitDirection(axis, normal)
        this
    }

    CadDslSolid box(Number length, Number height, Number thickness) {
        visitor.visitBox(length.toDouble(), height.toDouble(), thickness.toDouble())
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Make a Sphere whose center is the current position
     *
     * @param radius Sphere radius
     * @param direction coordinate system for the construction of the sphere
     * @param angleFrom first angle defining a spherical segment
     * @param angleTo second angle defining a spherical segment
     * @return
     */
    CadDslSolid sphere(Number radius, Number angleFrom = 0, Number angleTo = 2 * PI) {
        visitor.visitSphere(radius.toDouble(), angleFrom.toDouble(), angleTo.toDouble())
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Make a Cylinder whose center is the current position
     * @param radius
     * @param height
     * @param direction
     * @return
     */
    CadDslSolid cylinder(Number radius, Number height) {
        visitor.visitCylinder(radius.toDouble(), height.toDouble())
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid cone(Number radius1, Number radius2, Number height) {
        visitor.visitCone(radius1.toDouble(), radius2.toDouble(), height.toDouble())
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid torus(Number torusRadius, Number ringRadius) {
        visitor.visitTorus(torusRadius.toDouble(), ringRadius.toDouble())
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid thruSection(@DelegatesTo(value = CadDslThruSection, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitThruSection()
        if (c) {
            c.delegate = new CadDslThruSection(visitor: visitor)
            c.call()
        }
        visitor.visitThruSectionEnd()

        new CadDslSolid(visitor: visitor)
    }
}
