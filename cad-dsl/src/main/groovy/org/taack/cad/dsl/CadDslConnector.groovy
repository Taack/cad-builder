package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf

/**
 * Frame on a face (the origin on the face, z its outward normal) or on the axis of a cylindrical face, in the
 * coordinates of a part or of an assembly.
 * Get one with a face selector (topZ(), butX(pos), ...) on a part or on an instance, and join two with
 * {@link CadDslPlacement#mate} or {@link CadDslPlacement#align}.
 */
@CompileStatic
class CadDslConnector {
    final Frame frame
    /**
     * Part or assembly whose coordinates the frame is in
     */
    final CadDslPart owner

    CadDslConnector(Frame frame, CadDslPart owner) {
        this.frame = frame
        this.owner = owner
    }

    /**
     * Move along z: on a face, a positive distance leaves a gap between mated faces, a negative one sinks into the
     * face; on an axis, it slides along it
     */
    CadDslConnector offset(Number distance) {
        new CadDslConnector(new Frame(frame.origin + frame.z * distance, frame.z, frame.x), owner)
    }

    /**
     * Turn around z, counterclockwise looking at the face (against the axis)
     */
    CadDslConnector rotate(Number degrees) {
        Trsf r = Trsf.rotation(frame.z, degrees.toDouble(), frame.origin)
        new CadDslConnector(frame.transformed(r), owner)
    }

    @Override
    String toString() {
        "CadDslConnector{$frame, owner=$owner}"
    }
}
