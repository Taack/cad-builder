package org.taack

import org.nativelib.NativeLib

static void main(String[] args) {
    println "Hello world!"
    def b = NativeLib.cMakeBottle(1.0, 1.0, 0.1)
    NativeLib.pfff(b)
//    NativeLib.print_hello()
//    NativeLib.test_gl()
//    try (Arena arena = Arena.ofConfined()) {
//        println "Arena ..."
//        MemorySegment t = arena.allocateFrom('Test');
//        def w = NativeLib.GlfwOcctView_initWindow(640, 640, t)
//        println "Init Window ..."
//        NativeLib.GlfwOcctView_initViewer(w)
//        NativeLib.GlfwOcctView_initGui(w)
//        println "Make Bottle +++"
////        def b = NativeLib.cMakeBottle(1.0, 1.0, 0.1)
////        println "Make Bottle ---"
////        NativeLib.GlfwOcctView_displayInContext(w, b)
//        NativeLib.GlfwOcctView_displaySomething(w)
//        println "GlfwOcctView_displayInContext ---"
//        NativeLib.GlfwOcctView_mainloop(w)
////        NativeLib.GlfwOcctView_cleanup(w)
//    }
}
import java.lang.foreign.Arena

import java.lang.foreign.MemorySegment
