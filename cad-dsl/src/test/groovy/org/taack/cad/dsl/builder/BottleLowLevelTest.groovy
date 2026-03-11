package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.taack.occt.NativeLib

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

@CompileStatic
class BottleLowLevelTest {

    @CompileStatic
    static void mainBottle() {
        double myWidth = 50.0
        double myHeight = 70.0
        double myThickness = 30.0
        def aPnt1 = NativeLib.gp_pnt_new(-myWidth / 2d, 0, 0)
        def aPnt2 = NativeLib.gp_pnt_new(-myWidth / 2d, -myThickness / 4d, 0)
        def aPnt3 = NativeLib.gp_pnt_new(0, -myThickness / 2d, 0)
        def aPnt4 = NativeLib.gp_pnt_new(myWidth / 2d, -myThickness / 4d, 0)
        def aPnt5 = NativeLib.gp_pnt_new(myWidth / 2d, 0, 0)

        def anArcOfCircle = NativeLib.gc_make_arc_of_circle(aPnt2, aPnt3, aPnt4)
        def aSegment1 = NativeLib.gc_make_segment(aPnt1, aPnt2)
        def aSegment2 = NativeLib.gc_make_segment(aPnt4, aPnt5)

        println "Profile: Define the Topology"

        def anEdge1 = NativeLib.brep_builderapi_make_edge(aSegment1)
        def anEdge2 = NativeLib.brep_builderapi_make_edge(anArcOfCircle)
        def anEdge3 = NativeLib.brep_builderapi_make_edge(aSegment2)
        def aWire = NativeLib.brep_builderapi_make_wire_topo_ds_wire(anEdge1, anEdge2, anEdge3)

        println "Complete Profile"

        def xAxis = NativeLib.gp_ox()
        def aTrsf = NativeLib.gp_trsf()

        NativeLib.gp_trsf_set_mirror(aTrsf, xAxis)
        def aBRepTrsf = NativeLib.brep_builderapi_transform(aWire, aTrsf)
        def aMirroredShape = NativeLib.brep_builderapi_make_shape_Shape(aBRepTrsf)
        def aMirroredWire = NativeLib.topo_ds_wire(aMirroredShape)

        def mkWire = NativeLib.brep_builderapi_makewire_new()
        NativeLib.brep_builderapi_wire_add(mkWire, aWire)
        NativeLib.brep_builderapi_wire_add(mkWire, aMirroredWire)
        def myWireProfile = NativeLib.brep_builderapi_make_wire_topo_ds_wire2(mkWire)

        println "Body: Prism the Profile"

        def myFaceProfile = NativeLib.brep_builderapi_make_face_from_wire(myWireProfile)
        def aPrismVec = NativeLib.gp_vec_new(0d, 0d, myHeight)
        def myBody = NativeLib.brep_primapi_make_prism(myFaceProfile, aPrismVec)

        println "Body: Apply Fillets"

        def mkFillet = NativeLib.brep_filletapi_make_fillet(myBody)
        def anEdgeExplorer = NativeLib.top_exp_explorer(myBody, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
        while (NativeLib.top_exp_explorer_more(anEdgeExplorer)) {
            def anEdge = NativeLib.topo_ds_edge(NativeLib.top_exp_explorer_current(anEdgeExplorer))
            //Add edge to fillet algorithm
            NativeLib.brep_filletapi_make_fillet_add(mkFillet, myThickness / 12d, anEdge)
            NativeLib.top_exp_explorer_next(anEdgeExplorer)
        }

        myBody = NativeLib.brep_builderapi_make_shape_Shape(mkFillet)

        println "Body: Add the Neck"

        def neckLocation = NativeLib.gp_pnt_new(0d, 0d, myHeight)
        def neckAxis = NativeLib.gp_dz()
        def neckAx2 = NativeLib.gp_ax2(neckLocation, neckAxis)

        double myNeckRadius = myThickness / 4
        double myNeckHeight = myHeight / 10

        def MKCylinder = NativeLib.brep_primapi_make_cylinder(neckAx2, myNeckRadius, myNeckHeight)
        def myNeck = NativeLib.brep_builderapi_make_shape_Shape(MKCylinder)

        myBody = NativeLib.brep_algoapi_fuse(myBody, myNeck)

        println "Body: Create a Hollowed Solid"

        double zMax = -1
        def faceToRemove = NativeLib.topods_face_new()

        for (def aFaceExplorer = NativeLib.top_exp_explorer(myBody, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal()); NativeLib.top_exp_explorer_more(aFaceExplorer); NativeLib.top_exp_explorer_next(aFaceExplorer)) {
            def aFace = NativeLib.top_exp_explorer_current_face(aFaceExplorer)
            def aSurface = NativeLib.brep_tool_surface(aFace)
            if (NativeLib.geom_surface_is_geom_plane(aSurface) == 1) {
                def aPlan = NativeLib.downcast_geom_plane(aSurface)
                def aPnt = NativeLib.geom_plane_location(aPlan)
                double aZ = NativeLib.gp_pnt_z(aPnt)
                if (aZ > zMax) {
                    zMax = aZ
                    faceToRemove = aFace
                }
            }
        }

        def facesToRemove = NativeLib.top_tools_list_of_shape()
        NativeLib.top_tools_list_of_shape_append(facesToRemove, faceToRemove)
        def aSolidMaker = NativeLib.brep_offset_api_make_thick_solid()
        NativeLib.brep_offset_api_make_thick_solid_join(aSolidMaker, myBody, facesToRemove, -myThickness / 50d, 0.001d)

        myBody = NativeLib.brep_builderapi_make_shape_Shape(aSolidMaker)

        // Threading: Create Surfaces
        def aCyl1 = NativeLib.geom_cylindrical_surface_create(neckAx2, myNeckRadius * 0.99)
        def aCyl2 = NativeLib.geom_cylindrical_surface_create(neckAx2, myNeckRadius * 1.05)

        // Threading: Define 2D Curves
        def aPnt = NativeLib.make_gp_pnt2d(2.0 * Math.PI, myNeckHeight / 2.0d)
        def aDir = NativeLib.make_gp_dir2d(2.0 * Math.PI, myNeckHeight / 4.0d)
        def anAx2d = NativeLib.gp_ax_2d_new_pt_dir(aPnt, aDir)

        double aMajor = 2.0 * Math.PI
        double aMinor = myNeckHeight / 10

        def anEllipse1 = NativeLib.geom2d_ellipse_create(anAx2d, aMajor, aMinor, 1)
        def anEllipse2 = NativeLib.geom2d_ellipse_create(anAx2d, aMajor, aMinor / 4.0d, 1)

        def anArc1 = NativeLib.geom2d_trimmed_curve_create(anEllipse1, 0, Math.PI, 1, 1)
        def anArc2 = NativeLib.geom2d_trimmed_curve_create(anEllipse2, 0, Math.PI, 1, 1)


        def anEllipsePnt1 = NativeLib.geom2d_ellipse_value(anEllipse1, 0.0d)
        def anEllipsePnt2 = NativeLib.geom2d_ellipse_value(anEllipse1, Math.PI)
        def aSegment = NativeLib.gce2d_make_segment(anEllipsePnt1, anEllipsePnt2)

        // Threading: Build Edges and Wiresnew
        def anEdge1OnSurf1 = NativeLib.brep_builderapi_make_edge2(anArc1, aCyl1)
        def anEdge2OnSurf1 = NativeLib.brep_builderapi_make_edge2(aSegment, aCyl1)
        def anEdge1OnSurf2 = NativeLib.brep_builderapi_make_edge2(anArc2, aCyl2)
        def anEdge2OnSurf2 = NativeLib.brep_builderapi_make_edge2(aSegment, aCyl2)

        def threadingWire1 = NativeLib.brep_builderapi_make_wire_topo_ds_wire2p(anEdge1OnSurf1, anEdge2OnSurf1)
        def threadingWire2 = NativeLib.brep_builderapi_make_wire_topo_ds_wire2p(anEdge1OnSurf2, anEdge2OnSurf2)

        NativeLib.brep_lib_build_curves_3d(threadingWire1)
        NativeLib.brep_lib_build_curves_3d(threadingWire2)

        // Create Threading
        def aTool = NativeLib.brep_tool_thru_sections(1, 0, 1.0e-06d)
        NativeLib.brep_tool_thru_sections_add_wire(aTool, threadingWire1)
        NativeLib.brep_tool_thru_sections_add_wire(aTool, threadingWire2)
        NativeLib.brep_tool_thru_sections_check_compatibility(aTool, 0)

        def myThreading = NativeLib.brep_builderapi_make_shape_Shape(aTool)

        // Building the Resulting Compound
        def aRes = NativeLib.topods_compound_create()
        def aBuilder = NativeLib.brep_builder_create()
        NativeLib.brep_builder_make_compound(aBuilder, aRes)
        NativeLib.brep_builder_add(aBuilder, aRes, myBody)
        NativeLib.brep_builder_add(aBuilder, aRes, myThreading)

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment t = arena.allocateFrom('Test.png')
            NativeLib.dumpShape(myBody, 512, 512, t)
        }

        NativeLib.visualize(aRes)
    }

    @Test
    void "Make Bottle Using Low Level API"() {
        mainBottle()
    }
//    @Test
//    MemorySegment "Profile: Define the Topology"() {
//
//        Vec2d aPnt1 = new Vec2d(-myWidth / 2, 0)
//        Vec2d aPnt2 = new Vec2d(-myWidth / 2d, -myThickness / 4d)
//        Vec2d aPnt3 = new Vec2d(0, -myThickness / 2)
//        Vec2d aPnt4 = new Vec2d(myWidth / 2, -myThickness / 4)
//        Vec2d aPnt5 = new Vec2d(myWidth / 2d, 0)
//
////        cb().from(aPnt1)
////                .edge(aPnt2)
//
//    }

}
