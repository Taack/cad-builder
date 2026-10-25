package org.taack.cad.intellij;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The nodes of a glTF model showing a feature: those of its part, for each place the part occupies in the model
 *
 * The runner writes a node for the displayed part or assembly, then, depth first, one for each component in the order
 * they were placed (write_glb_assembly of cad-dsl-native): the placements of the features number the nodes the same way.
 */
final class CadHighlight {
    private static final Set<String> NODES = Set.of("part", "solid", "assembly");
    // features without a shape of their own, which the preview shows by their nodes only
    private static final Set<String> NO_SHAPE = Set.of("part", "solid", "assembly", "place");

    private final Map<String, CadFeature> features = new HashMap<>();
    private final Map<String, CadFeature> parents = new HashMap<>();
    private final Map<String, List<Integer>> nodes = new HashMap<>();
    private int count;

    /**
     * @param displayed Id of the feature the model displays, see CadModel
     */
    CadHighlight(List<CadFeature> roots, @Nullable String displayed) {
        roots.forEach(it -> index(it, null));
        CadFeature root = displayed == null ? null : features.get(displayed);
        if (root != null) number(root);
    }

    private void index(CadFeature feature, @Nullable CadFeature parent) {
        features.put(feature.id(), feature);
        if (parent != null) parents.put(feature.id(), parent);
        feature.children().forEach(it -> index(it, feature));
    }

    // Numbers the nodes of feature and of its components, as the runner writes them; false when one is unknown
    private boolean number(CadFeature feature) {
        nodes.computeIfAbsent(feature.id(), it -> new ArrayList<>()).add(count++);
        if (!feature.kind().equals("assembly")) return true;
        for (CadFeature place : places(feature, new ArrayList<>())) {
            CadFeature placed = place.placed() == null ? null : features.get(place.placed());
            if (placed == null) return false;
            nodes.computeIfAbsent(place.id(), it -> new ArrayList<>()).add(count);
            if (!number(placed)) return false;
        }
        return true;
    }

    // The placements of an assembly, in order, under the methods of the user that made them
    private static List<CadFeature> places(CadFeature assembly, List<CadFeature> places) {
        for (CadFeature child : assembly.children()) {
            if (child.kind().equals("place")) places.add(child);
            else if (child.kind().equals("group")) places(child, places);
        }
        return places;
    }

    /**
     * @return The nodes showing the feature of id: its own when it is a part, an assembly or a placement, those of its
     * part otherwise, those of its features for a group of them; none when its part is not in the model
     */
    List<Integer> nodes(@Nullable String id) {
        CadFeature feature = id == null ? null : features.get(id);
        if (feature == null) return List.of();
        return new ArrayList<>(nodesOf(feature, new LinkedHashSet<>()));
    }

    private Set<Integer> nodesOf(CadFeature feature, Set<Integer> found) {
        if (nodes.containsKey(feature.id())) {
            found.addAll(nodes.get(feature.id()));
        } else if (feature.kind().equals("group") && !hasNodeAbove(feature)) {
            feature.children().forEach(it -> nodesOf(it, found));
        } else {
            for (CadFeature above = parents.get(feature.id()); above != null; above = parents.get(above.id())) {
                if (nodes.containsKey(above.id())) {
                    found.addAll(nodes.get(above.id()));
                    break;
                }
                if (NODES.contains(above.kind())) break;
            }
        }
        return found;
    }

    // Whether a part or a solid holds the feature: a group in a part is shown by the part
    private boolean hasNodeAbove(CadFeature feature) {
        for (CadFeature above = parents.get(feature.id()); above != null; above = parents.get(above.id())) {
            if (NODES.contains(above.kind())) return !above.kind().equals("assembly");
        }
        return false;
    }

    /**
     * @return Whether the feature of id may have shapes of its own to show over its part: a primitive, a prism, the
     * tools of a cut...
     */
    boolean hasShape(@Nullable String id) {
        CadFeature feature = id == null ? null : features.get(id);
        return feature != null && !NO_SHAPE.contains(feature.kind());
    }
}
