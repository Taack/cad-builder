package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslNode

import java.lang.foreign.MemorySegment

/**
 * Part of an assembly: a native CadNode holding a copy of a solid.
 */
@CompileStatic
class OcctPart implements ICadDslNode {
    final MemorySegment node
    // Solid of the part, for the face queries
    final MemorySegment shape

    OcctPart(MemorySegment node, MemorySegment shape) {
        this.node = node
        this.shape = shape
    }
}
