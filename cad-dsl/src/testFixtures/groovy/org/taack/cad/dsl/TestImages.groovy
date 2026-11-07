package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * The images of the DSL tests: display(image()) renders the shape of a test to
 * {@code <cad.test.images>/<qualified test class>/<test method>.png}, when the kernel renders images off screen (its test task
 * sets the system property cad.test.images); else image() is null, and display(null) only checks the shape.
 * <p>
 * Registered for every test by autodetection (META-INF/services), it names the images after the running test, then
 * converts with {@link CadImages} those a kernel would have left as Netpbm data.
 */
@CompileStatic
class TestImages implements BeforeEachCallback, AfterEachCallback {
    private static final ThreadLocal<File> testDir = new ThreadLocal<>()
    private static final ThreadLocal<String> testName = new ThreadLocal<>()
    private static final ThreadLocal<List<File>> written = ThreadLocal.withInitial { [] as List<File> }

    /** The image of the running test, a new one at each call: name.png, then name-2.png ... null without images */
    static String image() {
        File dir = testDir.get()
        if (dir == null) return null
        dir.mkdirs()
        List<File> files = written.get()
        File f = new File(dir, files.empty ? "${testName.get()}.png" : "${testName.get()}-${files.size() + 1}.png")
        files << f
        f.path
    }

    @Override
    void beforeEach(ExtensionContext context) {
        String root = System.getProperty('cad.test.images')
        written.get().clear()
        if (!root) {
            testDir.remove()
            return
        }
        testDir.set(new File(root, context.requiredTestClass.name))
        testName.set(context.requiredTestMethod.name.replaceAll(/[^A-Za-z0-9._-]+/, '-').replaceAll(/^-|-$/, ''))
    }

    @Override
    void afterEach(ExtensionContext context) {
        for (File f in written.get()) {
            CadImages.convert(f)
        }
        written.get().clear()
    }
}
