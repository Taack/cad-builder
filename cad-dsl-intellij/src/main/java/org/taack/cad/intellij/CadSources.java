package org.taack.cad.intellij;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.ArchiveFileSystem;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Finds the code that made a feature of a run
 */
final class CadSources {
    private CadSources() {}

    /**
     * @param script The script of the run
     * @return The file beside the script, else the class of the feature: a class of the project, or of a jar of parts
     */
    static @Nullable VirtualFile source(Project project, @Nullable String script, CadFeature feature) {
        if (feature.file() != null && script != null) {
            Path beside = Path.of(script).resolveSibling(feature.file());
            if (Files.isRegularFile(beside)) return LocalFileSystem.getInstance().findFileByNioFile(beside);
        }
        if (feature.className() == null || DumbService.isDumb(project)) return null;
        // closures are inner classes: MjParts$_body_closure1 is in MjParts
        String outer = feature.className().replaceFirst("\\$.*", "");
        PsiClass found = JavaPsiFacade.getInstance(project).findClass(outer, GlobalSearchScope.allScope(project));
        if (found == null) return null;
        PsiFile file = found.getNavigationElement().getContainingFile();
        return file == null ? null : file.getVirtualFile();
    }

    /**
     * @return The source of the feature when it is a file of the project, not of a jar, null otherwise
     */
    static @Nullable VirtualFile projectSource(Project project, @Nullable String script, CadFeature feature) {
        if (feature.line() <= 0) return null;
        VirtualFile file = source(project, script, feature);
        return file == null || file.getFileSystem() instanceof ArchiveFileSystem ? null : file;
    }
}
