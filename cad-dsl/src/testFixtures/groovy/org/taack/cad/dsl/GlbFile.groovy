package org.taack.cad.dsl

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets

/**
 * A binary glTF 2.0 file read back, its layout checked, to test what the kernels write.
 */
@CompileStatic
class GlbFile {
    static final int MAGIC = 0x46546C67
    static final int CHUNK_JSON = 0x4E4F534A
    static final int CHUNK_BIN = 0x004E4942
    static final int MODE_LINES = 1
    static final int MODE_TRIANGLES = 4
    private static final Map<String, Integer> COMPONENTS = [SCALAR: 1, VEC3: 3, VEC4: 4]
    private static final int COMPONENT_SIZE = 4

    final Map json
    private final ByteBuffer bin

    private GlbFile(Map json, ByteBuffer bin) {
        this.json = json
        this.bin = bin
    }

    static GlbFile read(File file) {
        ByteBuffer glb = ByteBuffer.wrap(file.bytes).order(ByteOrder.LITTLE_ENDIAN)
        check(glb.getInt() == MAGIC, "not a glTF file: $file")
        check(glb.getInt() == 2, 'glTF version')
        check(glb.getInt() == file.length(), 'total length')
        byte[] json = new byte[glb.getInt()]
        check(glb.getInt() == CHUNK_JSON, 'JSON chunk first')
        glb.get(json)
        ByteBuffer bin = null
        if (glb.hasRemaining()) {
            int length = glb.getInt()
            check(glb.getInt() == CHUNK_BIN, 'BIN chunk second')
            bin = glb.slice(glb.position(), length).order(ByteOrder.LITTLE_ENDIAN)
            glb.position(glb.position() + length)
        }
        check(!glb.hasRemaining(), 'nothing after the chunks')
        GlbFile gltf = new GlbFile((Map) new JsonSlurper().parseText(new String(json, StandardCharsets.UTF_8)), bin)
        gltf.checkAccessors()
        gltf
    }

    private static void check(boolean ok, String what) {
        if (!ok) throw new IllegalStateException("Invalid glTF: $what")
    }

    private void checkAccessors() {
        List<Map> buffers = list('buffers')
        if (buffers) check((buffers.first().byteLength as int) == bin.limit(), 'buffer length')
        list('accessors').each { Map accessor ->
            Map view = list('bufferViews')[accessor.bufferView as int]
            int length = (accessor.count as int) * COMPONENTS[(String) accessor.type] * COMPONENT_SIZE
            check((view.byteLength as int) == length, "view length of $accessor")
            check((view.byteOffset as int) % COMPONENT_SIZE == 0, "view alignment of $accessor")
            check((view.byteOffset as int) + length <= bin.limit(), "view of $accessor in the buffer")
        }
    }

    List<Map> list(String name) {
        (List<Map>) json[name] ?: []
    }

    Map node(String name) {
        list('nodes').find { it.name == name }
    }

    /**
     * @return The values of an accessor
     */
    float[] floats(int accessor) {
        Map a = list('accessors')[accessor]
        Map view = list('bufferViews')[a.bufferView as int]
        float[] values = new float[(a.count as int) * COMPONENTS[(String) a.type]]
        bin.slice(view.byteOffset as int, view.byteLength as int).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(values)
        values
    }

    int count(int accessor) {
        list('accessors')[accessor].count as int
    }

    /**
     * @return The primitive of the given mode of mesh, null if none
     */
    Map primitive(int mesh, int mode) {
        ((List<Map>) list('meshes')[mesh].primitives).find { it.mode == mode }
    }
}
