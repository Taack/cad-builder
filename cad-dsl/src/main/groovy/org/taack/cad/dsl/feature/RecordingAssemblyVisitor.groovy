package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.Color
import org.taack.cad.dsl.ICadDslAssemblyVisitor
import org.taack.cad.dsl.ICadDslNode
import org.taack.cad.dsl.ICadDslVisitor
import org.taack.cad.dsl.geom.Frame
import org.taack.cad.dsl.geom.Trsf
import org.taack.cad.dsl.geom.Vec

/**
 * Records the parts, sub-assemblies and placements of an assembly as {@link CadFeature}s.
 *
 * The kernel gets its own visitors and nodes: the recording ones are unwrapped before they reach it.
 */
@CompileStatic
final class RecordingAssemblyVisitor implements ICadDslAssemblyVisitor {
    @Delegate
    final ICadDslAssemblyVisitor kernel
    final CadFeature feature
    private final CadFeatureRecorder recorder

    RecordingAssemblyVisitor(CadFeatureRecorder recorder, ICadDslAssemblyVisitor kernel, String name) {
        this.recorder = recorder
        this.kernel = kernel
        feature = recorder.root(new CadFeature('assembly', name ?: 'assembly', CadFeatureFrame.current()))
        recorder.nodes.put(this, feature)
    }

    private static ICadDslNode unwrap(ICadDslNode node) {
        node instanceof RecordingAssemblyVisitor ? ((RecordingAssemblyVisitor) node).kernel : node
    }

    @Override
    ICadDslNode visitPart(String name, Color color, ICadDslVisitor solid) {
        def part = new CadFeature('part', name ?: 'part', CadFeatureFrame.current())
        if (solid instanceof RecordingVisitor) {
            CadFeature built = ((RecordingVisitor) solid).root
            part.children.addAll(built.children)
            recorder.roots.remove(built)
            solid = ((RecordingVisitor) solid).kernel
        }
        feature.children << part
        recorder.last = part
        ICadDslNode node = kernel.visitPart(name, color, solid)
        recorder.nodes.put(node, part)
        node
    }

    @Override
    void visitPlace(ICadDslNode node, Trsf location) {
        CadFeature placed = recorder.nodes.get(node)
        // a sub-assembly goes under the assembly it is first placed in
        if (placed != null && recorder.roots.remove(placed)) feature.children << placed
        def place = new CadFeature('place', "place ${placed?.label ?: 'node'}", CadFeatureFrame.current())
        place.placed = placed
        feature.children << place
        recorder.last = place
        kernel.visitPlace(unwrap(node), location)
    }

    @Override
    void display(String fileName) {
        recorder.displayed(fileName, feature)
        kernel.display(fileName)
    }

    @Override
    Frame visitFaceFrame(ICadDslNode part, Vec direction, Vec position) {
        kernel.visitFaceFrame(unwrap(part), direction, position)
    }

    @Override
    Frame visitAxisFrame(ICadDslNode part, Vec position) {
        kernel.visitAxisFrame(unwrap(part), position)
    }
}
