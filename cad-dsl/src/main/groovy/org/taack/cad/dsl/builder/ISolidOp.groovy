package org.taack.cad.dsl.builder

interface ISolidOp {
    ISolid fuse(ISolid... solid)

    ISolid cut(ISolid... solid)

    ISolid fillet(BigDecimal radius)

    ISolid fillet(IEdge edge, BigDecimal radius)

    ISolid makeThickSolidByJoin(BigDecimal thickness, BigDecimal tol, IFace... faceToRemove)
}
