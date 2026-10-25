package org.taack.cad.intellij;

/**
 * A binary glTF file written by a display() of a script, read before the runner deletes it at the next run
 */
public record CadModel(String name, byte[] glb) {}
