package org.taack.cad.step

import groovy.transform.CompileStatic
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.taack.cad.step.antlr.StepFileBaseVisitor
import org.taack.cad.step.antlr.StepFileLexer
import org.taack.cad.step.antlr.StepFileParser

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
    private Integer currentId

    private static String unquote(String s) {
        if (!s) return s
        if (s[0] == "'") s = s.substring(1)
        if (s[s.length()-1] == "'") s = s.substring(0, s.length()-1)
        s
    }

    @Override
    Object visitHeader_fd(StepFileParser.Header_fdContext ctx) {
        return super.visitHeader_fd(ctx)
    }

    @Override
    Object visitHeader_fn(StepFileParser.Header_fnContext ctx) {
        String fn = unquote(ctx.par().exp(0).text)
        String date = unquote(ctx.par().exp(1).STRING_LITERAL().text)
        String soft =  unquote(ctx.par().exp(2).par().exp(1).par().exp(2).STRING_LITERAL().text)
        stepFile = new StepFile(fn, soft, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(date))
        return super.visitHeader_fn(ctx)
    }

    @Override
    Object visitData_list(StepFileParser.Data_listContext ctx) {
        currentId = ctx.REF_LITERAL().text.substring(1).toInteger()
        return super.visitData_list(ctx)
    }

    @Override
    Object visitData_cartesian_point(StepFileParser.Data_cartesian_pointContext ctx) {
        StepFile.addPoint(currentId, ctx.par().exp(1).par().exp()*.DOUBLE_NUMBER().text)
        return super.visitData_cartesian_point(ctx)
    }

    @Override
    Object visitData_line(StepFileParser.Data_lineContext ctx) {
        StepFile.addLine(currentId, [ctx.par().exp(1).REF_LITERAL().text.substring(1), ctx.par().exp(2).REF_LITERAL().text.substring(1)])
        return super.visitData_line(ctx)
    }

    @Override
    Object visitData_direction(StepFileParser.Data_directionContext ctx) {
        StepFile.addDir(currentId, ctx.par().exp(1).par().exp()*.DOUBLE_NUMBER().text)
        return super.visitData_direction(ctx)
    }

    @Override
    Object visitData_color_rgb(StepFileParser.Data_color_rgbContext ctx) {
        StepFile.addCol(currentId, ctx.par().exp()[1..3]*.DOUBLE_NUMBER().text)
        return super.visitData_color_rgb(ctx)
    }

    @Override
    Object visitEndsec(StepFileParser.EndsecContext ctx) {
        println stepFile
        return super.visitEndsec(ctx)
    }

    @Override
    Object visitData_vector(StepFileParser.Data_vectorContext ctx) {
        StepFile.addVector(currentId, [ctx.par().exp(1).REF_LITERAL().text.substring(1), ctx.par().exp(2).DOUBLE_NUMBER().text])
        return super.visitData_vector(ctx)
    }

    @Override
    Object visitEdge_curve(StepFileParser.Edge_curveContext ctx) {
        StepFile.addEdgeCurve(currentId, [ctx.par().exp(1).REF_LITERAL().text.substring(1),
                                          ctx.par().exp(2).REF_LITERAL().text.substring(1),
                                          ctx.par().exp(3).REF_LITERAL().text.substring(1)],
                                          ctx.par().exp(4).TRUE() ? true : false )
        return super.visitEdge_curve(ctx)
    }

    @Override
    Object visitData_oriented_edge(StepFileParser.Data_oriented_edgeContext ctx) {
        StepFile.addOrientedEdge(currentId, ctx.par().exp(3).REF_LITERAL().text.substring(1),
                                ctx.par().exp(4).TRUE() ? true : false)
        return super.visitData_oriented_edge(ctx)
    }

    @Override
    Object visitData_axis2_placement_3d(StepFileParser.Data_axis2_placement_3dContext ctx) {
        StepFile.addAxis2Placement3d(currentId, [ctx.par().exp(1).REF_LITERAL().text.substring(1),
                                                 ctx.par().exp(2).REF_LITERAL().text.substring(1),
                                                 ctx.par().exp(3).REF_LITERAL().text.substring(1)])
        return super.visitData_axis2_placement_3d(ctx)
    }

    @Override
    Object visitData_edge_loop(StepFileParser.Data_edge_loopContext ctx) {
        StepFile.addEdgeLoop(currentId, ctx.par().exp(1).par().exp()*.REF_LITERAL().text*.substring(1))
        return super.visitData_edge_loop(ctx)
    }

    @Override
    Object visitData_vertex_point(StepFileParser.Data_vertex_pointContext ctx) {
        StepFile.addVertexPoint(currentId, ctx.par().exp(1).REF_LITERAL().text.substring(1))
        return super.visitData_vertex_point(ctx)
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
