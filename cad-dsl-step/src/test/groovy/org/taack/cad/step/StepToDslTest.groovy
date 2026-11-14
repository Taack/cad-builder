package org.taack.cad.step

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.AssemblyTest
import org.taack.cad.dsl.CadDslSolid
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

/**
 * Models made with the DSL, exported to STEP, converted back to scripts, which must rebuild them.
 */
@CompileStatic
class StepToDslTest {
    @TempDir
    File dir

    /**
     * @return The script converted from the STEP file, checked to rebuild it
     */
    private String converted(File step) {
        File script = new File(dir, step.name.replace('.step', '.groovy'))
        script.text = StepToDsl.convert(step)
        assertEquals(0d, StepToDsl.check(step, script), 1e-6, script.text)
        script.text
    }

    private String converted(String name, CadDslSolid model) {
        File step = new File(dir, "${name}.step")
        model.display(step.path)
        converted(step)
    }

    @Test
    void "A box is a box"() {
        String script = converted('box', cd().position(new Vec(1, 2, 3)) { box(10, 20, 30) })
        assertTrue(script.contains('box(10, 20, 30)'), script)
    }

    @Test
    void "A pillow block is made of prisms"() {
        String script = converted('block', cd().box(80, 60, 10).topZ().wireFrom() {
            [[-34, -24], [0, 48], [68, 0], [0, -48]].each { int x, int y ->
                move(x, y)
                circle(4)
            }
        }.toFace().hole(-2.4).topZ().wireFrom() {
            circle(4)
        }.toFace().prism(2.4))
        assertTrue(script.contains('.toFace().prism('), script)
    }

    @Test
    void "A turned part is a revolution"() {
        String script = converted('turned', cd().wireFrom(new Vec2d()) {
            edge(new Vec2d(10, 0))
            edge(new Vec2d(10, 5))
            edge(new Vec2d(6, 8))
            edge(new Vec2d(6, 20))
            edge(new Vec2d(0, 20))
            edge(new Vec2d())
        }.toFace().revolution(new Vec(), new Vec(0, 1, 0), 360))
        assertTrue(script.contains('.toFace().revolution('), script)
    }

    @Test
    void "Holes across the prisms are cut from them"() {
        String script = converted('drilled', cd().box(40, 30, 20).topZ().wireFrom() {
            circle(5)
        }.toFace().hole(-20).topX().wireFrom() {
            circle(3)
        }.toFace().hole(-40))
        assertTrue(script.contains('.toFace().hole('), script)
    }

    /**
     * A 5/8"-24 thread above a flat: neither a revolution nor 2.5D, its helical flanks are free-form faces, which OCCT
     * did not end defeaturing. The thread is drawn as rings, the flat cut from them.
     */
    @Test
    @Timeout(value = 300, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void "A thread is made of rings, the features beside it cut from them"() {
        double pitch = 25.4d / 24d
        File step = new File(dir, 'threaded.step')
        cd().cylinder(7.6d, 20d).cut {
            position(new Vec(6d, -10d, -1d)) { box(5d, 20d, 9d) }
        }.fuse {
            position(new Vec(0d, 0d, 10d)) {
                helix(7.5d, pitch, 8d).profile(new Vec2d(-0.1d, 0d)) {
                    edge(new Vec2d(0.45d, 0.4d))
                    edge(new Vec2d(0.45d, 0.55d))
                    edge(new Vec2d(-0.1d, 0.95d))
                    edge(new Vec2d(-0.1d, 0d))
                }
            }
        }.display(step.path)
        File script = new File(dir, 'threaded.groovy')
        script.text = StepToDsl.convert(step)
        assertTrue(script.text.contains('drawn as rings'), script.text)
        assertTrue(script.text.contains('.toFace().revolution('), script.text)
        // rings against a helix: their teeth are half in the grooves of the other
        double difference = StepToDsl.check(step, script)
        assertTrue(difference < 0.02d, "difference ${difference}\n${script.text}")
    }

    @Test
    void "Assemblies keep their tree, each part written once"() {
        File step = new File(dir, 'cart.step')
        AssemblyTest.cart().display(step.path)
        String script = converted(step)
        assertTrue(script.contains("assembly('cart')"), script)
        // chassis, wheel, shaft and cap
        assertEquals(4, script.count('= part('), script)
        // axle: shaft and 2 wheels, cart: chassis, 2 axles and the cap
        assertEquals(7, script.count('place('), script)
    }
}
