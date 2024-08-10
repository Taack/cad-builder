package org.taack.cad.dsl

import org.nativelib.NativeLib as nl

import java.lang.foreign.MemorySegment

class Edge extends Vertice implements Selector {

    private Vec origin = new Vec(0.0)
    private MemorySegment wire = nl.brep_builderapi_make_wire()

    /**
     * Initial position of a new wire
     *
     * @param newOrigin
     * @return
     */
    CadBuilder from(Vec newOrigin) {
        wire = nl.brep_builderapi_make_wire()
        origin = newOrigin
        this as CadBuilder
    }

    /**
     * Add an Edge to the current wire
     * @param to
     * @return
     */
    CadBuilder edge(Vec to) {
        nl.brep_builderapi_wire_add(wire, nl.brep_builderapi_make_edge_from_pts(origin.toGpPnt(), to.toGpPnt()))
        this as CadBuilder
    }

    /**
     * Center of current face
     * @param operations
     * @return
     */
    CadBuilder center(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) operations) {
        clockwiseLoc = [Vec.fromAPnt(nl.gp_pnt_center_of_mass(currentFace))]
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
        def centerOfFace = Vec.fromAPnt(nl.gp_pnt_center_of_mass(currentFace))
        def faceDir = Vec.fromADir(nl.gp_dir_normal_to_face(currentFace))
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
        currentShape = nl.make_hole(currentShape,
                nl.gp_ax1_new(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), from.doubleValue(), to.doubleValue())
        this as CadBuilder
    }

    private void holeHelper(Vec loc, MemorySegment dir, BigDecimal diameter, BigDecimal length) {
        currentShape = nl.make_hole_blind(currentShape,
                nl.gp_ax1_new(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue(), length.doubleValue())
        this as CadBuilder
    }

    void hole(BigDecimal diameter, BigDecimal length) {
        def gpDir = nl.gp_dir_normal_to_face(currentFace)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, length)
        }
    }

    void hole(BigDecimal diameter) {
        def gpDir = nl.gp_dir_normal_to_face(currentFace)
        println "hole $diameter Dir: ${Vec.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Vec.fromADir(gpDir)}"
            holeHelper(it, gpDir, diameter, 0.1, 0.0)
        }
    }
}
