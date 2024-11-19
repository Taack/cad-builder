package org.taack.cad.dsl.builder

/**
 * gp_Trsf operations
 * @param <IVec>
 */
interface ITransform {
    ICad move(Vec p)
    ICad rotate(Vec p)
    ICad mirror(Vec axis)
}