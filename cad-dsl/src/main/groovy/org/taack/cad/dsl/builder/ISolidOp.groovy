package org.taack.cad.dsl.builder

interface ISolidOp extends ITransform<Vec> {
    ICad fuse(ISolid... solid)

    ICad cut(ISolid... solid)

    ICad fillet(BigDecimal radius)

    ICad fillet(BigDecimal radius, IEdge... edge)

    ICad makeThickSolidByJoin(BigDecimal thickness, BigDecimal tol, IFace... faceToRemove)
}
