package org.taack

import groovy.transform.CompileStatic
import org.nativelib.NativeLib

@CompileStatic
static void main(String[] args) {
    println "Hello world!"
//    def b = NativeLib.cMakeBottle(1.0, 1.0, 0.1)
    def myWidth = 1.0
    def myHeight = 1.0
    def myThickness = 0.1
    def aPnt1 = NativeLib.make_gp_pnt(-myWidth / 2, 0, 0)
    def aPnt2 = NativeLib.make_gp_pnt(-myWidth / 2, -myThickness / 4, 0)
    def aPnt3 = NativeLib.make_gp_pnt(0, -myThickness / 2, 0)
    def aPnt4 = NativeLib.make_gp_pnt(myWidth / 2, -myThickness / 4, 0)
    def aPnt5 = NativeLib.make_gp_pnt(myWidth / 2, 0, 0)

    println "ptns"

    def anArcOfCircle = NativeLib.gc_make_arc_of_circle(aPnt2, aPnt3, aPnt4)
    def aSegment1 = NativeLib.gc_make_segment(aPnt1, aPnt2)
    def aSegment2 = NativeLib.gc_make_segment(aPnt4, aPnt5)

    println "segments"

    def anEdge1 = NativeLib.brep_builderapi_make_edge(aSegment1)
    println "e1"
    def anEdge2 = NativeLib.brep_builderapi_make_edge(anArcOfCircle)
    def anEdge3 = NativeLib.brep_builderapi_make_edge(aSegment2)

    println "edges"

    def aWire = NativeLib.brep_builderapi_make_wire_topo_ds_wire(anEdge1, anEdge2, anEdge3)

    println "wires"

    def xAxis = NativeLib.gp_ox()
    def aTrsf = NativeLib.gp_trsf()

    println "axis"

    NativeLib.gp_trsf_set_mirror(aTrsf, xAxis)
    println "m1"
    def aBRepTrsf = NativeLib.brep_builderapi_transform(aWire, aTrsf)
    println "m2"
    def aMirroredShape = NativeLib.brep_builderapi_transform_shape(aBRepTrsf)
    println "m3"
    def aMirroredWire = NativeLib.topo_ds_wire(aMirroredShape)
    println "m4"

    println "mirror"

    def mkWire = NativeLib.brep_builderapi_make_wire_new()
    println "profile1"
    NativeLib.brep_builderapi_wire_add(mkWire, aWire)
    println "profile2"
    NativeLib.brep_builderapi_wire_add(mkWire, aMirroredWire)
    println "profile3"
    def myWireProfile = NativeLib.brep_builderapi_make_wire_topo_ds_wire2(mkWire)

    println "profile"
    // Body: Prism the Profile
    def myFaceProfile = NativeLib.brep_builderapi_make_face(myWireProfile)
    def aPrismVec = NativeLib.make_gp_vec(0.0, 0.0, myHeight);
    def myBody = NativeLib.brep_primapi_make_prism(myFaceProfile, aPrismVec)

    println "body"

    NativeLib.pfff(myBody)


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
