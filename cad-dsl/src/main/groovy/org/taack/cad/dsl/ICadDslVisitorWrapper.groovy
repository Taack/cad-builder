package org.taack.cad.dsl

/**
 * A visitor doing something more around the visitor of a kernel: recording the features, the contours...
 * The kernel unwraps it to find its own, see {@link org.taack.cad.dsl.kernel.CadKernels#unwrap}.
 */
interface ICadDslVisitorWrapper {
    ICadDslVisitor getKernel()
}
