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

    CadDsl position(Vec pos, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitFrom(pos)
        if (c) {
            c.delegate = new CadDslEdge2d(visitor: visitor)
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
        visitor.visitBox(length, height, thickness)
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
    CadDslSolid sphere(Number radius, Number angleFrom = 0, Number angleTo = 2 * PI) {
        visitor.visitSphere(radius, angleFrom, angleTo)
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
        visitor.visitCylinder(radius, height)
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid torus(Number torusRadius, Number ringRadius) {
        visitor.visitTorus(torusRadius, ringRadius)
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

    CadDslSolid currentSolid() {
        new CadDslSolid(visitor: visitor)
    }

    CadDslSolid common(@DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitCommon()
        if (c) {
            c.delegate = this
            c.call()
        }
        visitor.visitCommonEnd()
        new CadDslSolid(visitor: visitor)
    }

    void display(String fileName = null) {
        visitor.display(fileName)
    }
}
