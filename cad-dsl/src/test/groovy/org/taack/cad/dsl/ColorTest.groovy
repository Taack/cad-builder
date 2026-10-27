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
    void "Colors are opaque unless given an alpha"() {
        assertEquals(1d, Color.RED.a)
        assertEquals(Color.rgb(255, 136, 0, 51), Color.hex('#ff880033'))
        assertEquals(new Color(1, 0, 0, 0.2), Color.RED.alpha(0.2))
        assertEquals(Color.hex('#ff8800'), Color.hex('#ff8800ff'))
    }

    @Test
    void "Invalid colors are rejected"() {
        assertThrows(IllegalArgumentException) { Color.hex('#f80') }
        assertThrows(IllegalArgumentException) { new Color(1.5, 0, 0) }
        assertThrows(IllegalArgumentException) { Color.RED.alpha(-0.1) }
    }
}
