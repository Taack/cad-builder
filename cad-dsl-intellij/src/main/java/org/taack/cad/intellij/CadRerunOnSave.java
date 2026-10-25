package org.taack.cad.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * Runs the previewed script again when it is saved or changed on disk, by a pull for instance
 */
final class CadRerunOnSave implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        if (!CadSettings.getInstance().getState().rerunOnSave) return;
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            CadPreview preview = project.getServiceIfCreated(CadPreview.class);
            if (preview == null) continue;
            boolean changed = events.stream().anyMatch(event -> event instanceof VFileContentChangeEvent change
                    && change.getFile().getName().endsWith(CadScripts.GROOVY_SUFFIX)
                    && change.getFile().isInLocalFileSystem()
                    && preview.dependsOn(Path.of(change.getFile().getPath())));
            if (changed) preview.rerun();
        }
    }
}
