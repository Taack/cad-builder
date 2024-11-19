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

    IProfile move(BigDecimal x, BigDecimal y)
    IProfile move(Vec2d from)

    IProfile lineTo(BigDecimal toX, BigDecimal toY)
    IProfile lineTo(Vec2d to)

    IProfile threePointArc(Vec2d p2, Vec2d p3)
    IProfile circle(BigDecimal radius)

    IProfile radiusArc(Vec2d sx, BigDecimal radius)

    IProfile close()

    IProfile rect(BigDecimal sx, BigDecimal sy)
    IProfile union(IProfile... profiles)

    IProfile center()

    IWire toWire()

}
