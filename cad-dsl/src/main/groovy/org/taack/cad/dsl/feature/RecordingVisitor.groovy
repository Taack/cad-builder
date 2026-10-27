package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.ICadDslVisitorWrapper
import org.taack.cad.dsl.PlateOptions
import org.taack.cad.dsl.SvgOptions
import org.taack.cad.dsl.TextOptions
import org.taack.cad.dsl.geom.ArcOfCircle2d
import org.taack.cad.dsl.geom.Circle2d
import org.taack.cad.dsl.geom.Ellipse2d
import org.taack.cad.dsl.geom.IClosedShape2d
import org.taack.cad.dsl.geom.IConstruction
import org.taack.cad.dsl.geom.ITrimmable2d
import org.taack.cad.dsl.geom.Mirrored2d
import org.taack.cad.dsl.geom.TrimmedCurve2d
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
final class RecordingVisitor implements ICadDslVisitor, ICadDslVisitorWrapper {
    @Delegate
    final ICadDslVisitor kernel
    final CadFeature root
    private final CadFeatureRecorder recorder
    private final Deque<CadFeature> open = new ArrayDeque<>()
    // operations starting a sketch or a wire, before the shape made of it
    private static final Set<String> SKETCHING = ['from', 'wire', 'profile', 'section', 'curve'] as Set
    /**
     * Whether the kernel did an operation making a shape, before the stop of the run: a sketch alone is not shown
     */
    boolean forwarded

    RecordingVisitor(CadFeatureRecorder recorder, ICadDslVisitor kernel) {
        this.recorder = recorder
        this.kernel = kernel
        root = recorder.root(new CadFeature('solid', 'solid', CadFeatureFrame.current()))
        open.push(root)
    }

    private CadFeature add(String kind, String label, boolean detail = false) {
        def f = recorder.record(new CadFeature(kind, label, CadFeatureFrame.current(), detail), root)
        open.peek().children << f
        recorder.last = f
        if (!f.suppressed && !detail && !(kind in SKETCHING)) forwarded = true
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

    // The shape f made, once the kernel made it
    private void shape(CadFeature f) {
        if (f.suppressed || !(kernel instanceof ICadDslFeatureShapes)) return
        ICadDslFeatureShapes shapes = (ICadDslFeatureShapes) kernel
        f.shape = shapes.featureShape(f.kind)
        f.shapes = shapes
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
        if (!begin('from', v(pos)).suppressed) kernel.visitFrom(pos)
    }

    @Override
    void visitFrom(Vec2d pos) {
        if (!begin('from', v(pos)).suppressed) kernel.visitFrom(pos)
    }

    // Whether the kernel did the from
    private boolean fromEnd() {
        CadFeature f = end()
        if (f.is(root)) return !f.suppressed
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
        !f.suppressed
    }

    @Override
    void visitFromEnd(Vec pos) {
        if (fromEnd()) kernel.visitFromEnd(pos)
    }

    @Override
    void visitFromEnd(Vec2d pos) {
        if (fromEnd()) kernel.visitFromEnd(pos)
    }

    @Override
    void visitMove(Vec2d to) {
        if (!add('move', "move ${v(to)}", true).suppressed) kernel.visitMove(to)
    }

    @Override
    void visitMove(Vec to) {
        if (!add('move', "move ${v(to)}", true).suppressed) kernel.visitMove(to)
    }

    @Override
    void visitTo(Vec2d to) {
        if (!add('to', "to ${v(to)}", true).suppressed) kernel.visitTo(to)
    }

    @Override
    void visitTo(Vec to) {
        if (!add('to', "to ${v(to)}", true).suppressed) kernel.visitTo(to)
    }

    @Override
    void visitEdge(Vec2d to) {
        if (!add('edge', "edge to ${v(to)}", true).suppressed) kernel.visitEdge(to)
    }

    @Override
    void visitEdge(Vec to) {
        if (!add('edge', "edge to ${v(to)}", true).suppressed) kernel.visitEdge(to)
    }

    @Override
    void visitArc(Vec2d to, Vec2d via) {
        if (!add('arc', "arc to ${v(to)}", true).suppressed) kernel.visitArc(to, via)
    }

    @Override
    void visitArc(Vec to, Vec via) {
        if (!add('arc', "arc to ${v(to)}", true).suppressed) kernel.visitArc(to, via)
    }

    @Override
    Circle2d visitCircle2d(double diameter, boolean reverse) {
        add('circle', "circle ⌀${n(diameter)}", true).suppressed ? new Circle2d(new Vec2d(0, 0), diameter, reverse) : kernel.visitCircle2d(diameter, reverse)
    }

    @Override
    Ellipse2d visitEllipse2d(Vec2d dir, double majDia, double minDia) {
        add('ellipse', "ellipse ${n(majDia)} × ${n(minDia)}", true).suppressed ? new Ellipse2d(new Vec2d(0, 0), dir, majDia, minDia) : kernel.visitEllipse2d(dir, majDia, minDia)
    }

    @Override
    void visitSpline(List<Vec2d> through) {
        if (!add('spline', "spline through ${through.size()} points", true).suppressed) kernel.visitSpline(through)
    }

    @Override
    void visitText(String content, double size, TextOptions options) {
        if (!add('text', "text '$content' ${n(size)}", true).suppressed) kernel.visitText(content, size, options)
    }

    @Override
    void visitSvg(List<SvgContour> contours, SvgOptions options) {
        if (!add('svg', "svg, ${contours.size()} contours", true).suppressed) kernel.visitSvg(contours, options)
    }

    @Override
    ITrimmable2d visitTrimmed(IClosedShape2d curve, double from, double to, boolean reverse) {
        add('trim', 'trimmed curve', true).suppressed ? new TrimmedCurve2d(curve, from, to, reverse) : kernel.visitTrimmed(curve, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, double from, double to, boolean reverse) {
        add('trim', 'trimmed circle', true).suppressed ? new ArcOfCircle2d(circle2d, from, to, reverse) : kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, double to, boolean reverse) {
        add('trim', 'trimmed circle', true).suppressed ? new ArcOfCircle2d(circle2d, from, to, reverse) : kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ArcOfCircle2d visitTrimmed(Circle2d circle2d, Vec2d from, Vec2d to, boolean reverse) {
        add('trim', 'trimmed circle', true).suppressed ? new ArcOfCircle2d(circle2d, from, to, reverse) : kernel.visitTrimmed(circle2d, from, to, reverse)
    }

    @Override
    ITrimmable2d visitMirror(ITrimmable2d curve, Vec2d pos, Vec2d dir) {
        add('mirror', 'mirrored curve', true).suppressed ? new Mirrored2d(curve, pos, dir) : kernel.visitMirror(curve, pos, dir)
    }

    @Override
    void visitAddToConstruction(IConstruction... toAdd) {
        if (!add('construction', "construction + ${toAdd.length}", true).suppressed) kernel.visitAddToConstruction(toAdd)
    }

    @Override
    void visitRemoveFromConstruction(IConstruction... toRemove) {
        if (!add('construction', "construction - ${toRemove.length}", true).suppressed) kernel.visitRemoveFromConstruction(toRemove)
    }

    @Override
    void visitAdapt3d(ITrimmable2d trimmed2dCurve, Vec dirX, Vec dirY) {
        if (!add('curve3d', 'curve to 3D', true).suppressed) kernel.visitAdapt3d(trimmed2dCurve, dirX, dirY)
    }

    @Override
    void visitDirection(Vec axis, Vec normal) {
        if (!add('direction', "direction ${v(axis)}", true).suppressed) kernel.visitDirection(axis, normal)
    }

    @Override
    void visitClosedWire() {
        if (!begin('wire', 'closed wire').suppressed) kernel.visitClosedWire()
    }

    @Override
    void visitClosedWireEnd() {
        if (!end().suppressed) kernel.visitClosedWireEnd()
    }

    @Override
    void visitWireFromSurface() {
        if (!begin('wire', 'wire on surface').suppressed) kernel.visitWireFromSurface()
    }

    @Override
    void visitWireFromSurfaceEnd() {
        if (!end().suppressed) kernel.visitWireFromSurfaceEnd()
    }

    // Faces and solids from sketches

    @Override
    void visitToFace() {
        CadFeature f = add('face', 'face')
        adopt(f, ['sketch'], true)
        if (!f.suppressed) kernel.visitToFace()
        shape(f)
    }

    @Override
    void visitToFaceFrom2d() {
        CadFeature f = add('face', 'face')
        adopt(f, ['sketch'], true)
        if (!f.suppressed) kernel.visitToFaceFrom2d()
        shape(f)
    }

    private CadFeature prism(String size, boolean cut, Double draft) {
        String label = (cut ? 'pocket ' : 'prism ') + size + (draft != null ? ", draft ${n(Math.toDegrees(draft))}°" : '')
        CadFeature f = add(cut ? 'pocket' : 'prism', label)
        adopt(f, ['face'], false)
        f
    }

    @Override
    void visitPrism(Vec dir, boolean cut) {
        CadFeature f = prism(v(dir), cut, null)
        if (!f.suppressed) kernel.visitPrism(dir, cut)
        shape(f)
    }

    @Override
    void visitPrism(double high, boolean cut) {
        CadFeature f = prism(n(high), cut, null)
        if (!f.suppressed) kernel.visitPrism(high, cut)
        shape(f)
    }

    @Override
    void visitPrism(Vec dir, boolean cut, double draft) {
        CadFeature f = prism(v(dir), cut, draft)
        if (!f.suppressed) kernel.visitPrism(dir, cut, draft)
        shape(f)
    }

    @Override
    void visitPrism(double high, boolean cut, double draft) {
        CadFeature f = prism(n(high), cut, draft)
        if (!f.suppressed) kernel.visitPrism(high, cut, draft)
        shape(f)
    }

    @Override
    void visitRevolution(Vec from, Vec dir, double angle) {
        String turn = angle == 0d ? 'full turn' : "${n(Math.toDegrees(angle))}°"
        CadFeature f = add('revolution', "revolution about ${v(dir)}, $turn")
        adopt(f, ['face', 'sketch'], false)
        if (!f.suppressed) kernel.visitRevolution(from, dir, angle)
        shape(f)
    }

    @Override
    void visitCylindricalSurface(double number) {
        if (!add('surface', "cylindrical surface ${n(number)}").suppressed) kernel.visitCylindricalSurface(number)
    }

    @Override
    void visitText3d(String content, double size, TextOptions options) {
        CadFeature f = add('text3d', "text '$content' ${n(size)}")
        if (!f.suppressed) kernel.visitText3d(content, size, options)
        shape(f)
    }

    @Override
    void visitSvg3d(List<SvgContour> contours, SvgOptions options) {
        CadFeature f = add('svg3d', "svg, ${contours.size()} contours")
        if (!f.suppressed) kernel.visitSvg3d(contours, options)
        shape(f)
    }

    // Primitives

    @Override
    void visitBox(double length, double height, double thickness) {
        CadFeature f = add('box', "box ${n(length)} × ${n(height)} × ${n(thickness)}")
        if (!f.suppressed) kernel.visitBox(length, height, thickness)
        shape(f)
    }

    @Override
    void visitSphere(double radius, double radian1, double radian2) {
        CadFeature f = add('sphere', "sphere r${n(radius)}")
        if (!f.suppressed) kernel.visitSphere(radius, radian1, radian2)
        shape(f)
    }

    @Override
    void visitCylinder(double radius, double height) {
        CadFeature f = add('cylinder', "cylinder r${n(radius)} × ${n(height)}")
        if (!f.suppressed) kernel.visitCylinder(radius, height)
        shape(f)
    }

    @Override
    void visitTorus(double torusRadius, double ringRadius) {
        CadFeature f = add('torus', "torus r${n(torusRadius)}, ring r${n(ringRadius)}")
        if (!f.suppressed) kernel.visitTorus(torusRadius, ringRadius)
        shape(f)
    }

    @Override
    void visitImportStep(String fileName) {
        CadFeature f = add('importStep', "STEP ${new File(fileName).name}")
        if (!f.suppressed) kernel.visitImportStep(fileName)
        shape(f)
    }

    @Override
    void visitCone(double r1, double r2, double height) {
        CadFeature f = add('cone', "cone r${n(r1)} to r${n(r2)} × ${n(height)}")
        if (!f.suppressed) kernel.visitCone(r1, r2, height)
        shape(f)
    }

    @Override
    void visitHelix(double radius, double pitch, double height, double endRadius) {
        if (!add('helix', "helix r${n(radius)}, pitch ${n(pitch)} × ${n(height)}").suppressed) kernel.visitHelix(radius, pitch, height, endRadius)
    }

    @Override
    void visitHelixProfile(Vec2d from) {
        if (!begin('profile', "profile at ${v(from)}").suppressed) kernel.visitHelixProfile(from)
    }

    @Override
    void visitHelixProfileEnd(Vec2d from) {
        if (!end().suppressed) kernel.visitHelixProfileEnd(from)
    }

    @Override
    void visitPath(Vec from) {
        if (!begin('path', "path from ${v(from)}").suppressed) kernel.visitPath(from)
    }

    @Override
    void visitPathEnd(Vec from) {
        if (!end().suppressed) kernel.visitPathEnd(from)
    }

    @Override
    void visitPathWire(double radius) {
        CadFeature f = add('wire', "wire r${n(radius)}")
        if (!f.suppressed) kernel.visitPathWire(radius)
        shape(f)
    }

    // Operations

    @Override
    void visitCut() {
        if (!begin('cut', 'cut').suppressed) kernel.visitCut()
    }

    @Override
    void visitCutEnd() {
        if (!end().suppressed) kernel.visitCutEnd()
    }

    @Override
    void visitFuse() {
        if (!begin('fuse', 'fuse').suppressed) kernel.visitFuse()
    }

    @Override
    void visitFuseEnd() {
        if (!end().suppressed) kernel.visitFuseEnd()
    }

    @Override
    void visitCommon() {
        if (!begin('common', 'common').suppressed) kernel.visitCommon()
    }

    @Override
    void visitCommonEnd() {
        if (!end().suppressed) kernel.visitCommonEnd()
    }

    @Override
    void visitCenter() {
        if (!begin('center', 'centered').suppressed) kernel.visitCenter()
    }

    @Override
    void visitCenterEnd() {
        if (!end().suppressed) kernel.visitCenterEnd()
    }

    @Override
    void visitCircularPattern() {
        if (!begin('pattern', 'circular pattern').suppressed) kernel.visitCircularPattern()
    }

    @Override
    void visitCircularPatternEnd(int count, Vec axis) {
        CadFeature f = end()
        f.label = "circular pattern × $count about ${v(axis)}"
        if (!f.suppressed) kernel.visitCircularPatternEnd(count, axis)
    }

    @Override
    void visitThruSection() {
        if (!begin('loft', 'thru section').suppressed) kernel.visitThruSection()
    }

    @Override
    void visitThruSectionEnd() {
        if (!end().suppressed) kernel.visitThruSectionEnd()
    }

    @Override
    void visitWing() {
        if (!begin('wing', 'wing').suppressed) kernel.visitWing()
    }

    @Override
    void visitWingSection(Vec leadingEdge, double chord, double twist, Vec2d from) {
        if (!begin('section', "section at ${v(leadingEdge)}, chord ${n(chord)}").suppressed) kernel.visitWingSection(leadingEdge, chord, twist, from)
    }

    @Override
    void visitWingSectionEnd(Vec2d from) {
        if (!end().suppressed) kernel.visitWingSectionEnd(from)
    }

    @Override
    void visitWingEnd() {
        if (!end().suppressed) kernel.visitWingEnd()
    }

    @Override
    void visitPlate(PlateOptions options) {
        if (!begin('plate', 'plate').suppressed) kernel.visitPlate(options)
    }

    @Override
    void visitPlateCurve(Vec from, boolean boundary) {
        if (!begin('curve', (boundary ? 'boundary at ' : 'curve at ') + v(from)).suppressed) kernel.visitPlateCurve(from, boundary)
    }

    @Override
    void visitPlateCurveEnd(Vec from) {
        if (!end().suppressed) kernel.visitPlateCurveEnd(from)
    }

    @Override
    void visitPlateThrough(Vec point) {
        if (!add('point', "through ${v(point)}", true).suppressed) kernel.visitPlateThrough(point)
    }

    @Override
    void visitPlateEnd() {
        if (!end().suppressed) kernel.visitPlateEnd()
    }

    @Override
    void display(String fileName) {
        recorder.displayed(fileName, root)
        if (forwarded || !recorder.stopping) kernel.display(fileName)
    }

    @Override
    void visitFace(Vec direction, Vec position) {
        if (!add('select', "on face ${v(direction)}" + (position != null ? " near ${v(position)}" : '')).suppressed) kernel.visitFace(direction, position)
    }

    @Override
    void visitFillet(double length) {
        if (!add('fillet', "fillet r${n(length)}").suppressed) kernel.visitFillet(length)
    }

    @Override
    void visitHollowedSolid(double thickness) {
        if (!add('shell', "shell ${n(thickness)}").suppressed) kernel.visitHollowedSolid(thickness)
    }

    @Override
    void visitSolidMirror(Vec pos, Vec dir) {
        if (!add('mirror', "mirror about ${v(dir)}").suppressed) kernel.visitSolidMirror(pos, dir)
    }

    @Override
    void visitSolidTranslate(Vec distance) {
        if (!add('translate', "translate ${v(distance)}").suppressed) kernel.visitSolidTranslate(distance)
    }

    @Override
    void visitSolidRotate(Vec pos, Vec dir, double angle, boolean clone) {
        if (!add('rotate', "rotate ${n(Math.toDegrees(angle))}° about ${v(dir)}" + (clone ? ', copy' : '')).suppressed) kernel.visitSolidRotate(pos, dir, angle, clone)
    }
}
