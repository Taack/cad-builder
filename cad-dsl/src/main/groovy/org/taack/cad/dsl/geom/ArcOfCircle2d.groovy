package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernels

@CompileStatic
class ArcOfCircle2d implements ITrimmable2d {
    final Circle2d circle2d
    // The trim is reversed; already applied to the trimmed curve, so getReverse() is false
    final boolean reversed
    final double angle1
    final double angle2

    final Vec2d ptFrom
    final Vec2d ptTo


    ArcOfCircle2d(Circle2d circle2d, double angle1, double angle2, boolean reverse) {
        this.reversed = reverse
        this.circle2d = circle2d
        this.angle1 = angle1
        this.angle2 = angle2
        this.ptFrom = null
        this.ptTo = null
    }

    ArcOfCircle2d(Circle2d circle2d, Vec2d ptFrom, Vec2d ptTo, boolean reverse) {
        this.reversed = reverse
        this.circle2d = circle2d
        this.angle1 = 0
        this.angle2 = 0
        this.ptFrom = ptFrom
        this.ptTo = ptTo
    }

    ArcOfCircle2d(Circle2d circle2d, Vec2d ptFrom, double angle, boolean reverse) {
        this.reversed = reverse
        this.circle2d = circle2d
        this.angle1 = angle
        this.angle2 = angle
        this.ptFrom = ptFrom
        this.ptTo = null
    }

    @Override
    Vec2d getStart() {
        return CadKernels.get().startPoint(this)
    }

    @Override
    Vec2d getEnd() {
        return CadKernels.get().endPoint(this)
    }

    @Override
    boolean getReverse() {
        return false
    }

    @Override
    Vec2d getTo() {
        return getEnd()
    }

    @Override
    String toString() {
        return "ArcOfCircle2d{" +
                "start=" + start +
                ", end=" + end +
                ", reverse=" + reversed +
                ", angle1=" + angle1 +
                ", angle2=" + angle2 +
                '}';
    }
}
