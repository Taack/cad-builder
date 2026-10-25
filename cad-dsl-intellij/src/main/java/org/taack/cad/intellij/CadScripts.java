package org.taack.cad.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

/**
 * Tells the CAD scripts among the Groovy files, and reads the imports CadScript adds to them
 */
public final class CadScripts {
    public static final String CAD_SCRIPT_SUFFIX = ".cad.groovy";
    static final String GROOVY_SUFFIX = ".groovy";
    static final String CAD_DSL_CLASS = "org.taack.cad.dsl.CadDsl";
    static final String IMPORTS_FILE_NAME = "script-imports.properties";
    static final String IMPORTS_FILE_PATH = "org/taack/cad/dsl/" + IMPORTS_FILE_NAME;

    /**
     * The imports CadScript adds to the scripts it runs, listed in script-imports.properties of cad-dsl
     */
    public record Imports(List<String> packages, List<String> staticClasses) {
        static final Imports NONE = new Imports(List.of(), List.of());
    }

    private CadScripts() {}

    /**
     * A Groovy script named *.cad.groovy, or in a module that has cad-dsl on its classpath
     */
    public static boolean isCadScript(@NotNull GroovyFile file) {
        String name = file.getName();
        if (!name.endsWith(GROOVY_SUFFIX) || !file.isScript()) return false;
        return name.endsWith(CAD_SCRIPT_SUFFIX) || hasCadDsl(file);
    }

    public static @NotNull Imports imports(@NotNull GroovyFile file) {
        Project project = file.getProject();
        GlobalSearchScope scope = classpathScope(file);
        if (scope == null || DumbService.isDumb(project)) return Imports.NONE;
        return CachedValuesManager.getCachedValue(file, () -> CachedValueProvider.Result.create(
                readImports(scope), ProjectRootManager.getInstance(project), PsiModificationTracker.getInstance(project)));
    }

    private static boolean hasCadDsl(GroovyFile file) {
        Project project = file.getProject();
        GlobalSearchScope scope = classpathScope(file);
        // not cached while indexing: the answer would stay false until the next change of the project roots
        if (scope == null || DumbService.isDumb(project)) return false;
        return CachedValuesManager.getCachedValue(file, () -> CachedValueProvider.Result.create(
                JavaPsiFacade.getInstance(project).findClass(CAD_DSL_CLASS, scope) != null,
                ProjectRootManager.getInstance(project)));
    }

    // Not file.getResolveScope(): the Groovy plugin asks the script type for it, which is what is being decided here
    private static @Nullable GlobalSearchScope classpathScope(GroovyFile file) {
        Module module = ModuleUtilCore.findModuleForFile(file.getOriginalFile());
        return module == null ? null : module.getModuleWithDependenciesAndLibrariesScope(false);
    }

    private static Imports readImports(GlobalSearchScope scope) {
        for (VirtualFile listed : FilenameIndex.getVirtualFilesByName(IMPORTS_FILE_NAME, scope)) {
            if (!listed.getPath().endsWith(IMPORTS_FILE_PATH)) continue;
            try {
                Properties imports = new Properties();
                imports.load(new StringReader(VfsUtilCore.loadText(listed)));
                return new Imports(names(imports, "starImports"), names(imports, "staticStarImports"));
            } catch (IOException e) {
                return Imports.NONE;
            }
        }
        return Imports.NONE;
    }

    private static List<String> names(Properties imports, String key) {
        return StringUtil.split(imports.getProperty(key, ""), ",").stream().map(String::trim).filter(it -> !it.isEmpty()).toList();
    }
}
