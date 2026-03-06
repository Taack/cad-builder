package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic

@CompileStatic
enum ShapeEnum {
    TopAbs_COMPOUND,
    TopAbs_COMPSOLID,
    TopAbs_SOLID,
    TopAbs_SHELL,
    TopAbs_FACE,
    TopAbs_WIRE,
    TopAbs_EDGE,
    TopAbs_VERTEX,
    TopAbs_SHAPE

    int getIndex() {
        ordinal()
    }
}
