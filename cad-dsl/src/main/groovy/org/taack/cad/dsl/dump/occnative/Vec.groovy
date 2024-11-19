package org.taack.cad.dsl.dump.occnative

import groovy.transform.CompileStatic
import org.nativelib.NativeLib
import org.taack.cad.dsl.builder.Axe
import org.taack.cad.dsl.builder.Vec

import java.lang.foreign.MemorySegment

@CompileStatic
final class NVec {
    @Delegate
    Vec vec

    NVec(Vec vec) {
        this.vec = vec
    }

    static Vec fromAPnt(MemorySegment aPnt) {
        new Vec(
                NativeLib.gp_pnt_x(aPnt).toBigDecimal(),
                NativeLib.gp_pnt_y(aPnt).toBigDecimal(),
                NativeLib.gp_pnt_z(aPnt).toBigDecimal()
        )
    }

    static Vec fromADir(MemorySegment aDir) {
        new Vec(
                NativeLib.gp_dir_x(aDir).toBigDecimal(),
                NativeLib.gp_dir_y(aDir).toBigDecimal(),
                NativeLib.gp_dir_z(aDir).toBigDecimal()
        )
    }

    MemorySegment toGpDir() {
        NativeLib.gp_dir_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
    }

    MemorySegment toGpPnt() {
        NativeLib.gp_pnt_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
    }

    MemorySegment toGpVec() {
        NativeLib.gp_vec_new(x.doubleValue(), y.doubleValue(), z.doubleValue())
    }

    BigDecimal cord(Axe axe) {
        switch (axe) {
            case Axe.X:
                return x
            case Axe.Y:
                return y
            case Axe.Z:
                return z
        }
    }

    @Override
    String toString() {
        return "Loc{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

}
