package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadImages
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.DisplayOption
import org.taack.cad.dsl.geom.Vec
import org.taack.occt.CadDisplayOptions

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

/**
 * A {@link DisplayOption} as the CadDisplayOptions of the C API.
 */
@CompileStatic
final class OcctDisplayOptions {
    private OcctDisplayOptions() {}

    /**
     * @return The options allocated in arena, NULL when option is null for the kernel's look
     */
    static MemorySegment of(Arena arena, DisplayOption option) {
        if (option == null) return MemorySegment.NULL
        // allocated zeroed: no camera, no background, no light
        MemorySegment s = CadDisplayOptions.allocate(arena)
        CadDisplayOptions.edgesVisible(s, option.edgesVisible ? 1 : 0)
        if (option.edgeColor) {
            CadDisplayOptions.hasEdgeColor(s, 1)
            rgb(CadDisplayOptions.edgeColor(s), 0, option.edgeColor)
        }
        if (option.cameraEye) {
            CadDisplayOptions.hasCamera(s, 1)
            xyz(CadDisplayOptions.eye(s), 0, option.cameraEye)
            xyz(CadDisplayOptions.target(s), 0, option.cameraTarget)
            xyz(CadDisplayOptions.up(s), 0, option.cameraUp)
        }
        CadDisplayOptions.fieldOfView(s, option.fieldOfView)
        CadDisplayOptions.transparentBackground(s, option.transparentBackground ? 1 : 0)
        if (option.backgroundTop) {
            CadDisplayOptions.hasBackground(s, 1)
            MemorySegment b = CadDisplayOptions.background(s)
            rgb(b, 0, option.backgroundTop)
            rgb(b, 3, option.backgroundMiddle)
            rgb(b, 6, option.backgroundBottom)
        }
        for (int i = 0; i < DisplayOption.LIGHT_COUNT; i++) {
            DisplayOption.Light light = option.light(i + 1)
            if (light == null) continue
            CadDisplayOptions.hasLight(s, i, 1)
            xyz(CadDisplayOptions.lightPosition(s), 3 * i, light.position)
            rgb(CadDisplayOptions.lightColor(s), 3 * i, light.color)
        }
        s
    }

    /**
     * Checks that an image with option can be written to fileName: a transparent background needs a .png, .gif or
     * .tiff file
     */
    static void check(DisplayOption option, String fileName) {
        if (option?.transparentBackground && !CadImages.keepsAlpha(fileName))
            throw new IllegalArgumentException("display($fileName): a transparent background needs a .png, .gif or .tiff file")
    }

    private static void xyz(MemorySegment array, int from, Vec v) {
        set(array, from, v.x, v.y, v.z)
    }

    private static void rgb(MemorySegment array, int from, Color c) {
        set(array, from, c.r, c.g, c.b)
    }

    private static void set(MemorySegment array, int from, double a, double b, double c) {
        array.setAtIndex(ValueLayout.JAVA_DOUBLE, from, a)
        array.setAtIndex(ValueLayout.JAVA_DOUBLE, from + 1, b)
        array.setAtIndex(ValueLayout.JAVA_DOUBLE, from + 2, c)
    }
}
