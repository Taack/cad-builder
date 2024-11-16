package org.taack.cad.dsl.builder

interface IKinematicModelingOp<IVec> {
    IKinematicModelingOp<IVec> makePrism(IVec vec)
    IKinematicModelingOp<IVec> makeRevolution(IVec vec)
}
