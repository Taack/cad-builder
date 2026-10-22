package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Edge2d implements IOpenShape2d {
    Vec2d to

    Edge2d(Vec2d to) {
        this.to = to
    }

    @Override
    String toString() {
        return "Edge2d{" +
                "to=" + to +
                '}'
    }

}
