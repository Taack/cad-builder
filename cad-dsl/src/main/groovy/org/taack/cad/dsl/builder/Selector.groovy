package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*

@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative
    Stack<MemorySegment> wireNatives = new Stack<>()

    MemorySegment getCurrentWireNative() {
        MemorySegment ret = new_BRepBuilderAPI_MakeWire()
        if (wireNatives.size() > 0) {
            wireNatives.eachWithIndex { MemorySegment it, int i ->
                _BRepBuilderAPI_MakeWire__Add__BRepBuilderAPI_MakeWire(ret, it)//ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(it))
            }
        }
        ret
    }

    Vec currentLoc


}
