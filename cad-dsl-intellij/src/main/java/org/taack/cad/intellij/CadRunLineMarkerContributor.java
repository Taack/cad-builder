package org.taack.cad.intellij;

import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.toplevel.GrTopStatement;

/**
 * A run icon on the first statement of the CAD scripts
 */
final class CadRunLineMarkerContributor extends RunLineMarkerContributor {
    @Override
    public @Nullable Info getInfo(@NotNull PsiElement element) {
        if (element.getFirstChild() != null || !(element.getContainingFile() instanceof GroovyFile file)) return null;
        GrTopStatement[] statements = file.getTopStatements();
        if (statements.length == 0 || PsiTreeUtil.getDeepestFirst(statements[0]) != element) return null;
        return CadScripts.isCadScript(file) ? withExecutorActions(AllIcons.RunConfigurations.TestState.Run) : null;
    }
}
