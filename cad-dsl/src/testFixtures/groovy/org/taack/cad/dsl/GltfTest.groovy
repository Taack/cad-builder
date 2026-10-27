package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.taack.cad.dsl.CadDsl.cd

@CompileStatic
@Tag('gltf')
class GltfTest {
    private static final double TOLERANCE = 1e-5
    private static final double HALF_SQRT2 = Math.sqrt(0.5)

    @TempDir
    File dir

    @AfterEach
    void viewerAgain() {
        CadDisplay.redirect(null)
    }

    private static List<Double> extent(Map position) {
        List<Number> min = (List<Number>) position.min, max = (List<Number>) position.max
        (0..2).collect { int axis -> max[axis].doubleValue() - min[axis].doubleValue() }
    }

    private static void assertValues(List<Double> expected, Object actual) {
        List<Number> values = (List<Number>) actual
        assertEquals(expected.size(), values.size(), "$actual")
        expected.eachWithIndex { double e, int i -> assertEquals(e, values[i].doubleValue(), TOLERANCE, "$actual") }
    }

    @Test
    void "A solid is written as a mesh of triangles and boundary lines"() {
        File file = new File(dir, 'box' + CadDisplay.GLTF_EXTENSION)
        cd().box(1, 2, 3).display(file.path)

        GlbFile gltf = GlbFile.read(file)
        assertEquals(1, gltf.list('meshes').size())
        Map faces = gltf.primitive(0, GlbFile.MODE_TRIANGLES)
        Map edges = gltf.primitive(0, GlbFile.MODE_LINES)
        // 6 faces of 2 triangles, 12 straight edges
        assertEquals(12 * 3, gltf.count(faces.indices as int))
        assertEquals(12 * 2, gltf.count(((Map) edges.attributes).POSITION as int))
        Map position = gltf.list('accessors')[((Map) faces.attributes).POSITION as int]
        assertValues([1d, 2d, 3d], extent(position))
        // in meters, Y up
        Map root = gltf.list('nodes').first()
        assertEquals(0, root.mesh)
        assertValues([0.001d, 0.001d, 0.001d], root.scale)
        assertValues([-HALF_SQRT2, 0d, 0d, HALF_SQRT2], root.rotation)
    }

    @Test
    void "An assembly is written as a tree of nodes sharing the meshes of its parts"() {
        File file = new File(dir, 'train' + CadDisplay.GLTF_EXTENSION)
        AnimationTest.gearTrain().display(file.path)

        GlbFile gltf = GlbFile.read(file)
        // base, the two gears, pin, rack
        assertEquals(5, gltf.list('meshes').size())
        Map root = gltf.node('gear train')
        assertEquals(6, ((List) root.children).size())
        List<Map> pins = gltf.list('nodes').findAll { it.name == 'pin' }
        assertEquals(2, pins.size())
        assertEquals(pins[0].mesh, pins[1].mesh)
        Map base = gltf.list('materials').find { it.name == 'base' }
        assertNotNull(base)
    }

    @Test
    void "A STEP file is imported with its tree, names and colors"() {
        File step = new File(dir, 'colors.step')
        CadDsl.assembly('colors') {
            place(part('block', Color.RED) { box(10, 10, 10) })
            def pin = part('pin', Color.BLUE) { cylinder(1, 5) }
            place(pin) { translate(20, 0, 0) }
            place(pin) { translate(30, 0, 0) }
        }.display(step.path)
        File file = new File(dir, 'colors' + CadDisplay.GLTF_EXTENSION)
        CadDsl.assembly { place(importStep(step.path)) }.display(file.path)

        GlbFile gltf = GlbFile.read(file)
        assertEquals(2, gltf.list('meshes').size())
        assertNotNull(gltf.node('colors'))
        List<Map> pins = gltf.list('nodes').findAll { it.name == 'pin' }
        assertEquals(2, pins.size())
        assertEquals(pins[0].mesh, pins[1].mesh)
        Map pin = gltf.list('materials').find { it.name == 'pin' }
        assertValues([0d, 0d, 1d, 1d], ((Map) pin.pbrMetallicRoughness).baseColorFactor)
    }

    @Test
    void "The animation has a key per frame for each moving component"() {
        File file = new File(dir, 'train' + CadDisplay.GLTF_EXTENSION)
        AnimationTest.gearTrain().display(file.path)

        GlbFile gltf = GlbFile.read(file)
        Map animation = gltf.list('animations').first()
        List<Map> samplers = (List<Map>) animation.samplers
        float[] times = gltf.floats(samplers.first().input as int)
        // 4 s at 12 frames per second
        assertEquals(48, times.length)
        assertEquals(47 / 12d, times.last() as double, TOLERANCE)
        assertTrue(samplers.every { gltf.count(it.output as int) == 48 })
        List<Map> nodes = gltf.list('nodes')
        Set<String> moving = ((List<Map>) animation.channels).collect { nodes[((Map) it.target).node as int].name as String }.toSet()
        assertTrue(moving.containsAll(['pin', 'rack']), "$moving")
        assertTrue(!moving.contains('base'), "$moving")
    }

    @Test
    void "Displays without a file name follow the redirection"() {
        File file = new File(dir, 'redirected' + CadDisplay.GLTF_EXTENSION)
        CadDisplay.redirect { file.path }
        cd().cylinder(1, 2).display()

        assertNotNull(GlbFile.read(file).primitive(0, GlbFile.MODE_TRIANGLES))
    }
}
