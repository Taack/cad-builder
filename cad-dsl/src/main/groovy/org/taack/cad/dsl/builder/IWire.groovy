package org.taack.cad.dsl.builder

import org.taack.cad.dsl.Vec2d

/**
 * TopoDS_Wire
 */
interface IWire {
    IWire add(IEdge... edges)
    IWire add(IWire... others)
    IWire transform(ITransform<Vec2d>... transform)
}
