package org.taack.cad.binding

import groovy.transform.CompileStatic
import org.taack.cad.dsl.TestImages

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.dumpShape

/**
 * The images of the tests of the bindings, which build their shapes without the DSL: see TestImages
 */
@CompileStatic
final class LowLevelImages {
    private LowLevelImages() {}

    /**
     * Renders shape to the image of the running test, when the test task asks for images (cad.test.images)
     *
     * @param shape A TopoDS_Shape
     */
    static void image(MemorySegment shape) {
        String file = TestImages.image()
        if (file == null) return
        try (Arena arena = Arena.ofConfined()) {
            if (dumpShape(shape, 1920, 1080, arena.allocateFrom(file)) == 0) throw new IllegalStateException("Cannot render $file")
        }
    }
}
