package org.taack.cad.dsl.geom

import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

interface ITrimmable2d extends IOpenShape2d, IConstruction {
    Vec2d getStart()
    Vec2d getEnd()
    boolean getReverse()

    MemorySegment trimmedCurve2d()
}