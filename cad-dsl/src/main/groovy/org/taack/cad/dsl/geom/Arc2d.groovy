package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Arc2d implements IOpenShape2d {
    Vec2d to
    Vec2d center

    Arc2d(Vec2d to, Vec2d center) {
        this.to = to
        this.center = center
    }

    @Override
    String toString() {
        return "Arc2d{" +
                "to=" + to +
                ", center=" + center +
                '}'
    }
}
