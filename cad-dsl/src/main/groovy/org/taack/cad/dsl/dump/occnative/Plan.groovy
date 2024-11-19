package org.taack.cad.dsl.dump.occnative

import groovy.transform.CompileStatic
import org.taack.cad.dsl.builder.ICad
import org.taack.cad.dsl.builder.IPlane
import org.taack.cad.dsl.builder.IProfile
import org.taack.cad.dsl.builder.Vec

@CompileStatic
class Plan extends Profile implements IPlane {
    @Override
    IPlane profile(@DelegatesTo(value = IProfile, strategy = Closure.DELEGATE_FIRST) Object operations) {
        return null
    }

    @Override
    ICad pad(Vec vec) {
        return null
    }

    @Override
    ICad pad(BigDecimal length) {
        return null
    }

    @Override
    ICad revolution(Vec vec) {
        return null
    }

    @Override
    ICad pocket(BigDecimal length) {
        return null
    }

    @Override
    ICad hole() {
        return null
    }
}
