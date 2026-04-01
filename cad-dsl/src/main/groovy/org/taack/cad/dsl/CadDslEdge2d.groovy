package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d


/**
 * Manage Drawing in face, directly or via construction closures
 */
@CompileStatic
class CadDslEdge2d implements CadDslBase {

    /**
     * Displace new position on this face.
     * @param to Move, starting from the old position
     */
    void move(Vec2d to) {
        visitor.visitMove(to)
    }

    /**
     * Displace new position on this face.
     * @param to Move, starting from the old position
     */
    void move(Number x, Number y) {
        visitor.visitMove(new Vec2d(x, y))
    }

    /**
     * Move to new position on this face.
     * @param to New start positon
     */
    void to(Vec2d to) {
        visitor.visitTo(to)
    }

    /**
     * Move to new position on this face.
     * @param to New start positon
     */
    void to(Number x, Number y) {
        visitor.visitTo(new Vec2d(x, y))
    }

    /**
     * Draw an Edge from the last position to the "to" position (which will become the last position).
     * @param to End of the Edge
     */
    CadDslEdge2d edge(Vec2d to) {
        visitor.visitEdge(to)
        this
    }

    /**
     * Draw an arc from the last position to the "to" position (which will become the last position)
     * passing to the via position.
     * @param to End of the arc
     * @param via Middle point of the arc
     */
    CadDslEdge2d arc(Vec2d to, Vec2d via) {
        visitor.visitArc(to, via)
        this
    }

    /**
     * Draw a closed circle at the last position
     * @param diameter Diameter of the circle
     */
    CadDslEdge2d circle(Number diameter) {
        visitor.visitCircle2d(diameter)
        this
    }

    /**
     * Draw a closed circle at the last position
     * @param diameter Diameter of the circle
     */
    CadDslEdge2d ellipse(Vec2d dir, Number majDia, Number minDia) {
        visitor.visitEllipse2d(dir, majDia, minDia)
        this
    }

//    /**
//     * Draw a closed rectangle whose center is the last position
//     * @param sX Size in X coords
//     * @param sY Size in Y coords
//     * @param c If not null, convert edges of this rectangle into construction points
//     */
//    void rect(Number sX, Number sY, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c = null) {
////        else {
//        double sXd = sX.toDouble()
//        double sYd = sY.toDouble()
//        visitor.visitMove new Vec2d(sXd / 2, sYd / 2)
//        edge(new Vec2d(sXd / 2, -sYd / 2))
//        edge(new Vec2d(-sXd / 2, -sYd / 2))
//        edge(new Vec2d(-sXd / 2, sYd / 2))
//        edge(new Vec2d(sXd / 2, sYd / 2))
////        if (c) visitor.visitConstruct2d(c)
//
////        }
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(sXd / 2, -sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, -sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
////            fromVec2d = oldFromVec2d + new Vec2d(-sXd / 2, sYd / 2)
////            c.delegate = new CadDslEdge2d(visitor: this)
////            c.call()
//
////            visitor.visitEdge(sX, sY, c)
//    }

    void closedWire(@DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST) Closure c) {
        visitor.visitClosedWire()
        c.delegate = this
        c.call()
        visitor.visitClosedWireEnd()
    }


    void trimmed(CadDslEdge2d curve, Number from, Number to) {
        visitor.visitTrimmed(curve, from, to)
    }

    Vec bound(Number U) {

    }
}
