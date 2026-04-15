package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec

@CompileStatic
class CadDslEdge extends CadDslEdge2d {
    /**
     * Displace new position on this face.
     * @param to    Move, starting from the old position
     */
    void move(Vec to) {
        visitor.visitMove(to)
    }

    /**
     * Move to new position on this face.
     * @param to    New start positon
     */
    void to(Vec to) {
        visitor.visitTo(to)
    }

    /**
     * Draw an Edge from the last position to the "to" position (which will become the last position).
     * @param to    End of the Edge
     */
    void edge(Vec to) {
        visitor.visitEdge(to)
    }

    void adapt3d(ITrimmable2d trimmedCurve, Vec dirX, Vec dirY) {
        visitor.visitAdapt3d(trimmedCurve, dirX, dirY)
    }

    /**
     * Draw an arc from the last position to the "to" position (which will become the last position)
     * passing to the via position.
     * @param to    End of the arc
     * @param via   Middle point of the arc
     */
    void arc(Vec to, Vec via) {
        visitor.visitArc(to, via)
    }
}
