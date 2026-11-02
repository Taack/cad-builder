package org.taack.lsp.groovy

import groovy.transform.CompileStatic
import org.eclipse.lsp4j.Position

/**
 * Lexical facts about a Groovy source at an offset, without parsing it: whether the offset is in a string or a comment,
 * and the brackets left open before it.
 */
@CompileStatic
final class SourceText {
    private SourceText() {}

    /**
     * @return The offset of an LSP position, both counting UTF-16 units like Java strings
     */
    static int offsetOf(String text, Position position) {
        int offset = 0
        for (int line = 0; line < position.line; line++) {
            int next = text.indexOf('\n', offset)
            if (next == -1) return text.length()
            offset = next + 1
        }
        int lineEnd = text.indexOf('\n', offset)
        if (lineEnd == -1) lineEnd = text.length()
        Math.min(offset + position.character, lineEnd)
    }

    static Position positionOf(String text, int offset) {
        int line = 0
        int lineStart = 0
        for (int i = 0; i < offset && i < text.length(); i++) {
            if (text.charAt(i) == '\n' as char) {
                line++
                lineStart = i + 1
            }
        }
        new Position(line, offset - lineStart)
    }

    static int identifierStart(String text, int offset) {
        int start = offset
        while (start > 0 && Character.isJavaIdentifierPart(text.charAt(start - 1))) start--
        start
    }

    static int identifierEnd(String text, int offset) {
        int end = offset
        while (end < text.length() && Character.isJavaIdentifierPart(text.charAt(end))) end++
        end
    }

    /**
     * Scans text up to end.
     * @return null when end is in a string or a comment, else the characters closing the brackets open at end
     */
    static String closersAt(String text, int end) {
        Deque<Character> open = new ArrayDeque<>()
        int i = 0
        while (i < end) {
            char c = text.charAt(i)
            char next = i + 1 < text.length() ? text.charAt(i + 1) : 0 as char
            if (c == '/' as char && next == '/' as char) {
                int eol = text.indexOf('\n', i)
                if (eol == -1 || eol >= end) return null
                i = eol + 1
            } else if (c == '/' as char && next == '*' as char) {
                int close = text.indexOf('*/', i + 2)
                if (close == -1 || close + 2 > end) return null
                i = close + 2
            } else if (c == '"' as char || c == '\'' as char) {
                int close = stringEnd(text, i)
                if (close == -1 || close > end) return null
                i = close
            } else {
                if (c == '(' as char) open.push(')' as char)
                else if (c == '[' as char) open.push(']' as char)
                else if (c == '{' as char) open.push('}' as char)
                else if ((c == ')' as char || c == ']' as char || c == '}' as char) && !open.isEmpty() && open.peek() == c) open.pop()
                i++
            }
        }
        StringBuilder closers = new StringBuilder()
        for (Character c in open) closers.append(c)
        closers.toString()
    }

    /**
     * @return The offset following the string starting at start, -1 when it is not closed
     */
    private static int stringEnd(String text, int start) {
        char quote = text.charAt(start)
        boolean triple = text.startsWith("$quote$quote$quote", start)
        int i = start + (triple ? 3 : 1)
        while (i < text.length()) {
            char c = text.charAt(i)
            if (c == '\\' as char) i += 2
            else if (triple && text.startsWith("$quote$quote$quote", i)) return i + 3
            else if (!triple && c == quote) return i + 1
            else if (!triple && c == '\n' as char) return -1
            else i++
        }
        -1
    }
}
