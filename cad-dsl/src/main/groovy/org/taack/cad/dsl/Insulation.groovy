package org.taack.cad.dsl

import groovy.transform.CompileStatic
import org.taack.cad.dsl.kernel.CadKernels
import org.taack.cad.dsl.kernel.InsulationPaths

import java.util.function.Function
import java.util.stream.Collectors

/**
 * The clearances and creepage distances between the conductors of an assembly, as IEC 60664-1 measures them:
 * <ul>
 * <li>a clearance is the shortest path through the air, around the insulating parts and the other conductors;</li>
 * <li>a creepage distance is the shortest path along the surfaces of the insulating parts, crossing through the air
 * the gaps narrower than the groove width X of the pollution degree (0.25 mm for 1, 1.0 mm for 2, 1.5 mm for 3), at
 * the bottom of the wider grooves as across the narrow ones, and the joints between the parts.</li>
 * </ul>
 * Both are measured on meshes of the solids, made once: the paths bend at points spaced along the edges of the meshes,
 * then slide along them to the shortest path. A floating conductor between two others adds the gaps on both sides:
 * give it a name, and sum the gaps to it.
 * <pre>
 * def insulation = new Insulation([earth: [stirrup], pole: [busbar, electrode]], [housing, base])
 * Gap clearance = insulation.clearance('earth', 'pole')
 * Gap creepage = insulation.creepage('earth', 'pole', 1.0d)
 * </pre>
 */
@CompileStatic
final class Insulation {
    /** The largest deviation of the meshes from the faces, in mm */
    static final double DEFLECTION = 0.02d
    /** The largest spacing of the points along the edges of the meshes, in mm */
    static final double STEP = 0.5d

    private final List<String> names
    private final InsulationPaths paths

    /**
     * @param conductors The conductive solids, by conductor: the solids of a conductor touch or are joined
     * @param insulators The insulating solids
     * @param deflection The largest deviation of the meshes from the faces, in mm: smaller is more precise and slower
     * @param step The largest spacing of the points along the edges of the meshes, in mm: smaller finds more paths
     * and is slower
     */
    Insulation(Map<String, List<? extends CadDslSolid>> conductors, List<? extends CadDslSolid> insulators,
               double deflection = DEFLECTION, double step = STEP) {
        names = conductors.keySet() as List<String>
        List<ICadDslVisitor> solids = []
        List<Integer> groups = []
        conductors.eachWithIndex { String name, List<? extends CadDslSolid> parts, int i ->
            parts.each { CadDslSolid s -> solids << s.visitor; groups << i }
        }
        insulators.each { CadDslSolid s -> solids << s.visitor; groups << -1 }
        paths = CadKernels.get().insulationPaths(solids, groups as int[], deflection, step)
    }

    /**
     * @param limit The length beyond which the path is not searched, in mm: the gap is then at least limit long, and
     * has no path. The longer the paths, the longer their search, in an assembly whose parts close the space around
     * the conductors.
     * @return The clearance between two conductors, the shortest path through the air
     */
    Gap clearance(String from, String to, double limit = Double.POSITIVE_INFINITY) {
        named(paths.clearance(group(from), group(to), limit), from, to)
    }

    /**
     * @param groove The width X of the grooves bridged, of the pollution degree, in mm
     * @param limit The length beyond which the path is not searched, as for {@link #clearance}
     * @return The creepage distance between two conductors, the shortest path along the insulating surfaces
     */
    Gap creepage(String from, String to, double groove, double limit = Double.POSITIVE_INFINITY) {
        named(paths.creepage(group(from), group(to), groove, limit), from, to)
    }

    /**
     * @return The clearances between every two conductors, the first one given first, searched in parallel, as
     * {@link #clearance}
     */
    List<Gap> clearances(double limit = Double.POSITIVE_INFINITY) {
        pairs().parallelStream().map({ List<String> p -> clearance(p[0], p[1], limit) } as Function<List<String>, Gap>)
                .collect(Collectors.toList())
    }

    /**
     * @return The creepage distances between every two conductors, the first one given first, searched in parallel,
     * as {@link #creepage}
     */
    List<Gap> creepages(double groove, double limit = Double.POSITIVE_INFINITY) {
        pairs().parallelStream().map({ List<String> p -> creepage(p[0], p[1], groove, limit) } as Function<List<String>, Gap>)
                .collect(Collectors.toList())
    }

    private List<List<String>> pairs() {
        List<List<String>> pairs = []
        for (int i = 0; i < names.size(); i++) for (int j = i + 1; j < names.size(); j++) pairs << [names[i], names[j]]
        pairs
    }

    private int group(String conductor) {
        int i = names.indexOf(conductor)
        if (i < 0) throw new IllegalArgumentException("no conductor '$conductor' in $names")
        i
    }

    private static Gap named(Gap gap, String from, String to) {
        new Gap(from, to, gap.length, gap.path)
    }
}
