// Generated from StepFile.g4 by ANTLR 4.13.2

   package org.taack.cad.step.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link StepFileParser}.
 */
public interface StepFileListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link StepFileParser#step_file}.
	 * @param ctx the parse tree
	 */
	void enterStep_file(StepFileParser.Step_fileContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#step_file}.
	 * @param ctx the parse tree
	 */
	void exitStep_file(StepFileParser.Step_fileContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#header_section}.
	 * @param ctx the parse tree
	 */
	void enterHeader_section(StepFileParser.Header_sectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#header_section}.
	 * @param ctx the parse tree
	 */
	void exitHeader_section(StepFileParser.Header_sectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#header_fd}.
	 * @param ctx the parse tree
	 */
	void enterHeader_fd(StepFileParser.Header_fdContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#header_fd}.
	 * @param ctx the parse tree
	 */
	void exitHeader_fd(StepFileParser.Header_fdContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#header_fn}.
	 * @param ctx the parse tree
	 */
	void enterHeader_fn(StepFileParser.Header_fnContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#header_fn}.
	 * @param ctx the parse tree
	 */
	void exitHeader_fn(StepFileParser.Header_fnContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#header_sc}.
	 * @param ctx the parse tree
	 */
	void enterHeader_sc(StepFileParser.Header_scContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#header_sc}.
	 * @param ctx the parse tree
	 */
	void exitHeader_sc(StepFileParser.Header_scContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#endsec}.
	 * @param ctx the parse tree
	 */
	void enterEndsec(StepFileParser.EndsecContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#endsec}.
	 * @param ctx the parse tree
	 */
	void exitEndsec(StepFileParser.EndsecContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_length_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_length_unit(StepFileParser.Data_length_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_length_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_length_unit(StepFileParser.Data_length_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#empty_par}.
	 * @param ctx the parse tree
	 */
	void enterEmpty_par(StepFileParser.Empty_parContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#empty_par}.
	 * @param ctx the parse tree
	 */
	void exitEmpty_par(StepFileParser.Empty_parContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#par}.
	 * @param ctx the parse tree
	 */
	void enterPar(StepFileParser.ParContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#par}.
	 * @param ctx the parse tree
	 */
	void exitPar(StepFileParser.ParContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#exp}.
	 * @param ctx the parse tree
	 */
	void enterExp(StepFileParser.ExpContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#exp}.
	 * @param ctx the parse tree
	 */
	void exitExp(StepFileParser.ExpContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#header}.
	 * @param ctx the parse tree
	 */
	void enterHeader(StepFileParser.HeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#header}.
	 * @param ctx the parse tree
	 */
	void exitHeader(StepFileParser.HeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_section}.
	 * @param ctx the parse tree
	 */
	void enterData_section(StepFileParser.Data_sectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_section}.
	 * @param ctx the parse tree
	 */
	void exitData_section(StepFileParser.Data_sectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_header}.
	 * @param ctx the parse tree
	 */
	void enterData_header(StepFileParser.Data_headerContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_header}.
	 * @param ctx the parse tree
	 */
	void exitData_header(StepFileParser.Data_headerContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item}.
	 * @param ctx the parse tree
	 */
	void enterData_item(StepFileParser.Data_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item}.
	 * @param ctx the parse tree
	 */
	void exitData_item(StepFileParser.Data_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item(StepFileParser.Data_item_serie_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item(StepFileParser.Data_item_serie_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_length_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_length_unit(StepFileParser.Data_item_serie_item_length_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_length_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_length_unit(StepFileParser.Data_item_serie_item_length_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_named_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_named_unit(StepFileParser.Data_item_serie_item_named_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_named_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_named_unit(StepFileParser.Data_item_serie_item_named_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_plane_angle_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_plane_angle_unit(StepFileParser.Data_item_serie_item_plane_angle_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_plane_angle_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_plane_angle_unit(StepFileParser.Data_item_serie_item_plane_angle_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_si_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_si_unit(StepFileParser.Data_item_serie_item_si_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_si_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_si_unit(StepFileParser.Data_item_serie_item_si_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_context}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_geometric_representation_context(StepFileParser.Data_item_serie_item_geometric_representation_contextContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_context}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_geometric_representation_context(StepFileParser.Data_item_serie_item_geometric_representation_contextContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_representation_relationship(StepFileParser.Data_item_serie_item_representation_relationshipContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_representation_relationship(StepFileParser.Data_item_serie_item_representation_relationshipContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship_with_transformation}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_representation_relationship_with_transformation(StepFileParser.Data_item_serie_item_representation_relationship_with_transformationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_relationship_with_transformation}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_representation_relationship_with_transformation(StepFileParser.Data_item_serie_item_representation_relationship_with_transformationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_shape_representation_relationship}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_shape_representation_relationship(StepFileParser.Data_item_serie_item_shape_representation_relationshipContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_shape_representation_relationship}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_shape_representation_relationship(StepFileParser.Data_item_serie_item_shape_representation_relationshipContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_global_uncertainty_assigned_context}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_global_uncertainty_assigned_context(StepFileParser.Data_item_serie_item_global_uncertainty_assigned_contextContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_global_uncertainty_assigned_context}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_global_uncertainty_assigned_context(StepFileParser.Data_item_serie_item_global_uncertainty_assigned_contextContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_global_unit_assigned_context}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_global_unit_assigned_context(StepFileParser.Data_item_serie_item_global_unit_assigned_contextContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_global_unit_assigned_context}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_global_unit_assigned_context(StepFileParser.Data_item_serie_item_global_unit_assigned_contextContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_context}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_representation_context(StepFileParser.Data_item_serie_item_representation_contextContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_context}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_representation_context(StepFileParser.Data_item_serie_item_representation_contextContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_bounded_curve}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_bounded_curve(StepFileParser.Data_item_serie_item_bounded_curveContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_bounded_curve}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_bounded_curve(StepFileParser.Data_item_serie_item_bounded_curveContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_b_spline_curve(StepFileParser.Data_item_serie_item_b_spline_curveContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_b_spline_curve(StepFileParser.Data_item_serie_item_b_spline_curveContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_b_spline_curve_with_knots(StepFileParser.Data_item_serie_item_b_spline_curve_with_knotsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_b_spline_curve_with_knots(StepFileParser.Data_item_serie_item_b_spline_curve_with_knotsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_curve}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_curve(StepFileParser.Data_item_serie_item_curveContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_curve}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_curve(StepFileParser.Data_item_serie_item_curveContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_item}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_geometric_representation_item(StepFileParser.Data_item_serie_item_geometric_representation_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_geometric_representation_item}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_geometric_representation_item(StepFileParser.Data_item_serie_item_geometric_representation_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_rational_b_spline_curve}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_rational_b_spline_curve(StepFileParser.Data_item_serie_item_rational_b_spline_curveContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_rational_b_spline_curve}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_rational_b_spline_curve(StepFileParser.Data_item_serie_item_rational_b_spline_curveContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_item}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_representation_item(StepFileParser.Data_item_serie_item_representation_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_representation_item}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_representation_item(StepFileParser.Data_item_serie_item_representation_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_item_serie_item_solid_angle_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_item_serie_item_solid_angle_unit(StepFileParser.Data_item_serie_item_solid_angle_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_item_serie_item_solid_angle_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_item_serie_item_solid_angle_unit(StepFileParser.Data_item_serie_item_solid_angle_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_list}.
	 * @param ctx the parse tree
	 */
	void enterData_list(StepFileParser.Data_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_list}.
	 * @param ctx the parse tree
	 */
	void exitData_list(StepFileParser.Data_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_styled_item}.
	 * @param ctx the parse tree
	 */
	void enterData_styled_item(StepFileParser.Data_styled_itemContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_styled_item}.
	 * @param ctx the parse tree
	 */
	void exitData_styled_item(StepFileParser.Data_styled_itemContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_circle}.
	 * @param ctx the parse tree
	 */
	void enterData_circle(StepFileParser.Data_circleContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_circle}.
	 * @param ctx the parse tree
	 */
	void exitData_circle(StepFileParser.Data_circleContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_brep_with_voids}.
	 * @param ctx the parse tree
	 */
	void enterData_brep_with_voids(StepFileParser.Data_brep_with_voidsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_brep_with_voids}.
	 * @param ctx the parse tree
	 */
	void exitData_brep_with_voids(StepFileParser.Data_brep_with_voidsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_cylindrical_surface}.
	 * @param ctx the parse tree
	 */
	void enterData_cylindrical_surface(StepFileParser.Data_cylindrical_surfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_cylindrical_surface}.
	 * @param ctx the parse tree
	 */
	void exitData_cylindrical_surface(StepFileParser.Data_cylindrical_surfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_product}.
	 * @param ctx the parse tree
	 */
	void enterData_product(StepFileParser.Data_productContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_product}.
	 * @param ctx the parse tree
	 */
	void exitData_product(StepFileParser.Data_productContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_closed_shell}.
	 * @param ctx the parse tree
	 */
	void enterData_closed_shell(StepFileParser.Data_closed_shellContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_closed_shell}.
	 * @param ctx the parse tree
	 */
	void exitData_closed_shell(StepFileParser.Data_closed_shellContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_oriented_closed_shell}.
	 * @param ctx the parse tree
	 */
	void enterData_oriented_closed_shell(StepFileParser.Data_oriented_closed_shellContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_oriented_closed_shell}.
	 * @param ctx the parse tree
	 */
	void exitData_oriented_closed_shell(StepFileParser.Data_oriented_closed_shellContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_shape_definition_representation}.
	 * @param ctx the parse tree
	 */
	void enterData_shape_definition_representation(StepFileParser.Data_shape_definition_representationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_shape_definition_representation}.
	 * @param ctx the parse tree
	 */
	void exitData_shape_definition_representation(StepFileParser.Data_shape_definition_representationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_face_bound}.
	 * @param ctx the parse tree
	 */
	void enterData_face_bound(StepFileParser.Data_face_boundContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_face_bound}.
	 * @param ctx the parse tree
	 */
	void exitData_face_bound(StepFileParser.Data_face_boundContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_product_definition_formation_with_specified_source}.
	 * @param ctx the parse tree
	 */
	void enterData_product_definition_formation_with_specified_source(StepFileParser.Data_product_definition_formation_with_specified_sourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_product_definition_formation_with_specified_source}.
	 * @param ctx the parse tree
	 */
	void exitData_product_definition_formation_with_specified_source(StepFileParser.Data_product_definition_formation_with_specified_sourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_application_protocol_definition}.
	 * @param ctx the parse tree
	 */
	void enterData_application_protocol_definition(StepFileParser.Data_application_protocol_definitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_application_protocol_definition}.
	 * @param ctx the parse tree
	 */
	void exitData_application_protocol_definition(StepFileParser.Data_application_protocol_definitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_conical_surface}.
	 * @param ctx the parse tree
	 */
	void enterData_conical_surface(StepFileParser.Data_conical_surfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_conical_surface}.
	 * @param ctx the parse tree
	 */
	void exitData_conical_surface(StepFileParser.Data_conical_surfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_next_assembly_usage_occurrence}.
	 * @param ctx the parse tree
	 */
	void enterData_next_assembly_usage_occurrence(StepFileParser.Data_next_assembly_usage_occurrenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_next_assembly_usage_occurrence}.
	 * @param ctx the parse tree
	 */
	void exitData_next_assembly_usage_occurrence(StepFileParser.Data_next_assembly_usage_occurrenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_presentation_layer_ass}.
	 * @param ctx the parse tree
	 */
	void enterData_presentation_layer_ass(StepFileParser.Data_presentation_layer_assContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_presentation_layer_ass}.
	 * @param ctx the parse tree
	 */
	void exitData_presentation_layer_ass(StepFileParser.Data_presentation_layer_assContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_mechanical_design_geometric_presentation_representation}.
	 * @param ctx the parse tree
	 */
	void enterData_mechanical_design_geometric_presentation_representation(StepFileParser.Data_mechanical_design_geometric_presentation_representationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_mechanical_design_geometric_presentation_representation}.
	 * @param ctx the parse tree
	 */
	void exitData_mechanical_design_geometric_presentation_representation(StepFileParser.Data_mechanical_design_geometric_presentation_representationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_context_dependent_shape_representation}.
	 * @param ctx the parse tree
	 */
	void enterData_context_dependent_shape_representation(StepFileParser.Data_context_dependent_shape_representationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_context_dependent_shape_representation}.
	 * @param ctx the parse tree
	 */
	void exitData_context_dependent_shape_representation(StepFileParser.Data_context_dependent_shape_representationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_application_context}.
	 * @param ctx the parse tree
	 */
	void enterData_application_context(StepFileParser.Data_application_contextContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_application_context}.
	 * @param ctx the parse tree
	 */
	void exitData_application_context(StepFileParser.Data_application_contextContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_spherical_surface}.
	 * @param ctx the parse tree
	 */
	void enterData_spherical_surface(StepFileParser.Data_spherical_surfaceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_spherical_surface}.
	 * @param ctx the parse tree
	 */
	void exitData_spherical_surface(StepFileParser.Data_spherical_surfaceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_product_definition_shape}.
	 * @param ctx the parse tree
	 */
	void enterData_product_definition_shape(StepFileParser.Data_product_definition_shapeContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_product_definition_shape}.
	 * @param ctx the parse tree
	 */
	void exitData_product_definition_shape(StepFileParser.Data_product_definition_shapeContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_advanced_brep_shape_representation}.
	 * @param ctx the parse tree
	 */
	void enterData_advanced_brep_shape_representation(StepFileParser.Data_advanced_brep_shape_representationContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_advanced_brep_shape_representation}.
	 * @param ctx the parse tree
	 */
	void exitData_advanced_brep_shape_representation(StepFileParser.Data_advanced_brep_shape_representationContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_cartesian_point}.
	 * @param ctx the parse tree
	 */
	void enterData_cartesian_point(StepFileParser.Data_cartesian_pointContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_cartesian_point}.
	 * @param ctx the parse tree
	 */
	void exitData_cartesian_point(StepFileParser.Data_cartesian_pointContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_area_style}.
	 * @param ctx the parse tree
	 */
	void enterData_area_style(StepFileParser.Data_area_styleContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_area_style}.
	 * @param ctx the parse tree
	 */
	void exitData_area_style(StepFileParser.Data_area_styleContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_axis2_placement_3d}.
	 * @param ctx the parse tree
	 */
	void enterData_axis2_placement_3d(StepFileParser.Data_axis2_placement_3dContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_axis2_placement_3d}.
	 * @param ctx the parse tree
	 */
	void exitData_axis2_placement_3d(StepFileParser.Data_axis2_placement_3dContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_direction}.
	 * @param ctx the parse tree
	 */
	void enterData_direction(StepFileParser.Data_directionContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_direction}.
	 * @param ctx the parse tree
	 */
	void exitData_direction(StepFileParser.Data_directionContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_vector}.
	 * @param ctx the parse tree
	 */
	void enterData_vector(StepFileParser.Data_vectorContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_vector}.
	 * @param ctx the parse tree
	 */
	void exitData_vector(StepFileParser.Data_vectorContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_color_rgb}.
	 * @param ctx the parse tree
	 */
	void enterData_color_rgb(StepFileParser.Data_color_rgbContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_color_rgb}.
	 * @param ctx the parse tree
	 */
	void exitData_color_rgb(StepFileParser.Data_color_rgbContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#edge_curve}.
	 * @param ctx the parse tree
	 */
	void enterEdge_curve(StepFileParser.Edge_curveContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#edge_curve}.
	 * @param ctx the parse tree
	 */
	void exitEdge_curve(StepFileParser.Edge_curveContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 */
	void enterData_b_spline_curve_with_knots(StepFileParser.Data_b_spline_curve_with_knotsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_b_spline_curve_with_knots}.
	 * @param ctx the parse tree
	 */
	void exitData_b_spline_curve_with_knots(StepFileParser.Data_b_spline_curve_with_knotsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_b_spline_surface_with_knots}.
	 * @param ctx the parse tree
	 */
	void enterData_b_spline_surface_with_knots(StepFileParser.Data_b_spline_surface_with_knotsContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_b_spline_surface_with_knots}.
	 * @param ctx the parse tree
	 */
	void exitData_b_spline_surface_with_knots(StepFileParser.Data_b_spline_surface_with_knotsContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_surface_style_usage}.
	 * @param ctx the parse tree
	 */
	void enterData_surface_style_usage(StepFileParser.Data_surface_style_usageContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_surface_style_usage}.
	 * @param ctx the parse tree
	 */
	void exitData_surface_style_usage(StepFileParser.Data_surface_style_usageContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_advanced_face}.
	 * @param ctx the parse tree
	 */
	void enterData_advanced_face(StepFileParser.Data_advanced_faceContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_advanced_face}.
	 * @param ctx the parse tree
	 */
	void exitData_advanced_face(StepFileParser.Data_advanced_faceContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_vertex_point}.
	 * @param ctx the parse tree
	 */
	void enterData_vertex_point(StepFileParser.Data_vertex_pointContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_vertex_point}.
	 * @param ctx the parse tree
	 */
	void exitData_vertex_point(StepFileParser.Data_vertex_pointContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_uncertainty_measure_with_unit}.
	 * @param ctx the parse tree
	 */
	void enterData_uncertainty_measure_with_unit(StepFileParser.Data_uncertainty_measure_with_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_uncertainty_measure_with_unit}.
	 * @param ctx the parse tree
	 */
	void exitData_uncertainty_measure_with_unit(StepFileParser.Data_uncertainty_measure_with_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_face_outer_bound}.
	 * @param ctx the parse tree
	 */
	void enterData_face_outer_bound(StepFileParser.Data_face_outer_boundContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_face_outer_bound}.
	 * @param ctx the parse tree
	 */
	void exitData_face_outer_bound(StepFileParser.Data_face_outer_boundContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_edge_loop}.
	 * @param ctx the parse tree
	 */
	void enterData_edge_loop(StepFileParser.Data_edge_loopContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_edge_loop}.
	 * @param ctx the parse tree
	 */
	void exitData_edge_loop(StepFileParser.Data_edge_loopContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_surface_style_fill_area}.
	 * @param ctx the parse tree
	 */
	void enterData_surface_style_fill_area(StepFileParser.Data_surface_style_fill_areaContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_surface_style_fill_area}.
	 * @param ctx the parse tree
	 */
	void exitData_surface_style_fill_area(StepFileParser.Data_surface_style_fill_areaContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_surface_side_style}.
	 * @param ctx the parse tree
	 */
	void enterData_surface_side_style(StepFileParser.Data_surface_side_styleContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_surface_side_style}.
	 * @param ctx the parse tree
	 */
	void exitData_surface_side_style(StepFileParser.Data_surface_side_styleContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_presentation_style_assignement}.
	 * @param ctx the parse tree
	 */
	void enterData_presentation_style_assignement(StepFileParser.Data_presentation_style_assignementContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_presentation_style_assignement}.
	 * @param ctx the parse tree
	 */
	void exitData_presentation_style_assignement(StepFileParser.Data_presentation_style_assignementContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_line}.
	 * @param ctx the parse tree
	 */
	void enterData_line(StepFileParser.Data_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_line}.
	 * @param ctx the parse tree
	 */
	void exitData_line(StepFileParser.Data_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_plane}.
	 * @param ctx the parse tree
	 */
	void enterData_plane(StepFileParser.Data_planeContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_plane}.
	 * @param ctx the parse tree
	 */
	void exitData_plane(StepFileParser.Data_planeContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_oriented_edge}.
	 * @param ctx the parse tree
	 */
	void enterData_oriented_edge(StepFileParser.Data_oriented_edgeContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_oriented_edge}.
	 * @param ctx the parse tree
	 */
	void exitData_oriented_edge(StepFileParser.Data_oriented_edgeContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#data_fill_area_style_colour}.
	 * @param ctx the parse tree
	 */
	void enterData_fill_area_style_colour(StepFileParser.Data_fill_area_style_colourContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#data_fill_area_style_colour}.
	 * @param ctx the parse tree
	 */
	void exitData_fill_area_style_colour(StepFileParser.Data_fill_area_style_colourContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#sep}.
	 * @param ctx the parse tree
	 */
	void enterSep(StepFileParser.SepContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#sep}.
	 * @param ctx the parse tree
	 */
	void exitSep(StepFileParser.SepContext ctx);
	/**
	 * Enter a parse tree produced by {@link StepFileParser#nl}.
	 * @param ctx the parse tree
	 */
	void enterNl(StepFileParser.NlContext ctx);
	/**
	 * Exit a parse tree produced by {@link StepFileParser#nl}.
	 * @param ctx the parse tree
	 */
	void exitNl(StepFileParser.NlContext ctx);
}