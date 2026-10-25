package org.taack.cad.runner

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.taack.cad.dsl.GlbFile

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertFalse
import static org.junit.jupiter.api.Assertions.assertTrue

@CompileStatic
class CadRunnerTest {
    @TempDir
    File dir

    // The events answering requests, the ready one first
    private List<Map> serve(List<Map> requests) {
        StringWriter output = new StringWriter()
        String input = requests.collect { JsonOutput.toJson(it) }.join('\n')
        new CadRunner(new File(dir, 'work')).serve(new BufferedReader(new StringReader(input)), output)
        output.toString().readLines().collect { (Map) new JsonSlurper().parseText(it) }
    }

    private File script(String name, String text) {
        File script = new File(dir, name)
        script.text = text
        script
    }

    @Test
    void "The runner announces its protocol"() {
        assertEquals([type: 'ready', protocol: CadRunner.PROTOCOL], serve([]).first())
    }

    @Test
    void "A run reports a glTF file per display and the parameters"() {
        File block = script('block.groovy', '''\
            cd().box(param('length', 10), 2, 3).display()
            cd().cylinder(1, 2).display()'''.stripIndent())

        List<Map> events = serve([[type: 'run', id: 1, script: block.path, params: [length: 20]]])

        List<Map> displays = events.findAll { it.type == 'display' }
        assertEquals(2, displays.size())
        GlbFile box = GlbFile.read(new File((String) displays.first().file))
        Map position = box.list('accessors')[((Map) box.primitive(0, GlbFile.MODE_TRIANGLES).attributes).POSITION as int]
        assertEquals(20d, ((List<Number>) position.max)[0].doubleValue() - ((List<Number>) position.min)[0].doubleValue(), 1e-4d)
        Map done = events.last()
        assertEquals('done', done.type)
        assertEquals(1, done.id)
        assertEquals([length: 20.0], done.params)
    }

    @Test
    void "A failing script is reported and the runner goes on"() {
        File broken = script('broken.groovy', 'cd().box(1, 2, 3).display()\nthrow new IllegalStateException("no rail")')
        File fine = script('fine.groovy', 'cd().box(1, 2, 3).display()')

        List<Map> events = serve([
                [type: 'run', id: 1, script: broken.path],
                [type: 'run', id: 2, script: fine.path]])

        Map error = events.find { it.type == 'error' }
        assertEquals(1, error.id)
        assertEquals('no rail', error.message)
        assertTrue(((String) error.trace).contains('broken.groovy'))
        // the model displayed before the failure is reported
        assertEquals(1, events.count { it.type == 'display' && it.id == 1 })
        assertEquals([type: 'done', id: 2], events.last().subMap(['type', 'id']))
    }

    @Test
    void "The files of a run are deleted when the next one starts"() {
        File fine = script('fine.groovy', 'cd().box(1, 2, 3).display()')

        List<Map> events = serve([[type: 'run', id: 1, script: fine.path], [type: 'run', id: 2, script: fine.path]])

        List<File> files = events.findAll { it.type == 'display' }.collect { new File((String) it.file) }
        assertFalse(files[0].exists())
        assertTrue(files[1].exists())
    }

    @Test
    void "Parameters given to a run do not leak into the next one"() {
        File block = script('block.groovy', "param('length', 10)")

        List<Map> events = serve([
                [type: 'run', id: 1, script: block.path, params: [length: 20]],
                [type: 'run', id: 2, script: block.path]])

        assertEquals([[length: 20.0], [length: 10.0]], events.findAll { it.type == 'done' }*.params)
    }

    @Test
    void "Requests after stop are ignored"() {
        List<Map> events = serve([[type: 'stop'], [type: 'unknown', id: 3]])
        assertEquals(['ready'], events*.type)
    }

    @Test
    void "Unknown requests are answered with an error"() {
        Map error = serve([[type: 'unknown', id: 3]]).last()
        assertEquals([type: 'error', id: 3], error.subMap(['type', 'id']))
    }
}
