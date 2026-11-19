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

par
    : '(' exp (sep exp)* ')' (sep exp)*
    ;

exp
    : par | ((STRING_LITERAL | DOUBLE_NUMBER | INTEGER_NUMBER | REF_LITERAL ) sep)+
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
    ;

data_list
    : data_length_unit | REF_LITERAL '=' data_item
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

data_line
    : 'LINE' par nl
    ;

data_oriented_edge
    : 'ORIENTED_EDGE' par nl
    ;

INTEGER_NUMBER
    : [0-9]+
    ;

DOUBLE_NUMBER
    : ('-')[0-9]+('.'[0-9]+('E'('-')[0-9]+))
    ;

REF_LITERAL
    : '#' INTEGER_NUMBER
    ;

STRING_LITERAL
    : '\'' (ESC | .)*? '\''
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