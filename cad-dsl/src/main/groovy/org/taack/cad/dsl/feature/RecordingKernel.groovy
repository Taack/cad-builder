package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.kernel.CadKernel

/**
 * Kernel that records the features of what it builds, built by another kernel:
 * CadKernels.set(new RecordingKernel(CadKernels.get(), recorder))
 */
@CompileStatic
final class RecordingKernel implements CadKernel {
    @Delegate
    final CadKernel kernel
    final CadFeatureRecorder recorder

    RecordingKernel(CadKernel kernel, CadFeatureRecorder recorder) {
        this.kernel = kernel
        this.recorder = recorder
    }

    @Override
    ICadDslVisitor newVisitor() {
        new RecordingVisitor(recorder, kernel.newVisitor())
    }

    @Override
    ICadDslAssemblyVisitor newAssemblyVisitor(String name) {
        new RecordingAssemblyVisitor(recorder, kernel.newAssemblyVisitor(name), name)
    }
}
