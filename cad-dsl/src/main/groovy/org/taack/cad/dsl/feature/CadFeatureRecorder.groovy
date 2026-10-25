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

    CadFeature root(CadFeature f) {
        roots << f
        f
    }

    /**
     * @return The tree of the features: maps of id, kind, label, file, line, children, plus detail, placed and class
     * when they apply
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
        List<Group> open = []
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
            }
            List<Map<String, Object>> container = open ? open.last().children : top
            container << node(f, open ? (String) open.last().node.id : parentId, container)
        }
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
        if (f.is(last)) node.last = true
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
