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
 * The kernel gets its own visitors and nodes: the recording ones are unwrapped before they reach it. After the stop of the
 * run, the parts and placements are recorded but not given to the kernel, nor those of what the kernel did not get.
 */
@CompileStatic
final class RecordingAssemblyVisitor implements ICadDslAssemblyVisitor {
    @Delegate
    final ICadDslAssemblyVisitor kernel
    final CadFeature feature
    private final CadFeatureRecorder recorder
    /**
     * Whether the kernel got a part or a placement
     */
    boolean forwarded
    // Index for the kernel of each component, -1 for the suppressed ones
    private final List<Integer> components = []
    private int kernelComponents

    // A part not given to the kernel
    private static final class Suppressed implements ICadDslNode {}

    RecordingAssemblyVisitor(CadFeatureRecorder recorder, ICadDslAssemblyVisitor kernel, String name) {
        this.recorder = recorder
        this.kernel = kernel
        feature = recorder.root(new CadFeature('assembly', name ?: 'assembly', CadFeatureFrame.current()))
        recorder.nodes.put(this, feature)
    }

    private static ICadDslNode unwrap(ICadDslNode node) {
        node instanceof RecordingAssemblyVisitor ? ((RecordingAssemblyVisitor) node).kernel : node
    }

    // Whether the kernel did not get node, or got nothing of it
    private boolean suppressed(ICadDslNode node) {
        node instanceof Suppressed || node instanceof RecordingAssemblyVisitor && recorder.stopping && !((RecordingAssemblyVisitor) node).forwarded
    }

    @Override
    ICadDslNode visitPart(String name, Color color, ICadDslVisitor solid) {
        def part = recorder.record(new CadFeature('part', name ?: 'part', CadFeatureFrame.current()), null)
        if (solid instanceof RecordingVisitor) {
            RecordingVisitor rv = (RecordingVisitor) solid
            part.children.addAll(rv.root.children)
            recorder.roots.remove(rv.root)
            if (recorder.stopping && !rv.forwarded) part.suppressed = true
            solid = rv.kernel
        }
        feature.children << part
        recorder.last = part
        ICadDslNode node = part.suppressed ? new Suppressed() : kernel.visitPart(name, color, solid)
        if (!part.suppressed) forwarded = true
        recorder.nodes.put(node, part)
        node
    }

    @Override
    ICadDslNode visitImportStep(String fileName, String name) {
        def part = recorder.record(new CadFeature('part', "STEP ${new File(fileName).name}", CadFeatureFrame.current()), null)
        feature.children << part
        recorder.last = part
        ICadDslNode node = part.suppressed ? new Suppressed() : kernel.visitImportStep(fileName, name)
        if (!part.suppressed) forwarded = true
        recorder.nodes.put(node, part)
        node
    }

    @Override
    void visitPlace(ICadDslNode node, Trsf location) {
        CadFeature placed = recorder.nodes.get(node)
        // a sub-assembly goes under the assembly it is first placed in
        if (placed != null && recorder.roots.remove(placed)) feature.children << placed
        def place = recorder.record(new CadFeature('place', "place ${placed?.label ?: 'node'}", CadFeatureFrame.current()), null)
        place.placed = placed
        if (suppressed(node)) place.suppressed = true
        feature.children << place
        recorder.last = place
        components << (place.suppressed ? -1 : kernelComponents++)
        if (place.suppressed) return
        forwarded = true
        kernel.visitPlace(unwrap(node), location)
    }

    @Override
    void visitMove(int component, Trsf location, List<Trsf> frames) {
        int index = component < components.size() ? components[component] : component
        if (index >= 0) kernel.visitMove(index, location, frames)
    }

    @Override
    void display(String fileName) {
        recorder.displayed(fileName, feature)
        if (forwarded || !recorder.stopping) kernel.display(fileName)
    }

    @Override
    Frame visitFaceFrame(ICadDslNode part, Vec direction, Vec position) {
        // a frame for the placements of a suppressed part, suppressed as well
        part instanceof Suppressed ? new Frame(position ?: new Vec(0, 0, 0), direction) : kernel.visitFaceFrame(unwrap(part), direction, position)
    }

    @Override
    Frame visitAxisFrame(ICadDslNode part, Vec position) {
        part instanceof Suppressed ? new Frame(position ?: new Vec(0, 0, 0), Vec.vZ) : kernel.visitAxisFrame(unwrap(part), position)
    }
}
