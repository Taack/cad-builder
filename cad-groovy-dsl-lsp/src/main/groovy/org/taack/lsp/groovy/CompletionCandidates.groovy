package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.ClassExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.transform.stc.ExtensionMethodNode
import org.eclipse.lsp4j.CompletionItem
import org.eclipse.lsp4j.CompletionItemKind
import org.eclipse.lsp4j.Range
import org.eclipse.lsp4j.TextEdit
import org.eclipse.lsp4j.jsonrpc.messages.Either

import java.lang.reflect.Modifier

/**
 * The completion items, in the order they are added, one per name, filtered by the prefix typed.
 * <p>
 * Members are read from the {@link ClassNode}s of the type checker, so classes compiled in the source and classes of
 * jars are listed the same way.
 */
@CompileStatic
class CompletionCandidates {
    private static final Set<String> HIDDEN = ['getMetaClass', 'setMetaClass', 'invokeMethod', 'getProperty',
                                               'setProperty', 'metaClass', 'wait', 'notify', 'notifyAll'] as Set

    private static class Entry {
        String name
        CompletionItemKind kind
        String detail
        String insertText
        int overloads
    }

    private final String prefix
    private final Map<String, Entry> entries = new LinkedHashMap<>()

    CompletionCandidates(String prefix) {
        this.prefix = prefix
    }

    void add(String name, CompletionItemKind kind, String detail, String insertText = name) {
        if (name.contains('$') || name == CursorProbe.CURSOR || HIDDEN.contains(name)) return
        if (!name.regionMatches(true, 0, prefix, 0, prefix.length())) return
        Entry e = entries.get(name)
        if (e != null) {
            // a method listed again: an overload, or the same method seen through a sub type
            if (kind == CompletionItemKind.Method && e.kind == CompletionItemKind.Method && detail != e.detail) {
                e.overloads++
                if (insertText != e.insertText) e.insertText = name
            }
            return
        }
        entries.put(name, new Entry(name: name, kind: kind, detail: detail, insertText: insertText))
    }

    void addVariable(VisibleVariables.Variable v) {
        add(v.name, CompletionItemKind.Variable, typeName(v.type))
    }

    /**
     * @param staticOnly Only the static members, for a class name before the dot
     * @param nonPublic  Also the private and protected members, for the class being edited
     */
    void addMembers(ClassNode type, boolean staticOnly, boolean nonPublic) {
        if (type == null) return
        for (ClassNode c in hierarchy(type)) {
            boolean script = c.script
            for (MethodNode m in c.methods) {
                if (!visible(m.modifiers, nonPublic) || m.synthetic || (staticOnly && !m.static)) continue
                if (script && (m.name == 'run' || m.name == 'main')) continue
                // accessors as properties, as a DSL is written
                String property = propertyName(m)
                if (property) {
                    if (!staticOnly) add(property, CompletionItemKind.Property, typeName(m.returnType))
                } else if (!isSetter(m)) {
                    add(m.name, CompletionItemKind.Method, signature(m), m.parameters.length == 0 ? m.name + '()' : m.name)
                }
            }
            for (PropertyNode p in c.properties) {
                if (staticOnly && !p.static) continue
                add(p.name, CompletionItemKind.Property, typeName(p.type))
            }
            for (FieldNode f in c.fields) {
                if (!visible(f.modifiers, nonPublic) || f.synthetic || (staticOnly && !f.static)) continue
                add(f.name, f.enum ? CompletionItemKind.EnumMember : CompletionItemKind.Field, typeName(f.type))
            }
        }
    }

    /**
     * The GDK methods, e.g. each or collect
     */
    void addExtensionMethods(ClassNode type, boolean staticOnly, Map<String, List<MethodNode>> extensions) {
        if (type == null) return
        for (ClassNode c in hierarchy(type)) {
            for (MethodNode m in extensions.get(c.name) ?: []) {
                boolean isStatic = m instanceof ExtensionMethodNode && ((ExtensionMethodNode) m).staticExtension
                if (isStatic != staticOnly) continue
                add(m.name, CompletionItemKind.Method, signature(m), m.parameters.length == 0 ? m.name + '()' : m.name)
            }
        }
    }

    List<CompletionItem> items(Range range, int max) {
        entries.values().take(max).collect { Entry e ->
            CompletionItem item = new CompletionItem(e.name)
            item.kind = e.kind
            item.detail = e.overloads ? "${e.detail} (+${e.overloads})".toString() : e.detail
            item.textEdit = Either.<TextEdit, org.eclipse.lsp4j.InsertReplaceEdit> forLeft(new TextEdit(range, e.insertText))
            item
        }
    }

    /**
     * The type, its super classes and its interfaces, Object last
     */
    private static List<ClassNode> hierarchy(ClassNode type) {
        ClassNode start = ClassHelper.isPrimitiveType(type) ? ClassHelper.getWrapper(type) : type
        List<ClassNode> res = []
        Set<String> seen = new HashSet<>()
        Deque<ClassNode> todo = new ArrayDeque<>([start])
        while (!todo.isEmpty()) {
            ClassNode c = todo.poll().redirect()
            if (c.name == 'java.lang.Object' || !seen.add(c.name)) continue
            res << c
            if (c.superClass != null) todo.add(c.superClass)
            for (ClassNode i in c.interfaces) todo.add(i)
        }
        res << ClassHelper.OBJECT_TYPE
        res
    }

    private static boolean visible(int modifiers, boolean nonPublic) {
        Modifier.isPublic(modifiers) || (nonPublic && !Modifier.isPublic(modifiers))
    }

    private static String propertyName(MethodNode m) {
        if (m.parameters.length != 0 || m.static || m.voidMethod) return null
        String n = m.name
        if (n.startsWith('get') && n.length() > 3 && Character.isUpperCase(n.charAt(3))) return decapitalize(n.substring(3))
        if (n.startsWith('is') && n.length() > 2 && Character.isUpperCase(n.charAt(2)) && m.returnType == ClassHelper.boolean_TYPE) return decapitalize(n.substring(2))
        null
    }

    private static boolean isSetter(MethodNode m) {
        m.parameters.length == 1 && !m.static && m.name.length() > 3 && m.name.startsWith('set') && Character.isUpperCase(m.name.charAt(3))
    }

    private static String decapitalize(String s) {
        s.length() > 1 && Character.isUpperCase(s.charAt(1)) ? s : Character.toLowerCase(s.charAt(0)).toString() + s.substring(1)
    }

    static String signature(MethodNode m) {
        // names are known for the classes compiled with -parameters only
        String params = m.parameters.collect { Parameter p ->
            String name = p.name ==~ /(param|arg)\d+/ ? '' : ' ' + p.name
            "${delegatesTo(p)}${typeName(p.type)}$name"
        }.join(', ')
        "${typeName(m.returnType)} ${m.name}($params)"
    }

    /**
     * @return "@DelegatesTo(Type) " for a closure parameter with a delegate type, else ""
     */
    private static String delegatesTo(Parameter p) {
        AnnotationNode a = p.getAnnotations(ClassHelper.make(DelegatesTo)).find()
        if (a == null) return ''
        Expression value = a.getMember('value')
        value instanceof ClassExpression ? "@DelegatesTo(${typeName(value.type)}) " : '@DelegatesTo '
    }

    static String typeName(ClassNode type) {
        if (type == null) return 'Object'
        if (type.isArray()) return typeName(type.componentType) + '[]'
        String name = type.nameWithoutPackage
        int inner = name.lastIndexOf('$')
        String simple = inner >= 0 ? name.substring(inner + 1) : name
        GenericsType[] generics = type.genericsTypes
        if (generics && !type.genericsPlaceHolder) {
            simple += '<' + generics.collect { GenericsType g -> g.placeholder || g.wildcard ? g.name : typeName(g.type) }.join(', ') + '>'
        }
        simple
    }
}
