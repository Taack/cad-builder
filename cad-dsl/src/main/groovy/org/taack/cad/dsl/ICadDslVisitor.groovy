package org.taack.cad.dsl

import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.geom.ArcOfCircle2d
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Ellipse2d
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.IConstruction
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.svg.SvgContour

interface ICadDslVisitor {
    void visitFrom(Vec pos)

    void visitFromEnd(Vec pos)

    void visitFrom(Vec2d pos)

    void visitFromEnd(Vec2d pos)

    void visitBox(double length, double height, double thickness)

    /**
     * @param radian1 Latitude where the sphere starts, from -PI / 2
     * @param radian2 Latitude where the sphere ends, up to PI / 2
     */
    void visitSphere(double radius, double radian1, double radian2)

    void visitCylinder(double radius, double height)

    void visitTorus(double torusRadius, double ringRadius)

    void visitCut()

    void visitCutEnd()

    void visitFuse()

    void visitFuseEnd()

    void visitFace(Vec direction, Vec position)

    void display(String fileName)

    void visitCenter()

    void visitCenterEnd()

    Circle2d visitCircle2d(double diameter, boolean reverse)

    void visitText(String content, double size, TextOptions options)

    void visitText3d(String content, double size, TextOptions options)

    void visitSvg(List<SvgContour> contours, SvgOptions options)

    void visitSvg3d(List<SvgContour> contours, SvgOptions options)

    void visitMove(Vec2d to)

    void visitMove(Vec to)

    void visitTo(Vec2d to)

    void visitTo(Vec to)

    void visitEdge(Vec2d to)

    void visitEdge(Vec to)

    void visitArc(Vec2d to, Vec2d via)

    void visitArc(Vec to, Vec via)

    void visitToFace()

    void visitToFaceFrom2d()

    /**
     * @param angle In radians, 0 for a full turn
     */
    void visitRevolution(Vec from, Vec dir, double angle)

    void visitPrism(Vec dir, boolean cut)

    void visitPrism(double high, boolean cut)

    void visitDirection(Vec axis, Vec normal)

    void visitCommon()

    void visitCommonEnd()

    void visitFillet(double length)

    void visitHollowedSolid(double thickness)

    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia)

    void visitWireFromSurface()

    void visitWireFromSurfaceEnd()

    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double to, boolean reverse)

    ArcOfCircle2d visitTrimmed(Circle2d circle2d, double from, double to, boolean reverse)

    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, double to, boolean reverse)

    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, Vec2d to, boolean reverse)

    void visitCylindricalSurface(double number)

    void visitClosedWire()

    void visitClosedWireEnd()

    void visitRemoveFromConstruction(IConstruction... toRemove)

    ITrimmable2d visitMirror(ITrimmable2d curve, Vec2d pos, Vec2d dir)

    void visitAddToConstruction(IConstruction... toAdd)

    void visitAdapt3d(ITrimmable2d trimmed2dCurve, Vec dirX, Vec dirY)

    void visitSolidMirror(Vec pos, Vec dir)

    void visitSolidTranslate(Vec distance)

    void visitSolidRotate(Vec pos, Vec dir, double angle, boolean clone)

    void visitCone(double r1, double r2, double height)

    void visitHelix(double radius, double pitch, double height, double endRadius)

    void visitHelixProfile(Vec2d from)

    void visitHelixProfileEnd(Vec2d from)

    void visitSpline(List<Vec2d> through)

    void visitWing()

    void visitWingSection(Vec leadingEdge, double chord, double twist, Vec2d from)

    void visitWingSectionEnd(Vec2d from)

    void visitWingEnd()

    void visitPlate(PlateOptions options)

    void visitPlateCurve(Vec from, boolean boundary)

    void visitPlateCurveEnd(Vec from)

    void visitPlateThrough(Vec point)

    void visitPlateEnd()

    void visitCircularPattern()

    void visitCircularPatternEnd(int count, Vec axis)

    void visitThruSection()

    void visitThruSectionEnd()
}