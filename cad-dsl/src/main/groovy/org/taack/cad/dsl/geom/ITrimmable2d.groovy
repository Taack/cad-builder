package org.taack.cad.dsl.geom

import org.taack.cad.builder.Vec2d

import java.lang.foreign.MemorySegment

interface ITrimmable2d {
    Vec2d getStart()
    Vec2d getEnd()
    boolean getReverse()

    MemorySegment trimmedCurve2d()
}