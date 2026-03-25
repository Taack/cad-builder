package org.taack.cad.dsl

import groovy.transform.CompileStatic

@CompileStatic
trait CadDslBase {

    ICadDslVisitor visitor

    CadDsl toCadDsl() {
        return new CadDsl(visitor: visitor)
    }
}
