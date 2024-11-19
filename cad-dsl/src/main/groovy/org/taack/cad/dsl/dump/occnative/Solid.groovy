package org.taack.cad.dsl.dump.occnative

import groovy.transform.CompileStatic
import org.nativelib.NativeLib
import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.IEdge
import org.taack.cad.dsl.builder.IFace
import org.taack.cad.dsl.builder.ISolid
import org.taack.cad.dsl.builder.ISolidOp
import org.taack.cad.dsl.builder.ISolidPrimitive
import org.taack.cad.dsl.builder.Vec

@CompileStatic
class Solid extends Plan implements ISolid, ISolidPrimitive, ISolidOp {
    private NVec loc = new NVec(new Vec(0.0, 0.0, 0.0))

    @Override
    IEdge[] getEdges() {
        return null
    }

    @Override
    IFace[] getFaces() {
        return null
    }

    @Override
    ICad topZ() {
        return null
    }

    @Override
    ICad box(BigDecimal sx, BigDecimal sy, BigDecimal sz) {
        currentShapeNative = NativeLib.brep_builderapi_make_shape(NativeLib.brep_primapi_make_box(sx.doubleValue(), sy.doubleValue(), sz.doubleValue()))
        instance
    }

    @Override
    ICad sphere(BigDecimal radius, Vec dir = Vec.vZ, BigDecimal fromAngle = null, BigDecimal toAngle = null) {
        def ax2 = NativeLib.gp_ax2(loc.toGpPnt(), new NVec(dir).toGpDir())
        currentShapeNative = NativeLib.brep_builderapi_make_shere(ax2, radius.toDouble(), fromAngle?.toDouble(), toAngle?.toDouble())
        instance
    }

    @Override
    ICad cylinder(BigDecimal radius, BigDecimal height, Vec dir = Vec.vZ) {
        def ax2 = NativeLib.gp_ax2(loc.toGpPnt(), new NVec(dir).toGpDir())
        currentShapeNative = NativeLib.brep_builderapi_make_cylinder(ax2, radius.toDouble(), height.toDouble())
        instance
    }

    @Override
    ICad torus(BigDecimal torusRadius, BigDecimal ringRadius, Vec dir = Vec.vZ) {
        def ax2 = NativeLib.gp_ax2(loc.toGpPnt(), new NVec(dir).toGpDir())
        currentShapeNative = NativeLib.brep_primapi_make_thorus(ax2, torusRadius.toDouble(), ringRadius.toDouble())
        instance
    }

    @Override
    ICad fuse(ISolid... solid) {
        return null
    }

    @Override
    ICad cut(ISolid... solid) {
        return null
    }

    @Override
    ICad fillet(BigDecimal radius) {
        return null
    }

    @Override
    ICad fillet(BigDecimal radius, IEdge... edge) {
        return null
    }

    @Override
    ICad makeThickSolidByJoin(BigDecimal thickness, BigDecimal tol, IFace... faceToRemove) {
        return null
    }

    @Override
    ICad move(Vec p) {
        return null
    }

    @Override
    ICad rotate(Vec p) {
        return null
    }

    @Override
    ICad mirror(Vec axis) {
        return null
    }
}
