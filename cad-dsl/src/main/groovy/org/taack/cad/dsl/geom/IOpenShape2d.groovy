package org.taack.cad.dsl.geom


import java.lang.foreign.MemorySegment

interface IOpenShape2d {
    MemorySegment makeWireAdd(Vec2d fromLocal)

    Vec2d getTo()
}