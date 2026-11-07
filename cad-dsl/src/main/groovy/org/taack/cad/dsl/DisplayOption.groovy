package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec

/**
 * How display() shows a model: edges, camera, frustum, background and lights. Built with {@link #builder()}:
 * <pre>
 * DisplayOption.builder()
 *     .edges(true, Color.CYAN)
 *     .camera(new Vec(100, -100, 100), new Vec(), Vec.vZ)
 *     .perspective(45)
 *     .background(Color.WHITE, Color.GRAY, Color.BLACK)   // or .transparentBackground() for a .png image
 *     .light(1, new Vec(100, 0, 100), Color.WHITE)
 *     .build()
 * </pre>
 * A setting left out is null (or the default below), and the kernel then keeps its own.
 */
@CompileStatic
final class DisplayOption {
    /** Number of light sources */
    static final int LIGHT_COUNT = 3

    /** A light source: its position and color */
    @CompileStatic
    static final class Light {
        final Vec position
        final Color color

        Light(Vec position, Color color) {
            this.position = Objects.requireNonNull(position, 'light position')
            this.color = Objects.requireNonNull(color, 'light color')
        }

        @Override
        String toString() {
            "Light{position=$position, color=$color}"
        }
    }

    /** Whether the boundaries of the faces are drawn */
    final boolean edgesVisible
    /** Color of the edges, null for the kernel's */
    final Color edgeColor
    /** Camera: where it is, the point it looks at, and its up direction; null for the kernel's */
    final Vec cameraEye
    final Vec cameraTarget
    final Vec cameraUp
    /** Frustum: perspective with this field of view in degrees, or orthographic when 0 */
    final double fieldOfView
    /** Images in .png files have a transparent background; the viewer keeps the kernel's */
    final boolean transparentBackground
    /** Background gradient from top to bottom, null for the kernel's */
    final Color backgroundTop
    final Color backgroundMiddle
    final Color backgroundBottom
    /** Light sources 1 to 3, a null one is off; all null for the kernel's lights */
    final List<Light> lights

    private DisplayOption(Builder b) {
        edgesVisible = b.edgesVisible
        edgeColor = b.edgeColor
        cameraEye = b.cameraEye
        cameraTarget = b.cameraTarget
        cameraUp = b.cameraUp
        fieldOfView = b.fieldOfView
        transparentBackground = b.transparentBackground
        backgroundTop = b.backgroundTop
        backgroundMiddle = b.backgroundMiddle
        backgroundBottom = b.backgroundBottom
        lights = Collections.unmodifiableList(Arrays.asList(b.lights))
    }

    static Builder builder() {
        new Builder()
    }

    /** @return Light source index, from 1 to 3, null if off */
    Light light(int index) {
        lights[checkLight(index) - 1]
    }

    private static int checkLight(int index) {
        if (index < 1 || index > LIGHT_COUNT) throw new IllegalArgumentException("Light index out of [1, $LIGHT_COUNT]: $index")
        index
    }

    @Override
    String toString() {
        "DisplayOption{edgesVisible=$edgesVisible, edgeColor=$edgeColor, camera=[$cameraEye, $cameraTarget, $cameraUp], " +
                "fieldOfView=$fieldOfView, transparentBackground=$transparentBackground, background=[$backgroundTop, $backgroundMiddle, $backgroundBottom], lights=$lights}"
    }

    @CompileStatic
    static final class Builder {
        private boolean edgesVisible = true
        private Color edgeColor
        private Vec cameraEye
        private Vec cameraTarget
        private Vec cameraUp
        private double fieldOfView
        private boolean transparentBackground
        private Color backgroundTop
        private Color backgroundMiddle
        private Color backgroundBottom
        private final Light[] lights = new Light[LIGHT_COUNT]

        private Builder() {}

        /** Shows or hides the boundaries of the faces */
        Builder edges(boolean visible) {
            edgesVisible = visible
            this
        }

        /** Shows the boundaries of the faces in color */
        Builder edges(boolean visible, Color color) {
            edgesVisible = visible
            edgeColor = color
            this
        }

        /** Camera at eye, looking at target, up toward up (Vec.vZ if null) */
        Builder camera(Vec eye, Vec target, Vec up = Vec.vZ) {
            cameraEye = Objects.requireNonNull(eye, 'camera eye')
            cameraTarget = Objects.requireNonNull(target, 'camera target')
            cameraUp = up ?: Vec.vZ
            this
        }

        /** Perspective frustum with a field of view in degrees, in ]0, 180[ */
        Builder perspective(Number degrees) {
            double d = degrees.doubleValue()
            if (d <= 0 || d >= 180) throw new IllegalArgumentException("Field of view out of ]0, 180[: $d")
            fieldOfView = d
            this
        }

        /** Orthographic frustum, the default */
        Builder orthographic() {
            fieldOfView = 0
            this
        }

        /** Transparent background, for images in .png files, instead of a gradient */
        Builder transparentBackground() {
            transparentBackground = true
            backgroundTop = null
            backgroundMiddle = null
            backgroundBottom = null
            this
        }

        /** Background gradient of three colors, from top to bottom, instead of a transparent one */
        Builder background(Color top, Color middle, Color bottom) {
            transparentBackground = false
            backgroundTop = Objects.requireNonNull(top, 'background top')
            backgroundMiddle = Objects.requireNonNull(middle, 'background middle')
            backgroundBottom = Objects.requireNonNull(bottom, 'background bottom')
            this
        }

        /** Light source index, from 1 to 3, at position, of color */
        Builder light(int index, Vec position, Color color = Color.WHITE) {
            lights[checkLight(index) - 1] = new Light(position, color)
            this
        }

        DisplayOption build() {
            new DisplayOption(this)
        }
    }
}
