package org.taack.cad.dsl.feature

/**
 * A kernel visitor giving the shape each operation makes, so that a feature can be shown on its own: optional, see
 * {@link RecordingVisitor}
 */
interface ICadDslFeatureShapes {
    /**
     * @param kind Kind of the last operation, see {@link CadFeature#kind}
     * @return The shape that operation made, before it was fused to the solid or cut from it; null if none
     */
    Object featureShape(String kind)

    /**
     * Writes shapes, given by {@link #featureShape}, to a binary glTF file, as a single part in the coordinates of the
     * solid
     */
    void writeShapes(List<Object> shapes, String fileName)
}
