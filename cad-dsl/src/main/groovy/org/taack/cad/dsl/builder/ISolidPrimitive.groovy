package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec

interface ISolidPrimitive {

    ICad cyl(Vec ax, BigDecimal radius, BigDecimal height)
    ICad box(BigDecimal sx, BigDecimal sy, BigDecimal sz)
    ICad box(Vec size)

}