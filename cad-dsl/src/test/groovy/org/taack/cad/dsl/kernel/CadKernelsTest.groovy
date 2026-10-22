package org.taack.cad.dsl.kernel

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertThrows
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
class CadKernelsTest {
    @Test
    void "Without kernel, building a model tells which artifact is missing"() {
        def e = assertThrows(IllegalStateException) { cd() }
        assertTrue(e.message.contains('cad-dsl-occt'))
    }
}
