package org.taack.cad.dsl.check

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.ICadDslVisitorWrapper
import org.taack.cad.dsl.feature.CadFeatureFrame
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d
import org.taack.cad.dsl.kernel.CadKernel

/**
 * Kernel recording the contours drawn by the models it builds, built by another kernel:
 * CadKernels.set(new ContourRecorder(CadKernels.get()))
 */
@CompileStatic
final class ContourRecorder implements CadKernel {
    @Delegate
    final CadKernel kernel
    final List<Contour> contours = []
    private int models, wires

    ContourRecorder(CadKernel kernel) {
        this.kernel = kernel
    }

    @Override
    ICadDslVisitor newVisitor() {
        new Visitor(kernel.newVisitor(), this, ++models)
    }

    /**
     * Records the edges, arcs and splines between a from and its end; a move or a to starts another contour
     */
    @CompileStatic
    static final class Visitor implements ICadDslVisitor, ICadDslVisitorWrapper {
        @Delegate
        final ICadDslVisitor kernel
        private final ContourRecorder recorder
        private final int model
        // the pen of each from not ended, the innermost first
        private final Deque<Pen> pens = new ArrayDeque<>()

        private static final class Pen {
            final boolean in3d
            final int wire
            double[] at
            Contour contour

            Pen(boolean in3d, int wire, double[] at) {
                this.in3d = in3d
                this.wire = wire
                this.at = at
            }
        }

        Visitor(ICadDslVisitor kernel, ContourRecorder recorder, int model) {
            this.kernel = kernel
            this.recorder = recorder
            this.model = model
        }

        private static double[] p(Vec v) { [v.x, v.y, v.z] as double[] }

        private static double[] p(Vec2d v) { [v.x, v.y, 0d] as double[] }

        private Contour drawing() {
            Pen pen = pens.peek()
            if (pen == null) return null
            if (pen.contour == null) {
                CadFeatureFrame frame = CadFeatureFrame.current().with { it ? it.last() : null }
                pen.contour = new Contour(frame ? "$frame.fileName:$frame.line" : '?', pen.in3d, model, pen.wire, pen.at)
            }
            pen.contour
        }

        private void lift(double[] at) {
            Pen pen = pens.peek()
            if (pen == null) return
            if (pen.contour?.elements) recorder.contours << pen.contour
            pen.contour = null
            pen.at = at
        }

        private void drawn(double[] to) {
            Pen pen = pens.peek()
            if (pen) pen.at = to
        }

        @Override
        void visitFrom(Vec pos) {
            kernel.visitFrom(pos)
            pens.push(new Pen(true, ++recorder.wires, p(pos)))
        }

        @Override
        void visitFrom(Vec2d pos) {
            kernel.visitFrom(pos)
            pens.push(new Pen(false, ++recorder.wires, p(pos)))
        }

        @Override
        void visitFromEnd(Vec pos) {
            lift(null)
            pens.poll()
            kernel.visitFromEnd(pos)
        }

        @Override
        void visitFromEnd(Vec2d pos) {
            lift(null)
            pens.poll()
            kernel.visitFromEnd(pos)
        }

        @Override
        void visitMove(Vec to) {
            kernel.visitMove(to)
            Pen pen = pens.peek()
            if (pen) lift(Contour.add(pen.at, p(to)))
        }

        @Override
        void visitMove(Vec2d to) {
            kernel.visitMove(to)
            Pen pen = pens.peek()
            if (pen) lift(Contour.add(pen.at, p(to)))
        }

        @Override
        void visitTo(Vec to) {
            kernel.visitTo(to)
            lift(p(to))
        }

        @Override
        void visitTo(Vec2d to) {
            kernel.visitTo(to)
            lift(p(to))
        }

        @Override
        void visitEdge(Vec to) {
            kernel.visitEdge(to)
            drawing()?.edge(p(to))
            drawn(p(to))
        }

        @Override
        void visitEdge(Vec2d to) {
            kernel.visitEdge(to)
            drawing()?.edge(p(to))
            drawn(p(to))
        }

        @Override
        void visitArc(Vec to, Vec via) {
            kernel.visitArc(to, via)
            drawing()?.arc(p(via), p(to))
            drawn(p(to))
        }

        @Override
        void visitArc(Vec2d to, Vec2d via) {
            kernel.visitArc(to, via)
            drawing()?.arc(p(via), p(to))
            drawn(p(to))
        }

        @Override
        void visitSpline(List<Vec2d> through) {
            kernel.visitSpline(through)
            if (!through) return
            drawing()?.spline(through.collect { Vec2d v -> p(v) })
            drawn(p(through.last()))
        }
    }
}
