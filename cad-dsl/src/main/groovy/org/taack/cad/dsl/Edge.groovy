package org.taack.cad.dsl

class Edge {
    void vertices(@DelegatesTo(value = Vertice, strategy = Closure.DELEGATE_ONLY) construction) {

    }
}
