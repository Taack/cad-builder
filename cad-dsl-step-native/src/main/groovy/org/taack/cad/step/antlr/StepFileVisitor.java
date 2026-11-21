// Generated from StepFile.g4 by ANTLR 4.13.2

   package org.taack.cad.step.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link StepFileParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface StepFileVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link StepFileParser#step_file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStep_file(StepFileParser.Step_fileContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#header_section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_section(StepFileParser.Header_sectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#header_fd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_fd(StepFileParser.Header_fdContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#header_fn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_fn(StepFileParser.Header_fnContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#header_sc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader_sc(StepFileParser.Header_scContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#endsec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEndsec(StepFileParser.EndsecContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#empty_par}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty_par(StepFileParser.Empty_parContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#par}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPar(StepFileParser.ParContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#exp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExp(StepFileParser.ExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHeader(StepFileParser.HeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_section}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_section(StepFileParser.Data_sectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_header}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_header(StepFileParser.Data_headerContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item(StepFileParser.Data_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item(StepFileParser.Data_item_serie_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_length_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_length_unit(StepFileParser.Data_item_serie_item_length_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_named_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_named_unit(StepFileParser.Data_item_serie_item_named_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_plane_angle_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_plane_angle_unit(StepFileParser.Data_item_serie_item_plane_angle_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_si_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_si_unit(StepFileParser.Data_item_serie_item_si_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_context}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_geometric_representation_context(StepFileParser.Data_item_serie_item_geometric_representation_contextContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_representation_relationship(StepFileParser.Data_item_serie_item_representation_relationshipContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship_with_transformation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_representation_relationship_with_transformation(StepFileParser.Data_item_serie_item_representation_relationship_with_transformationContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_shape_representation_relationship}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_shape_representation_relationship(StepFileParser.Data_item_serie_item_shape_representation_relationshipContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_global_uncertainty_assigned_context}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_global_uncertainty_assigned_context(StepFileParser.Data_item_serie_item_global_uncertainty_assigned_contextContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_global_unit_assigned_context}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_global_unit_assigned_context(StepFileParser.Data_item_serie_item_global_unit_assigned_contextContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_context}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_representation_context(StepFileParser.Data_item_serie_item_representation_contextContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_bounded_curve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_bounded_curve(StepFileParser.Data_item_serie_item_bounded_curveContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_b_spline_curve(StepFileParser.Data_item_serie_item_b_spline_curveContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_b_spline_curve_with_knots(StepFileParser.Data_item_serie_item_b_spline_curve_with_knotsContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_curve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_curve(StepFileParser.Data_item_serie_item_curveContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_geometric_representation_item(StepFileParser.Data_item_serie_item_geometric_representation_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_rational_b_spline_curve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_rational_b_spline_curve(StepFileParser.Data_item_serie_item_rational_b_spline_curveContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_representation_item(StepFileParser.Data_item_serie_item_representation_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_item_serie_item_solid_angle_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_item_serie_item_solid_angle_unit(StepFileParser.Data_item_serie_item_solid_angle_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_list}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_list(StepFileParser.Data_listContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_styled_item}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_styled_item(StepFileParser.Data_styled_itemContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_circle}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_circle(StepFileParser.Data_circleContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_brep_with_voids}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_brep_with_voids(StepFileParser.Data_brep_with_voidsContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_cylindrical_surface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_cylindrical_surface(StepFileParser.Data_cylindrical_surfaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_product}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_product(StepFileParser.Data_productContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_closed_shell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_closed_shell(StepFileParser.Data_closed_shellContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_oriented_closed_shell}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_oriented_closed_shell(StepFileParser.Data_oriented_closed_shellContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_shape_definition_representation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_shape_definition_representation(StepFileParser.Data_shape_definition_representationContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_face_bound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_face_bound(StepFileParser.Data_face_boundContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_product_definition_formation_with_specified_source}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_product_definition_formation_with_specified_source(StepFileParser.Data_product_definition_formation_with_specified_sourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_application_protocol_definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_application_protocol_definition(StepFileParser.Data_application_protocol_definitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_conical_surface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_conical_surface(StepFileParser.Data_conical_surfaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_next_assembly_usage_occurrence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_next_assembly_usage_occurrence(StepFileParser.Data_next_assembly_usage_occurrenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_presentation_layer_ass}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_presentation_layer_ass(StepFileParser.Data_presentation_layer_assContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_mechanical_design_geometric_presentation_representation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_mechanical_design_geometric_presentation_representation(StepFileParser.Data_mechanical_design_geometric_presentation_representationContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_context_dependent_shape_representation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_context_dependent_shape_representation(StepFileParser.Data_context_dependent_shape_representationContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_application_context}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_application_context(StepFileParser.Data_application_contextContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_spherical_surface}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_spherical_surface(StepFileParser.Data_spherical_surfaceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_product_definition_shape}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_product_definition_shape(StepFileParser.Data_product_definition_shapeContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_advanced_brep_shape_representation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_advanced_brep_shape_representation(StepFileParser.Data_advanced_brep_shape_representationContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_cartesian_point}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_cartesian_point(StepFileParser.Data_cartesian_pointContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_area_style}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_area_style(StepFileParser.Data_area_styleContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_axis2_placement_3d}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_axis2_placement_3d(StepFileParser.Data_axis2_placement_3dContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_direction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_direction(StepFileParser.Data_directionContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_vector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_vector(StepFileParser.Data_vectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_color_rgb}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_color_rgb(StepFileParser.Data_color_rgbContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#edge_curve}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEdge_curve(StepFileParser.Edge_curveContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_b_spline_curve_with_knots(StepFileParser.Data_b_spline_curve_with_knotsContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_b_spline_surface_with_knots}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_b_spline_surface_with_knots(StepFileParser.Data_b_spline_surface_with_knotsContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_surface_style_usage}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_surface_style_usage(StepFileParser.Data_surface_style_usageContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_advanced_face}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_advanced_face(StepFileParser.Data_advanced_faceContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_vertex_point}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_vertex_point(StepFileParser.Data_vertex_pointContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_uncertainty_measure_with_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_uncertainty_measure_with_unit(StepFileParser.Data_uncertainty_measure_with_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_face_outer_bound}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_face_outer_bound(StepFileParser.Data_face_outer_boundContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_edge_loop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_edge_loop(StepFileParser.Data_edge_loopContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_surface_style_fill_area}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_surface_style_fill_area(StepFileParser.Data_surface_style_fill_areaContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_surface_side_style}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_surface_side_style(StepFileParser.Data_surface_side_styleContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_presentation_style_assignement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_presentation_style_assignement(StepFileParser.Data_presentation_style_assignementContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_line(StepFileParser.Data_lineContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_plane}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_plane(StepFileParser.Data_planeContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_oriented_edge}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_oriented_edge(StepFileParser.Data_oriented_edgeContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#data_fill_area_style_colour}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_fill_area_style_colour(StepFileParser.Data_fill_area_style_colourContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#sep}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSep(StepFileParser.SepContext ctx);
	/**
	 * Visit a parse tree produced by {@link StepFileParser#nl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNl(StepFileParser.NlContext ctx);
}