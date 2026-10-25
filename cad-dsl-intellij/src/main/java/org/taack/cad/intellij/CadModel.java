package org.taack.cad.intellij;

import org.jetbrains.annotations.Nullable;

/**
 * A binary glTF file written by a display() of a script, read before the runner deletes it at the next run
 *
 * @param feature Id of the feature displayed, the root of the model, see CadFeature
 */
public record CadModel(String name, byte[] glb, @Nullable String feature) {}
