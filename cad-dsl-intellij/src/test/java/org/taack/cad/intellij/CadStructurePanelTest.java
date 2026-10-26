package org.taack.cad.intellij;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;
import java.util.Map;

public class CadStructurePanelTest extends BasePlatformTestCase {
    private CadStructurePanel panel;

    private static CadFeature feature(String id, String kind, int line, CadFeature... children) {
        return new CadFeature(id, kind, id, "block", "block.groovy", line, false, false, null, List.of(children));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.configureByText("block.groovy", """
                cd().box(10, 10, 5).cut {
                    [-2, 2].each { x -> position(new Vec(x, 0, 0)) { cylinder(1, 10) } }
                }.display()
                """);
        // the run of a script of the same name elsewhere: the features of the editor are found by the name of its file
        List<CadFeature> features = List.of(feature("solid0", "solid", 1,
                feature("solid0/box0", "box", 1),
                feature("solid0/cut0", "cut", 1,
                        feature("solid0/cut0/cylinder0", "cylinder", 2),
                        feature("solid0/cut0/cylinder1", "cylinder", 2))));
        CadRunRequest request = new CadRunRequest("/nowhere/block.groovy", List.of());
        CadPreview.getInstance(getProject()).show(new CadRunResult(request, List.of(), Map.of(), features, 10, null, null));
        panel = new CadStructurePanel(getProject());
        PlatformTestUtil.dispatchAllInvocationEventsInIdeEventQueue();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            Disposer.dispose(panel);
            CadPreview.getInstance(getProject()).selectFeature(null);
        } finally {
            super.tearDown();
        }
    }

    private String selectedAtLine(int line) {
        myFixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(line - 1, 4));
        return CadPreview.getInstance(getProject()).selectedFeature();
    }

    public void testTheCaretSelectsTheFeatureMadeAtItsLine() {
        assertEquals("solid0/cut0/cylinder0", selectedAtLine(2));
    }

    public void testTheDeepestFeatureOfALineIsSelected() {
        assertEquals("solid0/box0", selectedAtLine(1));
    }

    public void testALineWithoutFeatureKeepsTheSelection() {
        selectedAtLine(2);
        assertEquals("solid0/cut0/cylinder0", selectedAtLine(3));
    }
}
