package org.taack.cad.intellij;

import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;

/**
 * Follows a run, on the thread of the runner
 */
public interface CadRunListener {
    default void started() {}

    /** Not run: a later run of the same script was submitted before it started */
    default void skipped() {}

    /** What the script and the kernel print, or the runner while it starts */
    default void output(@NotNull String text, @NotNull Key<?> outputType) {}

    void finished(@NotNull CadRunResult result);
}
