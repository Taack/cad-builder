package org.taack

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl
import org.taack.cad.dsl.generator.direct.ShapeEnum

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment


@CompileStatic
static void main(String[] args) {
    double myWidth = 50.0
    double myHeight = 70.0
    double myThickness = 30.0
    def aPnt1 = nl.gp_pnt_new(-myWidth / 2d, 0, 0)
    def aPnt2 = nl.gp_pnt_new(-myWidth / 2d, -myThickness / 4d, 0)
    def aPnt3 = nl.gp_pnt_new(0, -myThickness / 2d, 0)
    def aPnt4 = nl.gp_pnt_new(myWidth / 2d, -myThickness / 4d, 0)
    def aPnt5 = nl.gp_pnt_new(myWidth / 2d, 0, 0)

    def anArcOfCircle = nl.gc_make_arc_of_circle(aPnt2, aPnt3, aPnt4)
    def aSegment1 = nl.gc_make_segment(aPnt1, aPnt2)
    def aSegment2 = nl.gc_make_segment(aPnt4, aPnt5)

    println "Profile: Define the Topology"

    def anEdge1 = nl.brep_builderapi_make_edge(aSegment1)
    def anEdge2 = nl.brep_builderapi_make_edge(anArcOfCircle)
    def anEdge3 = nl.brep_builderapi_make_edge(aSegment2)
    def aWire = nl.brep_builderapi_make_wire_topo_ds_wire(anEdge1, anEdge2, anEdge3)

    println "Complete Profile"

    def xAxis = nl.gp_ox()
    def aTrsf = nl.gp_trsf()

    nl.gp_trsf_set_mirror(aTrsf, xAxis)
    def aBRepTrsf = nl.brep_builderapi_transform(aWire, aTrsf)
    def aMirroredShape = nl.brep_builderapi_make_shape(aBRepTrsf)
    def aMirroredWire = nl.topo_ds_wire(aMirroredShape)

    def mkWire = nl.brep_builderapi_make_wire_new()
    nl.brep_builderapi_wire_add(mkWire, aWire)
    nl.brep_builderapi_wire_add(mkWire, aMirroredWire)
    def myWireProfile = nl.brep_builderapi_make_wire_topo_ds_wire2(mkWire)

    println "Body: Prism the Profile"

    def myFaceProfile = nl.brep_builderapi_make_face_from_wire(myWireProfile)
    def aPrismVec = nl.gp_vec_new(0d, 0d, myHeight)
    def myBody = nl.brep_primapi_make_prism(myFaceProfile, aPrismVec)

    println "Body: Apply Fillets"

    def mkFillet = nl.brep_filletapi_make_fillet(myBody)
    def anEdgeExplorer = nl.top_exp_explorer(myBody, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
    while (nl.top_exp_explorer_more(anEdgeExplorer)) {
        def anEdge = nl.topo_ds_edge(nl.top_exp_explorer_current(anEdgeExplorer))
        //Add edge to fillet algorithm
        nl.brep_filletapi_make_fillet_add(mkFillet, myThickness / 12d, anEdge)
        nl.top_exp_explorer_next(anEdgeExplorer)
    }

    myBody = nl.brep_builderapi_make_shape(mkFillet)

    println "Body: Add the Neck"

    def neckLocation = nl.gp_pnt_new(0d, 0d, myHeight)
    def neckAxis = nl.gp_dz()
    def neckAx2 = nl.gp_ax2(neckLocation, neckAxis)

    double myNeckRadius = myThickness / 4
    double myNeckHeight = myHeight / 10

    def MKCylinder = nl.brep_primapi_make_cylinder(neckAx2, myNeckRadius, myNeckHeight)
    def myNeck = nl.brep_builderapi_make_shape(MKCylinder)

    myBody = nl.brep_algoapi_fuse(myBody, myNeck)

    println "Body: Create a Hollowed Solid"

    double zMax = -1
    def faceToRemove = nl.topods_face_new()

    for (def aFaceExplorer = nl.top_exp_explorer(myBody, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal()); nl.top_exp_explorer_more(aFaceExplorer); nl.top_exp_explorer_next(aFaceExplorer)) {
        def aFace = nl.top_exp_explorer_current_face(aFaceExplorer)
        def aSurface = nl.brep_tool_surface(aFace)
        if (nl.geom_surface_is_geom_plane(aSurface) == 1) {
            def aPlan = nl.downcast_geom_plane(aSurface)
            def aPnt = nl.geom_plane_location(aPlan)
            double aZ = nl.gp_pnt_z(aPnt)
            if (aZ > zMax) {
                zMax = aZ
                faceToRemove = aFace
            }
        }
    }

    def facesToRemove = nl.top_tools_list_of_shape()
    nl.top_tools_list_of_shape_append(facesToRemove, faceToRemove)
    def aSolidMaker = nl.brep_offset_api_make_thick_solid()
    nl.brep_offset_api_make_thick_solid_join(aSolidMaker, myBody, facesToRemove, -myThickness / 50d, 0.001d)

    myBody = nl.brep_builderapi_make_shape(aSolidMaker)

    try (Arena arena = Arena.ofConfined()) {
        MemorySegment t = arena.allocateFrom('Test.png');
        nl.dumpShape(myBody, 512, 512, t)
    }

    nl.visualize(myBody)
}