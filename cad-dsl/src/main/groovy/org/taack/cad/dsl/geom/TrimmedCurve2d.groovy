package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernels

/**
 * Part of a closed 2D curve between two of its parameters
 */
@CompileStatic
class TrimmedCurve2d implements ITrimmable2d {
    final IClosedShape2d curve
    // The trim is reversed; already applied to the trimmed curve, so getReverse() is false
    final boolean reversed
    final double u1
    final double u2

    TrimmedCurve2d(IClosedShape2d curve, double u1, double u2, boolean reverse) {
        this.curve = curve
        this.u1 = u1
        this.u2 = u2
        this.reversed = reverse
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
        return "TrimmedCurve2d{" +
                "curve=" + curve +
                ", u1=" + u1 +
                ", u2=" + u2 +
                ", reverse=" + reversed +
                '}'
    }
}
