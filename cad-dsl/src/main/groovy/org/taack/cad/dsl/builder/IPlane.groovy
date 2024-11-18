package org.taack.cad.dsl.builder

interface IPlane extends IKinematicModelingOp<Vec> {
    IPlane profile(@DelegatesTo(value = IProfile, strategy = Closure.DELEGATE_FIRST) operations)
}
