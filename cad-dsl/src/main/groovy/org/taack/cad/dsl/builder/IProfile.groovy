package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec2d

/**
 * Manipulate Geom_TrimmedCurve to create support Geometry:
 *  GC_MakeArcOfCircle
 *  GC_MakeSegment
 *  ...
 */
interface IProfile {

    IProfile origin(Vec2d sketchPos)

    IProfile lineTo(Vec2d to)

    IProfile threePointArc(Vec2d p2, Vec2d p3)

    IProfile radiusArc(Vec2d sx, BigDecimal radius)

    IProfile close()

    IProfile rect(BigDecimal sx, BigDecimal sy)

    IProfile circle(BigDecimal radius)

    IProfile pos(Vec2d pos)

    IWire toWire()

}
