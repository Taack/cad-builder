package org.taack.cad.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.resolve.imports.GrImportContributor;
import org.jetbrains.plugins.groovy.lang.resolve.imports.GroovyImport;
import org.jetbrains.plugins.groovy.lang.resolve.imports.StarImport;
import org.jetbrains.plugins.groovy.lang.resolve.imports.StaticStarImport;

import java.util.ArrayList;
import java.util.List;

/**
 * Declares to the IDE the imports CadScript adds to the CAD scripts when it runs them
 */
final class CadImportContributor implements GrImportContributor {
    @Override
    public @NotNull List<GroovyImport> getFileImports(@NotNull GroovyFile file) {
        if (!CadScripts.isCadScript(file)) return List.of();
        CadScripts.Imports imports = CadScripts.imports(file);
        List<GroovyImport> declared = new ArrayList<>();
        imports.packages().forEach(it -> declared.add(new StarImport(it)));
        imports.staticClasses().forEach(it -> declared.add(new StaticStarImport(it)));
        return declared;
    }
}
