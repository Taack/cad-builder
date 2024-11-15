package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec

interface ISolid {

    ISolid cyl(Vec ax, BigDecimal radius, BigDecimal height)

    IEdge[] getEdges()

    IFace[] getFaces()

    IFace topZ()
}