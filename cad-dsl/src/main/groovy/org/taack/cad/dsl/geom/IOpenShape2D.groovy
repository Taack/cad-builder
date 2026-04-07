package org.taack.cad.dsl.geom

import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

interface IOpenShape2D {
    MemorySegment makeWireAdd(Vec2d fromLocal)

    Vec2d getTo()
}