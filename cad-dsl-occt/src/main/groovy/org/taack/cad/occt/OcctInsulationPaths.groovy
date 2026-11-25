package org.taack.cad.occt

import groovy.transform.CompileStatic
import org.taack.cad.dsl.Gap
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.kernel.CadKernels
import org.taack.cad.dsl.kernel.InsulationPaths
import org.taack.occt.NativeLib

import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

import static org.taack.cad.occt.Natives.lastError
import static org.taack.cad.occt.Natives.own
import static org.taack.occt.NativeLib.*

/**
 * The meshes of the solids and the paths between them, made by the C API (capi_insulation.cpp).
 */
@CompileStatic
final class OcctInsulationPaths implements InsulationPaths {
    private final MemorySegment paths

    OcctInsulationPaths(List<ICadDslVisitor> solids, int[] groups, double deflection, double step) {
        def shapes = own(new_TopTools_ListOfShape(), NativeLib::delete_TopTools_ListOfShape)
        solids.each { ICadDslVisitor solid ->
            def kernel = CadKernels.unwrap(solid)
            if (!(kernel instanceof CadDslVisitor)) throw new IllegalArgumentException("not an OCCT solid: $solid")
            _TopTools_ListOfShape__Append__TopoDS_Shape(shapes, ((CadDslVisitor) kernel).shape)
        }
        try (Arena arena = Arena.ofConfined()) {
            paths = own(new_InsulationPaths__shapes_groups_deflection_step(shapes, arena.allocateFrom(ValueLayout.JAVA_INT, groups), deflection, step), NativeLib::delete_InsulationPaths)
        }
        if (paths == MemorySegment.NULL) throw new IllegalStateException("insulation: ${lastError()}")
    }

    @Override
    Gap clearance(int from, int to, double limit) {
        gap(R_InsulationPaths__clearance__from_to_limit(paths, from, to, limit))
    }

    @Override
    Gap creepage(int from, int to, double groove, double limit) {
        gap(R_InsulationPaths__creepage__from_to_groove_limit(paths, from, to, groove, limit))
    }

    // {length, n, x1, y1, z1, ...}, of n points; with none, the length the path is longer than, or -1 for no path
    private static Gap gap(MemorySegment values) {
        if (values == MemorySegment.NULL) throw new IllegalStateException("insulation: ${lastError()}")
        try {
            double[] head = values.reinterpret(2 * ValueLayout.JAVA_DOUBLE.byteSize()).toArray(ValueLayout.JAVA_DOUBLE)
            int n = (int) head[1]
            double[] v = values.reinterpret((2 + 3 * n) * ValueLayout.JAVA_DOUBLE.byteSize()).toArray(ValueLayout.JAVA_DOUBLE)
            List<Vec> path = (0..<n).collect { int i -> new Vec(v[2 + 3 * i], v[3 + 3 * i], v[4 + 3 * i]) }
            new Gap(null, null, head[0] < 0 ? Double.POSITIVE_INFINITY : head[0], path)
        } finally {
            occt_free_doubles(values)
        }
    }
}
