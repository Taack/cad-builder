package org.taack.cad.dsl

import org.taack.cad.builder.Vec
import org.taack.cad.builder.Vec2d

interface ICadDslVisitor {
    void visitFrom(Vec pos)

    void visitFromEnd(Vec pos)

    void visitFrom(Vec2d pos)

    void visitFromEnd(Vec2d pos)

    void visitBox(Number length, Number height, Number thickness)

    void visitSphere(Number radius)

    void visitCylinder(Number radius, Number height)

    void visitTorus(Number torusRadius, Number ringRadius)

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

    void visitMove(Vec2d to)

    void visitTo(Vec2d to)

    void visitEdge(Vec2d to)

    void visitArc(Vec2d to, Vec2d via)

    void visitToFace()

    void visitRevolution(Vec from, Vec dir)

    void visitPrism(Vec dir)

    void visitMirror(Vec2d pos, Vec2d dir)

    void visitMirror(Vec pos, Vec dir)

    void visitDirection(Vec axis, Vec normal)

    void visitCommon()

    void visitCommonEnd()
}