package org.taack

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

enum TopAbs_ShapeEnum {
    TopAbs_COMPOUND,
    TopAbs_COMPSOLID,
    TopAbs_SOLID,
    TopAbs_SHELL,
    TopAbs_FACE,
    TopAbs_WIRE,
    TopAbs_EDGE,
    TopAbs_VERTEX,
    TopAbs_SHAPE

    int getIndex() {
        ordinal()
    }
}

;


@CompileStatic
static void main(String[] args) {
    println "Hello world!"
//    def b = nl.cMakeBottle(1.0, 1.0, 0.1)
    def myWidth = 1.0
    def myHeight = 1.0
    def myThickness = 0.1
    def aPnt1 = nl.make_gp_pnt(-myWidth / 2d, 0, 0)
    def aPnt2 = nl.make_gp_pnt(-myWidth / 2d, -myThickness / 4d, 0)
    def aPnt3 = nl.make_gp_pnt(0, -myThickness / 2d, 0)
    def aPnt4 = nl.make_gp_pnt(myWidth / 2d, -myThickness / 4d, 0)
    def aPnt5 = nl.make_gp_pnt(myWidth / 2d, 0, 0)

    println "ptns"

    def anArcOfCircle = nl.gc_make_arc_of_circle(aPnt2, aPnt3, aPnt4)
    def aSegment1 = nl.gc_make_segment(aPnt1, aPnt2)
    def aSegment2 = nl.gc_make_segment(aPnt4, aPnt5)

    println "segments"

    def anEdge1 = nl.brep_builderapi_make_edge(aSegment1)
    println "e1"
    def anEdge2 = nl.brep_builderapi_make_edge(anArcOfCircle)
    def anEdge3 = nl.brep_builderapi_make_edge(aSegment2)

    println "edges"

    def aWire = nl.brep_builderapi_make_wire_topo_ds_wire(anEdge1, anEdge2, anEdge3)

    println "wires"

    def xAxis = nl.gp_ox()
    def aTrsf = nl.gp_trsf()

    println "axis"

    nl.gp_trsf_set_mirror(aTrsf, xAxis)
    println "m1"
    def aBRepTrsf = nl.brep_builderapi_transform(aWire, aTrsf)
    println "m2"
    def aMirroredShape = nl.brep_builderapi_transform_shape(aBRepTrsf)
    println "m3"
    def aMirroredWire = nl.topo_ds_wire(aMirroredShape)
    println "m4"

    println "mirror"

    def mkWire = nl.brep_builderapi_make_wire_new()
    println "profile1"
    nl.brep_builderapi_wire_add(mkWire, aWire)
    println "profile2"
    nl.brep_builderapi_wire_add(mkWire, aMirroredWire)
    println "profile3"
    def myWireProfile = nl.brep_builderapi_make_wire_topo_ds_wire2(mkWire)

    println "profile"
    // Body: Prism the Profile
    def myFaceProfile = nl.brep_builderapi_make_face(myWireProfile)
    def aPrismVec = nl.make_gp_vec(0d, 0d, myHeight as double)
    def myBody = nl.brep_primapi_make_prism(myFaceProfile, aPrismVec)

    println "body"

    def mkFillet = nl.brep_filletapi_make_fillet(myBody)
    def anEdgeExplorer = nl.top_exp_explorer(myBody, TopAbs_ShapeEnum.TopAbs_EDGE.index, TopAbs_ShapeEnum.TopAbs_SHAPE.index)
    while (nl.top_exp_explorer_more(anEdgeExplorer)) {
        def anEdge = nl.topo_ds_edge(nl.top_exp_explorer_current(anEdgeExplorer))
        //Add edge to fillet algorithm
        nl.brep_filletapi_make_fillet_add(mkFillet, myThickness / 12d, anEdge)
        nl.top_exp_explorer_next(anEdgeExplorer)
    }

    myBody = nl.brep_filletapi_make_fillet_shape(mkFillet)


    nl.pfff(myBody)


//    nl.print_hello()
//    nl.test_gl()
//    try (Arena arena = Arena.ofConfined()) {
//        println "Arena ..."
//        MemorySegment t = arena.allocateFrom('Test');
//        def w = nl.GlfwOcctView_initWindow(640, 640, t)
//        println "Init Window ..."
//        nl.GlfwOcctView_initViewer(w)
//        nl.GlfwOcctView_initGui(w)
//        println "Make Bottle +++"
////        def b = nl.cMakeBottle(1.0, 1.0, 0.1)
////        println "Make Bottle ---"
////        nl.GlfwOcctView_displayInContext(w, b)
//        nl.GlfwOcctView_displaySomething(w)
//        println "GlfwOcctView_displayInContext ---"
//        nl.GlfwOcctView_mainloop(w)
////        nl.GlfwOcctView_cleanup(w)
//    }
}
import java.lang.foreign.Arena

import java.lang.foreign.MemorySegment
