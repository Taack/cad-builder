package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Occurrence of a part in an assembly, returned by {@link CadDslAssembly#place}.
 * Its face selectors return connectors in the assembly coordinates.
 */
@CompileStatic
class CadDslInstance implements CadDslFaceSelectors {
    final CadDslPart part
    /**
     * Assembly the part is placed in
     */
    final CadDslAssembly assembly
    /**
     * From the part coordinates to the assembly ones
     */
    final Trsf location

    CadDslInstance(CadDslPart part, CadDslAssembly assembly, Trsf location) {
        this.part = part
        this.assembly = assembly
        this.location = location
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
        new CadDslConnector(connector.frame.transformed(location), assembly)
    }
}
