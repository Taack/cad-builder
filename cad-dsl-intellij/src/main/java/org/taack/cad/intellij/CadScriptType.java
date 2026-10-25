package org.taack.cad.intellij;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.extensions.GroovyRunnableScriptType;

import javax.swing.Icon;

/**
 * The CAD scripts, run by the CAD runner rather than as plain Groovy scripts: no runner for the Groovy run configuration
 */
final class CadScriptType extends GroovyRunnableScriptType {
    static final CadScriptType INSTANCE = new CadScriptType();

    private CadScriptType() {
        super("cad-dsl");
    }

    @Override
    public @NotNull Icon getScriptIcon() {
        return CadIcons.SCRIPT;
    }
}
