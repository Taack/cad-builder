package org.taack.cad.intellij;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStream;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Sends a script to the runner of the project, prints what it prints, and shows its models in the CAD Preview
 */
final class CadRunState implements RunProfileState {
    private final Project project;
    private final CadRunRequest request;

    CadRunState(Project project, CadRunRequest request) {
        this.project = project;
        this.request = request;
    }

    @Override
    public @NotNull ExecutionResult execute(Executor executor, @NotNull ProgramRunner<?> runner) {
        ConsoleView console = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        RunHandler handler = new RunHandler();
        console.attachToProcess(handler);
        return new DefaultExecutionResult(console, handler);
    }

    /**
     * The run as the IDE sees a process: it ends when the runner answers, the runner itself goes on
     */
    private final class RunHandler extends ProcessHandler implements CadRunListener {
        private volatile @Nullable Future<?> run;

        @Override
        public void startNotify() {
            super.startNotify();
            run = CadRunner.getInstance(project).submit(request, this);
        }

        @Override
        public void started() {
            CadPreview.getInstance(project).running(request);
        }

        @Override
        public void output(@NotNull String text, @NotNull Key<?> outputType) {
            notifyTextAvailable(text, outputType);
        }

        @Override
        public void finished(@NotNull CadRunResult result) {
            if (result.succeeded()) {
                notifyTextAvailable(summary(result), ProcessOutputTypes.SYSTEM);
            } else {
                notifyTextAvailable((result.trace() == null ? result.error() : result.trace()) + "\n", ProcessOutputTypes.STDERR);
            }
            CadPreview.getInstance(project).show(result);
            notifyProcessTerminated(result.succeeded() ? 0 : 1);
        }

        private static String summary(CadRunResult result) {
            String params = result.params().entrySet().stream()
                    .map(it -> it.getKey() + " = " + it.getValue())
                    .collect(Collectors.joining(", "));
            return "\nRun in " + result.millis() + " ms, " + result.models().size() + " model(s) displayed"
                    + (params.isEmpty() ? "" : ", params: " + params) + "\n";
        }

        @Override
        protected void destroyProcessImpl() {
            Future<?> submitted = run;
            if (submitted != null && submitted.cancel(false)) notifyProcessTerminated(1);
            else CadRunner.getInstance(project).stop();
        }

        @Override
        protected void detachProcessImpl() {
            notifyProcessDetached();
        }

        @Override
        public boolean detachIsDefault() {
            return false;
        }

        @Override
        public @Nullable OutputStream getProcessInput() {
            return null;
        }
    }
}
