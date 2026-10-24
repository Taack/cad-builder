package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.file.Path

import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class CadScriptTest {
    @TempDir
    Path dir

    @Test
    void "Sample script exports its shape"() {
        File step = dir.resolve('block.step').toFile()
        CadScript.run(new File('../samples/pillow-block.groovy'), [step.path])
        assertTrue(step.length() > 0)
    }

    @Test
    void "Scripts can use the DSL without imports"() {
        File script = dir.resolve('sphere.groovy').toFile()
        File step = dir.resolve('sphere.step').toFile()
        script.text = 'cd().position(new Vec(0, 0, 1)) { sphere(sqrt(4)) }.display(args[0])'
        CadScript.run(script, [step.path])
        assertTrue(step.length() > 0)
    }
}
