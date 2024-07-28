package org.taack

import org.nativelib.NativeLib

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

static void main(String[] args) {
    println "Hello world!"
    NativeLib.print_hello()
    NativeLib.test_gl()
//    try (Arena arena = Arena.ofConfined()) {
//        MemorySegment t = arena.allocateFrom('Test');
//        def w = NativeLib.GlfwOcctView_initWindow(640, 640, t)
//        NativeLib.GlfwOcctView_initViewer(w)
//        NativeLib.GlfwOcctView_initGui(w)
//        def b = NativeLib.cMakeBottle(1.0, 1.0, 0.1)
//        NativeLib.GlfwOcctView_displayInContext(w, b)
//        NativeLib.GlfwOcctView_mainloop(w)
//        NativeLib.GlfwOcctView_cleanup(w)
//    }
}