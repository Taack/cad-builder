package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec

interface ISolid {

    IEdge[] getEdges()

    IFace[] getFaces()

    IFace topZ()
}