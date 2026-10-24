package org.taack.cad.freecad

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadParams
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.PlateOptions
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions
import org.taack.cad.dsl.geom.*
import org.taack.cad.dsl.svg.SvgContour

/**
 * Model built in a FreeCAD document: each call is run by the Model of cad_dsl_freecad.py, which makes the features.
 */
@CompileStatic
class FreeCadVisitor implements ICadDslVisitor {
    final FreeCadSession session = FreeCadSession.get()
    // Handle of the model in FreeCAD
    final int handle = session.call(null, 'newModel') as int

    private Object call(String method, Object... args) {
        session.call(handle, method, args)
    }

    @Override
    void visitFrom(Vec pos) {
        call('visitFrom3d', pos)
    }

    @Override
    void visitFromEnd(Vec pos) {
        call('visitFromEnd3d', pos)
    }

    @Override
    void visitFrom(Vec2d pos) {
        call('visitFrom2d', pos)
    }

    @Override
    void visitFromEnd(Vec2d pos) {
        call('visitFromEnd2d', pos)
    }

    @Override
    void visitBox(double length, double height, double thickness) {
        call('visitBox', length, height, thickness)
    }

    @Override
    void visitSphere(double radius, double radian1, double radian2) {
        call('visitSphere', radius, radian1, radian2)
    }

    @Override
    void visitCylinder(double radius, double height) {
        call('visitCylinder', radius, height)
    }

    @Override
    void visitTorus(double torusRadius, double ringRadius) {
        call('visitTorus', torusRadius, ringRadius)
    }

    @Override
    void visitCut() {
        call('visitCut')
    }

    @Override
    void visitCutEnd() {
        call('visitCutEnd')
    }

    @Override
    void visitFuse() {
        call('visitFuse')
    }

    @Override
    void visitFuseEnd() {
        call('visitFuseEnd')
    }

    @Override
    void visitFace(Vec direction, Vec position) {
        call('visitFace', direction, position)
    }

    /**
     * @param fileName null to check the model (and show it in FreeCAD with -Dcad.freecad.gui=true), else a .step,
     * .stl, .brep, .iges or .FCStd file, whose numbers equal to a parameter (see {@link CadParams}) are bound to its
     * cell in the Params spreadsheet
     */
    @Override
    void display(String fileName) {
        session.call(null, 'setParams', CadParams.values())
        call('display', fileName)
        if (!fileName && FreeCadKernel.guiEnabled()) FreeCadKernel.show(session, handle)
    }

    @Override
    void visitCenter() {
        call('visitCenter')
    }

    @Override
    void visitCenterEnd() {
        call('visitCenterEnd')
    }

    @Override
    Circle2d visitCircle2d(double radius, boolean reverse) {
        int id = session.newId()
        List pos = (List) call('visitCircle2d', radius, reverse, id)
        session.register(new Circle2d(new Vec2d(pos[0] as double, pos[1] as double), radius, reverse), id)
    }

    @Override
    void visitText(String content, double size, TextOptions options) {
        call('visitText', content, size, options)
    }

    @Override
    void visitText3d(String content, double size, TextOptions options) {
        call('visitText3d', content, size, options)
    }

    @Override
    void visitSvg(List<SvgContour> contours, SvgOptions options) {
        call('visitSvg', contours, options)
    }

    @Override
    void visitSvg3d(List<SvgContour> contours, SvgOptions options) {
        call('visitSvg3d', contours, options)
    }

    @Override
    void visitMove(Vec2d to) {
        call('visitMove2d', to)
    }

    @Override
    void visitMove(Vec to) {
        call('visitMove3d', to)
    }

    @Override
    void visitTo(Vec2d to) {
        call('visitTo2d', to)
    }

    @Override
    void visitTo(Vec to) {
        call('visitTo3d', to)
    }

    @Override
    void visitEdge(Vec2d to) {
        call('visitEdge2d', to)
    }

    @Override
    void visitEdge(Vec to) {
        call('visitEdge3d', to)
    }

    @Override
    void visitArc(Vec2d to, Vec2d via) {
        call('visitArc2d', to, via)
    }

    @Override
    void visitArc(Vec to, Vec via) {
        call('visitArc3d', to, via)
    }

    @Override
    void visitToFace() {
        call('visitToFace')
    }

    @Override
    void visitToFaceFrom2d() {
        call('visitToFaceFrom2d')
    }

    @Override
    void visitRevolution(Vec from, Vec dir, double angle) {
        call('visitRevolution', from, dir, angle)
    }

    @Override
    void visitPrism(Vec dir, boolean cut) {
        call('visitPrism', dir, cut)
    }

    @Override
    void visitPrism(double high, boolean cut) {
        call('visitPrismHeight', high, cut)
    }

    @Override
    void visitDirection(Vec axis, Vec normal) {
        call('visitDirection', axis, normal)
    }

    @Override
    void visitCommon() {
        call('visitCommon')
    }

    @Override
    void visitCommonEnd() {
        call('visitCommonEnd')
    }

    @Override
    void visitFillet(double length) {
        call('visitFillet', length)
    }

    @Override
    void visitHollowedSolid(double thickness) {
        call('visitHollowedSolid', thickness)
    }

    @Override
    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia) {
        int id = session.newId()
        List pos = (List) call('visitEllipse2d', dir, majDia, minDia, id)
        session.register(new Ellipse2d(new Vec2d(pos[0] as double, pos[1] as double), dir, majDia, minDia), id)
    }

    @Override
    void visitWireFromSurface() {
        call('visitWireFromSurface')
    }

    @Override
    void visitWireFromSurfaceEnd() {
        call('visitWireFromSurfaceEnd')
    }

    @Override
    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double to, boolean reverse) {
        trimmed(new TrimmedCurve2d(curve, from, to, reverse))
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, double from, double to, boolean reverse) {
        trimmed(new ArcOfCircle2d(circle2d, from, to, reverse))
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, double to, boolean reverse) {
        trimmed(new ArcOfCircle2d(circle2d, from, to, reverse))
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, Vec2d to, boolean reverse) {
        trimmed(new ArcOfCircle2d(circle2d, from, to, reverse))
    }

    private <T extends ITrimmable2d> T trimmed(T curve) {
        call('visitTrimmed', curve)
        curve
    }

    @Override
    void visitCylindricalSurface(double radius) {
        call('visitCylindricalSurface', radius)
    }

    @Override
    void visitClosedWire() {
        call('visitClosedWire')
    }

    @Override
    void visitClosedWireEnd() {
        call('visitClosedWireEnd')
    }

    @Override
    void visitRemoveFromConstruction(IConstruction... toRemove) {
        call('visitRemoveFromConstruction', (Object) toRemove.toList())
    }

    @Override
    ITrimmable2d visitMirror(ITrimmable2d curve, Vec2d pos, Vec2d dir) {
        Mirrored2d mirrored = new Mirrored2d(curve, pos, dir)
        call('visitMirror', mirrored)
        mirrored
    }

    @Override
    void visitAddToConstruction(IConstruction... toAdd) {
        call('visitAddToConstruction', (Object) toAdd.toList())
    }

    @Override
    void visitAdapt3d(ITrimmable2d trimmed2dCurve, Vec dirX, Vec dirY) {
        call('visitAdapt3d', trimmed2dCurve, dirX, dirY)
    }

    @Override
    void visitSolidMirror(Vec pos, Vec dir) {
        call('visitSolidMirror', pos, dir)
    }

    @Override
    void visitSolidTranslate(Vec distance) {
        call('visitSolidTranslate', distance)
    }

    @Override
    void visitSolidRotate(Vec pos, Vec dir, double angle, boolean clone) {
        call('visitSolidRotate', pos, dir, angle, clone)
    }

    @Override
    void visitCone(double r1, double r2, double height) {
        call('visitCone', r1, r2, height)
    }

    @Override
    void visitHelix(double radius, double pitch, double height, double endRadius) {
        call('visitHelix', radius, pitch, height, endRadius)
    }

    @Override
    void visitHelixProfile(Vec2d from) {
        call('visitHelixProfile', from)
    }

    @Override
    void visitHelixProfileEnd(Vec2d from) {
        call('visitHelixProfileEnd', from)
    }

    @Override
    void visitSpline(List<Vec2d> through) {
        call('visitSpline', through)
    }

    @Override
    void visitWing() {
        call('visitWing')
    }

    @Override
    void visitWingSection(Vec leadingEdge, double chord, double twist, Vec2d from) {
        call('visitWingSection', leadingEdge, chord, twist, from)
    }

    @Override
    void visitWingSectionEnd(Vec2d from) {
        call('visitWingSectionEnd', from)
    }

    @Override
    void visitWingEnd() {
        call('visitWingEnd')
    }

    @Override
    void visitPlate(PlateOptions options) {
        call('visitPlate', options)
    }

    @Override
    void visitPlateCurve(Vec from, boolean boundary) {
        call('visitPlateCurve', from, boundary)
    }

    @Override
    void visitPlateCurveEnd(Vec from) {
        call('visitPlateCurveEnd', from)
    }

    @Override
    void visitPlateThrough(Vec point) {
        call('visitPlateThrough', point)
    }

    @Override
    void visitPlateEnd() {
        call('visitPlateEnd')
    }

    @Override
    void visitCircularPattern() {
        call('visitCircularPattern')
    }

    @Override
    void visitCircularPatternEnd(int count, Vec axis) {
        call('visitCircularPatternEnd', count, axis)
    }

    @Override
    void visitThruSection() {
        call('visitThruSection')
    }

    @Override
    void visitThruSectionEnd() {
        call('visitThruSectionEnd')
    }
}
