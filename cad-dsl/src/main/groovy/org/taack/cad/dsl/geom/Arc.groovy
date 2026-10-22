package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Arc implements IOpenShape {
    Vec to
    Vec center

    Arc(Vec to, Vec center) {
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
