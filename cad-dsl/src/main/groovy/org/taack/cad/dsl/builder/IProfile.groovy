package org.taack.cad.dsl.builder


/**
 * Manipulate Geom_TrimmedCurve to create support Geometry:
 *  GC_MakeArcOfCircle
 *  GC_MakeSegment
 *  ...
 *
 *  Default centered
 *  Check if face is plane
 */
interface IProfile {

    IProfile origin(BigDecimal x, BigDecimal y)
    IProfile origin(Vec2d from)

    IProfile lineTo(BigDecimal toX, BigDecimal toY)
    IProfile lineTo(Vec2d to)

    IProfile threePointArc(Vec2d p2, Vec2d p3)

    IProfile radiusArc(Vec2d sx, BigDecimal radius)

    IProfile close()

    IProfile rect(BigDecimal sx, BigDecimal sy)

    IProfile vertex()
    IProfile center()
    IProfile union(IProfile... profiles)
    IProfile circle(BigDecimal radius)

    IProfile pos(Vec2d pos)

    IWire toWire()

}
