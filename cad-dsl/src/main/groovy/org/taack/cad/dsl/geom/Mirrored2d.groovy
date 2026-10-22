package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernels

@CompileStatic
class Mirrored2d implements ITrimmable2d {
    ITrimmable2d curve
    Vec2d pos
    Vec2d dir

    Mirrored2d(ITrimmable2d curve, Vec2d pos, Vec2d dir) {
        this.curve = curve
        this.pos = pos
        this.dir = dir
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
        return "Mirrored2d{" +
                "start=" + start +
                "end=" + end +
                "pos=" + pos +
                ", dir=" + dir +
                '}';
    }
}
