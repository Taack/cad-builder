package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Position of a part placed in an assembly (see {@link CadDslAssembly#place}): a mate, an alignment or a joint, then
 * moves applied in the order they are written, in the assembly coordinates. The face selectors are those of the part
 * placed. The position follows the instances the target connector is on, when they move.
 */
@CompileStatic
class CadDslPlacement implements CadDslFaceSelectors {
    final CadDslPart part
    final CadDslAssembly assembly
    /**
     * Degree of freedom left by revolute() or slider(), null otherwise
     */
    CadDslJoint joint
    private CadDslConnector connector
    private CadDslConnector target
    private boolean opposite
    private Trsf moves = Trsf.IDENTITY

    CadDslPlacement(CadDslPart part, CadDslAssembly assembly) {
        this.part = part
        this.assembly = assembly
    }

    @Override
    CadDslConnector face(Vec direction, Vec position) {
        part.face(direction, position)
    }

    @Override
    CadDslConnector cylinderAxis(Vec position) {
        part.cylinderAxis(position)
    }

    /**
     * Put the part so that its connector lies on the target one, face against face: same origin, opposite normals,
     * same x direction. It replaces the moves written before.
     * @param connector Connector of the part placed (e.g. butZ())
     * @param target    Connector in the assembly (e.g. instance.topZ())
     */
    CadDslPlacement mate(CadDslConnector connector, CadDslConnector target) {
        put(connector, target, true, null)
    }

    /**
     * Put the part so that its connector coincides with the target one: same origin, same z and x directions, e.g.
     * to slide a wheel on a shaft: align(axis(), shaft.axis().offset(10)). It replaces the moves written before.
     * @param connector Connector of the part placed (e.g. axis())
     * @param target    Connector in the assembly (e.g. instance.axis())
     */
    CadDslPlacement align(CadDslConnector connector, CadDslConnector target) {
        put(connector, target, false, null)
    }

    /**
     * Align the connectors, the part turned by angle around their z: a hinge, a wheel on its shaft.
     * Drive the angle with instance.angle = { double t -> ... } in {@link CadDslAssembly#animate}.
     * @param degrees Angle, counterclockwise looking against z
     */
    CadDslPlacement revolute(CadDslConnector connector, CadDslConnector target, Number degrees = 0) {
        put(connector, target, false, new CadDslJoint(CadDslJoint.Kind.REVOLUTE, degrees.toDouble()))
    }

    /**
     * Align the connectors, the part moved by distance along their z: a piston, a drawer.
     * Drive the distance with instance.distance = { double t -> ... } in {@link CadDslAssembly#animate}.
     */
    CadDslPlacement slider(CadDslConnector connector, CadDslConnector target, Number distance = 0) {
        put(connector, target, false, new CadDslJoint(CadDslJoint.Kind.SLIDER, distance.toDouble()))
    }

    private CadDslPlacement put(CadDslConnector connector, CadDslConnector target, boolean opposite, CadDslJoint joint) {
        if (!connector.owner.is(part)) throw new IllegalArgumentException("$connector is not a connector of the part placed, $part")
        if (!target.owner.is(assembly)) throw new IllegalArgumentException("$target is not in the coordinates of the assembly ${assembly.name ?: ''}: select it on an instance placed in it")
        this.connector = connector
        this.target = target
        this.opposite = opposite
        this.joint = joint
        moves = Trsf.IDENTITY
        this
    }

    CadDslPlacement translate(Vec distance) {
        moves = Trsf.translation(distance) * moves
        this
    }

    CadDslPlacement translate(Number x, Number y, Number z) {
        translate(new Vec(x, y, z))
    }

    /**
     * @param axis    Rotation axis direction
     * @param degrees Angle, counterclockwise looking against axis
     * @param around  A point of the rotation axis, the origin by default
     */
    CadDslPlacement rotate(Vec axis, Number degrees, Vec around = new Vec()) {
        moves = Trsf.rotation(axis, degrees.toDouble(), around) * moves
        this
    }

    /**
     * @return From the part coordinates to the assembly ones, t seconds into the animation
     */
    Trsf locationAt(double t) {
        if (!target) return moves
        Frame to = target.frameAt(t)
        Trsf l = (opposite ? new Frame(to.origin, -to.z, to.x) : to).toTrsf()
        if (joint) l = l * joint.trsfAt(t)
        moves * l * connector.frameAt(t).toTrsf().inverse()
    }
}
