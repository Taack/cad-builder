package org.taack.cad.dsl

class Edge extends Vertice implements Selector {
    CadBuilder vertices(@DelegatesTo(value = Vertice, strategy = Closure.DELEGATE_ONLY) operations) {

    }
}
