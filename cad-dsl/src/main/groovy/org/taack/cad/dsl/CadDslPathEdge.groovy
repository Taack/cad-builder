package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.taack.cad.dsl.geom.Vec

/**
 * Draws a path, and records its center line as it goes
 */
@CompileStatic
@PackageScope
class CadDslPathEdge extends CadDslEdge {
    PathCenterLine line

    @Override
    void edge(Vec to) {
        line.edge(to)
        super.edge(to)
    }

    @Override
    void arc(Vec to, Vec via) {
        line.arc(to, via)
        super.arc(to, via)
    }

    @Override
    void move(Vec to) {
        line.known = false
        super.move(to)
    }

    @Override
    void to(Vec to) {
        line.known = false
        super.to(to)
    }
}
