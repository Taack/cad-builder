package org.taack.cad.intellij;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.util.execution.ParametersListUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CadRunConfiguration extends LocatableConfigurationBase<CadRunConfigurationOptions> {
    CadRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected @NotNull CadRunConfigurationOptions getOptions() {
        return (CadRunConfigurationOptions) super.getOptions();
    }

    public String getScriptPath() {
        return getOptions().getScriptPath();
    }

    public void setScriptPath(String path) {
        getOptions().setScriptPath(path);
    }

    public String getScriptArgs() {
        return getOptions().getScriptArgs();
    }

    public void setScriptArgs(String args) {
        getOptions().setScriptArgs(args);
    }

    CadRunRequest request() {
        return new CadRunRequest(getScriptPath(), ParametersListUtil.parse(getScriptArgs()));
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationError {
        String script = getScriptPath();
        if (script == null || script.isBlank()) throw new RuntimeConfigurationError("No script");
        if (!Files.isRegularFile(Path.of(script))) throw new RuntimeConfigurationError("No such script: " + script);
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new CadRunConfigurationEditor(getProject());
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new CadRunState(getProject(), request());
    }

    @Override
    public @Nullable String suggestedName() {
        String script = getScriptPath();
        return script == null || script.isBlank() ? null : Path.of(script).getFileName().toString();
    }
}
