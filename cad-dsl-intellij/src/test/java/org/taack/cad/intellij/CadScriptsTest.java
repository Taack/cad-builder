package org.taack.cad.intellij;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;

import java.nio.file.Path;

public class CadScriptsTest extends LightJavaCodeInsightFixtureTestCase {
    // the JDK running the tests: the mock JDK of the test framework has no java.lang.Math
    private static final LightProjectDescriptor WITH_CAD_DSL = new DefaultLightProjectDescriptor(
            () -> JavaSdk.getInstance().createJdk("test JDK", System.getProperty("java.home"), false)) {
        @Override
        public void configureModule(@NotNull Module module, @NotNull ModifiableRootModel model, @NotNull ContentEntry contentEntry) {
            super.configureModule(module, model, contentEntry);
            Path jar = Path.of(System.getProperty("cad.dsl.jar"));
            PsiTestUtil.addLibrary(model, "cad-dsl", jar.getParent().toString(), jar.getFileName().toString());
        }
    };

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return WITH_CAD_DSL;
    }

    public void testScriptsOfAModuleWithTheDslAreCadScripts() {
        assertTrue(CadScripts.isCadScript(groovy("rail.groovy", "cd().box(1, 2, 3).display()")));
    }

    public void testClassesAndGradleScriptsAreNot() {
        assertFalse(CadScripts.isCadScript(groovy("Contour.groovy", "class Contour {}")));
        assertFalse(CadScripts.isCadScript(groovy("build.gradle", "plugins { id 'java' }")));
    }

    public void testImportsAreReadFromTheDsl() {
        CadScripts.Imports imports = CadScripts.imports(groovy("rail.groovy", "cd()"));
        assertTrue(imports.packages().contains("org.taack.cad.dsl.geom"));
        assertTrue(imports.staticClasses().contains("org.taack.cad.dsl.CadDsl"));
    }

    public void testDslClassesResolveWithoutImports() {
        PsiElement vec = resolveAtCaret("def v = new V<caret>ec(1, 2, 3)");
        assertEquals("org.taack.cad.dsl.geom.Vec", ((PsiClass) vec).getQualifiedName());
    }

    public void testStaticMethodsResolveWithoutImports() {
        PsiMethod cd = (PsiMethod) resolveAtCaret("c<caret>d().box(1, 2, 3)");
        assertEquals("org.taack.cad.dsl.CadDsl", cd.getContainingClass().getQualifiedName());
        PsiMethod sqrt = (PsiMethod) resolveAtCaret("def side = sq<caret>rt(2)");
        assertEquals("java.lang.Math", sqrt.getContainingClass().getQualifiedName());
    }

    public void testFirstStatementHasARunMarker() {
        GroovyFile script = groovy("rail.groovy", "// a rail\ncd().box(1, 2, 3).display()\ncd()");
        CadRunLineMarkerContributor markers = new CadRunLineMarkerContributor();
        PsiElement first = script.findElementAt(script.getText().indexOf("cd()"));
        PsiElement second = script.findElementAt(script.getText().lastIndexOf("cd()"));
        assertNotNull(markers.getInfo(first));
        assertNull(markers.getInfo(second));
    }

    private GroovyFile groovy(String name, String text) {
        return (GroovyFile) myFixture.configureByText(name, text);
    }

    private PsiElement resolveAtCaret(String text) {
        myFixture.configureByText("part.groovy", text);
        PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion();
        PsiElement resolved = reference.resolve();
        assertNotNull("Unresolved: " + reference.getCanonicalText(), resolved);
        return resolved;
    }
}
