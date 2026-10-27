package org.taack.cad.dsl.kernel

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.ICadDslVisitorWrapper

/**
 * Lookup of the {@link CadKernel} available on the classpath.
 */
@CompileStatic
final class CadKernels {
    private static volatile CadKernel kernel

    private CadKernels() {}

    /**
     * @return The kernel set with {@link #set}, or the first one registered as a {@link java.util.ServiceLoader} provider
     */
    static CadKernel get() {
        CadKernel k = kernel
        if (k != null) return k
        synchronized (CadKernels) {
            if (kernel == null) {
                Iterator<CadKernel> providers = ServiceLoader.load(CadKernel).iterator()
                if (!providers.hasNext())
                    throw new IllegalStateException("No CAD kernel found on the classpath: add a kernel implementation, 'org.taack:cad-dsl-occt' or 'org.taack:cad-dsl-freecad'")
                kernel = providers.next()
            }
            kernel
        }
    }

    /**
     * @return The visitor of the kernel, inside the wrappers around it
     */
    static ICadDslVisitor unwrap(ICadDslVisitor visitor) {
        ICadDslVisitor v = visitor
        while (v instanceof ICadDslVisitorWrapper) v = ((ICadDslVisitorWrapper) v).kernel
        v
    }

    /**
     * Use this kernel instead of looking one up, null to look it up again.
     */
    static void set(CadKernel k) {
        kernel = k
    }
}
