// Generated from /home/auo/dev/cad-builder/cad-dsl-step-native/src/main/antlr/step_file.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link step_fileParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface step_fileVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link step_fileParser#step_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStep_file(step_fileParser.Step_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#header_section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_section(step_fileParser.Header_sectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#header_fd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_fd(step_fileParser.Header_fdContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#header_fn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_fn(step_fileParser.Header_fnContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#header_sc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_sc(step_fileParser.Header_scContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#endsec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndsec(step_fileParser.EndsecContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_length_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_length_unit(step_fileParser.Data_length_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#par}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar(step_fileParser.ParContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(step_fileParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader(step_fileParser.HeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_section(step_fileParser.Data_sectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_header(step_fileParser.Data_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item(step_fileParser.Data_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_list(step_fileParser.Data_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_styled_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_styled_item(step_fileParser.Data_styled_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_presentation_layer_ass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_presentation_layer_ass(step_fileParser.Data_presentation_layer_assContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_cartesian_point}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_cartesian_point(step_fileParser.Data_cartesian_pointContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_area_style}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_area_style(step_fileParser.Data_area_styleContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_line(step_fileParser.Data_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#data_oriented_edge}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_oriented_edge(step_fileParser.Data_oriented_edgeContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#sep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSep(step_fileParser.SepContext ctx);
	/**
	 * Visit a parse tree produced by {@link step_fileParser#nl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNl(step_fileParser.NlContext ctx);
}