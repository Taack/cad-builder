package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.PlateOptions
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions
import org.taack.cad.dsl.geom.ArcOfCircle2d
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Ellipse2d
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.IConstruction
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.svg.SvgContour

import static org.taack.cad.dsl.feature.CadFeatureRecorder.n
import static org.taack.cad.dsl.feature.CadFeatureRecorder.v

/**
 * Records the operations of a solid as {@link CadFeature}s, then lets the kernel visitor do them.
 *
 * The begin/end pairs of the visitor (cut, fuse, from...) nest the features. A sketch (the edges between visitFrom and
 * visitFromEnd) goes under the face made of it, and a face under the prism or revolution made of it.
 */
@CompileStatic
final class RecordingVisitor implements ICadDslVisitor {
    @Delegate
    final ICadDslVisitor kernel
    final CadFeature root
    private final CadFeatureRecorder recorder
    private final Deque<CadFeature> open = new ArrayDeque<>()

    RecordingVisitor(CadFeatureRecorder recorder, ICadDslVisitor kernel) {
        this.recorder = recorder
        this.kernel = kernel
        root = recorder.root(new CadFeature('solid', 'solid', CadFeatureFrame.current()))
        open.push(root)
    }

    private CadFeature add(String kind, String label, boolean detail = false) {
        def f = new CadFeature(kind, label, CadFeatureFrame.current(), detail)
        open.peek().children << f
        recorder.last = f
        f
    }

    private CadFeature begin(String kind, String label) {
        def f = add(kind, label)
        open.push(f)
        f
    }

    private CadFeature end() {
        open.size() > 1 ? open.pop() : root
    }

    // The siblings of kind just before f, moved under it
    private void adopt(CadFeature f, Collection<String> kinds, boolean all) {
        List<CadFeature> siblings = open.peek().children
        int i = siblings.size() - 1
        while (i > 0 && siblings[i - 1].kind in kinds) {
            i--
            if (!all) break
        }
        List<CadFeature> taken = siblings.subList(i, siblings.size() - 1)
        f.children.addAll(0, taken)
        taken.clear()
    }

    // Sketch and position

    @Override
    void visitFrom(Vec pos) {
        begin('from', v(pos))
        kernel.visitFrom(pos)
    }

    @Override
    void visitFrom(Vec2d pos) {
        begin('from', v(pos))
        kernel.visitFrom(pos)
    }

    private void fromEnd() {
        CadFeature f = end()
        if (f.is(root)) return
        if (f.children.every { it.detail || it.kind in ['text3d', 'svg3d'] }) {
            f.kind = 'sketch'
            f.label = "sketch at $f.label"
        } else if (f.children.size() == 1) {
            // position(...) { box(...) }: the box at that position
            CadFeature only = f.children[0]
            only.label += " at $f.label"
            List<CadFeature> siblings = open.peek().children
            siblings[siblings.size() - 1] = only
        } else {
            f.kind = 'position'
            f.label = "at $f.label"
        }
    }

    @Override
    void visitFromEnd(Vec pos) {
        fromEnd()
        kernel.visitFromEnd(pos)
    }

    @Override
    void visitFromEnd(Vec2d pos) {
        fromEnd()
        kernel.visitFromEnd(pos)
    }

    @Override
    void visitMove(Vec2d to) {
        add('move', "move ${v(to)}", true)
        kernel.visitMove(to)
    }

    @Override
    void visitMove(Vec to) {
        add('move', "move ${v(to)}", true)
        kernel.visitMove(to)
    }

    @Override
    void visitTo(Vec2d to) {
        add('to', "to ${v(to)}", true)
        kernel.visitTo(to)
    }

    @Override
    void visitTo(Vec to) {
        add('to', "to ${v(to)}", true)
        kernel.visitTo(to)
    }

    @Override
    void visitEdge(Vec2d to) {
        add('edge', "edge to ${v(to)}", true)
        kernel.visitEdge(to)
    }

    @Override
    void visitEdge(Vec to) {
        add('edge', "edge to ${v(to)}", true)
        kernel.visitEdge(to)
    }

    @Override
    void visitArc(Vec2d to, Vec2d via) {
        add('arc', "arc to ${v(to)}", true)
        kernel.visitArc(to, via)
    }

    @Override
    void visitArc(Vec to, Vec via) {
        add('arc', "arc to ${v(to)}", true)
        kernel.visitArc(to, via)
    }

    @Override
    Circle2d visitCircle2d(double diameter, boolean reverse) {
        add('circle', "circle ⌀${n(diameter)}", true)
        kernel.visitCircle2d(diameter, reverse)
    }

    @Override
    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia) {
        add('ellipse', "ellipse ${n(majDia)} × ${n(minDia)}", true)
        kernel.visitEllipse2d(dir, majDia, minDia)
    }

    @Override
    void visitSpline(List<Vec2d> through) {
        add('spline', "spline through ${through.size()} points", true)
        kernel.visitSpline(through)
    }

    @Override
    void visitText(String content, double size, TextOptions options) {
        add('text', "text '$content' ${n(size)}", true)
        kernel.visitText(content, size, options)
    }

    @Override
    void visitSvg(List<SvgContour> contours, SvgOptions options) {
        add('svg', "svg, ${contours.size()} contours", true)
        kernel.visitSvg(contours, options)
    }

    @Override
    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double to, boolean reverse) {
        add('trim', 'trimmed curve', true)
        kernel.visitTrimmed(curve, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, double from, double to, boolean reverse) {
        add('trim', 'trimmed circle', true)
        kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, double to, boolean reverse) {
        add('trim', 'trimmed circle', true)
        kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, Vec2d to, boolean reverse) {
        add('trim', 'trimmed circle', true)
        kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ITrimmable2d visitMirror(ITrimmable2d curve, Vec2d pos, Vec2d dir) {
        add('mirror', 'mirrored curve', true)
        kernel.visitMirror(curve, pos, dir)
    }

    @Override
    void visitAddToConstruction(IConstruction... toAdd) {
        add('construction', "construction + ${toAdd.length}", true)
        kernel.visitAddToConstruction(toAdd)
    }

    @Override
    void visitRemoveFromConstruction(IConstruction... toRemove) {
        add('construction', "construction - ${toRemove.length}", true)
        kernel.visitRemoveFromConstruction(toRemove)
    }

    @Override
    void visitAdapt3d(ITrimmable2d trimmed2dCurve, Vec dirX, Vec dirY) {
        add('curve3d', 'curve to 3D', true)
        kernel.visitAdapt3d(trimmed2dCurve, dirX, dirY)
    }

    @Override
    void visitDirection(Vec axis, Vec normal) {
        add('direction', "direction ${v(axis)}", true)
        kernel.visitDirection(axis, normal)
    }

    @Override
    void visitClosedWire() {
        begin('wire', 'closed wire')
        kernel.visitClosedWire()
    }

    @Override
    void visitClosedWireEnd() {
        end()
        kernel.visitClosedWireEnd()
    }

    @Override
    void visitWireFromSurface() {
        begin('wire', 'wire on surface')
        kernel.visitWireFromSurface()
    }

    @Override
    void visitWireFromSurfaceEnd() {
        end()
        kernel.visitWireFromSurfaceEnd()
    }

    // Faces and solids from sketches

    @Override
    void visitToFace() {
        adopt(add('face', 'face'), ['sketch'], true)
        kernel.visitToFace()
    }

    @Override
    void visitToFaceFrom2d() {
        adopt(add('face', 'face'), ['sketch'], true)
        kernel.visitToFaceFrom2d()
    }

    private void prism(String size, boolean cut, Double draft) {
        String label = (cut ? 'pocket ' : 'prism ') + size + (draft != null ? ", draft ${n(Math.toDegrees(draft))}°" : '')
        adopt(add(cut ? 'pocket' : 'prism', label), ['face'], false)
    }

    @Override
    void visitPrism(Vec dir, boolean cut) {
        prism(v(dir), cut, null)
        kernel.visitPrism(dir, cut)
    }

    @Override
    void visitPrism(double high, boolean cut) {
        prism(n(high), cut, null)
        kernel.visitPrism(high, cut)
    }

    @Override
    void visitPrism(Vec dir, boolean cut, double draft) {
        prism(v(dir), cut, draft)
        kernel.visitPrism(dir, cut, draft)
    }

    @Override
    void visitPrism(double high, boolean cut, double draft) {
        prism(n(high), cut, draft)
        kernel.visitPrism(high, cut, draft)
    }

    @Override
    void visitRevolution(Vec from, Vec dir, double angle) {
        String turn = angle == 0d ? 'full turn' : "${n(Math.toDegrees(angle))}°"
        adopt(add('revolution', "revolution about ${v(dir)}, $turn"), ['face', 'sketch'], false)
        kernel.visitRevolution(from, dir, angle)
    }

    @Override
    void visitCylindricalSurface(double number) {
        add('surface', "cylindrical surface ${n(number)}")
        kernel.visitCylindricalSurface(number)
    }

    @Override
    void visitText3d(String content, double size, TextOptions options) {
        add('text3d', "text '$content' ${n(size)}")
        kernel.visitText3d(content, size, options)
    }

    @Override
    void visitSvg3d(List<SvgContour> contours, SvgOptions options) {
        add('svg3d', "svg, ${contours.size()} contours")
        kernel.visitSvg3d(contours, options)
    }

    // Primitives

    @Override
    void visitBox(double length, double height, double thickness) {
        add('box', "box ${n(length)} × ${n(height)} × ${n(thickness)}")
        kernel.visitBox(length, height, thickness)
    }

    @Override
    void visitSphere(double radius, double radian1, double radian2) {
        add('sphere', "sphere r${n(radius)}")
        kernel.visitSphere(radius, radian1, radian2)
    }

    @Override
    void visitCylinder(double radius, double height) {
        add('cylinder', "cylinder r${n(radius)} × ${n(height)}")
        kernel.visitCylinder(radius, height)
    }

    @Override
    void visitTorus(double torusRadius, double ringRadius) {
        add('torus', "torus r${n(torusRadius)}, ring r${n(ringRadius)}")
        kernel.visitTorus(torusRadius, ringRadius)
    }

    @Override
    void visitCone(double r1, double r2, double height) {
        add('cone', "cone r${n(r1)} to r${n(r2)} × ${n(height)}")
        kernel.visitCone(r1, r2, height)
    }

    @Override
    void visitHelix(double radius, double pitch, double height, double endRadius) {
        add('helix', "helix r${n(radius)}, pitch ${n(pitch)} × ${n(height)}")
        kernel.visitHelix(radius, pitch, height, endRadius)
    }

    @Override
    void visitHelixProfile(Vec2d from) {
        begin('profile', "profile at ${v(from)}")
        kernel.visitHelixProfile(from)
    }

    @Override
    void visitHelixProfileEnd(Vec2d from) {
        end()
        kernel.visitHelixProfileEnd(from)
    }

    // Operations

    @Override
    void visitCut() {
        begin('cut', 'cut')
        kernel.visitCut()
    }

    @Override
    void visitCutEnd() {
        end()
        kernel.visitCutEnd()
    }

    @Override
    void visitFuse() {
        begin('fuse', 'fuse')
        kernel.visitFuse()
    }

    @Override
    void visitFuseEnd() {
        end()
        kernel.visitFuseEnd()
    }

    @Override
    void visitCommon() {
        begin('common', 'common')
        kernel.visitCommon()
    }

    @Override
    void visitCommonEnd() {
        end()
        kernel.visitCommonEnd()
    }

    @Override
    void visitCenter() {
        begin('center', 'centered')
        kernel.visitCenter()
    }

    @Override
    void visitCenterEnd() {
        end()
        kernel.visitCenterEnd()
    }

    @Override
    void visitCircularPattern() {
        begin('pattern', 'circular pattern')
        kernel.visitCircularPattern()
    }

    @Override
    void visitCircularPatternEnd(int count, Vec axis) {
        end().label = "circular pattern × $count about ${v(axis)}"
        kernel.visitCircularPatternEnd(count, axis)
    }

    @Override
    void visitThruSection() {
        begin('loft', 'thru section')
        kernel.visitThruSection()
    }

    @Override
    void visitThruSectionEnd() {
        end()
        kernel.visitThruSectionEnd()
    }

    @Override
    void visitWing() {
        begin('wing', 'wing')
        kernel.visitWing()
    }

    @Override
    void visitWingSection(Vec leadingEdge, double chord, double twist, Vec2d from) {
        begin('section', "section at ${v(leadingEdge)}, chord ${n(chord)}")
        kernel.visitWingSection(leadingEdge, chord, twist, from)
    }

    @Override
    void visitWingSectionEnd(Vec2d from) {
        end()
        kernel.visitWingSectionEnd(from)
    }

    @Override
    void visitWingEnd() {
        end()
        kernel.visitWingEnd()
    }

    @Override
    void visitPlate(PlateOptions options) {
        begin('plate', 'plate')
        kernel.visitPlate(options)
    }

    @Override
    void visitPlateCurve(Vec from, boolean boundary) {
        begin('curve', (boundary ? 'boundary at ' : 'curve at ') + v(from))
        kernel.visitPlateCurve(from, boundary)
    }

    @Override
    void visitPlateCurveEnd(Vec from) {
        end()
        kernel.visitPlateCurveEnd(from)
    }

    @Override
    void visitPlateThrough(Vec point) {
        add('point', "through ${v(point)}", true)
        kernel.visitPlateThrough(point)
    }

    @Override
    void visitPlateEnd() {
        end()
        kernel.visitPlateEnd()
    }

    @Override
    void visitFace(Vec direction, Vec position) {
        add('select', "on face ${v(direction)}" + (position != null ? " near ${v(position)}" : ''))
        kernel.visitFace(direction, position)
    }

    @Override
    void visitFillet(double length) {
        add('fillet', "fillet r${n(length)}")
        kernel.visitFillet(length)
    }

    @Override
    void visitHollowedSolid(double thickness) {
        add('shell', "shell ${n(thickness)}")
        kernel.visitHollowedSolid(thickness)
    }

    @Override
    void visitSolidMirror(Vec pos, Vec dir) {
        add('mirror', "mirror about ${v(dir)}")
        kernel.visitSolidMirror(pos, dir)
    }

    @Override
    void visitSolidTranslate(Vec distance) {
        add('translate', "translate ${v(distance)}")
        kernel.visitSolidTranslate(distance)
    }

    @Override
    void visitSolidRotate(Vec pos, Vec dir, double angle, boolean clone) {
        add('rotate', "rotate ${n(Math.toDegrees(angle))}° about ${v(dir)}" + (clone ? ', copy' : ''))
        kernel.visitSolidRotate(pos, dir, angle, clone)
    }
}
