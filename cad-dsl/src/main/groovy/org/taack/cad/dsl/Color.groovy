package org.taack.cad.dsl

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * sRGB color of a body, each component in [0, 1], with its opacity: alpha, from 0 (invisible) to 1 (opaque, the default).
 */
@CompileStatic
@EqualsAndHashCode
final class Color {
    final double r
    final double g
    final double b
    final double a

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

    Color(Number r, Number g, Number b, Number a = 1) {
        this.r = component(r.toDouble())
        this.g = component(g.toDouble())
        this.b = component(b.toDouble())
        this.a = component(a.toDouble())
    }

    /**
     * @return The color of components r, g, b and alpha a in [0, 255]
     */
    static Color rgb(int r, int g, int b, int a = 255) {
        new Color(r / 255d, g / 255d, b / 255d, a / 255d)
    }

    /**
     * @param hex '#rrggbb' or 'rrggbb', '#rrggbbaa' or 'rrggbbaa' with the alpha
     */
    static Color hex(String hex) {
        String digits = hex.startsWith('#') ? hex.substring(1) : hex
        if (!(digits ==~ /\p{XDigit}{6}(\p{XDigit}{2})?/)) throw new IllegalArgumentException("Not a #rrggbb or #rrggbbaa color: $hex")
        long v = Long.parseLong(digits.length() == 6 ? digits + 'ff' : digits, 16)
        rgb((int) (v >> 24) & 0xff, (int) (v >> 16) & 0xff, (int) (v >> 8) & 0xff, (int) v & 0xff)
    }

    /**
     * @param a Alpha in [0, 1], 0.3 for a part seen through
     * @return This color with alpha a: Color.BLUE.alpha(0.3)
     */
    Color alpha(Number a) {
        new Color(r, g, b, a)
    }

    private static double component(double c) {
        if (c < 0 || c > 1) throw new IllegalArgumentException("Color component out of [0, 1]: $c")
        c
    }

    @Override
    String toString() {
        "Color{r=$r, g=$g, b=$b, a=$a}"
    }
}
