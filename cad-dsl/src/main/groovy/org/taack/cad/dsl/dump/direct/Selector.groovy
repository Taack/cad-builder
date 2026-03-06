package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.Vec

import java.lang.foreign.MemorySegment

@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative


    Vec currentLoc


}
