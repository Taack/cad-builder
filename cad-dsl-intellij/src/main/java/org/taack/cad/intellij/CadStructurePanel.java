package org.taack.cad.intellij;

import com.intellij.icons.AllIcons;
import com.intellij.ide.CommonActionsManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The tree of the features of the last run: its parts, operations and sketches, each one opening the line of the script
 * that made it, and selected when the caret of an editor moves to that line. The script can be rolled back to a feature:
 * a bar goes under it, and the features after it, not done, are grayed.
 */
final class CadStructurePanel implements CadPreview.Listener, Disposable {
    static final String TOOL_WINDOW_ID = "CAD Structure";
    // levels expanded when a script is shown for the first time: assemblies, parts, and their features
    private static final int EXPANDED_LEVELS = 3;

    private final Project project;
    private final SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true, true);
    private final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
    private final Tree tree = new Tree(model);
    private boolean details;
    private boolean followEditor = true;
    private List<CadFeature> features = List.of();
    private @Nullable String shownScript;
    private @Nullable String stopAt;
    private final AnAction rollBack = new DumbAwareAction("Roll Back Here",
            "Run the script up to the selected feature: the ones after it are not done", AllIcons.Actions.Rollback) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            CadPreview.getInstance(project).rollBack(selectedId());
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(selectedId() != null);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }
    };
    private final AnAction rollForward = new DumbAwareAction("Roll Forward", "Run the script to its end again",
            AllIcons.Actions.Forward) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            CadPreview.getInstance(project).rollBack(null);
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(stopAt != null);
        }

        @Override
        public @NotNull ActionUpdateThread getActionUpdateThread() {
            return ActionUpdateThread.EDT;
        }
    };

    CadStructurePanel(Project project) {
        this.project = project;
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setCellRenderer(new Renderer());
        tree.getEmptyText().setText("Run a CAD script to see its features");
        TreeSpeedSearch.installOn(tree, true, path -> feature(path.getLastPathComponent()) instanceof CadFeature f ? f.label() : "");
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent event) {
                return navigate();
            }
        }.installOn(tree);
        tree.addTreeSelectionListener(event -> CadPreview.getInstance(project).selectFeature(selectedId()));
        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER && navigate()) event.consume();
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addCaretListener(new CaretListener() {
            @Override
            public void caretPositionChanged(@NotNull CaretEvent event) {
                Editor editor = event.getEditor();
                VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
                if (followEditor && editor.getProject() == project && file != null) select(file, event.getNewPosition().line + 1);
            }
        }, this);
        DefaultActionGroup popup = new DefaultActionGroup(rollBack, rollForward);
        PopupHandler.installPopupMenu(tree, popup, "CadStructurePopup");
        panel.setToolbar(toolbar().getComponent());
        panel.setContent(ScrollPaneFactory.createScrollPane(tree, true));
        CadPreview preview = CadPreview.getInstance(project);
        preview.addListener(this, this);
        CadRunResult last = preview.last();
        if (last != null) shown(last);
    }

    JComponent getComponent() {
        return panel;
    }

    private ActionToolbar toolbar() {
        DefaultActionGroup actions = new DefaultActionGroup();
        DefaultTreeExpander expander = new DefaultTreeExpander(tree);
        actions.add(CommonActionsManager.getInstance().createExpandAllAction(expander, tree));
        actions.add(CommonActionsManager.getInstance().createCollapseAllAction(expander, tree));
        actions.addSeparator();
        actions.add(rollBack);
        actions.add(rollForward);
        actions.addSeparator();
        actions.add(new DumbAwareToggleAction("Follow Editor", "Select the feature made at the line of the caret",
                AllIcons.General.AutoscrollFromSource) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return followEditor;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                followEditor = state;
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        });
        actions.add(new DumbAwareToggleAction("Sketch Elements", "Show the edges, arcs and texts of the sketches", AllIcons.Nodes.Variable) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return details;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                details = state;
                rebuild(false);
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        });
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("CadStructure", actions, true);
        toolbar.setTargetComponent(panel);
        return toolbar;
    }

    @Override
    public void running(@NotNull CadRunRequest request) {}

    @Override
    public void featureSelected(@Nullable String feature) {
        // a feature picked in the preview. The one selected by then: a selection made since would be undone, then
        // selected again by its own notification, endlessly
        ApplicationManager.getApplication().invokeLater(() -> {
            String selected = CadPreview.getInstance(project).selectedFeature();
            if (Objects.equals(selected, selectedId())) return;
            TreePath path = selected == null ? null : find((DefaultMutableTreeNode) model.getRoot(), selected);
            if (path == null) {
                tree.clearSelection();
                return;
            }
            tree.setSelectionPath(path);
            tree.scrollPathToVisible(path);
        }, project.getDisposed());
    }

    private static @Nullable TreePath find(DefaultMutableTreeNode node, String id) {
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            if (((CadFeature) child.getUserObject()).id().equals(id)) return new TreePath(child.getPath());
            TreePath below = find(child, id);
            if (below != null) return below;
        }
        return null;
    }

    @Override
    public void shown(@NotNull CadRunResult result) {
        ApplicationManager.getApplication().invokeLater(() -> {
            // a run that failed before its first feature keeps the tree of the last one
            if (result.features().isEmpty() && !result.succeeded()) return;
            boolean newScript = !result.request().script().equals(shownScript);
            shownScript = result.request().script();
            features = result.features();
            stopAt = result.stopAt();
            rebuild(newScript);
        }, project.getDisposed());
    }

    // Shows the features, with the nodes expanded and selected as before when the script is the same
    private void rebuild(boolean newScript) {
        Set<String> expanded = new HashSet<>();
        if (!newScript) {
            collectIds(tree.getExpandedDescendants(new TreePath(model.getRoot())), expanded);
        }
        String selected = selectedId();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        features.forEach(it -> root.add(node(it)));
        model.setRoot(root);
        TreePath error = restore(root, expanded, newScript ? EXPANDED_LEVELS : 0, selected);
        if (error != null) {
            tree.setSelectionPath(error);
            tree.scrollPathToVisible(error);
        }
    }

    private static void collectIds(@Nullable Enumeration<TreePath> paths, Set<String> ids) {
        if (paths == null) return;
        for (TreePath path : Collections.list(paths)) {
            if (feature(path.getLastPathComponent()) instanceof CadFeature f) ids.add(f.id());
        }
    }

    private DefaultMutableTreeNode node(CadFeature feature) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(feature);
        for (CadFeature child : feature.children()) {
            if (details || !child.detail()) node.add(node(child));
        }
        return node;
    }

    // Expands the nodes of ids, and those above levels; selects the one of selected; returns the path of the failing
    // feature, if any
    private @Nullable TreePath restore(DefaultMutableTreeNode node, Set<String> ids, int levels, @Nullable String selected) {
        TreePath error = null;
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            CadFeature feature = (CadFeature) child.getUserObject();
            TreePath path = new TreePath(child.getPath());
            if (levels > 0 || ids.contains(feature.id())) tree.expandPath(path);
            if (feature.id().equals(selected)) tree.setSelectionPath(path);
            if (feature.error()) error = path;
            TreePath below = restore(child, ids, levels - 1, selected);
            if (below != null) error = below;
        }
        return error;
    }

    private @Nullable String selectedId() {
        TreePath path = tree.getSelectionPath();
        return path != null && feature(path.getLastPathComponent()) instanceof CadFeature f ? f.id() : null;
    }

    private static @Nullable Object feature(Object node) {
        return node instanceof DefaultMutableTreeNode n ? n.getUserObject() : null;
    }

    /**
     * Selects a feature made at line of file, unless the selected one is. When several are, the deepest is, the box
     * rather than its solid on cd().box(...), and the first of them, the first of the cuts repeated by a loop
     */
    void select(VirtualFile file, int line) {
        List<TreePath> made = new ArrayList<>();
        collectMadeAt((DefaultMutableTreeNode) model.getRoot(), file, line, made);
        if (made.isEmpty() || made.contains(tree.getSelectionPath())) return;
        TreePath deepest = made.getFirst();
        for (TreePath path : made) if (path.getPathCount() > deepest.getPathCount()) deepest = path;
        tree.setSelectionPath(deepest);
        tree.scrollPathToVisible(deepest);
    }

    private void collectMadeAt(DefaultMutableTreeNode node, VirtualFile file, int line, List<TreePath> made) {
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            CadFeature feature = (CadFeature) child.getUserObject();
            if (feature.line() == line && file.getName().equals(feature.file())) {
                // another file of that name, in a jar of parts for instance, does not count
                VirtualFile source = source(feature);
                if (source == null || source.equals(file)) made.add(new TreePath(child.getPath()));
            }
            collectMadeAt(child, file, line, made);
        }
    }

    // Opens the line of the selected feature
    private boolean navigate() {
        TreePath path = tree.getSelectionPath();
        if (path == null || !(feature(path.getLastPathComponent()) instanceof CadFeature feature) || feature.line() <= 0) return false;
        VirtualFile file = source(feature);
        if (file == null) return false;
        new OpenFileDescriptor(project, file, feature.line() - 1, 0).navigate(true);
        return true;
    }

    private @Nullable VirtualFile source(CadFeature feature) {
        return CadSources.source(project, shownScript, feature);
    }

    private final class Renderer extends ColoredTreeCellRenderer {
        @Override
        public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded,
                                          boolean leaf, int row, boolean hasFocus) {
            setBorder(null);
            if (!(feature(value) instanceof CadFeature feature)) return;
            setIcon(feature.suppressed() ? IconLoader.getDisabledIcon(icon(feature)) : icon(feature));
            append(feature.label(), feature.error() ? SimpleTextAttributes.ERROR_ATTRIBUTES
                    : feature.suppressed() ? SimpleTextAttributes.GRAYED_ATTRIBUTES : SimpleTextAttributes.REGULAR_ATTRIBUTES);
            // the rollback bar
            if (feature.id().equals(stopAt)) setBorder(JBUI.Borders.customLine(JBUI.CurrentTheme.Focus.focusColor(), 0, 0, 2, 0));
            long elements = feature.details();
            if (elements > 0 && !details) append("  " + elements + (elements == 1 ? " element" : " elements"), SimpleTextAttributes.GRAYED_ATTRIBUTES);
            if (feature.file() != null) append("  " + feature.file() + ":" + feature.line(), SimpleTextAttributes.GRAYED_SMALL_ATTRIBUTES);
        }

        private Icon icon(CadFeature feature) {
            if (feature.error()) return AllIcons.General.Error;
            if (feature.detail()) return AllIcons.Nodes.Variable;
            return switch (feature.kind()) {
                case "assembly" -> AllIcons.Nodes.ModuleGroup;
                case "part", "solid" -> AllIcons.Nodes.Module;
                case "group" -> AllIcons.Nodes.Method;
                case "place" -> AllIcons.Nodes.Related;
                case "sketch", "face", "wire", "profile", "section", "curve" -> AllIcons.Nodes.Template;
                case "position", "select" -> AllIcons.Nodes.Target;
                default -> AllIcons.Nodes.Field;
            };
        }
    }

    @Override
    public void dispose() {}
}
