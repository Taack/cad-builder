package org.taack.cad.dsl.geom


import java.lang.foreign.MemorySegment

interface IOpenShape {
    MemorySegment makeWireAdd(Vec fromLocal)

    Vec getTo()

}