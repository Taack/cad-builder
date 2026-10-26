package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * The images of the DSL tests: display(image()) renders the shape of a test to
 * {@code <cad.test.images>/<test class>/<test method>.png}, when the kernel renders images off screen (its test task
 * sets the system property cad.test.images); else image() is null, and display(null) only checks the shape.
 * <p>
 * Registered for every test by autodetection (META-INF/services), it names the images after the running test, then
 * turns into PNG the PPM data an OCCT built without FreeImage writes whatever the extension.
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
        testDir.set(new File(root, context.requiredTestClass.simpleName))
        testName.set(context.requiredTestMethod.name.replaceAll(/[^A-Za-z0-9._-]+/, '-').replaceAll(/^-|-$/, ''))
    }

    @Override
    void afterEach(ExtensionContext context) {
        for (File f in written.get()) {
            if (f.isFile()) toPng(f)
        }
        written.get().clear()
    }

    // Rewrites as PNG a file holding binary PPM (P6) data
    private static void toPng(File f) {
        byte[] data = f.bytes
        if (data.length < 2 || (char) data[0] != ('P' as char) || (char) data[1] != ('6' as char)) return
        // header: P6, width, height, maxval, separated by whitespace, # comments to the end of their line
        int at = 2
        int[] values = new int[3]
        for (int i = 0; i < 3; i++) {
            while (true) {
                char c = (char) data[at]
                if (c == ('#' as char)) {
                    while ((char) data[at] != ('\n' as char)) at++
                } else if (!Character.isWhitespace(c)) break
                at++
            }
            int v = 0
            while (Character.isDigit((char) data[at])) v = v * 10 + Character.digit((char) data[at++], 10)
            values[i] = v
        }
        at++
        int width = values[0], height = values[1]
        if (values[2] > 255) return
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int r = data[at++] & 0xff, g = data[at++] & 0xff, b = data[at++] & 0xff
                img.setRGB(x, y, (r << 16) | (g << 8) | b)
            }
        }
        ImageIO.write(img, 'png', f)
    }
}
