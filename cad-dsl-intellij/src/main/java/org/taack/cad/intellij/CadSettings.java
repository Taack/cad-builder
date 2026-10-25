package org.taack.cad.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/**
 * Where the CAD runner is, for every project. An empty path is looked for in the project, as built by cad-builder.
 */
@Service(Service.Level.APP)
@State(name = "CadDslSettings", storages = @Storage("cad-dsl.xml"))
public final class CadSettings implements PersistentStateComponent<CadSettings.State> {
    public static final class State {
        /** build/install/cad-dsl-runner of cad-dsl-runner */
        public String runnerHome = "";
        /** The Java 25 running the runner, the runtime of the IDE when empty */
        public String javaHome = "";
        /** The directory of the cad-dsl-native library */
        public String nativeLibraryDir = "";
        public boolean rerunOnSave = true;
    }

    private State state = new State();

    public static CadSettings getInstance() {
        return ApplicationManager.getApplication().getService(CadSettings.class);
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
