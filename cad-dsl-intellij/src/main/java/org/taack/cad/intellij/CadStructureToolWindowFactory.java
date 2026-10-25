package org.taack.cad.intellij;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

final class CadStructureToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        CadStructurePanel structure = new CadStructurePanel(project);
        Content content = ContentFactory.getInstance().createContent(structure.getComponent(), "", false);
        content.setDisposer(structure);
        toolWindow.getContentManager().addContent(content);
    }
}
