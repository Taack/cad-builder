package org.taack.cad.dsl.builder

interface IKinematicModelingOp<IVec> {
    ICad pad(IVec vec)
    ICad pad(BigDecimal length)
    ICad revolution(IVec vec)
    ICad pocket(BigDecimal length)
    ICad hole()

}
