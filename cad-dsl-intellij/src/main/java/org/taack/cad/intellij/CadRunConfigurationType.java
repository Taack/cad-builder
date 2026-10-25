package org.taack.cad.intellij;

import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CadRunConfigurationType extends ConfigurationTypeBase {
    static final String ID = "CadDslScript";

    CadRunConfigurationType() {
        super(ID, "CAD Script", "Runs a CAD DSL script and shows its models in the CAD Preview", CadIcons.SCRIPT);
        addFactory(new ConfigurationFactory(this) {
            @Override
            public @NotNull String getId() {
                return ID;
            }

            @Override
            public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new CadRunConfiguration(project, this, "CAD Script");
            }

            @Override
            public @Nullable Class<? extends BaseState> getOptionsClass() {
                return CadRunConfigurationOptions.class;
            }
        });
    }

    static CadRunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(CadRunConfigurationType.class);
    }

    ConfigurationFactory factory() {
        return getConfigurationFactories()[0];
    }
}
