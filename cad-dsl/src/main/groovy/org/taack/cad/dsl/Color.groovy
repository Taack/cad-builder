package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * sRGB color of a body, each component in [0, 1].
 */
@CompileStatic
@EqualsAndHashCode
final class Color {
    final double r
    final double g
    final double b

    final static Color BLACK = rgb(0, 0, 0)
    final static Color WHITE = rgb(255, 255, 255)
    final static Color GRAY = rgb(128, 128, 128)
    final static Color RED = rgb(255, 0, 0)
    final static Color GREEN = rgb(0, 255, 0)
    final static Color BLUE = rgb(0, 0, 255)
    final static Color YELLOW = rgb(255, 255, 0)
    final static Color CYAN = rgb(0, 255, 255)
    final static Color MAGENTA = rgb(255, 0, 255)
    final static Color ORANGE = rgb(255, 165, 0)

    Color(Number r, Number g, Number b) {
        this.r = component(r.toDouble())
        this.g = component(g.toDouble())
        this.b = component(b.toDouble())
    }

    /**
     * @return The color of components r, g, b in [0, 255]
     */
    static Color rgb(int r, int g, int b) {
        new Color(r / 255d, g / 255d, b / 255d)
    }

    /**
     * @param hex '#rrggbb' or 'rrggbb'
     */
    static Color hex(String hex) {
        String digits = hex.startsWith('#') ? hex.substring(1) : hex
        if (!(digits ==~ /\p{XDigit}{6}/)) throw new IllegalArgumentException("Not a #rrggbb color: $hex")
        int v = Integer.parseInt(digits, 16)
        rgb((v >> 16) & 0xff, (v >> 8) & 0xff, v & 0xff)
    }

    private static double component(double c) {
        if (c < 0 || c > 1) throw new IllegalArgumentException("Color component out of [0, 1]: $c")
        c
    }

    @Override
    String toString() {
        "Color{r=$r, g=$g, b=$b}"
    }
}
