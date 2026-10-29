package org.taack.cad.intellij;

import com.intellij.execution.ExecutionListener;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Tells the preview when a run of a CAD script is prepared, so that the files saved before it do not run the previewed
 * script again
 */
final class CadExecutionListener implements ExecutionListener {
    private final Project project;

    CadExecutionListener(Project project) {
        this.project = project;
    }

    @Override
    public void processStartScheduled(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        if (env.getRunProfile() instanceof CadRunConfiguration) CadPreview.getInstance(project).runScheduled(env);
    }

    @Override
    public void processNotStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env) {
        CadPreview.getInstance(project).runStarted(env);
    }

    @Override
    public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
        CadPreview.getInstance(project).runStarted(env);
    }

    @Override
    public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
        CadPreview.getInstance(project).runStarted(env);
    }
}
