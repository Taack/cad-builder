package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

@CompileStatic
class ColorTest {
    @Test
    void "Hex and rgb colors are the same"() {
        assertEquals(Color.rgb(255, 136, 0), Color.hex('#ff8800'))
        assertEquals(Color.rgb(255, 136, 0), Color.hex('FF8800'))
        assertEquals(Color.RED, new Color(1, 0, 0))
    }

    @Test
    void "Invalid colors are rejected"() {
        assertThrows(IllegalArgumentException) { Color.hex('#f80') }
        assertThrows(IllegalArgumentException) { new Color(1.5, 0, 0) }
    }
}
