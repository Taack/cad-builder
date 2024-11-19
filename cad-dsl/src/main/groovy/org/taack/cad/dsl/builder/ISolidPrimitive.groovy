package org.taack.cad.dsl.builder

interface ISolidPrimitive {

    ICad box(BigDecimal sx, BigDecimal sy, BigDecimal sz)
    ICad sphere(BigDecimal radius, Vec dir, BigDecimal fromAngle, BigDecimal toAngle)
    ICad cylinder(BigDecimal radius, BigDecimal length, Vec dir)
    ICad cylinder(BigDecimal radius, BigDecimal length)
    ICad torus(BigDecimal torusRadius, BigDecimal ringRadius, Vec dir)
    ICad torus(BigDecimal torusRadius, BigDecimal ringRadius)
}