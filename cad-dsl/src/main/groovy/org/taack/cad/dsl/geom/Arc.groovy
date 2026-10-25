package org.taack.cad.dsl.geom

import groovy.transform.CompileStatic

@CompileStatic
class Arc implements IOpenShape {
    Vec to
    Vec via

    Arc(Vec to, Vec via) {
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
