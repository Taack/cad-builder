package org.taack.cad.dsl.builder

/**
 * TopoDS_Wire
 */
interface IWire {
    IWire mirror(Vec2d pos, Vec2d dir)
}
