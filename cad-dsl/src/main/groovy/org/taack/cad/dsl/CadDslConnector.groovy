package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf

/**
 * Frame on a face (the origin on the face, z its outward normal) or on the axis of a cylindrical face, in the
 * coordinates of a part or of an assembly.
 * Get one with a face selector (topZ(), butX(pos), axis(), ...) on a part or on an instance, and join two with
 * {@link CadDslPlacement#mate}, {@link CadDslPlacement#align} or a joint. It follows the instances it is on when they
 * move.
 */
@CompileStatic
class CadDslConnector {
    /**
     * Frame in the coordinates of the part it was selected on
     */
    final Frame local
    /**
     * Instances leading from owner to that part, outermost first; empty when the frame is on owner itself
     */
    final List<CadDslInstance> path
    /**
     * Part or assembly whose coordinates the frame is in
     */
    final CadDslPart owner

    CadDslConnector(Frame local, List<CadDslInstance> path, CadDslPart owner) {
        this.local = local
        this.path = path
        this.owner = owner
    }

    /**
     * @return The frame in the owner coordinates, at the start of the animation
     */
    Frame getFrame() {
        frameAt(0)
    }

    /**
     * @return The frame in the owner coordinates, t seconds into the animation
     */
    Frame frameAt(double t) {
        if (path.empty) return local
        Trsf location = Trsf.IDENTITY
        for (CadDslInstance i in path) location = location * i.locationAt(t)
        local.transformed(location)
    }

    /**
     * Move along z: on a face, a positive distance leaves a gap between mated faces, a negative one sinks into the
     * face; on an axis, it slides along it
     */
    CadDslConnector offset(Number distance) {
        moved(new Frame(local.origin + local.z * distance, local.z, local.x))
    }

    /**
     * Turn around z, counterclockwise looking at the face (against the axis)
     */
    CadDslConnector rotate(Number degrees) {
        moved(local.transformed(Trsf.rotation(local.z, degrees.toDouble(), local.origin)))
    }

    /**
     * Turn z the other way, keeping the origin and x
     */
    CadDslConnector reverse() {
        moved(new Frame(local.origin, -local.z, local.x))
    }

    private CadDslConnector moved(Frame f) {
        new CadDslConnector(f, path, owner)
    }

    @Override
    String toString() {
        "CadDslConnector{$frame, owner=$owner}"
    }
}
