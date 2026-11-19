//ISO-10303-21
grammar step_file;
step_file
    : header_section data_section EOF
    ;

header_section
    : 'ISO-10303-21;' header header_fd header_fn header_sc endsec
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

data_namr_unit
    : REF_LITERAL '=( NAMED_UNIT ( * ) PLANE_ANGLE_UNIT ( ) SI_UNIT ( $, .RADIAN. ) );'
    ;

par
    : '(' exp (sep exp)* ')' (sep exp)*
    ;

exp
    : ( par | (STRING_LITERAL | DOUBLE_NUMBER | INTEGER_NUMBER | REF_LITERAL | ASTERIX | FALSE | UNSPECIFIED | BOTH | LENGTH_MEASURE)  )+
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
    ;

data_list
    : data_length_unit
    | data_namr_unit
    | REF_LITERAL '=' data_item
    ;

data_styled_item
    : 'STYLED_ITEM' par nl
    ;

data_presentation_layer_ass
    : 'PRESENTATION_LAYER_ASSIGNMENT' par nl
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

data_vertex_point
    : 'VERTEX_POINT' par nl
    ;

data_uncertainty_measure_with_unit
    : 'UNCERTAINTY_MEASURE_WITH_UNIT' par nl
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

data_oriented_edge
    : 'ORIENTED_EDGE' par nl
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
    : 'LENGTH_MEASURE' '(' DOUBLE_NUMBER ')'
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