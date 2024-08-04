package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.nativelib.NativeLib as nl

@CompileStatic
class Face extends Edge implements Selector {

    Face() {
    }

    CadBuilder hole(BigDecimal diameter) {
        currentShape = nl.make_hole(currentShape,
                nl.gp_ax1_new(
                        nl.gp_pnt_center_of_mass(currentFace),
                        nl.gp_dir_normal_to_face(currentFace)
                ), diameter.doubleValue())
        this as CadBuilder
    }

    Face rect(BigDecimal x, BigDecimal y, boolean forConstruction = false) {

    }

    Face edges(@DelegatesTo(value = Edge, strategy = Closure.DELEGATE_ONLY) operations) {

    }
}
