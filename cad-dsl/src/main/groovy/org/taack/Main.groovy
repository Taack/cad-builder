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




@CompileStatic
static void main(String[] args) {
    double myWidth = 1.0
    double myHeight = 1.0
    double myThickness = 0.1
    def aPnt1 = nl.make_gp_pnt(-myWidth / 2d, 0, 0)
    def aPnt2 = nl.make_gp_pnt(-myWidth / 2d, -myThickness / 4d, 0)
    def aPnt3 = nl.make_gp_pnt(0, -myThickness / 2d, 0)
    def aPnt4 = nl.make_gp_pnt(myWidth / 2d, -myThickness / 4d, 0)
    def aPnt5 = nl.make_gp_pnt(myWidth / 2d, 0, 0)

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
    def aMirroredShape = nl.brep_builderapi_transform_shape(aBRepTrsf)
    def aMirroredWire = nl.topo_ds_wire(aMirroredShape)

    def mkWire = nl.brep_builderapi_make_wire_new()
    nl.brep_builderapi_wire_add(mkWire, aWire)
    nl.brep_builderapi_wire_add(mkWire, aMirroredWire)
    def myWireProfile = nl.brep_builderapi_make_wire_topo_ds_wire2(mkWire)

    println "Body: Prism the Profile"

    def myFaceProfile = nl.brep_builderapi_make_face(myWireProfile)
    def aPrismVec = nl.make_gp_vec(0d, 0d, myHeight)
    def myBody = nl.brep_primapi_make_prism(myFaceProfile, aPrismVec)

    println "Body: Apply Fillets"

    def mkFillet = nl.brep_filletapi_make_fillet(myBody)
    def anEdgeExplorer = nl.top_exp_explorer(myBody, TopAbs_ShapeEnum.TopAbs_EDGE.index, TopAbs_ShapeEnum.TopAbs_SHAPE.index)
    while (nl.top_exp_explorer_more(anEdgeExplorer)) {
        def anEdge = nl.topo_ds_edge(nl.top_exp_explorer_current(anEdgeExplorer))
        //Add edge to fillet algorithm
        nl.brep_filletapi_make_fillet_add(mkFillet, myThickness / 12d, anEdge)
        nl.top_exp_explorer_next(anEdgeExplorer)
    }

    myBody = nl.brep_filletapi_make_fillet_shape(mkFillet)

    println "Body: Add the Neck"

    def neckLocation = nl.make_gp_pnt(0d, 0d, myHeight)
    def neckAxis = nl.gp_dz()
    def neckAx2 = nl.gp_ax2(neckLocation, neckAxis)

    double myNeckRadius = myThickness / 4
    double myNeckHeight = myHeight / 10

    def MKCylinder = nl.brep_primapi_make_cylinder(neckAx2, myNeckRadius, myNeckHeight)
    def myNeck = nl.brep_primapi_make_cylinder_shape(MKCylinder)

    myBody = nl.brep_algoapi_fuse(myBody, myNeck)

    nl.visualize(myBody)
}