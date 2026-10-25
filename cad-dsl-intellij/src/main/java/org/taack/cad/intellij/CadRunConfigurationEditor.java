package org.taack.cad.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

final class CadRunConfigurationEditor extends SettingsEditor<CadRunConfiguration> {
    private final TextFieldWithBrowseButton scriptPath = new TextFieldWithBrowseButton();
    private final RawCommandLineEditor scriptArgs = new RawCommandLineEditor();

    CadRunConfigurationEditor(Project project) {
        scriptPath.addBrowseFolderListener(project, FileChooserDescriptorFactory.createSingleFileDescriptor("groovy"));
    }

    @Override
    protected void resetEditorFrom(@NotNull CadRunConfiguration configuration) {
        scriptPath.setText(configuration.getScriptPath());
        scriptArgs.setText(configuration.getScriptArgs());
    }

    @Override
    protected void applyEditorTo(@NotNull CadRunConfiguration configuration) {
        configuration.setScriptPath(scriptPath.getText().trim());
        configuration.setScriptArgs(scriptArgs.getText());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Script:", scriptPath)
                .addLabeledComponent("Script arguments:", scriptArgs)
                .addTooltip("Read by the script from args")
                .getPanel();
    }
}
