package org.taack.cad.dsl.feature

import groovy.transform.CompileStatic
import org.taack.cad.dsl.geom.Vec
import org.taack.cad.dsl.geom.Vec2d

/**
 * Features recorded during a run by {@link RecordingKernel}, and the tree shown for them.
 *
 * In the tree, the features a method of the user built (MjParts.body(...) for instance) are grouped under a node of
 * that method, at its call site.
 */
@CompileStatic
final class CadFeatureRecorder {
    /**
     * Solids not made into parts, and assemblies not placed in another one
     */
    final List<CadFeature> roots = []
    /**
     * Feature of each part and assembly node given to the kernel
     */
    final Map<Object, CadFeature> nodes = new IdentityHashMap<>()
    /**
     * Last feature recorded: the one failing when the kernel throws
     */
    CadFeature last
    /**
     * Whether the run failed, at the last feature or after it
     */
    boolean failed

    // Feature each display() of the run showed, by the file it wrote
    private final Map<String, CadFeature> displays = [:]
    private int count
    // The operations after the one of rank stopAfter are not done: those of the solid of rank stopSolid, or all of them
    private int stopAfter = Integer.MAX_VALUE
    private int stopSolid = -1

    CadFeature root(CadFeature f) {
        roots << record(f, null)
        f
    }

    /**
     * Ranks f in the run, part of solid, and tells whether it is suppressed
     */
    CadFeature record(CadFeature f, CadFeature solid) {
        f.seq = ++count
        f.solid = solid
        f.suppressed = f.seq > stopAfter && (stopSolid < 0 || solid?.seq == stopSolid)
        f
    }

    /**
     * Whether the run stops at a feature: the solids, parts and assemblies left with no operation done are not shown
     */
    boolean isStopping() {
        stopAfter != Integer.MAX_VALUE
    }

    /**
     * Stops this run, of the script last ran, after the feature of id in the tree of last, and after the features under
     * it: the operations after it are recorded but not done. When that feature is in a solid, only the operations of that
     * solid are stopped, the solid is shown in its assembly as it was at that feature.
     * @return Whether last has a feature of id
     */
    boolean stopAt(CadFeatureRecorder last, String id) {
        List<CadFeature> under = last.ids.findAll { it.value == id || it.value.startsWith(id + '/') }*.key
        if (!under) return false
        stopAfter = under*.seq.max()
        Set<CadFeature> solids = under.collect { it.solid } as Set
        stopSolid = solids.size() == 1 && solids.first() != null ? solids.first().seq : -1
        true
    }

    void displayed(String fileName, CadFeature f) {
        if (fileName) displays[fileName] = f
    }

    /**
     * @return The id, in the last {@link #tree}, of the feature whose display() wrote fileName, null if none
     */
    String displayedId(String fileName) {
        CadFeature f = displays[fileName]
        f == null ? null : ids.get(f)
    }

    /**
     * Writes the shapes of the feature of id in the last {@link #tree} to a binary glTF file: its own, else those of
     * the features under it (the tools of a cut...), in the coordinates of its solid
     * @return Whether the feature has shapes
     */
    boolean writeShapes(String id, String fileName) {
        CadFeature f = ids.find { it.value == id }?.key
        if (f == null || f.kind in ['part', 'solid', 'assembly', 'place']) return false
        List<CadFeature> shaped = f.shape != null ? [f] : shaped(f)
        if (!shaped) return false
        shaped.first().shapes.writeShapes(shaped*.shape, fileName)
        true
    }

    // The features with a shape under f, not under a part or an assembly: those have their own coordinates
    private static List<CadFeature> shaped(CadFeature f) {
        f.children.collectMany { CadFeature c ->
            c.shape != null ? [c] : c.kind in ['part', 'solid', 'assembly'] ? [] : shaped(c)
        }
    }

    /**
     * @return The tree of the features: maps of id, kind, label, children, plus class, file and line of the call, detail,
     * placed (the id of the feature placed), error (on the last feature of a failed run) and suppressed (after the stop
     * of the run) when they apply
     */
    List<Map<String, Object>> tree() {
        ids.clear()
        slugs.clear()
        children(new CadFeature('script', 'script', []), roots, null)
    }

    private static final class Group {
        final CadFeatureFrame method
        final CadFeatureFrame callSite
        final Map<String, Object> node
        final List<Map<String, Object>> children = []

        Group(CadFeatureFrame method, CadFeatureFrame callSite, Map<String, Object> node) {
            this.method = method
            this.callSite = callSite
            this.node = node
            node.children = children
        }

        boolean same(CadFeatureFrame method, CadFeatureFrame callSite) {
            this.method.sameMethod(method) && this.callSite == callSite
        }
    }

    private final Map<CadFeature, String> ids = new IdentityHashMap<>()

    private List<Map<String, Object>> children(CadFeature parent, List<CadFeature> features, String parentId) {
        List<Map<String, Object>> top = []
        List<Group> open = [], groups = []
        for (CadFeature f in features) {
            List<CadFeatureFrame> stack = f.stack
            int k = 0
            while (k < parent.stack.size() && k < stack.size() && parent.stack[k].sameMethod(stack[k])) k++
            // the methods of the user entered since the parent was called
            List<List<CadFeatureFrame>> entered = []
            for (int i = Math.max(k, 1); i < stack.size(); i++)
                if (stack[i].method && !stack[i].sameMethod(stack[i - 1])) entered << [stack[i], stack[i - 1]]
            int j = 0
            while (j < open.size() && j < entered.size() && open[j].same(entered[j][0], entered[j][1])) j++
            while (open.size() > j) open.removeLast()
            for (int g = j; g < entered.size(); g++) {
                List<Map<String, Object>> container = open ? open.last().children : top
                String ownerId = open ? (String) open.last().node.id : parentId
                CadFeatureFrame method = entered[g][0], callSite = entered[g][1]
                Map<String, Object> node = [id      : id(ownerId, container, 'group', method.methodName),
                                            kind    : 'group', label: "${method.methodName}(...)",
                                            class   : method.className, file: callSite.fileName, line: callSite.line] as Map<String, Object>
                container << node
                open << new Group(method, callSite, node)
                groups << open.last()
            }
            List<Map<String, Object>> container = open ? open.last().children : top
            container << node(f, open ? (String) open.last().node.id : parentId, container)
        }
        // a group of suppressed features is, the inner groups first
        groups.reverseEach { Group g -> if (g.children.every { it.suppressed }) g.node.suppressed = true }
        top
    }

    private Map<String, Object> node(CadFeature f, String parentId, List<Map<String, Object>> siblings) {
        String name = f.kind in ['part', 'assembly'] ? f.label : null
        String id = id(parentId, siblings, f.kind, name)
        ids.put(f, id)
        Map<String, Object> node = [id: id, kind: f.kind, label: f.label] as Map<String, Object>
        if (f.source) {
            node.class = f.source.className
            node.file = f.source.fileName
            node.line = f.source.line
        }
        if (f.detail) node.detail = true
        if (f.placed) node.placed = ids.get(f.placed)
        if (failed && f.is(last)) node.error = true
        if (f.suppressed) node.suppressed = true
        node.children = children(f, f.children, id)
        node
    }

    // From the path in the tree, so that a feature keeps its id from one run to the next while the script keeps its
    // structure: kind and rank among the siblings of that kind, or the name of a part, assembly or method
    private String id(String parentId, List<Map<String, Object>> siblings, String kind, String name) {
        String slug = name != null ? "$kind:$name" : kind
        int same = siblings.count { slugs.get(it.id) == slug } as int
        String id = (parentId ? parentId + '/' : '') + (name != null ? (same ? "$slug#$same" : slug) : "$kind$same")
        slugs.put(id, slug)
        id
    }

    // Slug of each id, to rank the siblings
    private final Map<Object, String> slugs = [:]

    /**
     * @return The tree as indented text, sketch elements counted under their sketch
     */
    String text() {
        def out = new StringBuilder()
        text(tree(), '', out)
        out.toString()
    }

    private static void text(List<Map<String, Object>> nodes, String indent, StringBuilder out) {
        for (Map<String, Object> node in nodes) {
            if (node.detail) continue
            List<Map<String, Object>> children = (List<Map<String, Object>>) node.children
            int details = children.count { it.detail } as int
            out << indent << node.label
            if (details) out << " [$details elements]"
            if (node.file) out << '  ' << node.file << ':' << node.line
            out << '  #' << node.id << '\n'
            text(children, indent + '  ', out)
        }
    }

    static String n(double d) {
        if (d == Math.rint(d) && Math.abs(d) < 1e15) return Long.toString((long) d)
        new BigDecimal(d).setScale(3, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString()
    }

    static String v(Vec2d p) {
        p == null ? '()' : p instanceof Vec ? "(${n(p.x)}, ${n(p.y)}, ${n(((Vec) p).z)})" : "(${n(p.x)}, ${n(p.y)})"
    }
}
