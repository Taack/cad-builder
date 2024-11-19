package org.taack.cad.dsl.dump.occnative

import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.IEdge
import org.taack.cad.dsl.builder.IFace
import org.taack.cad.dsl.builder.ISolid
import org.taack.cad.dsl.builder.ISolidOp
import org.taack.cad.dsl.builder.ISolidPrimitive
import org.taack.cad.dsl.builder.Vec

class Solid extends Plan implements ISolid, ISolidPrimitive, ISolidOp {
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
        return null
    }

    @Override
    ICad sphere(BigDecimal radius, Vec dir, BigDecimal fromAngle, BigDecimal toAngle) {
        return null
    }

    @Override
    ICad cylinder(BigDecimal radius, BigDecimal length) {
        return null
    }

    @Override
    ICad torus(BigDecimal torusRadius, BigDecimal ringRadius) {
        return null
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
