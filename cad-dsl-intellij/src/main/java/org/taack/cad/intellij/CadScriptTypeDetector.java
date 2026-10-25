package org.taack.cad.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.extensions.GroovyScriptTypeDetector;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

final class CadScriptTypeDetector extends GroovyScriptTypeDetector {
    CadScriptTypeDetector() {
        super(CadScriptType.INSTANCE);
    }

    @Override
    public boolean isSpecificScriptFile(@NotNull GroovyFile script) {
        return CadScripts.isCadScript(script);
    }
}
