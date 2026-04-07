package org.taack.cad.dsl.geom

import org.taack.cad.builder.Vec

import java.lang.foreign.MemorySegment

interface IOpenShape {
    MemorySegment makeWireAdd(Vec fromLocal)

    Vec getTo()

}