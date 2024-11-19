package org.taack.cad.dsl.builder

interface IPlane extends IKinematicModelingOp {
    IPlane profile(@DelegatesTo(value = IProfile, strategy = Closure.DELEGATE_FIRST) operations)
}
