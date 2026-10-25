package org.taack.cad.intellij;

import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public final class CadRunConfigurationOptions extends LocatableRunConfigurationOptions {
    private final StoredProperty<String> scriptPath = string("").provideDelegate(this, "scriptPath");
    private final StoredProperty<String> scriptArgs = string("").provideDelegate(this, "scriptArgs");

    String getScriptPath() {
        return scriptPath.getValue(this);
    }

    void setScriptPath(String path) {
        scriptPath.setValue(this, path);
    }

    String getScriptArgs() {
        return scriptArgs.getValue(this);
    }

    void setScriptArgs(String args) {
        scriptArgs.setValue(this, args);
    }
}
