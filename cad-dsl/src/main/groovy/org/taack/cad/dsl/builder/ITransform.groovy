package org.taack.cad.dsl.builder

/**
 * gp_Trsf operations
 * @param <IVec>
 */
interface ITransform<IVec> {
    ICad move(IVec p)
    ICad rotate(IVec p)
    ICad mirror(IVec axis)
}