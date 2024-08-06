package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

@CompileStatic
class CadBuilder extends Face {

    static CadBuilder cb() {
        new CadBuilder()
    }

    CadBuilder box(BigDecimal x, BigDecimal y, BigDecimal z) {
        currentShape = nl.brep_builderapi_make_shape(nl.brep_primapi_make_box(x.doubleValue(), y.doubleValue(), z.doubleValue()))
        return this
    }

    void display(String fileName = null, int w = 640, int h = 480) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                nl.dumpShape(currentShape, w, h, t)
            }
        } else nl.visualize(currentShape)
    }

    CadBuilder sketch(@DelegatesTo(value = Sketch, strategy = Closure.DELEGATE_FIRST) sketch) {
        this
    }
}
