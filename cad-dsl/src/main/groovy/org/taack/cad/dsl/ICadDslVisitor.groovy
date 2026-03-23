package org.taack.cad.dsl

import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

interface ICadDslVisitor {
    void visitFrom(Vec pos)

    void visitFromEnd(Vec pos)

    void visitFrom(Vec2d pos)

    void visitFromEnd(Vec2d pos)

    void visitBox(Number length, Number height, Number thickness, Vec direction)

    void visitSphere(Number radius, Vec direction)

    void visitCylinder(Number radius, Number height, Vec direction)

    void visitTorus(Number torusRadius, Number ringRadius, Vec direction)

    void visitCut()

    void visitCutEnd()

    void visitFuse()

    void visitFuseEnd()

    void visitFace(Vec direction)

    void display(String fileName)

    void visitCenter()

    void visitCenterEnd()

    void visiteCircle(Number diameter)

    void visitHole(Number depth)

    void visitRect(Number sX, Number sY, @DelegatesTo(value = CadDslEdge2d, strategy = Closure.DELEGATE_FIRST)Closure c)
}