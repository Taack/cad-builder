package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

@CompileStatic
class Mirrored2d implements ITrimmable2d {
    private MemorySegment trimmed = null

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
        return Vec2d.fromAPnt(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__StartPoint(trimmedCurve2d()), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    Vec2d getEnd() {
        return Vec2d.fromAPnt(own(new_gp_Pnt2d__Geom2d_TrimmedCurve__EndPoint(trimmedCurve2d()), NativeLib::delete_gp_Pnt2d))
    }

    @Override
    boolean getReverse() {
        return false
    }

    @Override
    MemorySegment trimmedCurve2d() {
        if (!trimmed) {
            MemorySegment mirrorAxis = own(new_gp_Ax2d__pt_dir(pos.toGpPnt2d(), dir.toGpDir2d()), NativeLib::delete_gp_Ax2d)
            trimmed = own(handle_Geom2d_Geometry__Copy(curve.trimmedCurve2d()), NativeLib::delete_handle_Geom2d_Geometry)
            _Geom2d_TrimmedCurve__Mirror__ax2(trimmed, mirrorAxis)
            _Geom2d_TrimmedCurve__Reverse(trimmed)
        }
        return trimmed
    }

    @Override
    MemorySegment makeWireAdd(Vec2d fromLocal) {
        return trimmedCurve2d()
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
