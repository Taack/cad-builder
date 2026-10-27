package org.taack.cad.intellij;

import com.google.gson.JsonParser;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.DumbAwareToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.utils.JBCefLocalRequestHandler;
import com.intellij.ui.jcef.utils.JBCefStreamResourceHandler;
import com.intellij.util.ui.JBUI;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shows the binary glTF files of the last run with three.js, in a browser of the IDE that loads nothing from the network
 */
final class CadPreviewPanel implements CadPreview.Listener, Disposable {
    private static final String PROTOCOL = "http";
    private static final String HOST = "cad-preview";
    private static final String MODEL_PATH = "model.glb";
    private static final String SHAPE_PATH = "shape.glb";
    private static final List<String> PAGE_FILES = List.of("index.html", "preview.js");
    private static final String THREE_DIR = "three/";
    private static final String THREE_FILES = THREE_DIR + "files.txt";
    private static final Map<String, String> NOT_CACHED = Map.of("Cache-Control", "no-store");

    private final Project project;
    private final SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true, true);
    private final JBLabel status = new JBLabel("Run a CAD script to preview it");
    private final ComboBox<String> modelChoice = new ComboBox<>();
    private JBCefBrowser browser;
    private volatile List<CadModel> models = List.of();
    // the features of the run whose models are shown, and the shapes of the selected one
    private List<CadFeature> features = List.of();
    private volatile byte[] shape = new byte[0];
    private int highlights;
    private volatile int selected;
    private volatile boolean pageLoaded;
    private boolean edges = true;
    private int version;
    private String shownScript;

    CadPreviewPanel(Project project) {
        this.project = project;
        status.setBorder(JBUI.Borders.empty(2, 6));
        status.setVerticalAlignment(SwingConstants.TOP);
        modelChoice.setVisible(false);
        modelChoice.addActionListener(event -> {
            if (modelChoice.getSelectedIndex() >= 0 && modelChoice.getSelectedIndex() != selected) {
                selected = modelChoice.getSelectedIndex();
                showModel(true);
                highlight();
            }
        });
        JPanel top = new JPanel(new BorderLayout());
        top.add(toolbar().getComponent(), BorderLayout.CENTER);
        top.add(modelChoice, BorderLayout.EAST);
        panel.setToolbar(top);
        JPanel content = new JPanel(new BorderLayout());
        content.add(JBCefApp.isSupported() ? browser() : unsupported(), BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);
        panel.setContent(content);
        CadPreview preview = CadPreview.getInstance(project);
        preview.addListener(this, this);
        CadRunResult last = preview.last();
        if (last != null) shown(last);
    }

    JComponent getComponent() {
        return panel;
    }

    private JComponent browser() {
        browser = JBCefBrowser.createBuilder().build();
        Disposer.register(this, browser);
        JBCefLocalRequestHandler files = new JBCefLocalRequestHandler(PROTOCOL, HOST);
        for (String file : pageFiles()) {
            files.addResource(file, () -> new JBCefStreamResourceHandler(pageFile(file), mimeType(file), this, Map.of()));
        }
        files.addResource(MODEL_PATH, () -> new JBCefStreamResourceHandler(model(), "model/gltf-binary", this, NOT_CACHED));
        files.addResource(SHAPE_PATH, () -> new JBCefStreamResourceHandler(new ByteArrayInputStream(shape), "model/gltf-binary", this, NOT_CACHED));
        browser.getJBCefClient().addRequestHandler(files, browser.getCefBrowser());
        JBCefJSQuery pick = JBCefJSQuery.create((JBCefBrowserBase) browser);
        Disposer.register(this, pick);
        pick.addHandler(nodes -> {
            ApplicationManager.getApplication().invokeLater(() -> picked(nodes), project.getDisposed());
            return null;
        });
        browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame frame, int httpStatusCode) {
                if (!frame.isMain()) return;
                cefBrowser.executeJavaScript("window.cadPick = nodes => {" + pick.inject("nodes") + "};", cefBrowser.getURL(), 0);
                pageLoaded = true;
                ApplicationManager.getApplication().invokeLater(() -> {
                    if (!models.isEmpty()) {
                        showModel(true);
                        highlight();
                    }
                });
            }
        }, browser.getCefBrowser());
        String theme = JBColor.isBright() ? "light" : "dark";
        browser.loadURL(PROTOCOL + "://" + HOST + "/index.html?theme=" + theme);
        return browser.getComponent();
    }

    private static JComponent unsupported() {
        JBLabel message = new JBLabel("The preview needs JCEF, which this IDE does not provide. "
                + "The glTF files of the runs open in any glTF viewer.", SwingConstants.CENTER);
        message.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return message;
    }

    private List<String> pageFiles() {
        List<String> files = new ArrayList<>(PAGE_FILES);
        try (InputStream three = pageFile(THREE_FILES)) {
            new String(three.readAllBytes(), StandardCharsets.UTF_8).lines().forEach(it -> files.add(THREE_DIR + it));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return files;
    }

    private InputStream pageFile(String file) {
        InputStream stream = CadPreviewPanel.class.getResourceAsStream("/preview/" + file);
        if (stream == null) throw new IllegalStateException("Missing preview file " + file);
        return stream;
    }

    private InputStream model() {
        List<CadModel> shown = models;
        return new ByteArrayInputStream(selected < shown.size() ? shown.get(selected).glb() : new byte[0]);
    }

    private static String mimeType(String file) {
        if (file.endsWith(".html")) return "text/html";
        if (file.endsWith(".js")) return "text/javascript";
        return "application/octet-stream";
    }

    private ActionToolbar toolbar() {
        DefaultActionGroup actions = new DefaultActionGroup();
        actions.add(new DumbAwareAction("Rerun", "Run the previewed script again", AllIcons.Actions.Rerun) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CadPreview.getInstance(project).rerun();
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
                e.getPresentation().setEnabled(CadPreview.getInstance(project).last() != null);
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.BGT;
            }
        });
        actions.add(new DumbAwareAction("Stop Runner", "Stop the runner and the script it runs", AllIcons.Actions.Suspend) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                CadRunner.getInstance(project).stop();
            }
        });
        actions.addSeparator();
        actions.add(new DumbAwareAction("Fit", "Fit the view to the model", AllIcons.General.FitContent) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                call("cadPreview.fit()");
            }
        });
        actions.add(new DumbAwareToggleAction("Edges", "Show the edges of the faces", AllIcons.Graph.Grid) {
            @Override
            public boolean isSelected(@NotNull AnActionEvent e) {
                return edges;
            }

            @Override
            public void setSelected(@NotNull AnActionEvent e, boolean state) {
                edges = state;
                call("cadPreview.edges(" + state + ")");
            }

            @Override
            public @NotNull ActionUpdateThread getActionUpdateThread() {
                return ActionUpdateThread.EDT;
            }
        });
        actions.addSeparator();
        actions.add(new DumbAwareAction("Settings", "CAD DSL settings", AllIcons.General.Settings) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, CadSettingsConfigurable.class);
            }
        });
        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("CadPreview", actions, true);
        toolbar.setTargetComponent(panel);
        return toolbar;
    }

    @Override
    public void running(@NotNull CadRunRequest request) {
        ApplicationManager.getApplication().invokeLater(() -> {
            status.setForeground(JBColor.foreground());
            status.setText("Running " + fileName(request.script()) + "…");
        }, project.getDisposed());
    }

    @Override
    public void shown(@NotNull CadRunResult result) {
        ApplicationManager.getApplication().invokeLater(() -> {
            String script = fileName(result.request().script());
            if (!result.succeeded()) {
                status.setForeground(JBColor.RED);
                status.setText(script + ": " + firstLine(result.error()));
                status.setToolTipText(result.trace());
            } else if (result.models().isEmpty()) {
                status.setForeground(JBColor.foreground());
                status.setText(script + " displays nothing: call display() to preview a shape or an assembly");
            } else {
                status.setForeground(JBColor.foreground());
                status.setText(script + " in " + result.millis() + " ms");
                status.setToolTipText(null);
            }
            // a failed run keeps the models of the last one that worked
            if (result.models().isEmpty()) return;
            boolean refit = !result.request().script().equals(shownScript);
            shownScript = result.request().script();
            models = result.models();
            DefaultComboBoxModel<String> names = new DefaultComboBoxModel<>(models.stream().map(CadModel::name).toArray(String[]::new));
            selected = refit || selected >= models.size() ? models.size() - 1 : selected;
            names.setSelectedItem(models.get(selected).name());
            modelChoice.setModel(names);
            modelChoice.setVisible(models.size() > 1);
            features = result.features();
            showModel(refit);
            highlight();
        }, project.getDisposed());
    }

    @Override
    public void featureSelected(@Nullable String feature) {
        ApplicationManager.getApplication().invokeLater(this::highlight, project.getDisposed());
    }

    /**
     * Selects the feature of the part clicked in the model, and opens the line that made it. The part comes first, then
     * the placements above it: the line placing a part of a jar opens, for instance. Clicking nothing selects nothing.
     *
     * @param json The glTF nodes picked, the deepest first, see preview.js
     */
    private void picked(String json) {
        List<Integer> nodes = new ArrayList<>();
        JsonParser.parseString(json).getAsJsonArray().forEach(it -> nodes.add(it.getAsInt()));
        CadHighlight highlight = shownHighlight();
        List<String> picked = highlight.picked(nodes);
        CadPreview preview = CadPreview.getInstance(project);
        for (String id : picked) {
            CadFeature feature = highlight.feature(id);
            VirtualFile file = feature == null ? null : CadSources.projectSource(project, shownScript, feature);
            if (file == null) continue;
            preview.selectFeature(id);
            // after the CAD Structure selects it, so that the caret moved to its line keeps it selected
            ApplicationManager.getApplication().invokeLater(
                    () -> new OpenFileDescriptor(project, file, feature.line() - 1, 0).navigate(true), project.getDisposed());
            return;
        }
        preview.selectFeature(picked.isEmpty() ? null : picked.getFirst());
    }

    // the nodes of the features in the model shown
    private CadHighlight shownHighlight() {
        List<CadModel> shown = models;
        return new CadHighlight(features, selected < shown.size() ? shown.get(selected).feature() : null);
    }

    // Highlights the parts of the selected feature in the model shown, and shows its own shapes over them
    private void highlight() {
        String feature = CadPreview.getInstance(project).selectedFeature();
        CadHighlight highlight = shownHighlight();
        List<Integer> nodes = highlight.nodes(feature);
        int request = ++highlights;
        call("cadPreview.highlight(" + nodes + ")");
        if (feature == null || nodes.isEmpty() || !highlight.hasShape(feature)) {
            call("cadPreview.overlay(null)");
            return;
        }
        CadRunner.getInstance(project).shape(feature).thenAccept(glb -> ApplicationManager.getApplication().invokeLater(() -> {
            // a later selection wins
            if (request != highlights) return;
            shape = glb == null ? new byte[0] : glb;
            call(glb == null ? "cadPreview.overlay(null)" : "cadPreview.overlay('" + PROTOCOL + "://" + HOST + "/" + SHAPE_PATH + "?v=" + request + "')");
        }, project.getDisposed()));
    }

    private void showModel(boolean refit) {
        if (!pageLoaded) return;
        // the version changes the URL, so the page fetches the new model
        call("cadPreview.show('" + PROTOCOL + "://" + HOST + "/" + MODEL_PATH + "?v=" + ++version + "', " + refit + ")");
    }

    private void call(String javaScript) {
        if (browser != null && pageLoaded) browser.getCefBrowser().executeJavaScript(javaScript, browser.getCefBrowser().getURL(), 0);
    }

    private static String fileName(String script) {
        return Path.of(script).getFileName().toString();
    }

    private static String firstLine(String text) {
        int end = text.indexOf('\n');
        return end < 0 ? text : text.substring(0, end);
    }

    @Override
    public void dispose() {}
}
