package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernels

import static java.lang.Math.*

@CompileStatic
class CadDsl extends CadDslSolid implements CadDslBase {
    static CadDsl cd() {

        new CadDsl(visitor: CadKernels.get().newVisitor())
    }

    /**
     * Assemble parts and sub-assemblies, see {@link CadDslAssembly}.
     * @param name Assembly name, kept in STEP files
     */
    static CadDslAssembly assembly(String name = null, @DelegatesTo(value = CadDslAssembly, strategy = Closure.DELEGATE_FIRST) Closure c) {
        CadDslAssembly.build(name, c)
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

    /**
     * Start a helix whose axis is the current direction, from the current position.
     * Sweep a section along it with {@link CadDslHelix#profile} or {@link CadDslHelix#wire}.
     * @param radius    Helix radius at the start
     * @param pitch     Axial advance per turn, negative for a left-handed helix
     * @param height    Total height along the axis
     * @param endRadius Helix radius at the top, for a conical helix (defaults to radius)
     * @return
     */
    CadDslHelix helix(Number radius, Number pitch, Number height, Number endRadius = radius) {
        visitor.visitHelix(radius.toDouble(), pitch.toDouble(), height.toDouble(), endRadius.toDouble())
        new CadDslHelix(visitor: visitor)
    }

    /**
     * Make a wing (or a blade) by lofting through sections declared with {@link CadDslWing#section}.
     * From the current position, the chord goes along X, the span along Y and the thickness along Z.
     * @return
     */
    CadDslSolid wing(@DelegatesTo(value = CadDslWing, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitWing()
        c.delegate = new CadDslWing(visitor: visitor)
        c.call()
        visitor.visitWingEnd()
        new CadDslSolid(visitor: visitor)
    }

    /**
     * Repeat the solids made in the closure count times, evenly spaced around axis through the current position.
     * The copies join the enclosing boolean operation (fuse, cut, common), so use it inside one.
     * @param count Total number of instances, the original included
     * @param axis  Rotation axis (defaults to Z)
     */
    CadDsl circularPattern(int count, Vec axis = Vec.vZ, @DelegatesTo(value = CadDsl, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitCircularPattern()
        c.delegate = this
        c.call()
        visitor.visitCircularPatternEnd(count, axis)
        this
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
