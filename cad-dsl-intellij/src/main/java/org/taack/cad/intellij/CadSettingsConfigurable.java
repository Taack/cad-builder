package org.taack.cad.intellij;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.util.Objects;

final class CadSettingsConfigurable implements Configurable {
    private final TextFieldWithBrowseButton runnerHome = directoryField();
    private final TextFieldWithBrowseButton javaHome = directoryField();
    private final TextFieldWithBrowseButton nativeLibraryDir = directoryField();
    private final JBCheckBox rerunOnSave = new JBCheckBox("Rerun the previewed script when a script of its directory is saved");

    @Override
    public @Nls String getDisplayName() {
        return "CAD DSL";
    }

    @Override
    public JComponent createComponent() {
        JPanel form = FormBuilder.createFormBuilder()
                .addLabeledComponent("Runner installation:", runnerHome)
                .addTooltip("build/install/cad-dsl-runner, made by ./gradlew :cad-dsl-runner:installDist. Empty: looked for in the project.")
                .addLabeledComponent("Runner JDK (25):", javaHome)
                .addTooltip("Empty: the Java runtime of the IDE.")
                .addLabeledComponent("Native library directory:", nativeLibraryDir)
                .addTooltip("The directory of libcad-dsl-native. Empty: cad-dsl-native/build/lib/main/release of the project.")
                .addComponent(rerunOnSave)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        reset();
        return form;
    }

    @Override
    public boolean isModified() {
        CadSettings.State state = CadSettings.getInstance().getState();
        return !Objects.equals(runnerHome.getText(), state.runnerHome)
                || !Objects.equals(javaHome.getText(), state.javaHome)
                || !Objects.equals(nativeLibraryDir.getText(), state.nativeLibraryDir)
                || rerunOnSave.isSelected() != state.rerunOnSave;
    }

    @Override
    public void apply() {
        CadSettings.State state = CadSettings.getInstance().getState();
        state.runnerHome = runnerHome.getText().trim();
        state.javaHome = javaHome.getText().trim();
        state.nativeLibraryDir = nativeLibraryDir.getText().trim();
        state.rerunOnSave = rerunOnSave.isSelected();
    }

    @Override
    public void reset() {
        CadSettings.State state = CadSettings.getInstance().getState();
        runnerHome.setText(state.runnerHome);
        javaHome.setText(state.javaHome);
        nativeLibraryDir.setText(state.nativeLibraryDir);
        rerunOnSave.setSelected(state.rerunOnSave);
    }

    private static TextFieldWithBrowseButton directoryField() {
        TextFieldWithBrowseButton field = new TextFieldWithBrowseButton();
        field.addBrowseFolderListener(null, FileChooserDescriptorFactory.createSingleFolderDescriptor());
        return field;
    }
}
