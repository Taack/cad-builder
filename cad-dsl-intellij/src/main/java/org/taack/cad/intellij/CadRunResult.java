package org.taack.cad.intellij;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * How a run ended: the models displayed, the param() values, and the error when it failed
 */
public record CadRunResult(CadRunRequest request,
                           List<CadModel> models,
                           Map<String, String> params,
                           long millis,
                           @Nullable String error,
                           @Nullable String trace) {

    static CadRunResult failed(CadRunRequest request, String error, long millis) {
        return new CadRunResult(request, List.of(), Map.of(), millis, error, null);
    }

    public boolean succeeded() {
        return error == null;
    }
}
