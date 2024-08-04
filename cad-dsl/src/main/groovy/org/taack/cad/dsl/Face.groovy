package org.taack.cad.dsl

import groovy.transform.CompileStatic

import java.lang.foreign.MemorySegment
import org.nativelib.NativeLib as nl

@CompileStatic
class Face implements Selector {

    Face() {
    }

    CadBuilder hole(BigDecimal diameter) {
        currentShape = nl.make_hole(currentShape,
                nl.gp_ax1_new(nl.make_gp_pnt(
                        currentLoc.x.doubleValue(),
                        currentLoc.y.doubleValue(),
                        currentLoc.z.doubleValue(),
                ), nl.gp_dir_normal_to_face(currentFace)
                ), diameter.doubleValue())
        this as CadBuilder
    }

    Face rect(BigDecimal x, BigDecimal y, boolean forConstruction = false) {

    }


}
