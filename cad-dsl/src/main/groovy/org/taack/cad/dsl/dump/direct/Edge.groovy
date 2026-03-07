package org.taack.cad.dsl.dump.direct

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.Vec
import org.taack.cad.dsl.builder.Vec2d
import org.taack.occt.NativeLib as nl

import java.lang.foreign.MemorySegment

@CompileStatic
class Edge extends Vertice implements Selector {

    private List<Vec> edges = []
    private List<Vec> arcCenter = []
    private List<Integer> arcIndex = []

    /**
     * Initial position of a new wire
     *
     * @param newOrigin
     * @return
     */
    CadBuilder from(Vec2d newOrigin) {
        from(new Vec(newOrigin))
    }

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

    CadBuilder edge(Vec2d toPosition) {
        edge(new Vec(toPosition))
    }

        /**
     * Add an Arc to the current wire
     * @param toPosition
     * @param radius
     * @return
     */
    CadBuilder arc(Vec toPosition, Vec center) {
        edges.add toPosition
        arcCenter.add center
        arcIndex.add(edges.size() - 1)
        this as CadBuilder
    }

    CadBuilder arc(Vec2d toPosition, Vec2d center) {
        arc(new Vec(toPosition), new Vec(center))
    }

    CadBuilder toWire() {
        Vec fromLocal = currentLoc
        def wireNative = nl.brep_builderapi_makewire_new()

        int index = 0
        Iterator<Integer> arcIndexIt = arcIndex.size() > 0 ? arcIndex.iterator() : null
        Iterator<Vec> arcCenterIt = arcCenter.size() > 0 ? arcCenter.iterator() : null
        Integer arcIndexCur = arcIndex.size() > 0 ? arcIndexIt.next() : null

        println "arcIndexCur: $arcIndexCur, ${arcIndex}"

        for (Vec to : edges) {
            if (arcIndexCur != null && index == arcIndexCur) {
                def arcCenter = arcCenterIt.next()
                arcIndexCur = arcIndexIt.hasNext() ? arcIndexIt.next() : 0
                println "Arc from: $fromLocal, to: $to, radius: $arcCenter, arcIndexCur(next): $arcIndexCur"
                def arcNative = nl.gc_make_arc_of_circle(fromLocal.toGpPnt(), arcCenter.toGpPnt(), to.toGpPnt())
                def arcEdge = nl.brep_builderapi_make_edge(arcNative)
                fromLocal = to
                nl.brep_builderapi_wire_add_edge(wireNative, arcEdge)
            } else {
                println "Edge from: $fromLocal, to: $to"
                def edgeNative = nl.brep_builderapi_make_edge_from_pts(fromLocal.toGpPnt(), to.toGpPnt())
                fromLocal = to
                nl.brep_builderapi_wire_add_makeedge(wireNative, edgeNative)
            }
            index++

        }
        wireNatives.add wireNative
        this as CadBuilder
    }

    /**
     * Turns Edges wire into face
     * @return
     */
    CadBuilder toFace(Vec plan = new Vec(0.0d, 0.0d, 1.0d)) {
        def pXY = plan.toGpPln()
        def aFace = nl.brep_builderapi_make_face_from_plane(pXY)
        def builder = nl.brep_builder_create()
//        nl.brep_builder_add_wire(builder, aFace, nl.brep_builderapi_make_wire_topo_ds_wire2(currentWireNative))
//        nl.brep_builder_add_wire(builder, aFace, currentWireNative)

        if (wireNatives.size() > 0) {
            wireNatives.eachWithIndex { MemorySegment it, int i ->
                nl.brep_builder_add_wire(builder, aFace, nl.brep_builderapi_make_wire_topo_ds_wire2(it))
            }
        }
        currentFaceNative = aFace
        this as CadBuilder
    }
    /**
     * Center of current face
     * @param operations
     * @return
     */
    CadBuilder center(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) Closure operations) {
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
    CadBuilder rect(BigDecimal sx, BigDecimal sy, @DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) Closure operations) {
        def centerOfFace = Vec.fromAPnt(nl.gp_pnt_center_of_mass(currentFaceNative))
        def faceDir = Vec.fromADir(nl.gp_dir_normal_to_face(currentFaceNative))
        println "rect ($sx, $sy), faceDir = ${faceDir}, centerOfFace = ${centerOfFace}"
        def gCoords1 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx / 2.0, -sy / 2.0))
        def gCoords2 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx / 2.0, -sy / 2.0))
        def gCoords3 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(sx / 2.0, sy / 2.0))
        def gCoords4 = Vec.globalLocFromLocal(centerOfFace, faceDir, new Vec2d(-sx / 2.0, sy / 2.0))

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
