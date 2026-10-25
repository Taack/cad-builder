package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Arc2d implements IOpenShape2d {
    Vec2d to
    Vec2d via

    Arc2d(Vec2d to, Vec2d via) {
        this.to = to
        this.via = via
    }

    @Override
    String toString() {
        return "Arc2d{" +
                "to=" + to +
                ", via=" + via +
                '}'
    }
}
