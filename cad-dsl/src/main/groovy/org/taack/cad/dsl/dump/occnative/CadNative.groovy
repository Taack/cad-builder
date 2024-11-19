package org.taack.cad.dsl.dump.occnative

import groovy.transform.CompileStatic
import org.nativelib.NativeLib
import org.taack.cad.dsl.builder.ICad

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

@CompileStatic
final class CadNative extends Solid implements ICad {

    CadNative() {
        instance = this
    }

    @Override
    ICad display() {
        NativeLib.visualize(currentShapeNative)
        return null
    }

    @Override
    ICad display(String fileName, int w = 640, int h = 480) {
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment t = arena.allocateFrom(fileName)
            if (fileName.endsWith(".step")) {
                NativeLib.write_step(currentShapeNative, t)
            } else if (fileName.endsWith(".stl")) {
                NativeLib.write_stl(currentShapeNative, t)
            } else
                NativeLib.dumpShape(currentShapeNative, w, h, t)
        }
        this
    }
}
