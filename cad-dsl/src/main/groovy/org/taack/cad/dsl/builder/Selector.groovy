package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative
    Stack<MemorySegment> wireNatives = new Stack<>()

    MemorySegment addCurrentWireNative(MemorySegment wireNative = null) {
        MemorySegment ret = wireNative ?: new_BRepBuilderAPI_MakeWire()
        if (wireNatives.size() > 0) {
            wireNatives.eachWithIndex { MemorySegment it, int i ->
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(ret, it)
            }
        }
        ret
    }

    MemorySegment toShape() {
        if (currentShapeNative) currentShapeNative
        else if (currentFaceNative) currentFaceNative
        else ref_TopoDS_Shape__BRepBuilderAPI_MakeWire__Shape(addCurrentWireNative())
    }

    Vec currentLoc
}
