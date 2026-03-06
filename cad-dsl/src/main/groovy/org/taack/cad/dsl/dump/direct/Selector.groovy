package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.Vec
import org.taack.occt.NativeLib as nl

import java.lang.foreign.MemorySegment


enum Qty {
    max, min
}


@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative


    Vec currentLoc


}
