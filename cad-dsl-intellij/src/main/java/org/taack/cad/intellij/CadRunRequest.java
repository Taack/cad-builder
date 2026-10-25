package org.taack.cad.intellij;

import java.util.List;

/**
 * A script to run, with the arguments it reads from args
 */
public record CadRunRequest(String script, List<String> args) {}
