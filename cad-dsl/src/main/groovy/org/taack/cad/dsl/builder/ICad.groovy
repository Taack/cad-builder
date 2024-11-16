package org.taack.cad.dsl.builder


interface ICad extends ISolid, ISolidOp, ISolidPrimitive, IProfile, IWire {
    ICad display()
}