package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import groovyjarjarantlr4.v4.runtime.CharStreams
import groovyjarjarantlr4.v4.runtime.Token
import org.apache.groovy.parser.antlr4.GroovyLangLexer
import org.apache.groovy.parser.antlr4.GroovyLexer
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.control.SourceUnit
import org.eclipse.lsp4j.SemanticTokensLegend

import java.lang.reflect.Modifier
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Semantic tokens of a Groovy source (LSP 3.16), in two passes:
 * <ul>
 *     <li>lexical, from the Groovy lexer, which works on code that does not parse: keywords, strings, comments,
 *     numbers, annotations, and the identifiers starting with a capital and holding a lower case letter as types;</li>
 *     <li>semantic, from the AST once type checked, when the source parses: the identifiers become methods, properties,
 *     parameters, variables or types.</li>
 * </ul>
 * A token never spans lines: multi-line comments and strings are split, the clients need not support multi-line
 * tokens.
 */
@CompileStatic
final class SemanticTokenizer {
    static final List<String> TYPES = ['keyword', 'string', 'comment', 'number', 'decorator', 'type', 'method',
                                       'property', 'parameter', 'variable']
    static final List<String> MODIFIERS = ['declaration', 'static']
    static final SemanticTokensLegend LEGEND = new SemanticTokensLegend(TYPES, MODIFIERS)

    static final int KEYWORD = 0, STRING = 1, COMMENT = 2, NUMBER = 3, DECORATOR = 4, TYPE = 5, METHOD = 6,
                     PROPERTY = 7, PARAMETER = 8, VARIABLE = 9
    static final int DECLARATION = 1, STATIC = 2

    private static final int UNKNOWN = -1
    private static final Pattern KEYWORD_LITERAL = ~/'!?[a-z][a-z-]*'/

    private SemanticTokenizer() {}

    /**
     * A token, its line and column from 0
     */
    static final class Tok {
        final int line, column, length
        int type, modifiers

        Tok(int line, int column, int length, int type, int modifiers = 0) {
            this.line = line
            this.column = column
            this.length = length
            this.type = type
            this.modifiers = modifiers
        }

        @Override
        String toString() { "$line:$column+$length ${type >= 0 ? TYPES[type] : '?'}${modifiers ? '/' + modifiers : ''}" }
    }

    /**
     * The tokens of the source, the identifiers the lexer cannot classify included, with the type -1
     */
    static List<Tok> lex(String text) {
        List<Tok> res = []
        GroovyLangLexer lexer = new GroovyLangLexer(CharStreams.fromString(text))
        lexer.removeErrorListeners()
        boolean afterAt = false
        try {
            for (Token t = lexer.nextToken(); t.type != Token.EOF; t = lexer.nextToken()) {
                int type = classify(t)
                if (t.type == GroovyLexer.AT) {
                    afterAt = true
                } else if (afterAt && (t.type == GroovyLexer.CapitalizedIdentifier || t.type == GroovyLexer.Identifier)) {
                    type = DECORATOR
                    afterAt = false
                } else {
                    afterAt = false
                }
                if (type == UNKNOWN && t.type != GroovyLexer.Identifier && t.type != GroovyLexer.CapitalizedIdentifier) continue
                addSplit(res, t.line - 1, t.charPositionInLine, t.text, type)
            }
        } catch (Exception | StackOverflowError ignored) {
            // the tokens before the error are kept
        }
        res
    }

    private static int classify(Token t) {
        switch (t.type) {
            case GroovyLexer.NL: return t.text.startsWith('/') ? COMMENT : UNKNOWN
            case GroovyLexer.StringLiteral:
            case GroovyLexer.GStringBegin:
            case GroovyLexer.GStringPart:
            case GroovyLexer.GStringEnd: return STRING
            case GroovyLexer.IntegerLiteral:
            case GroovyLexer.FloatingPointLiteral: return NUMBER
            case GroovyLexer.BooleanLiteral:
            case GroovyLexer.NullLiteral:
            case GroovyLexer.BuiltInPrimitiveType: return KEYWORD
            case GroovyLexer.AT: return DECORATOR
            case GroovyLexer.CapitalizedIdentifier:
                // Color is a type, RIGHT a constant
                return t.text != t.text.toUpperCase() ? TYPE : UNKNOWN
        }
        String literal = GroovyLexer.VOCABULARY.getLiteralName(t.type)
        literal != null && KEYWORD_LITERAL.matcher(literal).matches() ? KEYWORD : UNKNOWN
    }

    private static void addSplit(List<Tok> res, int line, int column, String text, int type) {
        String[] parts = text.split('\r?\n', -1)
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].length() > 0) res << new Tok(line + i, i == 0 ? column : 0, parts[i].length(), type)
        }
    }

    /**
     * The types the AST gives to the identifiers, by position, line << 32 | column
     */
    static Map<Long, Tok> semantic(ModuleNode module, String text) {
        SemanticVisitor visitor = new SemanticVisitor(text.split('\r?\n', -1), module.context)
        for (ClassNode c in module.classes) visitor.visitClass(c)
        visitor.tokens
    }

    /**
     * The tokens of the source, encoded as LSP expects them: 5 integers per token, relative to the previous one
     *
     * @param module The type checked AST, null when the source does not parse
     */
    static List<Integer> tokens(String text, ModuleNode module) {
        List<Tok> lexical = lex(text)
        if (module != null) {
            Map<Long, Tok> byPosition = [:]
            try {
                byPosition = semantic(module, text)
            } catch (Exception | StackOverflowError ignored) {
                // the lexical tokens alone
            }
            for (Tok t in lexical) {
                Tok s = byPosition.get(key(t.line, t.column))
                if (s != null && s.length == t.length && t.type != KEYWORD && t.type != STRING && t.type != COMMENT) {
                    t.type = s.type
                    t.modifiers = s.modifiers
                }
            }
        }
        encode(lexical.findAll { Tok t -> t.type >= 0 })
    }

    static List<Integer> encode(List<Tok> tokens) {
        List<Integer> data = []
        int line = 0, column = 0, end = -1
        for (Tok t in tokens.sort(false) { Tok a, Tok b -> a.line <=> b.line ?: a.column <=> b.column }) {
            if (t.line == line && t.column < end) continue  // overlapping
            data.addAll([t.line - line, t.line == line ? t.column - column : t.column, t.length, t.type, t.modifiers])
            if (t.line != line) line = t.line
            column = t.column
            end = t.column + t.length
        }
        data
    }

    static long key(int line, int column) {
        ((long) line << 32) | column
    }

    private static final class SemanticVisitor extends ClassCodeVisitorSupport {
        final Map<Long, Tok> tokens = [:]
        private final String[] lines
        private final SourceUnit sourceUnit

        SemanticVisitor(String[] lines, SourceUnit sourceUnit) {
            this.lines = lines
            this.sourceUnit = sourceUnit
        }

        @Override
        protected SourceUnit getSourceUnit() {
            sourceUnit
        }

        /**
         * Mark the name at the position of the node, when the source holds it there: the nodes the compiler
         * generates or moves have no position, or another one
         */
        private void mark(ASTNode node, String name, int type, int modifiers = 0) {
            if (node == null || !name || node.lineNumber < 1 || node.columnNumber < 1) return
            int line = node.lineNumber - 1, column = node.columnNumber - 1
            if (line >= lines.length || !lines[line].startsWith(name, column)) return
            tokens.putIfAbsent(key(line, column), new Tok(line, column, name.length(), type, modifiers))
        }

        /**
         * Mark the first occurrence of the name in a declaration, after its modifiers and its type
         */
        private void markIn(ASTNode node, String name, int type, int modifiers) {
            if (node == null || !name || node.lineNumber < 1 || node.columnNumber < 1) return
            int last = node.lastLineNumber >= node.lineNumber ? node.lastLineNumber : node.lineNumber
            Pattern p = Pattern.compile('(?<![\\w$])' + Pattern.quote(name) + '(?![\\w$])')
            for (int l = node.lineNumber - 1; l < Math.min(last, lines.length); l++) {
                int from = l == node.lineNumber - 1 ? node.columnNumber - 1 : 0
                if (from > lines[l].length()) continue
                Matcher m = p.matcher(lines[l])
                if (m.find(from)) {
                    tokens.putIfAbsent(key(l, m.start()), new Tok(l, m.start(), name.length(), type, modifiers))
                    return
                }
            }
        }

        private static int staticOf(int modifiers) {
            Modifier.isStatic(modifiers) ? STATIC : 0
        }

        @Override
        protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
            if (!node.synthetic && !node.scriptBody && !isConstructor) markIn(node, node.name, METHOD, DECLARATION | staticOf(node.modifiers))
            for (Parameter p in node.parameters) markIn(p, p.name, PARAMETER, DECLARATION)
            super.visitConstructorOrMethod(node, isConstructor)
        }

        @Override
        void visitField(FieldNode node) {
            if (!node.synthetic) markIn(node, node.name, PROPERTY, DECLARATION | staticOf(node.modifiers))
            super.visitField(node)
        }

        @Override
        void visitProperty(PropertyNode node) {
            markIn(node.field ?: node, node.name, PROPERTY, DECLARATION | staticOf(node.modifiers))
            super.visitProperty(node)
        }

        @Override
        void visitClosureExpression(ClosureExpression expression) {
            if (expression.parameters) for (Parameter p in expression.parameters) markIn(p, p.name, PARAMETER, DECLARATION)
            super.visitClosureExpression(expression)
        }

        @Override
        void visitDeclarationExpression(DeclarationExpression expression) {
            if (expression.leftExpression instanceof VariableExpression) {
                VariableExpression v = (VariableExpression) expression.leftExpression
                mark(v, v.name, VARIABLE, DECLARATION)
            } else if (expression.leftExpression instanceof TupleExpression) {
                for (Expression e in ((TupleExpression) expression.leftExpression).expressions) {
                    if (e instanceof VariableExpression) mark(e, ((VariableExpression) e).name, VARIABLE, DECLARATION)
                }
            }
            super.visitDeclarationExpression(expression)
        }

        @Override
        void visitVariableExpression(VariableExpression expression) {
            Variable v = expression.accessedVariable
            if (v instanceof Parameter) mark(expression, expression.name, PARAMETER)
            else if (v instanceof FieldNode) mark(expression, expression.name, PROPERTY, staticOf(((FieldNode) v).modifiers))
            else if (v instanceof PropertyNode) mark(expression, expression.name, PROPERTY, staticOf(((PropertyNode) v).modifiers))
            else if (v instanceof DynamicVariable) mark(expression, expression.name, PROPERTY)
            else if (v != null && !expression.thisExpression && !expression.superExpression) mark(expression, expression.name, VARIABLE)
            super.visitVariableExpression(expression)
        }

        @Override
        void visitMethodCallExpression(MethodCallExpression call) {
            mark(call.method, call.methodAsString, METHOD)
            super.visitMethodCallExpression(call)
        }

        @Override
        void visitStaticMethodCallExpression(StaticMethodCallExpression call) {
            mark(call, call.method, METHOD, STATIC)
            super.visitStaticMethodCallExpression(call)
        }

        @Override
        void visitPropertyExpression(PropertyExpression expression) {
            mark(expression.property, expression.propertyAsString, PROPERTY)
            super.visitPropertyExpression(expression)
        }

        @Override
        void visitClassExpression(ClassExpression expression) {
            mark(expression, expression.type.nameWithoutPackage, TYPE)
            super.visitClassExpression(expression)
        }
    }
}
