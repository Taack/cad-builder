package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Occurrence of a part in an assembly, returned by {@link CadDslAssembly#place}.
 * Its face selectors return connectors in the assembly coordinates, which follow it when it moves.
 */
@CompileStatic
class CadDslInstance implements CadDslFaceSelectors {
    final CadDslPart part
    /**
     * Assembly the part is placed in
     */
    final CadDslAssembly assembly
    final CadDslPlacement placement

    CadDslInstance(CadDslPart part, CadDslAssembly assembly, CadDslPlacement placement) {
        this.part = part
        this.assembly = assembly
        this.placement = placement
    }

    /**
     * @return From the part coordinates to the assembly ones, at the start of the animation
     */
    Trsf getLocation() {
        locationAt(0)
    }

    /**
     * @return From the part coordinates to the assembly ones, t seconds into the animation
     */
    Trsf locationAt(double t) {
        placement.locationAt(t)
    }

    /**
     * Joint the part was placed with, null if none
     */
    CadDslJoint getJoint() {
        placement.joint
    }

    /**
     * Drive the angle of a revolute joint
     * @param law Angle in degrees from the time in seconds
     */
    void setAngle(Closure law) {
        joint(CadDslJoint.Kind.REVOLUTE).drive(law)
    }

    /**
     * Drive the distance of a slider joint
     * @param law Distance from the time in seconds
     */
    void setDistance(Closure law) {
        joint(CadDslJoint.Kind.SLIDER).drive(law)
    }

    CadDslJoint joint(CadDslJoint.Kind kind) {
        if (joint?.kind != kind) throw new IllegalStateException("${part.name ?: 'This instance'} is not placed with a ${kind.name().toLowerCase()} joint")
        joint
    }

    /**
     * @param position In the assembly coordinates
     */
    @Override
    CadDslConnector face(Vec direction, Vec position) {
        Trsf toPart = location.inverse()
        at(part.face(toPart.applyDir(direction), position ? toPart.apply(position) : null))
    }

    /**
     * @param position In the assembly coordinates
     */
    @Override
    CadDslConnector cylinderAxis(Vec position) {
        at(part.cylinderAxis(position ? location.inverse().apply(position) : null))
    }

    /**
     * @param connector Connector in the part coordinates, e.g. of an instance inside this sub-assembly
     * @return The same connector, in the assembly coordinates
     */
    CadDslConnector at(CadDslConnector connector) {
        if (!connector.owner.is(part)) throw new IllegalArgumentException("$connector is not in the coordinates of $part")
        new CadDslConnector(connector.local, [this] + connector.path, assembly)
    }
}
