package org.taack.cad.dsl.builder

interface IKinematicModelingOp {
    ICad pad(Vec vec)
    ICad pad(BigDecimal length)
    ICad revolution(Vec vec)
    ICad pocket(BigDecimal length)
    ICad hole()

}
