package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Edge implements IOpenShape {
    Vec to

    Edge(Vec to) {
        this.to = to
    }

    @Override
    String toString() {
        return "Edge{" +
                "to=" + to +
                '}'
    }
}
