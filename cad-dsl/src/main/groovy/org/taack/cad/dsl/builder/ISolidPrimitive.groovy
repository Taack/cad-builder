package org.taack.cad.dsl.builder

interface ISolidPrimitive {

    ICad cyl(Vec ax, BigDecimal radius, BigDecimal height)
    ICad box(BigDecimal sx, BigDecimal sy, BigDecimal sz)
    ICad box(Vec size)
    ICad sphere(BigDecimal radius, Vec dir, BigDecimal fromAngle, BigDecimal toAngle)
    ICad cylinder(BigDecimal radius, BigDecimal length)
}