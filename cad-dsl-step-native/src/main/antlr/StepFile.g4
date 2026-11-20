//ISO-10303-21
grammar StepFile;

@header {
   package org.taack.cad.step.antlr;
}


step_file
    : 'ISO-10303-21;' header_section data_section 'END-ISO-10303-21;' EOF
    ;

header_section
    : header header_fd header_fn header_sc endsec
    ;

header_fd
    : 'FILE_DESCRIPTION' par nl
    ;

header_fn
    : 'FILE_NAME' par nl
    ;

header_sc
    : 'FILE_SCHEMA' par nl
    ;

endsec
    : 'ENDSEC' nl
    ;

data_length_unit
    : REF_LITERAL '=( LENGTH_UNIT ( ) NAMED_UNIT ( * ) SI_UNIT ( .MILLI., .METRE. ) );'
    ;

empty_par
    : '(' ')'
    ;

par
    : '(' exp (sep exp)* ')' (sep exp)*
    | empty_par
    ;

exp
    : (
        par
        |
            (STRING_LITERAL
            | DOUBLE_NUMBER
            | INTEGER_NUMBER
            | REF_LITERAL
            | ASTERIX
            | FALSE
            | UNSPECIFIED
            | BOTH
            | LENGTH_MEASURE
            | STERADIAN
            | NOT_KNOWN
            | TRUE
            | DOL
            | RADIAN
            )
      )+
    ;

header
    : 'HEADER' nl
    ;

data_section
    : data_header data_list+ endsec
    ;

data_header
    : 'DATA' nl
    ;


data_item
    : data_styled_item
    | data_presentation_layer_ass
    | data_line
    | data_oriented_edge
    | data_cartesian_point
    | data_area_style
    | data_axis2_placement_3d
    | data_direction
    | data_b_spline_curve_with_knots
    | data_surface_style_usage
    | data_vertex_point
    | data_b_spline_surface_with_knots
    | data_surface_style_fill_area
    | data_surface_side_style
    | data_color_rgb
    | data_vector
    | edge_curve
    | data_uncertainty_measure_with_unit
    | data_presentation_style_assignement
    | data_face_outer_bound
    | data_edge_loop
    | data_plane
    | data_advanced_face
    | data_fill_area_style_colour
    | data_mechanical_design_geometric_presentation_representation
    | data_circle
    | data_cylindrical_surface
    | data_face_bound
    | data_shape_definition_representation
    | data_product_definition_formation_with_specified_source
    | data_application_context
    | data_context_dependent_shape_representation
    | data_application_protocol_definition
    | data_spherical_surface
    | data_product_definition_shape
    | data_brep_with_voids
    | data_conical_surface
    | data_product
    | data_advanced_brep_shape_representation
    | data_closed_shell
    | data_oriented_closed_shell
    | data_next_assembly_usage_occurrence
    ;

data_item_serie_item
    : data_item_serie_item_length_unit
    | data_item_serie_item_named_unit
    | data_item_serie_item_plane_angle_unit
    | data_item_serie_item_si_unit
    | data_item_serie_item_geometric_representation_context
    | data_item_serie_item_global_uncertainty_assigned_context
    | data_item_serie_item_global_unit_assigned_context
    | data_item_serie_item_representation_context
    | data_item_serie_item_bounded_curve
    | data_item_serie_item_b_spline_curve
    | data_item_serie_item_b_spline_curve_with_knots
    | data_item_serie_item_curve
    | data_item_serie_item_geometric_representation_item
    | data_item_serie_item_rational_b_spline_curve
    | data_item_serie_item_representation_item
    | data_item_serie_item_solid_angle_unit
    | data_item_serie_item_representation_relationship
    | data_item_serie_item_representation_relationship_with_transformation
    | data_item_serie_item_shape_representation_relationship
    ;

 data_item_serie_item_length_unit
    : 'LENGTH_UNIT' par
    ;

 data_item_serie_item_named_unit
    : 'NAMED_UNIT' par
    ;

 data_item_serie_item_plane_angle_unit
    : 'PLANE_ANGLE_UNIT' par
    ;

 data_item_serie_item_si_unit
    : 'SI_UNIT' par
    ;

  data_item_serie_item_geometric_representation_context
    : 'GEOMETRIC_REPRESENTATION_CONTEXT' par
    ;

  data_item_serie_item_representation_relationship
    : 'REPRESENTATION_RELATIONSHIP' par
    ;

  data_item_serie_item_representation_relationship_with_transformation
    : 'REPRESENTATION_RELATIONSHIP_WITH_TRANSFORMATION' par
    ;

  data_item_serie_item_shape_representation_relationship
    : 'SHAPE_REPRESENTATION_RELATIONSHIP' par
    ;

  data_item_serie_item_global_uncertainty_assigned_context
    : 'GLOBAL_UNCERTAINTY_ASSIGNED_CONTEXT' par
    ;

  data_item_serie_item_global_unit_assigned_context
    : 'GLOBAL_UNIT_ASSIGNED_CONTEXT' par
    ;

  data_item_serie_item_representation_context
    : 'REPRESENTATION_CONTEXT' par
    ;

  data_item_serie_item_bounded_curve
    : 'BOUNDED_CURVE' par
    ;

  data_item_serie_item_b_spline_curve
    : 'B_SPLINE_CURVE' par
    ;

  data_item_serie_item_b_spline_curve_with_knots
    : 'B_SPLINE_CURVE_WITH_KNOTS' par
    ;

  data_item_serie_item_curve
    : 'CURVE' par
    ;

  data_item_serie_item_geometric_representation_item
    : 'GEOMETRIC_REPRESENTATION_ITEM' par
    ;

  data_item_serie_item_rational_b_spline_curve
    : 'RATIONAL_B_SPLINE_CURVE' par
    ;

  data_item_serie_item_representation_item
    : 'REPRESENTATION_ITEM' par
    ;

  data_item_serie_item_solid_angle_unit
    : 'SOLID_ANGLE_UNIT' par
    ;

data_list
    : data_length_unit
    | REF_LITERAL '=(' data_item_serie_item+ ')' nl
    | REF_LITERAL '=' data_item
    ;

data_styled_item
    : 'STYLED_ITEM' par nl
    ;

data_circle
    : 'CIRCLE' par nl
    ;

data_brep_with_voids
    : 'BREP_WITH_VOIDS' par nl
    ;

data_cylindrical_surface
    : 'CYLINDRICAL_SURFACE' par nl
    ;

data_product
    : 'PRODUCT' par nl
    ;

data_closed_shell
    : 'CLOSED_SHELL' par nl
    ;

data_oriented_closed_shell
    : 'ORIENTED_CLOSED_SHELL' par nl
    ;

data_shape_definition_representation
    : 'SHAPE_DEFINITION_REPRESENTATION' par nl
    ;

data_face_bound
    : 'FACE_BOUND' par nl
    ;

data_product_definition_formation_with_specified_source
    : 'PRODUCT_DEFINITION_FORMATION_WITH_SPECIFIED_SOURCE' par nl
    ;

data_application_protocol_definition
    : 'APPLICATION_PROTOCOL_DEFINITION' par nl
    ;

data_conical_surface
    : 'CONICAL_SURFACE' par nl
    ;

data_next_assembly_usage_occurrence
    : 'NEXT_ASSEMBLY_USAGE_OCCURRENCE' par nl
    ;

data_presentation_layer_ass
    : 'PRESENTATION_LAYER_ASSIGNMENT' par nl
    ;

data_mechanical_design_geometric_presentation_representation
    : 'MECHANICAL_DESIGN_GEOMETRIC_PRESENTATION_REPRESENTATION' par nl
    ;

data_context_dependent_shape_representation
    : 'CONTEXT_DEPENDENT_SHAPE_REPRESENTATION' par nl
    ;

data_application_context
    : 'APPLICATION_CONTEXT' par nl
    ;

data_spherical_surface
    : 'SPHERICAL_SURFACE' par nl
    ;

data_product_definition_shape
    : 'PRODUCT_DEFINITION_SHAPE' par nl
    ;

data_advanced_brep_shape_representation
    : 'ADVANCED_BREP_SHAPE_REPRESENTATION' par nl
    ;

data_cartesian_point
    : 'CARTESIAN_POINT' par nl
    ;

data_area_style
    : 'FILL_AREA_STYLE' par nl
    ;

data_axis2_placement_3d
    : 'AXIS2_PLACEMENT_3D' par nl
    ;

data_direction
    : 'DIRECTION' par nl
    ;

data_vector
    : 'VECTOR' par nl
    ;

data_color_rgb
    : 'COLOUR_RGB' par nl
    ;

edge_curve
    : 'EDGE_CURVE' par nl
    ;

data_b_spline_curve_with_knots
    : 'B_SPLINE_CURVE_WITH_KNOTS' par nl
    ;

data_b_spline_surface_with_knots
    : 'B_SPLINE_SURFACE_WITH_KNOTS' par nl
    ;

data_surface_style_usage
    : 'SURFACE_STYLE_USAGE' par nl
    ;

data_advanced_face
    : 'ADVANCED_FACE' par nl
    ;

data_vertex_point
    : 'VERTEX_POINT' par nl
    ;

data_uncertainty_measure_with_unit
    : 'UNCERTAINTY_MEASURE_WITH_UNIT' par nl
    ;

data_face_outer_bound
    : 'FACE_OUTER_BOUND' par nl
    ;

data_edge_loop
    : 'EDGE_LOOP' par nl
    ;

data_surface_style_fill_area
    : 'SURFACE_STYLE_FILL_AREA' par nl
    ;

data_surface_side_style
    : 'SURFACE_SIDE_STYLE' par nl
    ;

data_presentation_style_assignement
    : 'PRESENTATION_STYLE_ASSIGNMENT' par nl
    ;

data_line
    : 'LINE' par nl
    ;

data_plane
    : 'PLANE' par nl
    ;

data_oriented_edge
    : 'ORIENTED_EDGE' par nl
    ;

data_fill_area_style_colour
    : 'FILL_AREA_STYLE_COLOUR' par nl
    ;

DOUBLE_NUMBER
    : [-0-9.E]+
    ;

INTEGER_NUMBER
    : [0-9]+
    ;

ASTERIX
    : '*'
    ;

BOTH
    : '.BOTH.'
    ;

FALSE
    : '.F.'
    ;

NOT_KNOWN
    : '.NOT_KNOWN.'
    ;

DOL
    : '$'
    ;

TRUE
    : '.T.'
    ;

RADIAN
    : '.RADIAN.'
    ;

UNSPECIFIED
    : '.UNSPECIFIED.'
    ;

REF_LITERAL
    : '#' INTEGER_NUMBER
    ;

STRING_LITERAL
    : '\'' (ESC | .)*? '\''
    ;

LENGTH_MEASURE
    : 'LENGTH_MEASURE'
    ;

STERADIAN
    : '.STERADIAN.'
    ;

ESC: '\\"';

sep
    : ','
    ;

nl
    : ';'
    ;

WS
    : [ \t\r\n\f]+ -> skip
    ;