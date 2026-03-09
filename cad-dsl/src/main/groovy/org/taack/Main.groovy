package org.taack

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.ShapeEnum
import org.taack.occt.NativeLib as nl

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

/**
 * Create The Bottle Using direct binding
 * C++ Code in cad-builder/cad-dsl/src/main/groovy/org/taack/cad/dsl/builder
 * See https://dev.opencascade.org/doc/overview/html/occt__tutorial.html
 */
@CompileStatic
static void mainBottle() {
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
    def aMirroredShape = nl.brep_builderapi_make_shape_Shape(aBRepTrsf)
    def aMirroredWire = nl.topo_ds_wire(aMirroredShape)

    def mkWire = nl.brep_builderapi_makewire_new()
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

    myBody = nl.brep_builderapi_make_shape_Shape(mkFillet)

    println "Body: Add the Neck"

    def neckLocation = nl.gp_pnt_new(0d, 0d, myHeight)
    def neckAxis = nl.gp_dz()
    def neckAx2 = nl.gp_ax2(neckLocation, neckAxis)

    double myNeckRadius = myThickness / 4
    double myNeckHeight = myHeight / 10

    def MKCylinder = nl.brep_primapi_make_cylinder(neckAx2, myNeckRadius, myNeckHeight)
    def myNeck = nl.brep_builderapi_make_shape_Shape(MKCylinder)

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

    myBody = nl.brep_builderapi_make_shape_Shape(aSolidMaker)

    // Threading: Create Surfaces
    def aCyl1 = nl.geom_cylindrical_surface_create(neckAx2, myNeckRadius * 0.99)
    def aCyl2 = nl.geom_cylindrical_surface_create(neckAx2, myNeckRadius * 1.05)

    // Threading: Define 2D Curves
    def aPnt = nl.make_gp_pnt2d(2.0 * Math.PI, myNeckHeight / 2.0d)
    def aDir = nl.make_gp_dir2d(2.0 * Math.PI, myNeckHeight / 4.0d)
    def anAx2d = nl.gp_ax_2d_new_pt_dir(aPnt, aDir)

    double aMajor = 2.0 * Math.PI
    double aMinor = myNeckHeight / 10

    def anEllipse1 = nl.geom2d_ellipse_create(anAx2d, aMajor, aMinor, 1)
    def anEllipse2 = nl.geom2d_ellipse_create(anAx2d, aMajor, aMinor / 4.0d, 1)

    def anArc1 = nl.geom2d_trimmed_curve_create(anEllipse1, 0, Math.PI, 1, 1)
    def anArc2 = nl.geom2d_trimmed_curve_create(anEllipse2, 0, Math.PI, 1, 1)


    def anEllipsePnt1 = nl.geom2d_ellipse_value(anEllipse1, 0.0d)
    def anEllipsePnt2 = nl.geom2d_ellipse_value(anEllipse1, Math.PI)
    def aSegment = nl.gce2d_make_segment(anEllipsePnt1, anEllipsePnt2)

    // Threading: Build Edges and Wiresnew
    def anEdge1OnSurf1 = nl.brep_builderapi_make_edge2(anArc1, aCyl1)
    def anEdge2OnSurf1 = nl.brep_builderapi_make_edge2(aSegment, aCyl1)
    def anEdge1OnSurf2 = nl.brep_builderapi_make_edge2(anArc2, aCyl2)
    def anEdge2OnSurf2 = nl.brep_builderapi_make_edge2(aSegment, aCyl2)

    def threadingWire1 = nl.brep_builderapi_make_wire_topo_ds_wire2p(anEdge1OnSurf1, anEdge2OnSurf1)
    def threadingWire2 = nl.brep_builderapi_make_wire_topo_ds_wire2p(anEdge1OnSurf2, anEdge2OnSurf2)

    nl.brep_lib_build_curves_3d(threadingWire1)
    nl.brep_lib_build_curves_3d(threadingWire2)

    // Create Threading
    def aTool = nl.brep_tool_thru_sections(1, 0, 1.0e-06d)
    nl.brep_tool_thru_sections_add_wire(aTool, threadingWire1)
    nl.brep_tool_thru_sections_add_wire(aTool, threadingWire2)
    nl.brep_tool_thru_sections_check_compatibility(aTool, 0)

    def myThreading = nl.brep_builderapi_make_shape_Shape(aTool)

    // Building the Resulting Compound
    def aRes = nl.topods_compound_create()
    def aBuilder = nl.brep_builder_create()
    nl.brep_builder_make_compound(aBuilder, aRes)
    nl.brep_builder_add(aBuilder, aRes, myBody)
    nl.brep_builder_add(aBuilder, aRes, myThreading)

    try (Arena arena = Arena.ofConfined()) {
        MemorySegment t = arena.allocateFrom('Test.png')
        nl.dumpShape(myBody, 512, 512, t)
    }

    nl.visualize(aRes)
}


mainBottle()

