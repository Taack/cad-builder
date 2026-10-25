package org.taack.cad.intellij;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

public final class CadIcons {
    public static final Icon SCRIPT = IconLoader.getIcon("/icons/cadScript.svg", CadIcons.class);
    public static final Icon PREVIEW = IconLoader.getIcon("/icons/cadPreview.svg", CadIcons.class);

    private CadIcons() {}
}
