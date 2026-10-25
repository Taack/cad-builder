package org.taack.cad.intellij;

import java.util.List;

/**
 * A script to run, with the arguments it reads from args, and the classpath of its module: the jars and classes it uses
 */
public record CadRunRequest(String script, List<String> args, List<String> classpath) {
    public CadRunRequest(String script, List<String> args) {
        this(script, args, List.of());
    }
}
