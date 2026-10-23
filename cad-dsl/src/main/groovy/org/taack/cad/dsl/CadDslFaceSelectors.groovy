package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * Face selectors returning connectors, as on solids: topZ() is the planar face facing +Z the farthest along Z,
 * topZ(pos) the one facing +Z closest to pos, with the connector origin at pos projected on it.
 * axis() selects a cylindrical face, see {@link #axis}.
 */
@CompileStatic
trait CadDslFaceSelectors {
    abstract CadDslConnector face(Vec direction, Vec position)

    abstract CadDslConnector cylinderAxis(Vec position)

    /**
     * Connector on the axis of a cylindrical face (a shaft, a hole): its z is the axis direction, pointing to +Z
     * (else +Y, else +X) in the part coordinates, its origin the start of the face along the axis.
     * Join two of them with {@link CadDslPlacement#align}.
     * @param pos null for the face of largest radius, else the face closest to pos, with the origin at pos projected
     * on the axis
     */
    CadDslConnector axis(Vec pos = null) { cylinderAxis(pos) }

    CadDslConnector topX(Vec pos = null) { face(Vec.vX, pos) }

    CadDslConnector topY(Vec pos = null) { face(Vec.vY, pos) }

    CadDslConnector topZ(Vec pos = null) { face(Vec.vZ, pos) }

    CadDslConnector butX(Vec pos = null) { face(-Vec.vX, pos) }

    CadDslConnector butY(Vec pos = null) { face(-Vec.vY, pos) }

    CadDslConnector butZ(Vec pos = null) { face(-Vec.vZ, pos) }
}
