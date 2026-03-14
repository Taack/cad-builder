package org.taack.cad.dsl.builder

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import static org.taack.cad.dsl.builder.CadBuilder.cb

@CompileStatic
class BottleTest {

    @CompileStatic
    static void mainBottle() {
        double myWidth = 50.0
        double myHeight = 70.0
        double myThickness = 30.0

        Vec v1 = new Vec(-myWidth / 2, 0, 0)
        Vec v11 = new Vec(0, -myThickness / 4, 0)
        Vec v2 = v1 + v11
        Vec v3 = v11 * 2
        Vec v4 = -v1 + v11
        Vec v5 = -v1


        cb().from(v1)
                .edge(v2)
                .arc(v4, v3)
                .edge(v5).toWire().toFace().prism().display()

        cb().from(v1)
                .edge(v2)
                .arc(v4, v3)
                .edge(v5).toWire().mirror(new Vec(), new Vec(1,0,0)).toFace().prism().display()

        cb().from(v1)
                .edge(v2)
                .arc(v4, v3)
                .edge(v5).toWire().mirror2(new Vec(), new Vec(1,0,0)).toFace().prism().display()

        MemorySegment aWire = cb().from(v1)
                .edge(v2)
                .arc(v4, v3)
                .edge(v5).toWire().mirror(new Vec(), new Vec(1,0,0)).toShape()


//        def aPnt1 = new_gp_Pnt__x_y_z(-myWidth / 2d, 0, 0)
//        def aPnt2 = new_gp_Pnt__x_y_z(-myWidth / 2d, -myThickness / 4d, 0)
//        def aPnt3 = new_gp_Pnt__x_y_z(0, -myThickness / 2d, 0)
//        def aPnt4 = new_gp_Pnt__x_y_z(myWidth / 2d, -myThickness / 4d, 0)
//        def aPnt5 = new_gp_Pnt__x_y_z(myWidth / 2d, 0, 0)
//
//        def anArcOfCircle = handle_Geom_TrimmedCurve__GC_MakeArcOfCircle_p1_p2_p3(aPnt2, aPnt3, aPnt4)
//        def aSegment1 = handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(aPnt1, aPnt2)
//        def aSegment2 = handle_Geom_TrimmedCurve__GC_MakeSegment__p1_p2(aPnt4, aPnt5)

//        println "Profile: Define the Topology"
//        def anEdge1 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(aSegment1)
//        def anEdge2 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(anArcOfCircle)
//        def anEdge3 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve(aSegment2)
//        def aWire = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2_e3(anEdge1, anEdge2, anEdge3)

        println "Complete Profile"
//        def xAxis = gp__OX()
//        def aTrsf = new_gp_Trsf()
//        _gp_Trsf__SetMirror__gp_Ax1(aTrsf, xAxis)
//
//        def aBRepTrsf = new_BRepBuilderAPI_Transform__TopoDS_Shape_gp_Trsf(aWire, aTrsf)
//        def aMirroredShape = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aBRepTrsf)
//        def aMirroredWire = ref_TopoDS__Wire__TopoDS_Shape(aMirroredShape)

//        def mkWire = new_BRepBuilderAPI_MakeWire()
//        _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aWire)
//        _BRepBuilderAPI_MakeWire__Add__TopoDS_Wire(mkWire, aMirroredWire)
//        def myWireProfile = ref_TopoDS_Wire__BRepBuilderAPI_MakeWire__Wire(mkWire)

        println "Body: Prism the Profile"



        def myFaceProfile = new_TopoDS_Face__BRepBuilderAPI_MakeFace__TopoDS_Wire(aWire)
        def aPrismVec = new_gp_Vec__x_y_z(0d, 0d, myHeight)
        def myBody = new_TopoDS_Shape__BRepPrimAPI_MakePrism__TopoDS_Face_gp_Vec(myFaceProfile, aPrismVec)

        visualize(myBody)

        println "Body: Apply Fillets"
        def mkFillet = new_BRepFilletAPI_MakeFillet__TopoDS_Shape(myBody)
        def anEdgeExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(myBody, ShapeEnum.TopAbs_EDGE.index, ShapeEnum.TopAbs_SHAPE.index)
        while (_TopExp_Explorer__More(anEdgeExplorer)) {
            def anEdge = ref_TopoDS_Edge__TopoDS_Shape(new_TopoDS_Shape__TopExp_Explorer__Current(anEdgeExplorer))
            //Add edge to fillet algorithm
            _BRepFilletAPI_MakeFillet__Add__radius_TopoDS_Edge(mkFillet, myThickness / 12d, anEdge)
            _TopExp_Explorer__Next(anEdgeExplorer)
        }

        myBody = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(mkFillet)

        println "Body: Add the Neck"
        def neckLocation = new_gp_Pnt__x_y_z(0d, 0d, myHeight)
        def neckAxis = new_gp_Dir_DZ()
        def neckAx2 = new_gp_Ax2__gp_Pnt_gp_Dir(neckLocation, neckAxis)

        double myNeckRadius = myThickness / 4
        double myNeckHeight = myHeight / 10

        def MKCylinder = new_BRepPrimAPI_MakeCylinder__gp_Ax2_r_h(neckAx2, myNeckRadius, myNeckHeight)
        def myNeck = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(MKCylinder)

        myBody = new_TopoDS_Shape__brep_algoapi_fuse__s1_s2(myBody, myNeck)

        println "Body: Create a Hollowed Solid"
        double zMax = -1
        def faceToRemove = new_TopoDS_Face()

        for (def aFaceExplorer = new_TopExp_Explorer__TopoDS_Shape_ToFind_ToAvoid(myBody, ShapeEnum.TopAbs_FACE.ordinal(), ShapeEnum.TopAbs_SHAPE.ordinal()); _TopExp_Explorer__More(aFaceExplorer); _TopExp_Explorer__Next(aFaceExplorer)) {
            def aFace = new_TopoDS_Face__TopExp_Explorer__Current(aFaceExplorer)
            def aSurface = handle_Geom_Surface__TopoDS_Face(aFace)
            if (int_Geom_Surface__is__Geom_Plane(aSurface) == 1) {
                def aPlan = handle_Geom_Plane__handle_Geom_Surface(aSurface)
                def aPnt = new_gp_Pnt__Geom_Plane(aPlan)
                double aZ = gp_Pnt__Z(aPnt)
                if (aZ > zMax) {
                    zMax = aZ
                    faceToRemove = aFace
                }
            }
        }

        def facesToRemove = new_TopTools_ListOfShape()
        _TopTools_ListOfShape__Append__TopoDS_Shape(facesToRemove, faceToRemove)
        def aSolidMaker = new_BRepOffsetAPI_MakeThickSolid()
        _BRepOffsetAPI_MakeThickSolid__MakeThickSolidByJoin__TopoDS_Shape_TopTools_ListOfShape_thickness_tol(aSolidMaker, myBody, facesToRemove, -myThickness / 50d, 0.001d)

        myBody = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aSolidMaker)

        // Threading: Create Surfaces
        def aCyl1 = handle_Geom_CylindricalSurface__ax2_radius(neckAx2, myNeckRadius * 0.99)
        def aCyl2 = handle_Geom_CylindricalSurface__ax2_radius(neckAx2, myNeckRadius * 1.05)

        // Threading: Define 2D Curves
        def aPnt = new_gp_Pnt2d__x_y(2.0 * Math.PI, myNeckHeight / 2.0d)
        def aDir = new_gp_Dir2d__x_y(2.0 * Math.PI, myNeckHeight / 4.0d)
        def anAx2d = new_gp_Ax2d__pt_dir(aPnt, aDir)

        double aMajor = 2.0 * Math.PI
        double aMinor = myNeckHeight / 10

        def anEllipse1 = handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(anAx2d, aMajor, aMinor, 1)
        def anEllipse2 = handle_Geom2d_Ellipse__a2_majorRadius_minorRadius_sense(anAx2d, aMajor, aMinor / 4.0d, 1)

        def anArc1 = handle_Geom2d_TrimmedCurve__curve_u1_u2(anEllipse1, 0, Math.PI, 1, 1)
        def anArc2 = handle_Geom2d_TrimmedCurve__curve_u1_u2(anEllipse2, 0, Math.PI, 1, 1)


        def anEllipsePnt1 = new_gp_Pnt2d__Geom2d_Curve__Value__u(anEllipse1, 0.0d)
        def anEllipsePnt2 = new_gp_Pnt2d__Geom2d_Curve__Value__u(anEllipse1, Math.PI)
        def aSegment = handle_Geom2d_TrimmedCurve__GCE2d_MakeSegment__p1_p2(anEllipsePnt1, anEllipsePnt2)

        // Threading: Build Edges and Wiresnew
        def anEdge1OnSurf1 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve_Geom_Surface(anArc1, aCyl1)
        def anEdge2OnSurf1 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve_Geom_Surface(aSegment, aCyl1)
        def anEdge1OnSurf2 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve_Geom_Surface(anArc2, aCyl2)
        def anEdge2OnSurf2 = new_TopoDS_Edge__BRepBuilderAPI_MakeEdge__Geom_Curve_Geom_Surface(aSegment, aCyl2)

        def threadingWire1 = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(anEdge1OnSurf1, anEdge2OnSurf1)
        def threadingWire2 = new_TopoDS_Wire__BRepBuilderAPI_MakeWire__TopoDS_Edge1_e2(anEdge1OnSurf2, anEdge2OnSurf2)

        _BRepLib__BuildCurves3d__TopoDS_Shape(threadingWire1)
        _BRepLib__BuildCurves3d__TopoDS_Shape(threadingWire2)

        // Create Threading
        def aTool = new_BRepOffsetAPI_ThruSections__isSolid_ruled_pres3d(1, 0, 1.0e-06d)
        _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(aTool, threadingWire1)
        _BRepOffsetAPI_ThruSections__AddWire__TopoDS_Wire(aTool, threadingWire2)
        _BRepOffsetAPI_ThruSections__CheckCompatibility__bool(aTool, 0)

        def myThreading = new_TopoDS_Shape__Shape__BRepBuilderAPI_MakeShape(aTool)

        // Building the Resulting Compound
        def aRes = new_TopoDS_Compound()
        def aBuilder = new_BRep_Builder()
        _TopoDS_Builder__MakeCompound__TopoDS_Compound(aBuilder, aRes)
        _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(aBuilder, aRes, myBody)
        _TopoDS_Builder__Add__resTopoDS_Shape_toAddTopoDS_Shape(aBuilder, aRes, myThreading)

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment t = arena.allocateFrom('Test.png')
            dumpShape(myBody, 512, 512, t)
        }

        visualize(aRes)
    }

    @Test
    void "Make Bottle Normal API"() {
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
