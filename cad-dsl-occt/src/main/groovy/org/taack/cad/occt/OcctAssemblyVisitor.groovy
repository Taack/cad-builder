package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslNode
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

import static org.taack.occt.NativeLib.*
import org.taack.occt.NativeLib
import static org.taack.cad.occt.Natives.own

/**
 * Assembly tree of native CadNodes: parts made of the solids built by {@link CadDslVisitor}s, and assemblies
 * of placed nodes. A node placed several times is shared.
 */
@CompileStatic
class OcctAssemblyVisitor implements ICadDslAssemblyVisitor {
    // Owns the native objects of this assembly (see Natives); the nodes copy the shapes and share their components
    private final Arena nativeScope = Natives.openScope()

    final MemorySegment node

    OcctAssemblyVisitor(String name) {
        try (Arena arena = Arena.ofConfined()) {
            node = own(handle_CadNode__assembly__name(cString(arena, name)), NativeLib::delete_handle_CadNode)
        }
    }

    @Override
    ICadDslNode visitPart(String name, Color color, ICadDslVisitor solid) {
        MemorySegment shape = ((CadDslVisitor) solid).resultShape()
        if (!shape) throw new IllegalArgumentException("Empty part ${name ?: ''}: build a solid before adding it to the assembly")
        CadDslVisitor.Tr.cur "assembly part: $name, $shape, color: $color"
        try (Arena arena = Arena.ofConfined()) {
            MemorySegment n = color ?
                    handle_CadNode__part__TopoDS_Shape_name_hasColor_r_g_b(shape, cString(arena, name), 1, color.r, color.g, color.b) :
                    handle_CadNode__part__TopoDS_Shape_name_hasColor_r_g_b(shape, cString(arena, name), 0, 0d, 0d, 0d)
            new OcctPart(own(n, NativeLib::delete_handle_CadNode), shape)
        }
    }

    @Override
    void visitPlace(ICadDslNode node, Trsf location) {
        MemorySegment n = node instanceof OcctAssemblyVisitor ? ((OcctAssemblyVisitor) node).node : ((OcctPart) node).node
        double[] v = location.values()
        MemorySegment trsf = own(new_gp_Trsf(), NativeLib::delete_gp_Trsf)
        _gp_Trsf__SetValues(trsf, v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11])
        _CadNode__AddComponent__node_location(this.node, n, trsf)
    }

    @Override
    Frame visitFaceFrame(ICadDslNode part, Vec direction, Vec position) {
        Vec p = position ?: new Vec()
        double[] f = Natives.doubles(R6_TopoDS_Shape__PlanarFace__dir_hasPos_pos(((OcctPart) part).shape,
                direction.x, direction.y, direction.z, position ? 1 : 0, p.x, p.y, p.z), 6)
        f ? new Frame(new Vec(f[0], f[1], f[2]), new Vec(f[3], f[4], f[5])) : null
    }

    @Override
    Frame visitAxisFrame(ICadDslNode part, Vec position) {
        Vec p = position ?: new Vec()
        double[] f = Natives.doubles(R6_TopoDS_Shape__CylindricalFaceAxis__hasPos_pos(((OcctPart) part).shape,
                position ? 1 : 0, p.x, p.y, p.z), 6)
        f ? new Frame(new Vec(f[0], f[1], f[2]), new Vec(f[3], f[4], f[5])) : null
    }

    @Override
    void display(String fileName) {
        if (fileName) {
            try (Arena arena = Arena.ofConfined()) {
                MemorySegment t = arena.allocateFrom(fileName)
                if (fileName.endsWith(".step")) {
                    write_step_assembly(node, t)
                } else if (fileName.endsWith(".stl")) {
                    write_stl_assembly(node, t)
                } else
                    dumpAssembly(node, 1920, 1080, t)
            }
        } else visualize_assembly(node)
    }

    private static MemorySegment cString(Arena arena, String s) {
        s == null ? MemorySegment.NULL : arena.allocateFrom(s)
    }
}
