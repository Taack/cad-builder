package org.taack.cad.dsl

import org.nativelib.NativeLib

import java.lang.foreign.MemorySegment

class Edge extends Vertice implements Selector {
    CadBuilder vertices(@DelegatesTo(value = Vertice, strategy = Closure.DELEGATE_FIRST) operations) {

    }


    CadBuilder center(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_FIRST) operations) {
        clockwiseLoc = [Loc.fromAPnt(NativeLib.gp_pnt_center_of_mass(currentFace))]
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
        def centerOfFace = Loc.fromAPnt(NativeLib.gp_pnt_center_of_mass(currentFace))
        def faceDir = Loc.fromADir(NativeLib.gp_dir_normal_to_face(currentFace))
        println "rect ($sx, $sy), faceDir = ${faceDir}, centerOfFace = ${centerOfFace}"
        def gCoords1 = Loc.globalLocFromLocal(centerOfFace, faceDir, new Loc2d(-sx/2.0,-sy/2.0))
        def gCoords2 = Loc.globalLocFromLocal(centerOfFace, faceDir, new Loc2d(sx/2.0,-sy/2.0))
        def gCoords3 = Loc.globalLocFromLocal(centerOfFace, faceDir, new Loc2d(sx/2.0,sy/2.0))
        def gCoords4 = Loc.globalLocFromLocal(centerOfFace, faceDir, new Loc2d(-sx/2.0,sy/2.0))

        clockwiseLoc = [gCoords1, gCoords2, gCoords3, gCoords4]
        println "rect $clockwiseLoc"
        operations.delegate = this
        operations.call()
        this as CadBuilder
    }

    private void holeHelper(BigDecimal diameter, Loc loc, MemorySegment dir) {
        currentShape = NativeLib.make_hole(currentShape,
                NativeLib.gp_ax1_new(
                        loc.toGpPnt(),
                        dir
                ), diameter.doubleValue())
        this as CadBuilder
    }

    void hole(BigDecimal diameter) {
        def gpDir = NativeLib.gp_dir_normal_to_face(currentFace)
        println "hole $diameter Dir: ${Loc.fromADir(gpDir)}"
        clockwiseLoc.each {
            println "holeHelper $diameter, Loc: $it, Dir: ${Loc.fromADir(gpDir)}"
            holeHelper(diameter, it, gpDir)
        }
    }

    void counterboredHole(BigDecimal holeDiameter, BigDecimal diameter, BigDecimal depth) {
        hole(diameter)
    }

    void countersunkHole() {

    }
}
