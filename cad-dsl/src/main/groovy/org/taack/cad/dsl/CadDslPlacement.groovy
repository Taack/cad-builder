package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Position of a part placed in an assembly (see {@link CadDslAssembly#place}): mate, then moves applied in the order
 * they are written, in the assembly coordinates. The face selectors are those of the part placed.
 */
@CompileStatic
class CadDslPlacement implements CadDslFaceSelectors {
    final CadDslPart part
    final CadDslAssembly assembly
    Trsf location = Trsf.IDENTITY

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
        Frame t = target.frame
        put(connector, target, new Frame(t.origin, -t.z, t.x))
    }

    /**
     * Put the part so that its connector coincides with the target one: same origin, same z and x directions, e.g.
     * to slide a wheel on a shaft: align(axis(), shaft.axis().offset(10)). It replaces the moves written before.
     * @param connector Connector of the part placed (e.g. axis())
     * @param target    Connector in the assembly (e.g. instance.axis())
     */
    CadDslPlacement align(CadDslConnector connector, CadDslConnector target) {
        put(connector, target, target.frame)
    }

    private CadDslPlacement put(CadDslConnector connector, CadDslConnector target, Frame to) {
        if (!connector.owner.is(part)) throw new IllegalArgumentException("$connector is not a connector of the part placed, $part")
        if (!target.owner.is(assembly)) throw new IllegalArgumentException("$target is not in the coordinates of the assembly ${assembly.name ?: ''}: select it on an instance placed in it")
        location = to.toTrsf() * connector.frame.toTrsf().inverse()
        this
    }

    CadDslPlacement translate(Vec distance) {
        location = Trsf.translation(distance) * location
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
        location = Trsf.rotation(axis, degrees.toDouble(), around) * location
        this
    }
}
