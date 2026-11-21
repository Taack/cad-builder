package org.taack.cad.step

import groovy.transform.CompileStatic
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.taack.cad.step.antlr.StepFileBaseVisitor
import org.taack.cad.step.antlr.StepFileLexer
import org.taack.cad.step.antlr.StepFileParser

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Converts STEP files to CAD DSL scripts: step2dsl &lt;file.step&gt; [script.groovy] [--check]
 *
 * The STEP file is read by FreeCAD, which recognizes the solids (see step2dsl.py): the script rebuilds the assembly
 * tree, each distinct part once, from primitives, prisms, revolutions, and the holes and bosses on them.
 */
@CompileStatic
final class StepToDslNative extends StepFileBaseVisitor {
    private StepToDslNative() {}

    private StepFile stepFile

    private static String unquote(String s) {
        if (!s) return s
        if (s[0] == "'") s = s.substring(1)
        if (s[s.length()-1] == "'") s = s.substring(0, s.length()-1)
        s
    }

    @Override
    Object visitHeader_fd(StepFileParser.Header_fdContext ctx) {
        println "fd: $ctx"
        return super.visitHeader_fd(ctx)
    }

    @Override
    Object visitHeader_fn(StepFileParser.Header_fnContext ctx) {
        String fn = unquote(ctx.par().exp(0).text)
        String date = unquote(ctx.par().exp(1).STRING_LITERAL().text)
        String soft =  unquote(ctx.par().exp(2).par().exp(1).par().exp(2).STRING_LITERAL().text)
        stepFile = new StepFile(fn, soft, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(date))
        println stepFile
        return super.visitHeader_fn(ctx)
    }

    /**
     * @return The Groovy script rebuilding the STEP file with the DSL
     */
    String convert(File step) {
        if (!step.file) throw new IllegalArgumentException("No such STEP file: $step")
        CharStream input = CharStreams.fromPath(step.toPath())
        StepFileLexer lexer = new StepFileLexer(input)
        CommonTokenStream tokens = new CommonTokenStream(lexer)
        StepFileParser parser = new StepFileParser(tokens)

        ParseTree tree = parser.step_file()
        visit(tree)
    }

    static void main(String[] args) {
        List<String> files = args.findAll { !it.startsWith('-') }
        println "${new File('.').absolutePath}"
        if (!files || files.size() > 2 || args.any { it.startsWith('-') && it != '--check' }) {
            System.err.println '''\
                Usage: step2dsl <file.step> [script.groovy] [--check]
                Writes the CAD DSL script rebuilding the STEP file, to the standard output when no script is given.
                --check runs the script with the FreeCAD kernel, and prints how far what it builds is from the STEP file.'''.stripIndent()
            System.exit(args.any { it in ['-h', '--help'] } ? 0 : 2)
        }
        File step = new File(files[0])
        StepToDslNative stepToDsl = new StepToDslNative()
        String script = stepToDsl.convert(step)
        println script
        System.exit(0)
    }
}
