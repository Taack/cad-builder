package org.taack.cad.intellij;

import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The last run of a project, shown by the CAD Preview tool window, and run again when its scripts are saved
 */
@Service(Service.Level.PROJECT)
public final class CadPreview {
    public static final String TOOL_WINDOW_ID = "CAD Preview";

    public interface Listener {
        void running(@NotNull CadRunRequest request);

        void shown(@NotNull CadRunResult result);

        /** The feature selected in the CAD Structure tool window or picked in the preview, null when none is */
        default void featureSelected(@Nullable String feature) {}
    }

    private final Project project;
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean rerunPending = new AtomicBoolean();
    // the runs of CAD scripts the IDE prepares, building and saving before them, and how many it ever scheduled
    private final Set<ExecutionEnvironment> scheduled = ConcurrentHashMap.newKeySet();
    private final AtomicLong schedules = new AtomicLong();
    private volatile @Nullable CadRunResult last;
    private volatile @Nullable String selectedFeature;
    // the feature the runs of a script stop at, see rollBack
    private volatile @Nullable String stopScript;
    private volatile @Nullable String stopAt;

    public CadPreview(Project project) {
        this.project = project;
    }

    public static CadPreview getInstance(@NotNull Project project) {
        return project.getService(CadPreview.class);
    }

    public @Nullable CadRunResult last() {
        return last;
    }

    public @Nullable String selectedFeature() {
        return selectedFeature;
    }

    /**
     * Selects a feature of the last run, to highlight it in the preview
     */
    public void selectFeature(@Nullable String feature) {
        if (Objects.equals(feature, selectedFeature)) return;
        selectedFeature = feature;
        listeners.forEach(it -> it.featureSelected(feature));
    }

    /**
     * @return The feature the runs of script stop at, null when they run to the end
     */
    public @Nullable String stopAt(@NotNull String script) {
        return script.equals(stopScript) ? stopAt : null;
    }

    /**
     * Runs the last script again up to feature, and the following runs of that script, until it is rolled forward
     *
     * @param feature A feature of the last run, null to run to the end again
     */
    public void rollBack(@Nullable String feature) {
        CadRunResult previous = last;
        if (previous == null) return;
        stopScript = feature == null ? null : previous.request().script();
        stopAt = feature;
        rerun();
    }

    public void addListener(@NotNull Listener listener, @NotNull Disposable parent) {
        listeners.add(listener);
        Disposer.register(parent, () -> listeners.remove(listener));
    }

    void running(@NotNull CadRunRequest request) {
        listeners.forEach(it -> it.running(request));
    }

    /**
     * Shows result, and opens the preview tool window when it has models to show, the structure one when it has features
     */
    void show(@NotNull CadRunResult result) {
        last = result;
        // the runner no longer has that feature: the script changed
        if (result.stopAt() == null && !result.features().isEmpty() && stopAt(result.request().script()) != null) {
            stopScript = null;
            stopAt = null;
        }
        listeners.forEach(it -> it.shown(result));
        if (!result.models().isEmpty()) showToolWindow(TOOL_WINDOW_ID);
        if (!result.features().isEmpty()) showToolWindow(CadStructurePanel.TOOL_WINDOW_ID);
    }

    private void showToolWindow(String id) {
        ApplicationManager.getApplication().invokeLater(() -> {
            ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(id);
            if (window != null && !window.isVisible()) window.show();
        }, project.getDisposed());
    }

    void runScheduled(@NotNull ExecutionEnvironment environment) {
        schedules.incrementAndGet();
        scheduled.add(environment);
    }

    void runStarted(@NotNull ExecutionEnvironment environment) {
        scheduled.remove(environment);
    }

    /**
     * Runs the last script again, once however many times it is asked before the run starts. A run being prepared
     * replaces it: saving before a run, of the same script or of another one, asks for a rerun too.
     */
    public void rerun() {
        CadRunResult previous = last;
        if (previous == null || !scheduled.isEmpty() || !rerunPending.compareAndSet(false, true)) return;
        CadRunRequest request = previous.request();
        long schedule = schedules.get();
        CadRunner.getInstance(project).submitUnlessSuperseded(request, new CadRunListener() {
            @Override
            public void started() {
                rerunPending.set(false);
                running(request);
            }

            @Override
            public void skipped() {
                rerunPending.set(false);
            }

            @Override
            public void finished(@NotNull CadRunResult result) {
                // a run scheduled meanwhile shows its own result, after this one would have flashed
                if (schedules.get() == schedule) show(result);
            }
        });
    }

    /**
     * Whether saving file changes the last script run: the script itself, or a class of its directory
     */
    boolean dependsOn(@NotNull Path file) {
        CadRunResult previous = last;
        if (previous == null) return false;
        Path script = Path.of(previous.request().script());
        return file.equals(script) || file.getParent() != null && file.getParent().equals(script.getParent());
    }
}
