package org.taack.cad.dsl.generator.direct

import org.nativelib.NativeLib as nl
import org.taack.cad.dsl.Selector
import org.taack.cad.dsl.Vec
import org.taack.cad.dsl.Vec2d

import java.lang.foreign.MemorySegment

class Edge extends Vertice implements Selector {

    private List<Vec> edges = []
    private MemorySegment wireNative

    /**
     * Initial position of a new wire
     *
     * @param newOrigin
     * @return
     */
    CadBuilder from(Vec newOrigin) {
        edges = []
        currentLoc = newOrigin
        this as CadBuilder
    }

    /**
     * Add an Edge to the current wire
     * @param toPosition
     * @return
     */
    CadBuilder edge(Vec toPosition) {
        edges.add toPosition
        this as CadBuilder
    }

    CadBuilder toWire() {
//        def listOfShapeNative = nl.top_tools_list_of_shape()
        Vec fromLocal = currentLoc
        wireNative = nl.brep_builderapi_makewire_new()

        for (Vec to : edges) {
            println "Edge from: $fromLocal, to: $to"
            def edgeNative = nl.brep_builderapi_make_edge_from_pts(fromLocal.toGpPnt(), to.toGpPnt())
            fromLocal = to
            nl.brep_builderapi_wire_add_makeedge(wireNative, edgeNative)
        }
        this as CadBuilder
    }

    /**
     * Turns Edges wire into face
     * @return
     */
    CadBuilder toFace() {
        currentFaceNative = nl.brep_builderapi_make_face_from_makewire(wireNative)
        this as CadBuilder
    }
    /**
     * Center of current face
     * @param operations
     * @return
     */
    CadBuilder center(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) operations) {
        clockwiseLoc = [Vec.fromAPnt(nl.gp_pnt_center_of_mass(currentFaceNative))]
        operations.delegate = this
        operations.call()
        this as CadBuilder
    }

    /**
     * Construction Rect. Center corresponds to currentFace center.
     * @param sx Size along x
     * @param sy Size along y
     * @param operations
     * @return Face
     */
    CadBuilder rect(BigDecimal sx, BigDecimal sy, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) operations) {
        def centerOfFace = Vec.fromAPnt(nl.gp_pnt_center_of_mass(currentFaceNative))
        def faceDir = Vec.fromADir(nl.gp_dir_normal_to_face(currentFaceNative))
        println "rect ($sx, $sy), faceDir = ${faceDir}, centerOfFace = ${centerOfFace}"
        def gCoords1 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx/2.0,-sy/2.0))
        def gCoords2 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx/2.0,-sy/2.0))
        def gCoords3 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx/2.0,sy/2.0))
        def gCoords4 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx/2.0,sy/2.0))

        clockwiseLoc = [gCoords1, gCoords2, gCoords3, gCoords4]
        println "rect $clockwiseLoc"
        operations.delegate = this
        operations.call()
        this as CadBuilder
    }

    private void holeHelper(Vec loc, MemorySegment dir, BigDecimal diameter, BigDecimal from, BigDecimal to) {
        currentShapeNative = nl.make_hole(currentShapeNative,
                nl.gp_ax1_new(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), from.doubleValue(), to.doubleValue())
        this as CadBuilder
    }

    private void holeHelper(Vec loc, MemorySegment dir, BigDecimal diameter, BigDecimal length) {
        currentShapeNative = nl.make_hole_blind(currentShapeNative,
                nl.gp_ax1_new(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), length.doubleValue())
        this as CadBuilder
    }

    void hole(BigDecimal diameter, BigDecimal length) {
        def gpDir = nl.gp_dir_normal_to_face(currentFaceNative)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, length)
        }
    }

    void hole(BigDecimal diameter) {
        def gpDir = nl.gp_dir_normal_to_face(currentFaceNative)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, 0.1, 0.0)
        }
    }

    CadBuilder cut(CadBuilder other) {
        MemorySegment trsf = nl.gp_trsf()
        println "cut currentLoc = $currentLoc, other.currentLoc = ${other.currentLoc}"
        nl.gp_trsf_set_translation(trsf, (new Vec(0.0) - currentLoc).toGpVec())
        def toolNative = nl.brep_builderapi_transform_shape(other.currentShapeNative, trsf, 1)
        def cutNative = nl.brep_algoapi_cut_ds_shape(currentShapeNative, toolNative)
        currentShapeNative = cutNative
        this as CadBuilder
    }

}
