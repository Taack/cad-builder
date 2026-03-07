package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment
import org.taack.occt.NativeLib as nl

@CompileStatic
trait Selector {

    MemorySegment currentShapeNative
    MemorySegment currentFaceNative
    Stack<MemorySegment> wireNatives = new Stack<>()

    MemorySegment getCurrentWireNative() {
        MemorySegment ret = nl.brep_builderapi_makewire_new()
        if (wireNatives.size() > 0) {
            wireNatives.eachWithIndex { MemorySegment it, int i ->
                nl.brep_builderapi_wire_add_wire(ret, it)//nl.brep_builderapi_make_wire_topo_ds_wire2(it))
            }
        }
        ret
    }

    Vec currentLoc


}
