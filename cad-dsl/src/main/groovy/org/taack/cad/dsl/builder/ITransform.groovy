package org.taack.cad.dsl.builder

/**
 * gp_Trsf operations
 * @param <IVec>
 */
interface ITransform<IVec> {
    ITransform<IVec> move(IVec p)
    ITransform<IVec> rotate(IVec p)
    ITransform<IVec> mirror(IVec axis)
}