package org.taack.cad.intellij;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

final class CadRunConfigurationProducer extends LazyRunConfigurationProducer<CadRunConfiguration> {
    @Override
    public @NotNull ConfigurationFactory getConfigurationFactory() {
        return CadRunConfigurationType.getInstance().factory();
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull CadRunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        GroovyFile script = cadScript(context);
        if (script == null) return false;
        sourceElement.set(script);
        configuration.setScriptPath(script.getVirtualFile().getPath());
        configuration.setName(script.getName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull CadRunConfiguration configuration, @NotNull ConfigurationContext context) {
        GroovyFile script = cadScript(context);
        return script != null && script.getVirtualFile().getPath().equals(configuration.getScriptPath());
    }

    private static @Nullable GroovyFile cadScript(ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        if (element == null || !(element.getContainingFile() instanceof GroovyFile file)) return null;
        VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null || !virtualFile.isInLocalFileSystem()) return null;
        return CadScripts.isCadScript(file) ? file : null;
    }
}
