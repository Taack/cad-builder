package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec2d

/**
 * TopoDS_Wire
 */
interface IWire {
    IWire mirror(Vec2d pos, Vec2d dir)
}
