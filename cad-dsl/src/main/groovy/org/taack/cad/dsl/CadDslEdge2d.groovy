package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.builder.Vec2d

@CompileStatic
class CadDslEdge2d {
    void edge(Vec2d to) {}
    void arc(Vec2d to, Vec2d via) {}
}
