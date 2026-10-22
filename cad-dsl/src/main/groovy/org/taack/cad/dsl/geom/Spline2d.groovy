package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadDslVisitor

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

import static org.taack.occt.NativeLib.handle_Geom2d_Curve__Geom2dAPI_Interpolate__xy_n
import org.taack.occt.NativeLib
import static org.taack.cad.dsl.helper.Natives.own

@CompileStatic
class Spline2d implements IOpenShape2d {
    final List<Vec2d> through

    Spline2d(List<Vec2d> through) {
        this.through = through
    }

    @Override
    Vec2d getTo() {
        through.last()
    }

    @Override
    MemorySegment makeWireAdd(Vec2d fromLocal) {
        CadDslVisitor.Tr.cur("Spline2d from: $fromLocal, through: $through")
        List<Vec2d> points = [fromLocal] + through
        double[] xy = new double[points.size() * 2]
        points.eachWithIndex { Vec2d p, int i ->
            xy[2 * i] = p.x
            xy[2 * i + 1] = p.y
        }
        try (Arena arena = Arena.ofConfined()) {
            return own(handle_Geom2d_Curve__Geom2dAPI_Interpolate__xy_n(arena.allocateFrom(ValueLayout.JAVA_DOUBLE, xy), points.size()), NativeLib::delete_handle_Geom2d_Curve)
        }
    }

    @Override
    String toString() {
        return "Spline2d{" +
                "through=" + through +
                '}'
    }
}
