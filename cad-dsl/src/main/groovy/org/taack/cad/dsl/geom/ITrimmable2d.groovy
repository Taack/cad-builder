package org.taack.cad.dsl.geom

interface ITrimmable2d extends IOpenShape2d, IConstruction {
    Vec2d getStart()
    Vec2d getEnd()
    boolean getReverse()
}
