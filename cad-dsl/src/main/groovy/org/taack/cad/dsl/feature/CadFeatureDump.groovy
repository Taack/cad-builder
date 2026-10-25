package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.CadScript
import org.taack.cad.dsl.kernel.CadKernels

/**
 * Prototype: runs a script, recording its features, and prints their tree.
 *
 *   CadFeatureDump &lt;script.groovy&gt; [args...]
 */
@CompileStatic
final class CadFeatureDump {
    static void main(String[] args) {
        def recorder = new CadFeatureRecorder()
        CadKernels.set(new RecordingKernel(CadKernels.get(), recorder))
        long start = System.nanoTime()
        Throwable failure = null
        try {
            CadScript.run(new File(args[0]), args.toList().drop(1))
        } catch (Throwable t) {
            failure = t
        }
        long ms = (System.nanoTime() - start).intdiv(1_000_000L)
        print recorder.text()
        println "-- ${ms} ms" + (failure ? ", failed at ${recorder.last}: $failure" : '')
    }
}
