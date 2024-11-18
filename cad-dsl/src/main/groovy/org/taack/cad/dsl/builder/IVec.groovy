package org.taack.cad.dsl.builder

interface IVec<T extends IVec> {

    T div(T other)

    T plus(T other)

    T minus(T other)

    T multiply(T other)

}
