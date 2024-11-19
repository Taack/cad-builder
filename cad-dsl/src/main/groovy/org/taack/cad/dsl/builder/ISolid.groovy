package org.taack.cad.dsl.builder

interface ISolid {

    IEdge[] getEdges()

    IFace[] getFaces()

    ICad topZ()
}