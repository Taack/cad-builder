package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.occt.Natives.own

/**
 * Conversions between the DSL vectors and OCCT gp_* objects.
 */
@CompileStatic
final class Occt {
    private Occt() {}

    static MemorySegment gpDir(Vec v) {
        own(new_gp_Dir__x_y_z(v.x, v.y, v.z), NativeLib::delete_gp_Dir)
    }

    static MemorySegment gpPnt(Vec v) {
        own(new_gp_Pnt__x_y_z(v.x, v.y, v.z), NativeLib::delete_gp_Pnt)
    }

    static MemorySegment gpVec(Vec v) {
        own(new_gp_Vec__x_y_z(v.x, v.y, v.z), NativeLib::delete_gp_Vec)
    }

    static MemorySegment gpPln(Vec v, Number t = 0) {
        own(new_gp_Pln__x_y_z_d(v.x, v.y, v.z, t.toDouble()), NativeLib::delete_gp_Pln)
    }

    static MemorySegment gpDir2d(Vec2d v) {
        own(new_gp_Dir2d__x_y(v.x, v.y), NativeLib::delete_gp_Dir2d)
    }

    static MemorySegment gpPnt2d(Vec2d v) {
        own(new_gp_Pnt2d__x_y(v.x, v.y), NativeLib::delete_gp_Pnt2d)
    }

    static MemorySegment gpVec2d(Vec2d v) {
        own(new_gp_Vec2d__x_y(v.x, v.y), NativeLib::delete_gp_Vec2d)
    }

    static Vec vec(MemorySegment gpPnt) {
        new Vec(gp_Pnt__X(gpPnt), gp_Pnt__Y(gpPnt), gp_Pnt__Z(gpPnt))
    }

    static Vec vecFromDir(MemorySegment gpDir) {
        new Vec(gp_Dir__X(gpDir), gp_Dir__Y(gpDir), gp_Dir__Z(gpDir))
    }

    static Vec2d vec2d(MemorySegment gpPnt2d) {
        new Vec2d(gp_Pnt2d__X(gpPnt2d), gp_Pnt2d__Y(gpPnt2d))
    }
}
