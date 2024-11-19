package org.taack.cad.dsl.builder


interface ICad extends ISolid, ISolidOp, ISolidPrimitive, IProfile, IPlane {
    ICad display()
    ICad display(String fileName)
    ICad display(String fileName, int w, int h)
}