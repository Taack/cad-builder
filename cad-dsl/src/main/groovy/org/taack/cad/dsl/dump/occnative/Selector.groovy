package org.taack.cad.dsl.dump.occnative

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.Vec

import java.lang.foreign.MemorySegment

@CompileStatic
trait Selector {

    ICad instance

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative

    Vec currentLoc
}
