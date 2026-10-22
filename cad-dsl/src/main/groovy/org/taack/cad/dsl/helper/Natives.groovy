package org.taack.cad.dsl.helper

import groovy.transform.CompileStatic

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.ref.WeakReference
import java.util.function.Consumer

import static org.taack.occt.NativeLib.occt_free_doubles

/**
 * Ownership of the objects returned by the OCCT C API (new_*, handle_*, ...).
 *
 * A model (CadDslVisitor, CadBuilder) opens a scope; every object created while it is the
 * current scope of the thread is attached to it with its delete_* function. The scope is an
 * automatic arena: all its objects are released together once neither the model nor any of
 * those segments is reachable. Releasing per model rather than per object keeps the borrowed
 * pointers (ref_*) valid as long as the model is in use.
 */
@CompileStatic
final class Natives {
    // Scopes opened on this thread, most recent last; the current one is the most recent still alive.
    private static final ThreadLocal<List<WeakReference<Arena>>> SCOPES = ThreadLocal.withInitial { [] as List<WeakReference<Arena>> }

    private Natives() {}

    /**
     * Opens a scope and makes it current for the calling thread.
     * The caller must keep a reference to the returned arena for the lifetime of its model.
     */
    static Arena openScope() {
        Arena scope = Arena.ofAuto()
        List<WeakReference<Arena>> scopes = SCOPES.get()
        scopes.removeIf { WeakReference<Arena> it -> it.get() == null }
        scopes << new WeakReference<Arena>(scope)
        scope
    }

    private static Arena currentScope() {
        List<WeakReference<Arena>> scopes = SCOPES.get()
        while (!scopes.empty) {
            Arena scope = scopes.last().get()
            if (scope != null) return scope
            scopes.removeLast()
        }
        null
    }

    /**
     * Attaches ptr to the current scope (or, outside any model, to its own automatic arena)
     * so that deleter releases it.
     */
    static MemorySegment own(MemorySegment ptr, Consumer<MemorySegment> deleter) {
        if (ptr == null || ptr.address() == 0L) return ptr
        ptr.reinterpret(currentScope() ?: Arena.ofAuto(), deleter)
    }

    /**
     * Copies a double array returned by the C API (R4_*, R6_*, R7_*, r4_*) and releases it.
     */
    static double[] doubles(MemorySegment ptr, int count) {
        if (ptr == null || ptr.address() == 0L) return null
        try {
            ptr.reinterpret(count * ValueLayout.JAVA_DOUBLE.byteSize()).toArray(ValueLayout.JAVA_DOUBLE)
        } finally {
            occt_free_doubles(ptr)
        }
    }
}
