package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec

@CompileStatic
class CadDslEdge {
    void edge(Vec to) {}
    void arc(Vec to, Vec via) {}
}
