package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d


/**
 * Manage Drawing in face, directly or via construction closures
 */
@CompileStatic
class CadDslEdge2d implements CadDslBase {

    /**
     * Displace new position on this face.
     * @param to    Move, starting from the old position
     */
    void move(Vec2d to) {
        visitor.visitMove(to)
    }

    /**
     * Move to new position on this face.
     * @param to    New start positon
     */
    void to(Vec2d to) {
        visitor.visitTo(to)
    }

    /**
     * Draw an Edge from the last position to the "to" position (which will become the last position).
     * @param to    End of the Edge
     */
    void edge(Vec2d to) {
        visitor.visitEdge(to)
    }

    /**
     * Draw an arc from the last position to the "to" position (which will become the last position)
     * passing to the via position.
     * @param to    End of the arc
     * @param via   Middle point of the arc
     */
    void arc(Vec2d to, Vec2d via) {
        visitor.visitArc(to, via)
    }

    /**
     * Draw a closed circle at the last position
     * @param diameter  Diameter of the circle
     */
    void circle(Number diameter) {
        visitor.visiteCircle(diameter)
    }

    /**
     * Draw a closed rectangle whose center is the last position
     * @param sX    Size in X coords
     * @param sY    Size in Y coords
     * @param c     If not null, convert edges of this rectangle into construction points
     */
    void rect(Number sX, Number sY, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
        visitor.visitRect(sX, sY, c)
    }

    /**
     * Transform Edges into construction points
     * @param c     If not null, convert edges of this rectangle into construction points
     */
    void edges(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {}
}
