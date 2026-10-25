package org.taack.cad.dsl

import groovy.transform.CompileStatic

import java.util.function.Supplier

/**
 * Where display() sends a model given no file name: the viewer of the kernel, unless the calls are redirected to files,
 * e.g. by a runner showing the models in an IDE.
 */
@CompileStatic
final class CadDisplay {
    /**
     * Extension of the binary glTF files that display() writes: the shaded faces and boundary edges of each part, the
     * tree of the assembly, and its animation
     */
    static final String GLTF_EXTENSION = '.glb'

    private static volatile Supplier<String> redirection

    private CadDisplay() {}

    /**
     * @return fileName, else the file name given by the redirection, else null for the viewer
     */
    static String target(String fileName) {
        fileName ?: redirection?.get()
    }

    /**
     * Send the display() calls without a file name to the files named by target, null to open the viewer again
     */
    static void redirect(Supplier<String> target) {
        redirection = target
    }
}
