// Generated from StepFile.g4 by ANTLR 4.13.2

   package org.taack.cad.step.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class StepFileParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, T__39=40, T__40=41, T__41=42, T__42=43, T__43=44, T__44=45, 
		T__45=46, T__46=47, T__47=48, T__48=49, T__49=50, T__50=51, T__51=52, 
		T__52=53, T__53=54, T__54=55, T__55=56, T__56=57, T__57=58, T__58=59, 
		T__59=60, T__60=61, T__61=62, T__62=63, T__63=64, T__64=65, T__65=66, 
		T__66=67, T__67=68, T__68=69, T__69=70, T__70=71, T__71=72, T__72=73, 
		T__73=74, T__74=75, DOUBLE_NUMBER=76, INTEGER_NUMBER=77, ASTERIX=78, BOTH=79, 
		FALSE=80, NOT_KNOWN=81, DOL=82, TRUE=83, RADIAN=84, UNSPECIFIED=85, REF_LITERAL=86, 
		STRING_LITERAL=87, LENGTH_MEASURE=88, STERADIAN=89, ESC=90, WS=91;
	public static final int
		RULE_step_file = 0, RULE_header_section = 1, RULE_header_fd = 2, RULE_header_fn = 3, 
		RULE_header_sc = 4, RULE_endsec = 5, RULE_data_length_unit = 6, RULE_empty_par = 7, 
		RULE_par = 8, RULE_exp = 9, RULE_header = 10, RULE_data_section = 11, 
		RULE_data_header = 12, RULE_data_item = 13, RULE_data_item_serie_item = 14, 
		RULE_data_item_serie_item_length_unit = 15, RULE_data_item_serie_item_named_unit = 16, 
		RULE_data_item_serie_item_plane_angle_unit = 17, RULE_data_item_serie_item_si_unit = 18, 
		RULE_data_item_serie_item_geometric_representation_context = 19, RULE_data_item_serie_item_representation_relationship = 20, 
		RULE_data_item_serie_item_representation_relationship_with_transformation = 21, 
		RULE_data_item_serie_item_shape_representation_relationship = 22, RULE_data_item_serie_item_global_uncertainty_assigned_context = 23, 
		RULE_data_item_serie_item_global_unit_assigned_context = 24, RULE_data_item_serie_item_representation_context = 25, 
		RULE_data_item_serie_item_bounded_curve = 26, RULE_data_item_serie_item_b_spline_curve = 27, 
		RULE_data_item_serie_item_b_spline_curve_with_knots = 28, RULE_data_item_serie_item_curve = 29, 
		RULE_data_item_serie_item_geometric_representation_item = 30, RULE_data_item_serie_item_rational_b_spline_curve = 31, 
		RULE_data_item_serie_item_representation_item = 32, RULE_data_item_serie_item_solid_angle_unit = 33, 
		RULE_data_list = 34, RULE_data_styled_item = 35, RULE_data_circle = 36, 
		RULE_data_brep_with_voids = 37, RULE_data_cylindrical_surface = 38, RULE_data_product = 39, 
		RULE_data_closed_shell = 40, RULE_data_oriented_closed_shell = 41, RULE_data_shape_definition_representation = 42, 
		RULE_data_face_bound = 43, RULE_data_product_definition_formation_with_specified_source = 44, 
		RULE_data_application_protocol_definition = 45, RULE_data_conical_surface = 46, 
		RULE_data_next_assembly_usage_occurrence = 47, RULE_data_presentation_layer_ass = 48, 
		RULE_data_mechanical_design_geometric_presentation_representation = 49, 
		RULE_data_context_dependent_shape_representation = 50, RULE_data_application_context = 51, 
		RULE_data_spherical_surface = 52, RULE_data_product_definition_shape = 53, 
		RULE_data_advanced_brep_shape_representation = 54, RULE_data_cartesian_point = 55, 
		RULE_data_area_style = 56, RULE_data_axis2_placement_3d = 57, RULE_data_direction = 58, 
		RULE_data_vector = 59, RULE_data_color_rgb = 60, RULE_edge_curve = 61, 
		RULE_data_b_spline_curve_with_knots = 62, RULE_data_b_spline_surface_with_knots = 63, 
		RULE_data_surface_style_usage = 64, RULE_data_advanced_face = 65, RULE_data_vertex_point = 66, 
		RULE_data_uncertainty_measure_with_unit = 67, RULE_data_face_outer_bound = 68, 
		RULE_data_edge_loop = 69, RULE_data_surface_style_fill_area = 70, RULE_data_surface_side_style = 71, 
		RULE_data_presentation_style_assignement = 72, RULE_data_line = 73, RULE_data_plane = 74, 
		RULE_data_oriented_edge = 75, RULE_data_fill_area_style_colour = 76, RULE_sep = 77, 
		RULE_nl = 78;
	private static String[] makeRuleNames() {
		return new String[] {
			"step_file", "header_section", "header_fd", "header_fn", "header_sc", 
			"endsec", "data_length_unit", "empty_par", "par", "exp", "header", "data_section", 
			"data_header", "data_item", "data_item_serie_item", "data_item_serie_item_length_unit", 
			"data_item_serie_item_named_unit", "data_item_serie_item_plane_angle_unit", 
			"data_item_serie_item_si_unit", "data_item_serie_item_geometric_representation_context", 
			"data_item_serie_item_representation_relationship", "data_item_serie_item_representation_relationship_with_transformation", 
			"data_item_serie_item_shape_representation_relationship", "data_item_serie_item_global_uncertainty_assigned_context", 
			"data_item_serie_item_global_unit_assigned_context", "data_item_serie_item_representation_context", 
			"data_item_serie_item_bounded_curve", "data_item_serie_item_b_spline_curve", 
			"data_item_serie_item_b_spline_curve_with_knots", "data_item_serie_item_curve", 
			"data_item_serie_item_geometric_representation_item", "data_item_serie_item_rational_b_spline_curve", 
			"data_item_serie_item_representation_item", "data_item_serie_item_solid_angle_unit", 
			"data_list", "data_styled_item", "data_circle", "data_brep_with_voids", 
			"data_cylindrical_surface", "data_product", "data_closed_shell", "data_oriented_closed_shell", 
			"data_shape_definition_representation", "data_face_bound", "data_product_definition_formation_with_specified_source", 
			"data_application_protocol_definition", "data_conical_surface", "data_next_assembly_usage_occurrence", 
			"data_presentation_layer_ass", "data_mechanical_design_geometric_presentation_representation", 
			"data_context_dependent_shape_representation", "data_application_context", 
			"data_spherical_surface", "data_product_definition_shape", "data_advanced_brep_shape_representation", 
			"data_cartesian_point", "data_area_style", "data_axis2_placement_3d", 
			"data_direction", "data_vector", "data_color_rgb", "edge_curve", "data_b_spline_curve_with_knots", 
			"data_b_spline_surface_with_knots", "data_surface_style_usage", "data_advanced_face", 
			"data_vertex_point", "data_uncertainty_measure_with_unit", "data_face_outer_bound", 
			"data_edge_loop", "data_surface_style_fill_area", "data_surface_side_style", 
			"data_presentation_style_assignement", "data_line", "data_plane", "data_oriented_edge", 
			"data_fill_area_style_colour", "sep", "nl"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'ISO-10303-21;'", "'END-ISO-10303-21;'", "'FILE_DESCRIPTION'", 
			"'FILE_NAME'", "'FILE_SCHEMA'", "'ENDSEC'", "'=( LENGTH_UNIT ( ) NAMED_UNIT ( * ) SI_UNIT ( .MILLI., .METRE. ) );'", 
			"'('", "')'", "'HEADER'", "'DATA'", "'LENGTH_UNIT'", "'NAMED_UNIT'", 
			"'PLANE_ANGLE_UNIT'", "'SI_UNIT'", "'GEOMETRIC_REPRESENTATION_CONTEXT'", 
			"'REPRESENTATION_RELATIONSHIP'", "'REPRESENTATION_RELATIONSHIP_WITH_TRANSFORMATION'", 
			"'SHAPE_REPRESENTATION_RELATIONSHIP'", "'GLOBAL_UNCERTAINTY_ASSIGNED_CONTEXT'", 
			"'GLOBAL_UNIT_ASSIGNED_CONTEXT'", "'REPRESENTATION_CONTEXT'", "'BOUNDED_CURVE'", 
			"'B_SPLINE_CURVE'", "'B_SPLINE_CURVE_WITH_KNOTS'", "'CURVE'", "'GEOMETRIC_REPRESENTATION_ITEM'", 
			"'RATIONAL_B_SPLINE_CURVE'", "'REPRESENTATION_ITEM'", "'SOLID_ANGLE_UNIT'", 
			"'=('", "'='", "'STYLED_ITEM'", "'CIRCLE'", "'BREP_WITH_VOIDS'", "'CYLINDRICAL_SURFACE'", 
			"'PRODUCT'", "'CLOSED_SHELL'", "'ORIENTED_CLOSED_SHELL'", "'SHAPE_DEFINITION_REPRESENTATION'", 
			"'FACE_BOUND'", "'PRODUCT_DEFINITION_FORMATION_WITH_SPECIFIED_SOURCE'", 
			"'APPLICATION_PROTOCOL_DEFINITION'", "'CONICAL_SURFACE'", "'NEXT_ASSEMBLY_USAGE_OCCURRENCE'", 
			"'PRESENTATION_LAYER_ASSIGNMENT'", "'MECHANICAL_DESIGN_GEOMETRIC_PRESENTATION_REPRESENTATION'", 
			"'CONTEXT_DEPENDENT_SHAPE_REPRESENTATION'", "'APPLICATION_CONTEXT'", 
			"'SPHERICAL_SURFACE'", "'PRODUCT_DEFINITION_SHAPE'", "'ADVANCED_BREP_SHAPE_REPRESENTATION'", 
			"'CARTESIAN_POINT'", "'FILL_AREA_STYLE'", "'AXIS2_PLACEMENT_3D'", "'DIRECTION'", 
			"'VECTOR'", "'COLOUR_RGB'", "'EDGE_CURVE'", "'B_SPLINE_SURFACE_WITH_KNOTS'", 
			"'SURFACE_STYLE_USAGE'", "'ADVANCED_FACE'", "'VERTEX_POINT'", "'UNCERTAINTY_MEASURE_WITH_UNIT'", 
			"'FACE_OUTER_BOUND'", "'EDGE_LOOP'", "'SURFACE_STYLE_FILL_AREA'", "'SURFACE_SIDE_STYLE'", 
			"'PRESENTATION_STYLE_ASSIGNMENT'", "'LINE'", "'PLANE'", "'ORIENTED_EDGE'", 
			"'FILL_AREA_STYLE_COLOUR'", "','", "';'", null, null, "'*'", "'.BOTH.'", 
			"'.F.'", "'.NOT_KNOWN.'", "'$'", "'.T.'", "'.RADIAN.'", "'.UNSPECIFIED.'", 
			null, null, "'LENGTH_MEASURE'", "'.STERADIAN.'", "'\\\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "DOUBLE_NUMBER", "INTEGER_NUMBER", "ASTERIX", 
			"BOTH", "FALSE", "NOT_KNOWN", "DOL", "TRUE", "RADIAN", "UNSPECIFIED", 
			"REF_LITERAL", "STRING_LITERAL", "LENGTH_MEASURE", "STERADIAN", "ESC", 
			"WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "StepFile.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public StepFileParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Step_fileContext extends ParserRuleContext {
		public Header_sectionContext header_section() {
			return getRuleContext(Header_sectionContext.class,0);
		}
		public Data_sectionContext data_section() {
			return getRuleContext(Data_sectionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(StepFileParser.EOF, 0); }
		public Step_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_step_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterStep_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitStep_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitStep_file(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Step_fileContext step_file() throws RecognitionException {
		Step_fileContext _localctx = new Step_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_step_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(T__0);
			setState(159);
			header_section();
			setState(160);
			data_section();
			setState(161);
			match(T__1);
			setState(162);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Header_sectionContext extends ParserRuleContext {
		public HeaderContext header() {
			return getRuleContext(HeaderContext.class,0);
		}
		public Header_fdContext header_fd() {
			return getRuleContext(Header_fdContext.class,0);
		}
		public Header_fnContext header_fn() {
			return getRuleContext(Header_fnContext.class,0);
		}
		public Header_scContext header_sc() {
			return getRuleContext(Header_scContext.class,0);
		}
		public EndsecContext endsec() {
			return getRuleContext(EndsecContext.class,0);
		}
		public Header_sectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header_section; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterHeader_section(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitHeader_section(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitHeader_section(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_sectionContext header_section() throws RecognitionException {
		Header_sectionContext _localctx = new Header_sectionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_header_section);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			header();
			setState(165);
			header_fd();
			setState(166);
			header_fn();
			setState(167);
			header_sc();
			setState(168);
			endsec();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Header_fdContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Header_fdContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header_fd; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterHeader_fd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitHeader_fd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitHeader_fd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_fdContext header_fd() throws RecognitionException {
		Header_fdContext _localctx = new Header_fdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_header_fd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			match(T__2);
			setState(171);
			par();
			setState(172);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Header_fnContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Header_fnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header_fn; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterHeader_fn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitHeader_fn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitHeader_fn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_fnContext header_fn() throws RecognitionException {
		Header_fnContext _localctx = new Header_fnContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_header_fn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(174);
			match(T__3);
			setState(175);
			par();
			setState(176);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Header_scContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Header_scContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header_sc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterHeader_sc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitHeader_sc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitHeader_sc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_scContext header_sc() throws RecognitionException {
		Header_scContext _localctx = new Header_scContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_header_sc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			match(T__4);
			setState(179);
			par();
			setState(180);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class EndsecContext extends ParserRuleContext {
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public EndsecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_endsec; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterEndsec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitEndsec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitEndsec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndsecContext endsec() throws RecognitionException {
		EndsecContext _localctx = new EndsecContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_endsec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(182);
			match(T__5);
			setState(183);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_length_unitContext extends ParserRuleContext {
		public TerminalNode REF_LITERAL() { return getToken(StepFileParser.REF_LITERAL, 0); }
		public Data_length_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_length_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_length_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_length_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_length_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_length_unitContext data_length_unit() throws RecognitionException {
		Data_length_unitContext _localctx = new Data_length_unitContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_data_length_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(185);
			match(REF_LITERAL);
			setState(186);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Empty_parContext extends ParserRuleContext {
		public Empty_parContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_empty_par; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterEmpty_par(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitEmpty_par(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitEmpty_par(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Empty_parContext empty_par() throws RecognitionException {
		Empty_parContext _localctx = new Empty_parContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_empty_par);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(188);
			match(T__7);
			setState(189);
			match(T__8);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParContext extends ParserRuleContext {
		public List<ExpContext> exp() {
			return getRuleContexts(ExpContext.class);
		}
		public ExpContext exp(int i) {
			return getRuleContext(ExpContext.class,i);
		}
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public Empty_parContext empty_par() {
			return getRuleContext(Empty_parContext.class,0);
		}
		public ParContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_par; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterPar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitPar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitPar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParContext par() throws RecognitionException {
		ParContext _localctx = new ParContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_par);
		int _la;
		try {
			int _alt;
			setState(211);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(191);
				match(T__7);
				setState(192);
				exp();
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__73) {
					{
					{
					setState(193);
					sep();
					setState(194);
					exp();
					}
					}
					setState(200);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(201);
				match(T__8);
				setState(207);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(202);
						sep();
						setState(203);
						exp();
						}
						} 
					}
					setState(209);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(210);
				empty_par();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpContext extends ParserRuleContext {
		public List<ParContext> par() {
			return getRuleContexts(ParContext.class);
		}
		public ParContext par(int i) {
			return getRuleContext(ParContext.class,i);
		}
		public List<TerminalNode> STRING_LITERAL() { return getTokens(StepFileParser.STRING_LITERAL); }
		public TerminalNode STRING_LITERAL(int i) {
			return getToken(StepFileParser.STRING_LITERAL, i);
		}
		public List<TerminalNode> DOUBLE_NUMBER() { return getTokens(StepFileParser.DOUBLE_NUMBER); }
		public TerminalNode DOUBLE_NUMBER(int i) {
			return getToken(StepFileParser.DOUBLE_NUMBER, i);
		}
		public List<TerminalNode> INTEGER_NUMBER() { return getTokens(StepFileParser.INTEGER_NUMBER); }
		public TerminalNode INTEGER_NUMBER(int i) {
			return getToken(StepFileParser.INTEGER_NUMBER, i);
		}
		public List<TerminalNode> REF_LITERAL() { return getTokens(StepFileParser.REF_LITERAL); }
		public TerminalNode REF_LITERAL(int i) {
			return getToken(StepFileParser.REF_LITERAL, i);
		}
		public List<TerminalNode> ASTERIX() { return getTokens(StepFileParser.ASTERIX); }
		public TerminalNode ASTERIX(int i) {
			return getToken(StepFileParser.ASTERIX, i);
		}
		public List<TerminalNode> FALSE() { return getTokens(StepFileParser.FALSE); }
		public TerminalNode FALSE(int i) {
			return getToken(StepFileParser.FALSE, i);
		}
		public List<TerminalNode> UNSPECIFIED() { return getTokens(StepFileParser.UNSPECIFIED); }
		public TerminalNode UNSPECIFIED(int i) {
			return getToken(StepFileParser.UNSPECIFIED, i);
		}
		public List<TerminalNode> BOTH() { return getTokens(StepFileParser.BOTH); }
		public TerminalNode BOTH(int i) {
			return getToken(StepFileParser.BOTH, i);
		}
		public List<TerminalNode> LENGTH_MEASURE() { return getTokens(StepFileParser.LENGTH_MEASURE); }
		public TerminalNode LENGTH_MEASURE(int i) {
			return getToken(StepFileParser.LENGTH_MEASURE, i);
		}
		public List<TerminalNode> STERADIAN() { return getTokens(StepFileParser.STERADIAN); }
		public TerminalNode STERADIAN(int i) {
			return getToken(StepFileParser.STERADIAN, i);
		}
		public List<TerminalNode> NOT_KNOWN() { return getTokens(StepFileParser.NOT_KNOWN); }
		public TerminalNode NOT_KNOWN(int i) {
			return getToken(StepFileParser.NOT_KNOWN, i);
		}
		public List<TerminalNode> TRUE() { return getTokens(StepFileParser.TRUE); }
		public TerminalNode TRUE(int i) {
			return getToken(StepFileParser.TRUE, i);
		}
		public List<TerminalNode> DOL() { return getTokens(StepFileParser.DOL); }
		public TerminalNode DOL(int i) {
			return getToken(StepFileParser.DOL, i);
		}
		public List<TerminalNode> RADIAN() { return getTokens(StepFileParser.RADIAN); }
		public TerminalNode RADIAN(int i) {
			return getToken(StepFileParser.RADIAN, i);
		}
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_exp);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(215); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(215);
					_errHandler.sync(this);
					switch (_input.LA(1)) {
					case T__7:
						{
						setState(213);
						par();
						}
						break;
					case DOUBLE_NUMBER:
					case INTEGER_NUMBER:
					case ASTERIX:
					case BOTH:
					case FALSE:
					case NOT_KNOWN:
					case DOL:
					case TRUE:
					case RADIAN:
					case UNSPECIFIED:
					case REF_LITERAL:
					case STRING_LITERAL:
					case LENGTH_MEASURE:
					case STERADIAN:
						{
						setState(214);
						_la = _input.LA(1);
						if ( !(((((_la - 76)) & ~0x3f) == 0 && ((1L << (_la - 76)) & 16383L) != 0)) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(217); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class HeaderContext extends ParserRuleContext {
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public HeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeaderContext header() throws RecognitionException {
		HeaderContext _localctx = new HeaderContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(219);
			match(T__9);
			setState(220);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_sectionContext extends ParserRuleContext {
		public Data_headerContext data_header() {
			return getRuleContext(Data_headerContext.class,0);
		}
		public EndsecContext endsec() {
			return getRuleContext(EndsecContext.class,0);
		}
		public List<Data_listContext> data_list() {
			return getRuleContexts(Data_listContext.class);
		}
		public Data_listContext data_list(int i) {
			return getRuleContext(Data_listContext.class,i);
		}
		public Data_sectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_section; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_section(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_section(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_section(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_sectionContext data_section() throws RecognitionException {
		Data_sectionContext _localctx = new Data_sectionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_data_section);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			data_header();
			setState(224); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(223);
				data_list();
				}
				}
				setState(226); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==REF_LITERAL );
			setState(228);
			endsec();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_headerContext extends ParserRuleContext {
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_headerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_header; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_header(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_header(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_header(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_headerContext data_header() throws RecognitionException {
		Data_headerContext _localctx = new Data_headerContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_data_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(T__10);
			setState(231);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_itemContext extends ParserRuleContext {
		public Data_styled_itemContext data_styled_item() {
			return getRuleContext(Data_styled_itemContext.class,0);
		}
		public Data_presentation_layer_assContext data_presentation_layer_ass() {
			return getRuleContext(Data_presentation_layer_assContext.class,0);
		}
		public Data_lineContext data_line() {
			return getRuleContext(Data_lineContext.class,0);
		}
		public Data_oriented_edgeContext data_oriented_edge() {
			return getRuleContext(Data_oriented_edgeContext.class,0);
		}
		public Data_cartesian_pointContext data_cartesian_point() {
			return getRuleContext(Data_cartesian_pointContext.class,0);
		}
		public Data_area_styleContext data_area_style() {
			return getRuleContext(Data_area_styleContext.class,0);
		}
		public Data_axis2_placement_3dContext data_axis2_placement_3d() {
			return getRuleContext(Data_axis2_placement_3dContext.class,0);
		}
		public Data_directionContext data_direction() {
			return getRuleContext(Data_directionContext.class,0);
		}
		public Data_b_spline_curve_with_knotsContext data_b_spline_curve_with_knots() {
			return getRuleContext(Data_b_spline_curve_with_knotsContext.class,0);
		}
		public Data_surface_style_usageContext data_surface_style_usage() {
			return getRuleContext(Data_surface_style_usageContext.class,0);
		}
		public Data_vertex_pointContext data_vertex_point() {
			return getRuleContext(Data_vertex_pointContext.class,0);
		}
		public Data_b_spline_surface_with_knotsContext data_b_spline_surface_with_knots() {
			return getRuleContext(Data_b_spline_surface_with_knotsContext.class,0);
		}
		public Data_surface_style_fill_areaContext data_surface_style_fill_area() {
			return getRuleContext(Data_surface_style_fill_areaContext.class,0);
		}
		public Data_surface_side_styleContext data_surface_side_style() {
			return getRuleContext(Data_surface_side_styleContext.class,0);
		}
		public Data_color_rgbContext data_color_rgb() {
			return getRuleContext(Data_color_rgbContext.class,0);
		}
		public Data_vectorContext data_vector() {
			return getRuleContext(Data_vectorContext.class,0);
		}
		public Edge_curveContext edge_curve() {
			return getRuleContext(Edge_curveContext.class,0);
		}
		public Data_uncertainty_measure_with_unitContext data_uncertainty_measure_with_unit() {
			return getRuleContext(Data_uncertainty_measure_with_unitContext.class,0);
		}
		public Data_presentation_style_assignementContext data_presentation_style_assignement() {
			return getRuleContext(Data_presentation_style_assignementContext.class,0);
		}
		public Data_face_outer_boundContext data_face_outer_bound() {
			return getRuleContext(Data_face_outer_boundContext.class,0);
		}
		public Data_edge_loopContext data_edge_loop() {
			return getRuleContext(Data_edge_loopContext.class,0);
		}
		public Data_planeContext data_plane() {
			return getRuleContext(Data_planeContext.class,0);
		}
		public Data_advanced_faceContext data_advanced_face() {
			return getRuleContext(Data_advanced_faceContext.class,0);
		}
		public Data_fill_area_style_colourContext data_fill_area_style_colour() {
			return getRuleContext(Data_fill_area_style_colourContext.class,0);
		}
		public Data_mechanical_design_geometric_presentation_representationContext data_mechanical_design_geometric_presentation_representation() {
			return getRuleContext(Data_mechanical_design_geometric_presentation_representationContext.class,0);
		}
		public Data_circleContext data_circle() {
			return getRuleContext(Data_circleContext.class,0);
		}
		public Data_cylindrical_surfaceContext data_cylindrical_surface() {
			return getRuleContext(Data_cylindrical_surfaceContext.class,0);
		}
		public Data_face_boundContext data_face_bound() {
			return getRuleContext(Data_face_boundContext.class,0);
		}
		public Data_shape_definition_representationContext data_shape_definition_representation() {
			return getRuleContext(Data_shape_definition_representationContext.class,0);
		}
		public Data_product_definition_formation_with_specified_sourceContext data_product_definition_formation_with_specified_source() {
			return getRuleContext(Data_product_definition_formation_with_specified_sourceContext.class,0);
		}
		public Data_application_contextContext data_application_context() {
			return getRuleContext(Data_application_contextContext.class,0);
		}
		public Data_context_dependent_shape_representationContext data_context_dependent_shape_representation() {
			return getRuleContext(Data_context_dependent_shape_representationContext.class,0);
		}
		public Data_application_protocol_definitionContext data_application_protocol_definition() {
			return getRuleContext(Data_application_protocol_definitionContext.class,0);
		}
		public Data_spherical_surfaceContext data_spherical_surface() {
			return getRuleContext(Data_spherical_surfaceContext.class,0);
		}
		public Data_product_definition_shapeContext data_product_definition_shape() {
			return getRuleContext(Data_product_definition_shapeContext.class,0);
		}
		public Data_brep_with_voidsContext data_brep_with_voids() {
			return getRuleContext(Data_brep_with_voidsContext.class,0);
		}
		public Data_conical_surfaceContext data_conical_surface() {
			return getRuleContext(Data_conical_surfaceContext.class,0);
		}
		public Data_productContext data_product() {
			return getRuleContext(Data_productContext.class,0);
		}
		public Data_advanced_brep_shape_representationContext data_advanced_brep_shape_representation() {
			return getRuleContext(Data_advanced_brep_shape_representationContext.class,0);
		}
		public Data_closed_shellContext data_closed_shell() {
			return getRuleContext(Data_closed_shellContext.class,0);
		}
		public Data_oriented_closed_shellContext data_oriented_closed_shell() {
			return getRuleContext(Data_oriented_closed_shellContext.class,0);
		}
		public Data_next_assembly_usage_occurrenceContext data_next_assembly_usage_occurrence() {
			return getRuleContext(Data_next_assembly_usage_occurrenceContext.class,0);
		}
		public Data_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_itemContext data_item() throws RecognitionException {
		Data_itemContext _localctx = new Data_itemContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_data_item);
		try {
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__32:
				enterOuterAlt(_localctx, 1);
				{
				setState(233);
				data_styled_item();
				}
				break;
			case T__45:
				enterOuterAlt(_localctx, 2);
				{
				setState(234);
				data_presentation_layer_ass();
				}
				break;
			case T__69:
				enterOuterAlt(_localctx, 3);
				{
				setState(235);
				data_line();
				}
				break;
			case T__71:
				enterOuterAlt(_localctx, 4);
				{
				setState(236);
				data_oriented_edge();
				}
				break;
			case T__52:
				enterOuterAlt(_localctx, 5);
				{
				setState(237);
				data_cartesian_point();
				}
				break;
			case T__53:
				enterOuterAlt(_localctx, 6);
				{
				setState(238);
				data_area_style();
				}
				break;
			case T__54:
				enterOuterAlt(_localctx, 7);
				{
				setState(239);
				data_axis2_placement_3d();
				}
				break;
			case T__55:
				enterOuterAlt(_localctx, 8);
				{
				setState(240);
				data_direction();
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 9);
				{
				setState(241);
				data_b_spline_curve_with_knots();
				}
				break;
			case T__60:
				enterOuterAlt(_localctx, 10);
				{
				setState(242);
				data_surface_style_usage();
				}
				break;
			case T__62:
				enterOuterAlt(_localctx, 11);
				{
				setState(243);
				data_vertex_point();
				}
				break;
			case T__59:
				enterOuterAlt(_localctx, 12);
				{
				setState(244);
				data_b_spline_surface_with_knots();
				}
				break;
			case T__66:
				enterOuterAlt(_localctx, 13);
				{
				setState(245);
				data_surface_style_fill_area();
				}
				break;
			case T__67:
				enterOuterAlt(_localctx, 14);
				{
				setState(246);
				data_surface_side_style();
				}
				break;
			case T__57:
				enterOuterAlt(_localctx, 15);
				{
				setState(247);
				data_color_rgb();
				}
				break;
			case T__56:
				enterOuterAlt(_localctx, 16);
				{
				setState(248);
				data_vector();
				}
				break;
			case T__58:
				enterOuterAlt(_localctx, 17);
				{
				setState(249);
				edge_curve();
				}
				break;
			case T__63:
				enterOuterAlt(_localctx, 18);
				{
				setState(250);
				data_uncertainty_measure_with_unit();
				}
				break;
			case T__68:
				enterOuterAlt(_localctx, 19);
				{
				setState(251);
				data_presentation_style_assignement();
				}
				break;
			case T__64:
				enterOuterAlt(_localctx, 20);
				{
				setState(252);
				data_face_outer_bound();
				}
				break;
			case T__65:
				enterOuterAlt(_localctx, 21);
				{
				setState(253);
				data_edge_loop();
				}
				break;
			case T__70:
				enterOuterAlt(_localctx, 22);
				{
				setState(254);
				data_plane();
				}
				break;
			case T__61:
				enterOuterAlt(_localctx, 23);
				{
				setState(255);
				data_advanced_face();
				}
				break;
			case T__72:
				enterOuterAlt(_localctx, 24);
				{
				setState(256);
				data_fill_area_style_colour();
				}
				break;
			case T__46:
				enterOuterAlt(_localctx, 25);
				{
				setState(257);
				data_mechanical_design_geometric_presentation_representation();
				}
				break;
			case T__33:
				enterOuterAlt(_localctx, 26);
				{
				setState(258);
				data_circle();
				}
				break;
			case T__35:
				enterOuterAlt(_localctx, 27);
				{
				setState(259);
				data_cylindrical_surface();
				}
				break;
			case T__40:
				enterOuterAlt(_localctx, 28);
				{
				setState(260);
				data_face_bound();
				}
				break;
			case T__39:
				enterOuterAlt(_localctx, 29);
				{
				setState(261);
				data_shape_definition_representation();
				}
				break;
			case T__41:
				enterOuterAlt(_localctx, 30);
				{
				setState(262);
				data_product_definition_formation_with_specified_source();
				}
				break;
			case T__48:
				enterOuterAlt(_localctx, 31);
				{
				setState(263);
				data_application_context();
				}
				break;
			case T__47:
				enterOuterAlt(_localctx, 32);
				{
				setState(264);
				data_context_dependent_shape_representation();
				}
				break;
			case T__42:
				enterOuterAlt(_localctx, 33);
				{
				setState(265);
				data_application_protocol_definition();
				}
				break;
			case T__49:
				enterOuterAlt(_localctx, 34);
				{
				setState(266);
				data_spherical_surface();
				}
				break;
			case T__50:
				enterOuterAlt(_localctx, 35);
				{
				setState(267);
				data_product_definition_shape();
				}
				break;
			case T__34:
				enterOuterAlt(_localctx, 36);
				{
				setState(268);
				data_brep_with_voids();
				}
				break;
			case T__43:
				enterOuterAlt(_localctx, 37);
				{
				setState(269);
				data_conical_surface();
				}
				break;
			case T__36:
				enterOuterAlt(_localctx, 38);
				{
				setState(270);
				data_product();
				}
				break;
			case T__51:
				enterOuterAlt(_localctx, 39);
				{
				setState(271);
				data_advanced_brep_shape_representation();
				}
				break;
			case T__37:
				enterOuterAlt(_localctx, 40);
				{
				setState(272);
				data_closed_shell();
				}
				break;
			case T__38:
				enterOuterAlt(_localctx, 41);
				{
				setState(273);
				data_oriented_closed_shell();
				}
				break;
			case T__44:
				enterOuterAlt(_localctx, 42);
				{
				setState(274);
				data_next_assembly_usage_occurrence();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_itemContext extends ParserRuleContext {
		public Data_item_serie_item_length_unitContext data_item_serie_item_length_unit() {
			return getRuleContext(Data_item_serie_item_length_unitContext.class,0);
		}
		public Data_item_serie_item_named_unitContext data_item_serie_item_named_unit() {
			return getRuleContext(Data_item_serie_item_named_unitContext.class,0);
		}
		public Data_item_serie_item_plane_angle_unitContext data_item_serie_item_plane_angle_unit() {
			return getRuleContext(Data_item_serie_item_plane_angle_unitContext.class,0);
		}
		public Data_item_serie_item_si_unitContext data_item_serie_item_si_unit() {
			return getRuleContext(Data_item_serie_item_si_unitContext.class,0);
		}
		public Data_item_serie_item_geometric_representation_contextContext data_item_serie_item_geometric_representation_context() {
			return getRuleContext(Data_item_serie_item_geometric_representation_contextContext.class,0);
		}
		public Data_item_serie_item_global_uncertainty_assigned_contextContext data_item_serie_item_global_uncertainty_assigned_context() {
			return getRuleContext(Data_item_serie_item_global_uncertainty_assigned_contextContext.class,0);
		}
		public Data_item_serie_item_global_unit_assigned_contextContext data_item_serie_item_global_unit_assigned_context() {
			return getRuleContext(Data_item_serie_item_global_unit_assigned_contextContext.class,0);
		}
		public Data_item_serie_item_representation_contextContext data_item_serie_item_representation_context() {
			return getRuleContext(Data_item_serie_item_representation_contextContext.class,0);
		}
		public Data_item_serie_item_bounded_curveContext data_item_serie_item_bounded_curve() {
			return getRuleContext(Data_item_serie_item_bounded_curveContext.class,0);
		}
		public Data_item_serie_item_b_spline_curveContext data_item_serie_item_b_spline_curve() {
			return getRuleContext(Data_item_serie_item_b_spline_curveContext.class,0);
		}
		public Data_item_serie_item_b_spline_curve_with_knotsContext data_item_serie_item_b_spline_curve_with_knots() {
			return getRuleContext(Data_item_serie_item_b_spline_curve_with_knotsContext.class,0);
		}
		public Data_item_serie_item_curveContext data_item_serie_item_curve() {
			return getRuleContext(Data_item_serie_item_curveContext.class,0);
		}
		public Data_item_serie_item_geometric_representation_itemContext data_item_serie_item_geometric_representation_item() {
			return getRuleContext(Data_item_serie_item_geometric_representation_itemContext.class,0);
		}
		public Data_item_serie_item_rational_b_spline_curveContext data_item_serie_item_rational_b_spline_curve() {
			return getRuleContext(Data_item_serie_item_rational_b_spline_curveContext.class,0);
		}
		public Data_item_serie_item_representation_itemContext data_item_serie_item_representation_item() {
			return getRuleContext(Data_item_serie_item_representation_itemContext.class,0);
		}
		public Data_item_serie_item_solid_angle_unitContext data_item_serie_item_solid_angle_unit() {
			return getRuleContext(Data_item_serie_item_solid_angle_unitContext.class,0);
		}
		public Data_item_serie_item_representation_relationshipContext data_item_serie_item_representation_relationship() {
			return getRuleContext(Data_item_serie_item_representation_relationshipContext.class,0);
		}
		public Data_item_serie_item_representation_relationship_with_transformationContext data_item_serie_item_representation_relationship_with_transformation() {
			return getRuleContext(Data_item_serie_item_representation_relationship_with_transformationContext.class,0);
		}
		public Data_item_serie_item_shape_representation_relationshipContext data_item_serie_item_shape_representation_relationship() {
			return getRuleContext(Data_item_serie_item_shape_representation_relationshipContext.class,0);
		}
		public Data_item_serie_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_itemContext data_item_serie_item() throws RecognitionException {
		Data_item_serie_itemContext _localctx = new Data_item_serie_itemContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_data_item_serie_item);
		try {
			setState(296);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(277);
				data_item_serie_item_length_unit();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 2);
				{
				setState(278);
				data_item_serie_item_named_unit();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 3);
				{
				setState(279);
				data_item_serie_item_plane_angle_unit();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 4);
				{
				setState(280);
				data_item_serie_item_si_unit();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 5);
				{
				setState(281);
				data_item_serie_item_geometric_representation_context();
				}
				break;
			case T__19:
				enterOuterAlt(_localctx, 6);
				{
				setState(282);
				data_item_serie_item_global_uncertainty_assigned_context();
				}
				break;
			case T__20:
				enterOuterAlt(_localctx, 7);
				{
				setState(283);
				data_item_serie_item_global_unit_assigned_context();
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 8);
				{
				setState(284);
				data_item_serie_item_representation_context();
				}
				break;
			case T__22:
				enterOuterAlt(_localctx, 9);
				{
				setState(285);
				data_item_serie_item_bounded_curve();
				}
				break;
			case T__23:
				enterOuterAlt(_localctx, 10);
				{
				setState(286);
				data_item_serie_item_b_spline_curve();
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 11);
				{
				setState(287);
				data_item_serie_item_b_spline_curve_with_knots();
				}
				break;
			case T__25:
				enterOuterAlt(_localctx, 12);
				{
				setState(288);
				data_item_serie_item_curve();
				}
				break;
			case T__26:
				enterOuterAlt(_localctx, 13);
				{
				setState(289);
				data_item_serie_item_geometric_representation_item();
				}
				break;
			case T__27:
				enterOuterAlt(_localctx, 14);
				{
				setState(290);
				data_item_serie_item_rational_b_spline_curve();
				}
				break;
			case T__28:
				enterOuterAlt(_localctx, 15);
				{
				setState(291);
				data_item_serie_item_representation_item();
				}
				break;
			case T__29:
				enterOuterAlt(_localctx, 16);
				{
				setState(292);
				data_item_serie_item_solid_angle_unit();
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 17);
				{
				setState(293);
				data_item_serie_item_representation_relationship();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 18);
				{
				setState(294);
				data_item_serie_item_representation_relationship_with_transformation();
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 19);
				{
				setState(295);
				data_item_serie_item_shape_representation_relationship();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_length_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_length_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_length_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_length_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_length_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_length_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_length_unitContext data_item_serie_item_length_unit() throws RecognitionException {
		Data_item_serie_item_length_unitContext _localctx = new Data_item_serie_item_length_unitContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_data_item_serie_item_length_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(298);
			match(T__11);
			setState(299);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_named_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_named_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_named_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_named_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_named_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_named_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_named_unitContext data_item_serie_item_named_unit() throws RecognitionException {
		Data_item_serie_item_named_unitContext _localctx = new Data_item_serie_item_named_unitContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_data_item_serie_item_named_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			match(T__12);
			setState(302);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_plane_angle_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_plane_angle_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_plane_angle_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_plane_angle_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_plane_angle_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_plane_angle_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_plane_angle_unitContext data_item_serie_item_plane_angle_unit() throws RecognitionException {
		Data_item_serie_item_plane_angle_unitContext _localctx = new Data_item_serie_item_plane_angle_unitContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_data_item_serie_item_plane_angle_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			match(T__13);
			setState(305);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_si_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_si_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_si_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_si_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_si_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_si_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_si_unitContext data_item_serie_item_si_unit() throws RecognitionException {
		Data_item_serie_item_si_unitContext _localctx = new Data_item_serie_item_si_unitContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_data_item_serie_item_si_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(307);
			match(T__14);
			setState(308);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_geometric_representation_contextContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_geometric_representation_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_geometric_representation_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_geometric_representation_context(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_geometric_representation_context(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_geometric_representation_context(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_geometric_representation_contextContext data_item_serie_item_geometric_representation_context() throws RecognitionException {
		Data_item_serie_item_geometric_representation_contextContext _localctx = new Data_item_serie_item_geometric_representation_contextContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_data_item_serie_item_geometric_representation_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(310);
			match(T__15);
			setState(311);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_representation_relationshipContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_representation_relationshipContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_representation_relationship; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_representation_relationship(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_representation_relationship(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_representation_relationship(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_representation_relationshipContext data_item_serie_item_representation_relationship() throws RecognitionException {
		Data_item_serie_item_representation_relationshipContext _localctx = new Data_item_serie_item_representation_relationshipContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_data_item_serie_item_representation_relationship);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(313);
			match(T__16);
			setState(314);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_representation_relationship_with_transformationContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_representation_relationship_with_transformationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_representation_relationship_with_transformation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_representation_relationship_with_transformation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_representation_relationship_with_transformation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_representation_relationship_with_transformation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_representation_relationship_with_transformationContext data_item_serie_item_representation_relationship_with_transformation() throws RecognitionException {
		Data_item_serie_item_representation_relationship_with_transformationContext _localctx = new Data_item_serie_item_representation_relationship_with_transformationContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_data_item_serie_item_representation_relationship_with_transformation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			match(T__17);
			setState(317);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_shape_representation_relationshipContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_shape_representation_relationshipContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_shape_representation_relationship; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_shape_representation_relationship(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_shape_representation_relationship(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_shape_representation_relationship(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_shape_representation_relationshipContext data_item_serie_item_shape_representation_relationship() throws RecognitionException {
		Data_item_serie_item_shape_representation_relationshipContext _localctx = new Data_item_serie_item_shape_representation_relationshipContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_data_item_serie_item_shape_representation_relationship);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(319);
			match(T__18);
			setState(320);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_global_uncertainty_assigned_contextContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_global_uncertainty_assigned_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_global_uncertainty_assigned_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_global_uncertainty_assigned_context(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_global_uncertainty_assigned_context(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_global_uncertainty_assigned_context(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_global_uncertainty_assigned_contextContext data_item_serie_item_global_uncertainty_assigned_context() throws RecognitionException {
		Data_item_serie_item_global_uncertainty_assigned_contextContext _localctx = new Data_item_serie_item_global_uncertainty_assigned_contextContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_data_item_serie_item_global_uncertainty_assigned_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322);
			match(T__19);
			setState(323);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_global_unit_assigned_contextContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_global_unit_assigned_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_global_unit_assigned_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_global_unit_assigned_context(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_global_unit_assigned_context(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_global_unit_assigned_context(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_global_unit_assigned_contextContext data_item_serie_item_global_unit_assigned_context() throws RecognitionException {
		Data_item_serie_item_global_unit_assigned_contextContext _localctx = new Data_item_serie_item_global_unit_assigned_contextContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_data_item_serie_item_global_unit_assigned_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(325);
			match(T__20);
			setState(326);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_representation_contextContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_representation_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_representation_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_representation_context(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_representation_context(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_representation_context(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_representation_contextContext data_item_serie_item_representation_context() throws RecognitionException {
		Data_item_serie_item_representation_contextContext _localctx = new Data_item_serie_item_representation_contextContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_data_item_serie_item_representation_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			match(T__21);
			setState(329);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_bounded_curveContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_bounded_curveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_bounded_curve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_bounded_curve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_bounded_curve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_bounded_curve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_bounded_curveContext data_item_serie_item_bounded_curve() throws RecognitionException {
		Data_item_serie_item_bounded_curveContext _localctx = new Data_item_serie_item_bounded_curveContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_data_item_serie_item_bounded_curve);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(331);
			match(T__22);
			setState(332);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_b_spline_curveContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_b_spline_curveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_b_spline_curve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_b_spline_curve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_b_spline_curve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_b_spline_curve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_b_spline_curveContext data_item_serie_item_b_spline_curve() throws RecognitionException {
		Data_item_serie_item_b_spline_curveContext _localctx = new Data_item_serie_item_b_spline_curveContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_data_item_serie_item_b_spline_curve);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(334);
			match(T__23);
			setState(335);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_b_spline_curve_with_knotsContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_b_spline_curve_with_knotsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_b_spline_curve_with_knots; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_b_spline_curve_with_knots(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_b_spline_curve_with_knots(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_b_spline_curve_with_knots(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_b_spline_curve_with_knotsContext data_item_serie_item_b_spline_curve_with_knots() throws RecognitionException {
		Data_item_serie_item_b_spline_curve_with_knotsContext _localctx = new Data_item_serie_item_b_spline_curve_with_knotsContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_data_item_serie_item_b_spline_curve_with_knots);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(337);
			match(T__24);
			setState(338);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_curveContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_curveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_curve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_curve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_curve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_curve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_curveContext data_item_serie_item_curve() throws RecognitionException {
		Data_item_serie_item_curveContext _localctx = new Data_item_serie_item_curveContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_data_item_serie_item_curve);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(340);
			match(T__25);
			setState(341);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_geometric_representation_itemContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_geometric_representation_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_geometric_representation_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_geometric_representation_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_geometric_representation_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_geometric_representation_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_geometric_representation_itemContext data_item_serie_item_geometric_representation_item() throws RecognitionException {
		Data_item_serie_item_geometric_representation_itemContext _localctx = new Data_item_serie_item_geometric_representation_itemContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_data_item_serie_item_geometric_representation_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(343);
			match(T__26);
			setState(344);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_rational_b_spline_curveContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_rational_b_spline_curveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_rational_b_spline_curve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_rational_b_spline_curve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_rational_b_spline_curve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_rational_b_spline_curve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_rational_b_spline_curveContext data_item_serie_item_rational_b_spline_curve() throws RecognitionException {
		Data_item_serie_item_rational_b_spline_curveContext _localctx = new Data_item_serie_item_rational_b_spline_curveContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_data_item_serie_item_rational_b_spline_curve);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(346);
			match(T__27);
			setState(347);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_representation_itemContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_representation_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_representation_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_representation_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_representation_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_representation_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_representation_itemContext data_item_serie_item_representation_item() throws RecognitionException {
		Data_item_serie_item_representation_itemContext _localctx = new Data_item_serie_item_representation_itemContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_data_item_serie_item_representation_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			match(T__28);
			setState(350);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_item_serie_item_solid_angle_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public Data_item_serie_item_solid_angle_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item_serie_item_solid_angle_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_item_serie_item_solid_angle_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_item_serie_item_solid_angle_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_item_serie_item_solid_angle_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_item_serie_item_solid_angle_unitContext data_item_serie_item_solid_angle_unit() throws RecognitionException {
		Data_item_serie_item_solid_angle_unitContext _localctx = new Data_item_serie_item_solid_angle_unitContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_data_item_serie_item_solid_angle_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			match(T__29);
			setState(353);
			par();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_listContext extends ParserRuleContext {
		public Data_length_unitContext data_length_unit() {
			return getRuleContext(Data_length_unitContext.class,0);
		}
		public TerminalNode REF_LITERAL() { return getToken(StepFileParser.REF_LITERAL, 0); }
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public List<Data_item_serie_itemContext> data_item_serie_item() {
			return getRuleContexts(Data_item_serie_itemContext.class);
		}
		public Data_item_serie_itemContext data_item_serie_item(int i) {
			return getRuleContext(Data_item_serie_itemContext.class,i);
		}
		public Data_itemContext data_item() {
			return getRuleContext(Data_itemContext.class,0);
		}
		public Data_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_listContext data_list() throws RecognitionException {
		Data_listContext _localctx = new Data_listContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_data_list);
		int _la;
		try {
			setState(369);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(355);
				data_length_unit();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(356);
				match(REF_LITERAL);
				setState(357);
				match(T__30);
				setState(359); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(358);
					data_item_serie_item();
					}
					}
					setState(361); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 2147479552L) != 0) );
				setState(363);
				match(T__8);
				setState(364);
				nl();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(366);
				match(REF_LITERAL);
				setState(367);
				match(T__31);
				setState(368);
				data_item();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_styled_itemContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_styled_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_styled_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_styled_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_styled_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_styled_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_styled_itemContext data_styled_item() throws RecognitionException {
		Data_styled_itemContext _localctx = new Data_styled_itemContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_data_styled_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			match(T__32);
			setState(372);
			par();
			setState(373);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_circleContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_circleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_circle; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_circle(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_circle(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_circle(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_circleContext data_circle() throws RecognitionException {
		Data_circleContext _localctx = new Data_circleContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_data_circle);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			match(T__33);
			setState(376);
			par();
			setState(377);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_brep_with_voidsContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_brep_with_voidsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_brep_with_voids; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_brep_with_voids(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_brep_with_voids(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_brep_with_voids(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_brep_with_voidsContext data_brep_with_voids() throws RecognitionException {
		Data_brep_with_voidsContext _localctx = new Data_brep_with_voidsContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_data_brep_with_voids);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			match(T__34);
			setState(380);
			par();
			setState(381);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_cylindrical_surfaceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_cylindrical_surfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_cylindrical_surface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_cylindrical_surface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_cylindrical_surface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_cylindrical_surface(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_cylindrical_surfaceContext data_cylindrical_surface() throws RecognitionException {
		Data_cylindrical_surfaceContext _localctx = new Data_cylindrical_surfaceContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_data_cylindrical_surface);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			match(T__35);
			setState(384);
			par();
			setState(385);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_productContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_productContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_product; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_product(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_product(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_product(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_productContext data_product() throws RecognitionException {
		Data_productContext _localctx = new Data_productContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_data_product);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(387);
			match(T__36);
			setState(388);
			par();
			setState(389);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_closed_shellContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_closed_shellContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_closed_shell; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_closed_shell(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_closed_shell(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_closed_shell(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_closed_shellContext data_closed_shell() throws RecognitionException {
		Data_closed_shellContext _localctx = new Data_closed_shellContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_data_closed_shell);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(391);
			match(T__37);
			setState(392);
			par();
			setState(393);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_oriented_closed_shellContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_oriented_closed_shellContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_oriented_closed_shell; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_oriented_closed_shell(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_oriented_closed_shell(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_oriented_closed_shell(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_oriented_closed_shellContext data_oriented_closed_shell() throws RecognitionException {
		Data_oriented_closed_shellContext _localctx = new Data_oriented_closed_shellContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_data_oriented_closed_shell);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			match(T__38);
			setState(396);
			par();
			setState(397);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_shape_definition_representationContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_shape_definition_representationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_shape_definition_representation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_shape_definition_representation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_shape_definition_representation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_shape_definition_representation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_shape_definition_representationContext data_shape_definition_representation() throws RecognitionException {
		Data_shape_definition_representationContext _localctx = new Data_shape_definition_representationContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_data_shape_definition_representation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(399);
			match(T__39);
			setState(400);
			par();
			setState(401);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_face_boundContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_face_boundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_face_bound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_face_bound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_face_bound(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_face_bound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_face_boundContext data_face_bound() throws RecognitionException {
		Data_face_boundContext _localctx = new Data_face_boundContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_data_face_bound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(403);
			match(T__40);
			setState(404);
			par();
			setState(405);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_product_definition_formation_with_specified_sourceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_product_definition_formation_with_specified_sourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_product_definition_formation_with_specified_source; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_product_definition_formation_with_specified_source(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_product_definition_formation_with_specified_source(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_product_definition_formation_with_specified_source(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_product_definition_formation_with_specified_sourceContext data_product_definition_formation_with_specified_source() throws RecognitionException {
		Data_product_definition_formation_with_specified_sourceContext _localctx = new Data_product_definition_formation_with_specified_sourceContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_data_product_definition_formation_with_specified_source);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(407);
			match(T__41);
			setState(408);
			par();
			setState(409);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_application_protocol_definitionContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_application_protocol_definitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_application_protocol_definition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_application_protocol_definition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_application_protocol_definition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_application_protocol_definition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_application_protocol_definitionContext data_application_protocol_definition() throws RecognitionException {
		Data_application_protocol_definitionContext _localctx = new Data_application_protocol_definitionContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_data_application_protocol_definition);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			match(T__42);
			setState(412);
			par();
			setState(413);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_conical_surfaceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_conical_surfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_conical_surface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_conical_surface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_conical_surface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_conical_surface(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_conical_surfaceContext data_conical_surface() throws RecognitionException {
		Data_conical_surfaceContext _localctx = new Data_conical_surfaceContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_data_conical_surface);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(415);
			match(T__43);
			setState(416);
			par();
			setState(417);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_next_assembly_usage_occurrenceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_next_assembly_usage_occurrenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_next_assembly_usage_occurrence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_next_assembly_usage_occurrence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_next_assembly_usage_occurrence(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_next_assembly_usage_occurrence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_next_assembly_usage_occurrenceContext data_next_assembly_usage_occurrence() throws RecognitionException {
		Data_next_assembly_usage_occurrenceContext _localctx = new Data_next_assembly_usage_occurrenceContext(_ctx, getState());
		enterRule(_localctx, 94, RULE_data_next_assembly_usage_occurrence);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(419);
			match(T__44);
			setState(420);
			par();
			setState(421);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_presentation_layer_assContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_presentation_layer_assContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_presentation_layer_ass; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_presentation_layer_ass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_presentation_layer_ass(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_presentation_layer_ass(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_presentation_layer_assContext data_presentation_layer_ass() throws RecognitionException {
		Data_presentation_layer_assContext _localctx = new Data_presentation_layer_assContext(_ctx, getState());
		enterRule(_localctx, 96, RULE_data_presentation_layer_ass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			match(T__45);
			setState(424);
			par();
			setState(425);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_mechanical_design_geometric_presentation_representationContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_mechanical_design_geometric_presentation_representationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_mechanical_design_geometric_presentation_representation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_mechanical_design_geometric_presentation_representation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_mechanical_design_geometric_presentation_representation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_mechanical_design_geometric_presentation_representation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_mechanical_design_geometric_presentation_representationContext data_mechanical_design_geometric_presentation_representation() throws RecognitionException {
		Data_mechanical_design_geometric_presentation_representationContext _localctx = new Data_mechanical_design_geometric_presentation_representationContext(_ctx, getState());
		enterRule(_localctx, 98, RULE_data_mechanical_design_geometric_presentation_representation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(427);
			match(T__46);
			setState(428);
			par();
			setState(429);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_context_dependent_shape_representationContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_context_dependent_shape_representationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_context_dependent_shape_representation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_context_dependent_shape_representation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_context_dependent_shape_representation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_context_dependent_shape_representation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_context_dependent_shape_representationContext data_context_dependent_shape_representation() throws RecognitionException {
		Data_context_dependent_shape_representationContext _localctx = new Data_context_dependent_shape_representationContext(_ctx, getState());
		enterRule(_localctx, 100, RULE_data_context_dependent_shape_representation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(T__47);
			setState(432);
			par();
			setState(433);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_application_contextContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_application_contextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_application_context; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_application_context(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_application_context(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_application_context(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_application_contextContext data_application_context() throws RecognitionException {
		Data_application_contextContext _localctx = new Data_application_contextContext(_ctx, getState());
		enterRule(_localctx, 102, RULE_data_application_context);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(435);
			match(T__48);
			setState(436);
			par();
			setState(437);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_spherical_surfaceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_spherical_surfaceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_spherical_surface; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_spherical_surface(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_spherical_surface(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_spherical_surface(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_spherical_surfaceContext data_spherical_surface() throws RecognitionException {
		Data_spherical_surfaceContext _localctx = new Data_spherical_surfaceContext(_ctx, getState());
		enterRule(_localctx, 104, RULE_data_spherical_surface);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(439);
			match(T__49);
			setState(440);
			par();
			setState(441);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_product_definition_shapeContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_product_definition_shapeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_product_definition_shape; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_product_definition_shape(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_product_definition_shape(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_product_definition_shape(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_product_definition_shapeContext data_product_definition_shape() throws RecognitionException {
		Data_product_definition_shapeContext _localctx = new Data_product_definition_shapeContext(_ctx, getState());
		enterRule(_localctx, 106, RULE_data_product_definition_shape);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(443);
			match(T__50);
			setState(444);
			par();
			setState(445);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_advanced_brep_shape_representationContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_advanced_brep_shape_representationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_advanced_brep_shape_representation; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_advanced_brep_shape_representation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_advanced_brep_shape_representation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_advanced_brep_shape_representation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_advanced_brep_shape_representationContext data_advanced_brep_shape_representation() throws RecognitionException {
		Data_advanced_brep_shape_representationContext _localctx = new Data_advanced_brep_shape_representationContext(_ctx, getState());
		enterRule(_localctx, 108, RULE_data_advanced_brep_shape_representation);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(447);
			match(T__51);
			setState(448);
			par();
			setState(449);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_cartesian_pointContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_cartesian_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_cartesian_point; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_cartesian_point(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_cartesian_point(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_cartesian_point(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_cartesian_pointContext data_cartesian_point() throws RecognitionException {
		Data_cartesian_pointContext _localctx = new Data_cartesian_pointContext(_ctx, getState());
		enterRule(_localctx, 110, RULE_data_cartesian_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			match(T__52);
			setState(452);
			par();
			setState(453);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_area_styleContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_area_styleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_area_style; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_area_style(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_area_style(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_area_style(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_area_styleContext data_area_style() throws RecognitionException {
		Data_area_styleContext _localctx = new Data_area_styleContext(_ctx, getState());
		enterRule(_localctx, 112, RULE_data_area_style);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(455);
			match(T__53);
			setState(456);
			par();
			setState(457);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_axis2_placement_3dContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_axis2_placement_3dContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_axis2_placement_3d; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_axis2_placement_3d(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_axis2_placement_3d(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_axis2_placement_3d(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_axis2_placement_3dContext data_axis2_placement_3d() throws RecognitionException {
		Data_axis2_placement_3dContext _localctx = new Data_axis2_placement_3dContext(_ctx, getState());
		enterRule(_localctx, 114, RULE_data_axis2_placement_3d);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			match(T__54);
			setState(460);
			par();
			setState(461);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_directionContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_directionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_direction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_direction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_direction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_direction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_directionContext data_direction() throws RecognitionException {
		Data_directionContext _localctx = new Data_directionContext(_ctx, getState());
		enterRule(_localctx, 116, RULE_data_direction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(463);
			match(T__55);
			setState(464);
			par();
			setState(465);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_vectorContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_vectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_vector; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_vector(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_vector(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_vector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_vectorContext data_vector() throws RecognitionException {
		Data_vectorContext _localctx = new Data_vectorContext(_ctx, getState());
		enterRule(_localctx, 118, RULE_data_vector);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(467);
			match(T__56);
			setState(468);
			par();
			setState(469);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_color_rgbContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_color_rgbContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_color_rgb; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_color_rgb(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_color_rgb(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_color_rgb(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_color_rgbContext data_color_rgb() throws RecognitionException {
		Data_color_rgbContext _localctx = new Data_color_rgbContext(_ctx, getState());
		enterRule(_localctx, 120, RULE_data_color_rgb);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(471);
			match(T__57);
			setState(472);
			par();
			setState(473);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Edge_curveContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Edge_curveContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_edge_curve; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterEdge_curve(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitEdge_curve(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitEdge_curve(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Edge_curveContext edge_curve() throws RecognitionException {
		Edge_curveContext _localctx = new Edge_curveContext(_ctx, getState());
		enterRule(_localctx, 122, RULE_edge_curve);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			match(T__58);
			setState(476);
			par();
			setState(477);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_b_spline_curve_with_knotsContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_b_spline_curve_with_knotsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_b_spline_curve_with_knots; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_b_spline_curve_with_knots(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_b_spline_curve_with_knots(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_b_spline_curve_with_knots(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_b_spline_curve_with_knotsContext data_b_spline_curve_with_knots() throws RecognitionException {
		Data_b_spline_curve_with_knotsContext _localctx = new Data_b_spline_curve_with_knotsContext(_ctx, getState());
		enterRule(_localctx, 124, RULE_data_b_spline_curve_with_knots);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			match(T__24);
			setState(480);
			par();
			setState(481);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_b_spline_surface_with_knotsContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_b_spline_surface_with_knotsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_b_spline_surface_with_knots; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_b_spline_surface_with_knots(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_b_spline_surface_with_knots(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_b_spline_surface_with_knots(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_b_spline_surface_with_knotsContext data_b_spline_surface_with_knots() throws RecognitionException {
		Data_b_spline_surface_with_knotsContext _localctx = new Data_b_spline_surface_with_knotsContext(_ctx, getState());
		enterRule(_localctx, 126, RULE_data_b_spline_surface_with_knots);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(483);
			match(T__59);
			setState(484);
			par();
			setState(485);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_surface_style_usageContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_surface_style_usageContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_surface_style_usage; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_surface_style_usage(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_surface_style_usage(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_surface_style_usage(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_surface_style_usageContext data_surface_style_usage() throws RecognitionException {
		Data_surface_style_usageContext _localctx = new Data_surface_style_usageContext(_ctx, getState());
		enterRule(_localctx, 128, RULE_data_surface_style_usage);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(487);
			match(T__60);
			setState(488);
			par();
			setState(489);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_advanced_faceContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_advanced_faceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_advanced_face; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_advanced_face(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_advanced_face(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_advanced_face(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_advanced_faceContext data_advanced_face() throws RecognitionException {
		Data_advanced_faceContext _localctx = new Data_advanced_faceContext(_ctx, getState());
		enterRule(_localctx, 130, RULE_data_advanced_face);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(491);
			match(T__61);
			setState(492);
			par();
			setState(493);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_vertex_pointContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_vertex_pointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_vertex_point; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_vertex_point(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_vertex_point(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_vertex_point(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_vertex_pointContext data_vertex_point() throws RecognitionException {
		Data_vertex_pointContext _localctx = new Data_vertex_pointContext(_ctx, getState());
		enterRule(_localctx, 132, RULE_data_vertex_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(495);
			match(T__62);
			setState(496);
			par();
			setState(497);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_uncertainty_measure_with_unitContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_uncertainty_measure_with_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_uncertainty_measure_with_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_uncertainty_measure_with_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_uncertainty_measure_with_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_uncertainty_measure_with_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_uncertainty_measure_with_unitContext data_uncertainty_measure_with_unit() throws RecognitionException {
		Data_uncertainty_measure_with_unitContext _localctx = new Data_uncertainty_measure_with_unitContext(_ctx, getState());
		enterRule(_localctx, 134, RULE_data_uncertainty_measure_with_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(499);
			match(T__63);
			setState(500);
			par();
			setState(501);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_face_outer_boundContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_face_outer_boundContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_face_outer_bound; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_face_outer_bound(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_face_outer_bound(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_face_outer_bound(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_face_outer_boundContext data_face_outer_bound() throws RecognitionException {
		Data_face_outer_boundContext _localctx = new Data_face_outer_boundContext(_ctx, getState());
		enterRule(_localctx, 136, RULE_data_face_outer_bound);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(503);
			match(T__64);
			setState(504);
			par();
			setState(505);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_edge_loopContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_edge_loopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_edge_loop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_edge_loop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_edge_loop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_edge_loop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_edge_loopContext data_edge_loop() throws RecognitionException {
		Data_edge_loopContext _localctx = new Data_edge_loopContext(_ctx, getState());
		enterRule(_localctx, 138, RULE_data_edge_loop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(507);
			match(T__65);
			setState(508);
			par();
			setState(509);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_surface_style_fill_areaContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_surface_style_fill_areaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_surface_style_fill_area; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_surface_style_fill_area(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_surface_style_fill_area(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_surface_style_fill_area(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_surface_style_fill_areaContext data_surface_style_fill_area() throws RecognitionException {
		Data_surface_style_fill_areaContext _localctx = new Data_surface_style_fill_areaContext(_ctx, getState());
		enterRule(_localctx, 140, RULE_data_surface_style_fill_area);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(511);
			match(T__66);
			setState(512);
			par();
			setState(513);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_surface_side_styleContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_surface_side_styleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_surface_side_style; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_surface_side_style(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_surface_side_style(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_surface_side_style(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_surface_side_styleContext data_surface_side_style() throws RecognitionException {
		Data_surface_side_styleContext _localctx = new Data_surface_side_styleContext(_ctx, getState());
		enterRule(_localctx, 142, RULE_data_surface_side_style);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(515);
			match(T__67);
			setState(516);
			par();
			setState(517);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_presentation_style_assignementContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_presentation_style_assignementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_presentation_style_assignement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_presentation_style_assignement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_presentation_style_assignement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_presentation_style_assignement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_presentation_style_assignementContext data_presentation_style_assignement() throws RecognitionException {
		Data_presentation_style_assignementContext _localctx = new Data_presentation_style_assignementContext(_ctx, getState());
		enterRule(_localctx, 144, RULE_data_presentation_style_assignement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(519);
			match(T__68);
			setState(520);
			par();
			setState(521);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_lineContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_line(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_line(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_lineContext data_line() throws RecognitionException {
		Data_lineContext _localctx = new Data_lineContext(_ctx, getState());
		enterRule(_localctx, 146, RULE_data_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(523);
			match(T__69);
			setState(524);
			par();
			setState(525);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_planeContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_planeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_plane; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_plane(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_plane(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_plane(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_planeContext data_plane() throws RecognitionException {
		Data_planeContext _localctx = new Data_planeContext(_ctx, getState());
		enterRule(_localctx, 148, RULE_data_plane);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(527);
			match(T__70);
			setState(528);
			par();
			setState(529);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_oriented_edgeContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_oriented_edgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_oriented_edge; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_oriented_edge(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_oriented_edge(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_oriented_edge(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_oriented_edgeContext data_oriented_edge() throws RecognitionException {
		Data_oriented_edgeContext _localctx = new Data_oriented_edgeContext(_ctx, getState());
		enterRule(_localctx, 150, RULE_data_oriented_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(531);
			match(T__71);
			setState(532);
			par();
			setState(533);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Data_fill_area_style_colourContext extends ParserRuleContext {
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public NlContext nl() {
			return getRuleContext(NlContext.class,0);
		}
		public Data_fill_area_style_colourContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_fill_area_style_colour; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterData_fill_area_style_colour(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitData_fill_area_style_colour(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitData_fill_area_style_colour(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_fill_area_style_colourContext data_fill_area_style_colour() throws RecognitionException {
		Data_fill_area_style_colourContext _localctx = new Data_fill_area_style_colourContext(_ctx, getState());
		enterRule(_localctx, 152, RULE_data_fill_area_style_colour);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(535);
			match(T__72);
			setState(536);
			par();
			setState(537);
			nl();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SepContext extends ParserRuleContext {
		public SepContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sep; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterSep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitSep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitSep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SepContext sep() throws RecognitionException {
		SepContext _localctx = new SepContext(_ctx, getState());
		enterRule(_localctx, 154, RULE_sep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(539);
			match(T__73);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NlContext extends ParserRuleContext {
		public NlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).enterNl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof StepFileListener ) ((StepFileListener)listener).exitNl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof StepFileVisitor ) return ((StepFileVisitor<? extends T>)visitor).visitNl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NlContext nl() throws RecognitionException {
		NlContext _localctx = new NlContext(_ctx, getState());
		enterRule(_localctx, 156, RULE_nl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(541);
			match(T__74);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001[\u0220\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0002/\u0007/\u00020\u00070\u00021\u00071\u0002"+
		"2\u00072\u00023\u00073\u00024\u00074\u00025\u00075\u00026\u00076\u0002"+
		"7\u00077\u00028\u00078\u00029\u00079\u0002:\u0007:\u0002;\u0007;\u0002"+
		"<\u0007<\u0002=\u0007=\u0002>\u0007>\u0002?\u0007?\u0002@\u0007@\u0002"+
		"A\u0007A\u0002B\u0007B\u0002C\u0007C\u0002D\u0007D\u0002E\u0007E\u0002"+
		"F\u0007F\u0002G\u0007G\u0002H\u0007H\u0002I\u0007I\u0002J\u0007J\u0002"+
		"K\u0007K\u0002L\u0007L\u0002M\u0007M\u0002N\u0007N\u0001\u0000\u0001\u0000"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u00c5\b\b"+
		"\n\b\f\b\u00c8\t\b\u0001\b\u0001\b\u0001\b\u0001\b\u0005\b\u00ce\b\b\n"+
		"\b\f\b\u00d1\t\b\u0001\b\u0003\b\u00d4\b\b\u0001\t\u0001\t\u0004\t\u00d8"+
		"\b\t\u000b\t\f\t\u00d9\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001\u000b"+
		"\u0004\u000b\u00e1\b\u000b\u000b\u000b\f\u000b\u00e2\u0001\u000b\u0001"+
		"\u000b\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r"+
		"\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0003\r\u0114\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u0129\b\u000e\u0001"+
		"\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0015\u0001\u0015\u0001\u0015\u0001\u0016\u0001\u0016\u0001\u0016\u0001"+
		"\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001"+
		"\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c\u0001"+
		"\u001d\u0001\u001d\u0001\u001d\u0001\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001f\u0001\u001f\u0001\u001f\u0001 \u0001 \u0001 \u0001!\u0001!\u0001"+
		"!\u0001\"\u0001\"\u0001\"\u0001\"\u0004\"\u0168\b\"\u000b\"\f\"\u0169"+
		"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0001\"\u0003\"\u0172\b\"\u0001"+
		"#\u0001#\u0001#\u0001#\u0001$\u0001$\u0001$\u0001$\u0001%\u0001%\u0001"+
		"%\u0001%\u0001&\u0001&\u0001&\u0001&\u0001\'\u0001\'\u0001\'\u0001\'\u0001"+
		"(\u0001(\u0001(\u0001(\u0001)\u0001)\u0001)\u0001)\u0001*\u0001*\u0001"+
		"*\u0001*\u0001+\u0001+\u0001+\u0001+\u0001,\u0001,\u0001,\u0001,\u0001"+
		"-\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0001.\u0001/\u0001/\u0001"+
		"/\u0001/\u00010\u00010\u00010\u00010\u00011\u00011\u00011\u00011\u0001"+
		"2\u00012\u00012\u00012\u00013\u00013\u00013\u00013\u00014\u00014\u0001"+
		"4\u00014\u00015\u00015\u00015\u00015\u00016\u00016\u00016\u00016\u0001"+
		"7\u00017\u00017\u00017\u00018\u00018\u00018\u00018\u00019\u00019\u0001"+
		"9\u00019\u0001:\u0001:\u0001:\u0001:\u0001;\u0001;\u0001;\u0001;\u0001"+
		"<\u0001<\u0001<\u0001<\u0001=\u0001=\u0001=\u0001=\u0001>\u0001>\u0001"+
		">\u0001>\u0001?\u0001?\u0001?\u0001?\u0001@\u0001@\u0001@\u0001@\u0001"+
		"A\u0001A\u0001A\u0001A\u0001B\u0001B\u0001B\u0001B\u0001C\u0001C\u0001"+
		"C\u0001C\u0001D\u0001D\u0001D\u0001D\u0001E\u0001E\u0001E\u0001E\u0001"+
		"F\u0001F\u0001F\u0001F\u0001G\u0001G\u0001G\u0001G\u0001H\u0001H\u0001"+
		"H\u0001H\u0001I\u0001I\u0001I\u0001I\u0001J\u0001J\u0001J\u0001J\u0001"+
		"K\u0001K\u0001K\u0001K\u0001L\u0001L\u0001L\u0001L\u0001M\u0001M\u0001"+
		"N\u0001N\u0001N\u0000\u0000O\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02468:<>@BDFHJLNPR"+
		"TVXZ\\^`bdfhjlnprtvxz|~\u0080\u0082\u0084\u0086\u0088\u008a\u008c\u008e"+
		"\u0090\u0092\u0094\u0096\u0098\u009a\u009c\u0000\u0001\u0001\u0000LY\u0214"+
		"\u0000\u009e\u0001\u0000\u0000\u0000\u0002\u00a4\u0001\u0000\u0000\u0000"+
		"\u0004\u00aa\u0001\u0000\u0000\u0000\u0006\u00ae\u0001\u0000\u0000\u0000"+
		"\b\u00b2\u0001\u0000\u0000\u0000\n\u00b6\u0001\u0000\u0000\u0000\f\u00b9"+
		"\u0001\u0000\u0000\u0000\u000e\u00bc\u0001\u0000\u0000\u0000\u0010\u00d3"+
		"\u0001\u0000\u0000\u0000\u0012\u00d7\u0001\u0000\u0000\u0000\u0014\u00db"+
		"\u0001\u0000\u0000\u0000\u0016\u00de\u0001\u0000\u0000\u0000\u0018\u00e6"+
		"\u0001\u0000\u0000\u0000\u001a\u0113\u0001\u0000\u0000\u0000\u001c\u0128"+
		"\u0001\u0000\u0000\u0000\u001e\u012a\u0001\u0000\u0000\u0000 \u012d\u0001"+
		"\u0000\u0000\u0000\"\u0130\u0001\u0000\u0000\u0000$\u0133\u0001\u0000"+
		"\u0000\u0000&\u0136\u0001\u0000\u0000\u0000(\u0139\u0001\u0000\u0000\u0000"+
		"*\u013c\u0001\u0000\u0000\u0000,\u013f\u0001\u0000\u0000\u0000.\u0142"+
		"\u0001\u0000\u0000\u00000\u0145\u0001\u0000\u0000\u00002\u0148\u0001\u0000"+
		"\u0000\u00004\u014b\u0001\u0000\u0000\u00006\u014e\u0001\u0000\u0000\u0000"+
		"8\u0151\u0001\u0000\u0000\u0000:\u0154\u0001\u0000\u0000\u0000<\u0157"+
		"\u0001\u0000\u0000\u0000>\u015a\u0001\u0000\u0000\u0000@\u015d\u0001\u0000"+
		"\u0000\u0000B\u0160\u0001\u0000\u0000\u0000D\u0171\u0001\u0000\u0000\u0000"+
		"F\u0173\u0001\u0000\u0000\u0000H\u0177\u0001\u0000\u0000\u0000J\u017b"+
		"\u0001\u0000\u0000\u0000L\u017f\u0001\u0000\u0000\u0000N\u0183\u0001\u0000"+
		"\u0000\u0000P\u0187\u0001\u0000\u0000\u0000R\u018b\u0001\u0000\u0000\u0000"+
		"T\u018f\u0001\u0000\u0000\u0000V\u0193\u0001\u0000\u0000\u0000X\u0197"+
		"\u0001\u0000\u0000\u0000Z\u019b\u0001\u0000\u0000\u0000\\\u019f\u0001"+
		"\u0000\u0000\u0000^\u01a3\u0001\u0000\u0000\u0000`\u01a7\u0001\u0000\u0000"+
		"\u0000b\u01ab\u0001\u0000\u0000\u0000d\u01af\u0001\u0000\u0000\u0000f"+
		"\u01b3\u0001\u0000\u0000\u0000h\u01b7\u0001\u0000\u0000\u0000j\u01bb\u0001"+
		"\u0000\u0000\u0000l\u01bf\u0001\u0000\u0000\u0000n\u01c3\u0001\u0000\u0000"+
		"\u0000p\u01c7\u0001\u0000\u0000\u0000r\u01cb\u0001\u0000\u0000\u0000t"+
		"\u01cf\u0001\u0000\u0000\u0000v\u01d3\u0001\u0000\u0000\u0000x\u01d7\u0001"+
		"\u0000\u0000\u0000z\u01db\u0001\u0000\u0000\u0000|\u01df\u0001\u0000\u0000"+
		"\u0000~\u01e3\u0001\u0000\u0000\u0000\u0080\u01e7\u0001\u0000\u0000\u0000"+
		"\u0082\u01eb\u0001\u0000\u0000\u0000\u0084\u01ef\u0001\u0000\u0000\u0000"+
		"\u0086\u01f3\u0001\u0000\u0000\u0000\u0088\u01f7\u0001\u0000\u0000\u0000"+
		"\u008a\u01fb\u0001\u0000\u0000\u0000\u008c\u01ff\u0001\u0000\u0000\u0000"+
		"\u008e\u0203\u0001\u0000\u0000\u0000\u0090\u0207\u0001\u0000\u0000\u0000"+
		"\u0092\u020b\u0001\u0000\u0000\u0000\u0094\u020f\u0001\u0000\u0000\u0000"+
		"\u0096\u0213\u0001\u0000\u0000\u0000\u0098\u0217\u0001\u0000\u0000\u0000"+
		"\u009a\u021b\u0001\u0000\u0000\u0000\u009c\u021d\u0001\u0000\u0000\u0000"+
		"\u009e\u009f\u0005\u0001\u0000\u0000\u009f\u00a0\u0003\u0002\u0001\u0000"+
		"\u00a0\u00a1\u0003\u0016\u000b\u0000\u00a1\u00a2\u0005\u0002\u0000\u0000"+
		"\u00a2\u00a3\u0005\u0000\u0000\u0001\u00a3\u0001\u0001\u0000\u0000\u0000"+
		"\u00a4\u00a5\u0003\u0014\n\u0000\u00a5\u00a6\u0003\u0004\u0002\u0000\u00a6"+
		"\u00a7\u0003\u0006\u0003\u0000\u00a7\u00a8\u0003\b\u0004\u0000\u00a8\u00a9"+
		"\u0003\n\u0005\u0000\u00a9\u0003\u0001\u0000\u0000\u0000\u00aa\u00ab\u0005"+
		"\u0003\u0000\u0000\u00ab\u00ac\u0003\u0010\b\u0000\u00ac\u00ad\u0003\u009c"+
		"N\u0000\u00ad\u0005\u0001\u0000\u0000\u0000\u00ae\u00af\u0005\u0004\u0000"+
		"\u0000\u00af\u00b0\u0003\u0010\b\u0000\u00b0\u00b1\u0003\u009cN\u0000"+
		"\u00b1\u0007\u0001\u0000\u0000\u0000\u00b2\u00b3\u0005\u0005\u0000\u0000"+
		"\u00b3\u00b4\u0003\u0010\b\u0000\u00b4\u00b5\u0003\u009cN\u0000\u00b5"+
		"\t\u0001\u0000\u0000\u0000\u00b6\u00b7\u0005\u0006\u0000\u0000\u00b7\u00b8"+
		"\u0003\u009cN\u0000\u00b8\u000b\u0001\u0000\u0000\u0000\u00b9\u00ba\u0005"+
		"V\u0000\u0000\u00ba\u00bb\u0005\u0007\u0000\u0000\u00bb\r\u0001\u0000"+
		"\u0000\u0000\u00bc\u00bd\u0005\b\u0000\u0000\u00bd\u00be\u0005\t\u0000"+
		"\u0000\u00be\u000f\u0001\u0000\u0000\u0000\u00bf\u00c0\u0005\b\u0000\u0000"+
		"\u00c0\u00c6\u0003\u0012\t\u0000\u00c1\u00c2\u0003\u009aM\u0000\u00c2"+
		"\u00c3\u0003\u0012\t\u0000\u00c3\u00c5\u0001\u0000\u0000\u0000\u00c4\u00c1"+
		"\u0001\u0000\u0000\u0000\u00c5\u00c8\u0001\u0000\u0000\u0000\u00c6\u00c4"+
		"\u0001\u0000\u0000\u0000\u00c6\u00c7\u0001\u0000\u0000\u0000\u00c7\u00c9"+
		"\u0001\u0000\u0000\u0000\u00c8\u00c6\u0001\u0000\u0000\u0000\u00c9\u00cf"+
		"\u0005\t\u0000\u0000\u00ca\u00cb\u0003\u009aM\u0000\u00cb\u00cc\u0003"+
		"\u0012\t\u0000\u00cc\u00ce\u0001\u0000\u0000\u0000\u00cd\u00ca\u0001\u0000"+
		"\u0000\u0000\u00ce\u00d1\u0001\u0000\u0000\u0000\u00cf\u00cd\u0001\u0000"+
		"\u0000\u0000\u00cf\u00d0\u0001\u0000\u0000\u0000\u00d0\u00d4\u0001\u0000"+
		"\u0000\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d2\u00d4\u0003\u000e"+
		"\u0007\u0000\u00d3\u00bf\u0001\u0000\u0000\u0000\u00d3\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d4\u0011\u0001\u0000\u0000\u0000\u00d5\u00d8\u0003\u0010"+
		"\b\u0000\u00d6\u00d8\u0007\u0000\u0000\u0000\u00d7\u00d5\u0001\u0000\u0000"+
		"\u0000\u00d7\u00d6\u0001\u0000\u0000\u0000\u00d8\u00d9\u0001\u0000\u0000"+
		"\u0000\u00d9\u00d7\u0001\u0000\u0000\u0000\u00d9\u00da\u0001\u0000\u0000"+
		"\u0000\u00da\u0013\u0001\u0000\u0000\u0000\u00db\u00dc\u0005\n\u0000\u0000"+
		"\u00dc\u00dd\u0003\u009cN\u0000\u00dd\u0015\u0001\u0000\u0000\u0000\u00de"+
		"\u00e0\u0003\u0018\f\u0000\u00df\u00e1\u0003D\"\u0000\u00e0\u00df\u0001"+
		"\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e0\u0001"+
		"\u0000\u0000\u0000\u00e2\u00e3\u0001\u0000\u0000\u0000\u00e3\u00e4\u0001"+
		"\u0000\u0000\u0000\u00e4\u00e5\u0003\n\u0005\u0000\u00e5\u0017\u0001\u0000"+
		"\u0000\u0000\u00e6\u00e7\u0005\u000b\u0000\u0000\u00e7\u00e8\u0003\u009c"+
		"N\u0000\u00e8\u0019\u0001\u0000\u0000\u0000\u00e9\u0114\u0003F#\u0000"+
		"\u00ea\u0114\u0003`0\u0000\u00eb\u0114\u0003\u0092I\u0000\u00ec\u0114"+
		"\u0003\u0096K\u0000\u00ed\u0114\u0003n7\u0000\u00ee\u0114\u0003p8\u0000"+
		"\u00ef\u0114\u0003r9\u0000\u00f0\u0114\u0003t:\u0000\u00f1\u0114\u0003"+
		"|>\u0000\u00f2\u0114\u0003\u0080@\u0000\u00f3\u0114\u0003\u0084B\u0000"+
		"\u00f4\u0114\u0003~?\u0000\u00f5\u0114\u0003\u008cF\u0000\u00f6\u0114"+
		"\u0003\u008eG\u0000\u00f7\u0114\u0003x<\u0000\u00f8\u0114\u0003v;\u0000"+
		"\u00f9\u0114\u0003z=\u0000\u00fa\u0114\u0003\u0086C\u0000\u00fb\u0114"+
		"\u0003\u0090H\u0000\u00fc\u0114\u0003\u0088D\u0000\u00fd\u0114\u0003\u008a"+
		"E\u0000\u00fe\u0114\u0003\u0094J\u0000\u00ff\u0114\u0003\u0082A\u0000"+
		"\u0100\u0114\u0003\u0098L\u0000\u0101\u0114\u0003b1\u0000\u0102\u0114"+
		"\u0003H$\u0000\u0103\u0114\u0003L&\u0000\u0104\u0114\u0003V+\u0000\u0105"+
		"\u0114\u0003T*\u0000\u0106\u0114\u0003X,\u0000\u0107\u0114\u0003f3\u0000"+
		"\u0108\u0114\u0003d2\u0000\u0109\u0114\u0003Z-\u0000\u010a\u0114\u0003"+
		"h4\u0000\u010b\u0114\u0003j5\u0000\u010c\u0114\u0003J%\u0000\u010d\u0114"+
		"\u0003\\.\u0000\u010e\u0114\u0003N\'\u0000\u010f\u0114\u0003l6\u0000\u0110"+
		"\u0114\u0003P(\u0000\u0111\u0114\u0003R)\u0000\u0112\u0114\u0003^/\u0000"+
		"\u0113\u00e9\u0001\u0000\u0000\u0000\u0113\u00ea\u0001\u0000\u0000\u0000"+
		"\u0113\u00eb\u0001\u0000\u0000\u0000\u0113\u00ec\u0001\u0000\u0000\u0000"+
		"\u0113\u00ed\u0001\u0000\u0000\u0000\u0113\u00ee\u0001\u0000\u0000\u0000"+
		"\u0113\u00ef\u0001\u0000\u0000\u0000\u0113\u00f0\u0001\u0000\u0000\u0000"+
		"\u0113\u00f1\u0001\u0000\u0000\u0000\u0113\u00f2\u0001\u0000\u0000\u0000"+
		"\u0113\u00f3\u0001\u0000\u0000\u0000\u0113\u00f4\u0001\u0000\u0000\u0000"+
		"\u0113\u00f5\u0001\u0000\u0000\u0000\u0113\u00f6\u0001\u0000\u0000\u0000"+
		"\u0113\u00f7\u0001\u0000\u0000\u0000\u0113\u00f8\u0001\u0000\u0000\u0000"+
		"\u0113\u00f9\u0001\u0000\u0000\u0000\u0113\u00fa\u0001\u0000\u0000\u0000"+
		"\u0113\u00fb\u0001\u0000\u0000\u0000\u0113\u00fc\u0001\u0000\u0000\u0000"+
		"\u0113\u00fd\u0001\u0000\u0000\u0000\u0113\u00fe\u0001\u0000\u0000\u0000"+
		"\u0113\u00ff\u0001\u0000\u0000\u0000\u0113\u0100\u0001\u0000\u0000\u0000"+
		"\u0113\u0101\u0001\u0000\u0000\u0000\u0113\u0102\u0001\u0000\u0000\u0000"+
		"\u0113\u0103\u0001\u0000\u0000\u0000\u0113\u0104\u0001\u0000\u0000\u0000"+
		"\u0113\u0105\u0001\u0000\u0000\u0000\u0113\u0106\u0001\u0000\u0000\u0000"+
		"\u0113\u0107\u0001\u0000\u0000\u0000\u0113\u0108\u0001\u0000\u0000\u0000"+
		"\u0113\u0109\u0001\u0000\u0000\u0000\u0113\u010a\u0001\u0000\u0000\u0000"+
		"\u0113\u010b\u0001\u0000\u0000\u0000\u0113\u010c\u0001\u0000\u0000\u0000"+
		"\u0113\u010d\u0001\u0000\u0000\u0000\u0113\u010e\u0001\u0000\u0000\u0000"+
		"\u0113\u010f\u0001\u0000\u0000\u0000\u0113\u0110\u0001\u0000\u0000\u0000"+
		"\u0113\u0111\u0001\u0000\u0000\u0000\u0113\u0112\u0001\u0000\u0000\u0000"+
		"\u0114\u001b\u0001\u0000\u0000\u0000\u0115\u0129\u0003\u001e\u000f\u0000"+
		"\u0116\u0129\u0003 \u0010\u0000\u0117\u0129\u0003\"\u0011\u0000\u0118"+
		"\u0129\u0003$\u0012\u0000\u0119\u0129\u0003&\u0013\u0000\u011a\u0129\u0003"+
		".\u0017\u0000\u011b\u0129\u00030\u0018\u0000\u011c\u0129\u00032\u0019"+
		"\u0000\u011d\u0129\u00034\u001a\u0000\u011e\u0129\u00036\u001b\u0000\u011f"+
		"\u0129\u00038\u001c\u0000\u0120\u0129\u0003:\u001d\u0000\u0121\u0129\u0003"+
		"<\u001e\u0000\u0122\u0129\u0003>\u001f\u0000\u0123\u0129\u0003@ \u0000"+
		"\u0124\u0129\u0003B!\u0000\u0125\u0129\u0003(\u0014\u0000\u0126\u0129"+
		"\u0003*\u0015\u0000\u0127\u0129\u0003,\u0016\u0000\u0128\u0115\u0001\u0000"+
		"\u0000\u0000\u0128\u0116\u0001\u0000\u0000\u0000\u0128\u0117\u0001\u0000"+
		"\u0000\u0000\u0128\u0118\u0001\u0000\u0000\u0000\u0128\u0119\u0001\u0000"+
		"\u0000\u0000\u0128\u011a\u0001\u0000\u0000\u0000\u0128\u011b\u0001\u0000"+
		"\u0000\u0000\u0128\u011c\u0001\u0000\u0000\u0000\u0128\u011d\u0001\u0000"+
		"\u0000\u0000\u0128\u011e\u0001\u0000\u0000\u0000\u0128\u011f\u0001\u0000"+
		"\u0000\u0000\u0128\u0120\u0001\u0000\u0000\u0000\u0128\u0121\u0001\u0000"+
		"\u0000\u0000\u0128\u0122\u0001\u0000\u0000\u0000\u0128\u0123\u0001\u0000"+
		"\u0000\u0000\u0128\u0124\u0001\u0000\u0000\u0000\u0128\u0125\u0001\u0000"+
		"\u0000\u0000\u0128\u0126\u0001\u0000\u0000\u0000\u0128\u0127\u0001\u0000"+
		"\u0000\u0000\u0129\u001d\u0001\u0000\u0000\u0000\u012a\u012b\u0005\f\u0000"+
		"\u0000\u012b\u012c\u0003\u0010\b\u0000\u012c\u001f\u0001\u0000\u0000\u0000"+
		"\u012d\u012e\u0005\r\u0000\u0000\u012e\u012f\u0003\u0010\b\u0000\u012f"+
		"!\u0001\u0000\u0000\u0000\u0130\u0131\u0005\u000e\u0000\u0000\u0131\u0132"+
		"\u0003\u0010\b\u0000\u0132#\u0001\u0000\u0000\u0000\u0133\u0134\u0005"+
		"\u000f\u0000\u0000\u0134\u0135\u0003\u0010\b\u0000\u0135%\u0001\u0000"+
		"\u0000\u0000\u0136\u0137\u0005\u0010\u0000\u0000\u0137\u0138\u0003\u0010"+
		"\b\u0000\u0138\'\u0001\u0000\u0000\u0000\u0139\u013a\u0005\u0011\u0000"+
		"\u0000\u013a\u013b\u0003\u0010\b\u0000\u013b)\u0001\u0000\u0000\u0000"+
		"\u013c\u013d\u0005\u0012\u0000\u0000\u013d\u013e\u0003\u0010\b\u0000\u013e"+
		"+\u0001\u0000\u0000\u0000\u013f\u0140\u0005\u0013\u0000\u0000\u0140\u0141"+
		"\u0003\u0010\b\u0000\u0141-\u0001\u0000\u0000\u0000\u0142\u0143\u0005"+
		"\u0014\u0000\u0000\u0143\u0144\u0003\u0010\b\u0000\u0144/\u0001\u0000"+
		"\u0000\u0000\u0145\u0146\u0005\u0015\u0000\u0000\u0146\u0147\u0003\u0010"+
		"\b\u0000\u01471\u0001\u0000\u0000\u0000\u0148\u0149\u0005\u0016\u0000"+
		"\u0000\u0149\u014a\u0003\u0010\b\u0000\u014a3\u0001\u0000\u0000\u0000"+
		"\u014b\u014c\u0005\u0017\u0000\u0000\u014c\u014d\u0003\u0010\b\u0000\u014d"+
		"5\u0001\u0000\u0000\u0000\u014e\u014f\u0005\u0018\u0000\u0000\u014f\u0150"+
		"\u0003\u0010\b\u0000\u01507\u0001\u0000\u0000\u0000\u0151\u0152\u0005"+
		"\u0019\u0000\u0000\u0152\u0153\u0003\u0010\b\u0000\u01539\u0001\u0000"+
		"\u0000\u0000\u0154\u0155\u0005\u001a\u0000\u0000\u0155\u0156\u0003\u0010"+
		"\b\u0000\u0156;\u0001\u0000\u0000\u0000\u0157\u0158\u0005\u001b\u0000"+
		"\u0000\u0158\u0159\u0003\u0010\b\u0000\u0159=\u0001\u0000\u0000\u0000"+
		"\u015a\u015b\u0005\u001c\u0000\u0000\u015b\u015c\u0003\u0010\b\u0000\u015c"+
		"?\u0001\u0000\u0000\u0000\u015d\u015e\u0005\u001d\u0000\u0000\u015e\u015f"+
		"\u0003\u0010\b\u0000\u015fA\u0001\u0000\u0000\u0000\u0160\u0161\u0005"+
		"\u001e\u0000\u0000\u0161\u0162\u0003\u0010\b\u0000\u0162C\u0001\u0000"+
		"\u0000\u0000\u0163\u0172\u0003\f\u0006\u0000\u0164\u0165\u0005V\u0000"+
		"\u0000\u0165\u0167\u0005\u001f\u0000\u0000\u0166\u0168\u0003\u001c\u000e"+
		"\u0000\u0167\u0166\u0001\u0000\u0000\u0000\u0168\u0169\u0001\u0000\u0000"+
		"\u0000\u0169\u0167\u0001\u0000\u0000\u0000\u0169\u016a\u0001\u0000\u0000"+
		"\u0000\u016a\u016b\u0001\u0000\u0000\u0000\u016b\u016c\u0005\t\u0000\u0000"+
		"\u016c\u016d\u0003\u009cN\u0000\u016d\u0172\u0001\u0000\u0000\u0000\u016e"+
		"\u016f\u0005V\u0000\u0000\u016f\u0170\u0005 \u0000\u0000\u0170\u0172\u0003"+
		"\u001a\r\u0000\u0171\u0163\u0001\u0000\u0000\u0000\u0171\u0164\u0001\u0000"+
		"\u0000\u0000\u0171\u016e\u0001\u0000\u0000\u0000\u0172E\u0001\u0000\u0000"+
		"\u0000\u0173\u0174\u0005!\u0000\u0000\u0174\u0175\u0003\u0010\b\u0000"+
		"\u0175\u0176\u0003\u009cN\u0000\u0176G\u0001\u0000\u0000\u0000\u0177\u0178"+
		"\u0005\"\u0000\u0000\u0178\u0179\u0003\u0010\b\u0000\u0179\u017a\u0003"+
		"\u009cN\u0000\u017aI\u0001\u0000\u0000\u0000\u017b\u017c\u0005#\u0000"+
		"\u0000\u017c\u017d\u0003\u0010\b\u0000\u017d\u017e\u0003\u009cN\u0000"+
		"\u017eK\u0001\u0000\u0000\u0000\u017f\u0180\u0005$\u0000\u0000\u0180\u0181"+
		"\u0003\u0010\b\u0000\u0181\u0182\u0003\u009cN\u0000\u0182M\u0001\u0000"+
		"\u0000\u0000\u0183\u0184\u0005%\u0000\u0000\u0184\u0185\u0003\u0010\b"+
		"\u0000\u0185\u0186\u0003\u009cN\u0000\u0186O\u0001\u0000\u0000\u0000\u0187"+
		"\u0188\u0005&\u0000\u0000\u0188\u0189\u0003\u0010\b\u0000\u0189\u018a"+
		"\u0003\u009cN\u0000\u018aQ\u0001\u0000\u0000\u0000\u018b\u018c\u0005\'"+
		"\u0000\u0000\u018c\u018d\u0003\u0010\b\u0000\u018d\u018e\u0003\u009cN"+
		"\u0000\u018eS\u0001\u0000\u0000\u0000\u018f\u0190\u0005(\u0000\u0000\u0190"+
		"\u0191\u0003\u0010\b\u0000\u0191\u0192\u0003\u009cN\u0000\u0192U\u0001"+
		"\u0000\u0000\u0000\u0193\u0194\u0005)\u0000\u0000\u0194\u0195\u0003\u0010"+
		"\b\u0000\u0195\u0196\u0003\u009cN\u0000\u0196W\u0001\u0000\u0000\u0000"+
		"\u0197\u0198\u0005*\u0000\u0000\u0198\u0199\u0003\u0010\b\u0000\u0199"+
		"\u019a\u0003\u009cN\u0000\u019aY\u0001\u0000\u0000\u0000\u019b\u019c\u0005"+
		"+\u0000\u0000\u019c\u019d\u0003\u0010\b\u0000\u019d\u019e\u0003\u009c"+
		"N\u0000\u019e[\u0001\u0000\u0000\u0000\u019f\u01a0\u0005,\u0000\u0000"+
		"\u01a0\u01a1\u0003\u0010\b\u0000\u01a1\u01a2\u0003\u009cN\u0000\u01a2"+
		"]\u0001\u0000\u0000\u0000\u01a3\u01a4\u0005-\u0000\u0000\u01a4\u01a5\u0003"+
		"\u0010\b\u0000\u01a5\u01a6\u0003\u009cN\u0000\u01a6_\u0001\u0000\u0000"+
		"\u0000\u01a7\u01a8\u0005.\u0000\u0000\u01a8\u01a9\u0003\u0010\b\u0000"+
		"\u01a9\u01aa\u0003\u009cN\u0000\u01aaa\u0001\u0000\u0000\u0000\u01ab\u01ac"+
		"\u0005/\u0000\u0000\u01ac\u01ad\u0003\u0010\b\u0000\u01ad\u01ae\u0003"+
		"\u009cN\u0000\u01aec\u0001\u0000\u0000\u0000\u01af\u01b0\u00050\u0000"+
		"\u0000\u01b0\u01b1\u0003\u0010\b\u0000\u01b1\u01b2\u0003\u009cN\u0000"+
		"\u01b2e\u0001\u0000\u0000\u0000\u01b3\u01b4\u00051\u0000\u0000\u01b4\u01b5"+
		"\u0003\u0010\b\u0000\u01b5\u01b6\u0003\u009cN\u0000\u01b6g\u0001\u0000"+
		"\u0000\u0000\u01b7\u01b8\u00052\u0000\u0000\u01b8\u01b9\u0003\u0010\b"+
		"\u0000\u01b9\u01ba\u0003\u009cN\u0000\u01bai\u0001\u0000\u0000\u0000\u01bb"+
		"\u01bc\u00053\u0000\u0000\u01bc\u01bd\u0003\u0010\b\u0000\u01bd\u01be"+
		"\u0003\u009cN\u0000\u01bek\u0001\u0000\u0000\u0000\u01bf\u01c0\u00054"+
		"\u0000\u0000\u01c0\u01c1\u0003\u0010\b\u0000\u01c1\u01c2\u0003\u009cN"+
		"\u0000\u01c2m\u0001\u0000\u0000\u0000\u01c3\u01c4\u00055\u0000\u0000\u01c4"+
		"\u01c5\u0003\u0010\b\u0000\u01c5\u01c6\u0003\u009cN\u0000\u01c6o\u0001"+
		"\u0000\u0000\u0000\u01c7\u01c8\u00056\u0000\u0000\u01c8\u01c9\u0003\u0010"+
		"\b\u0000\u01c9\u01ca\u0003\u009cN\u0000\u01caq\u0001\u0000\u0000\u0000"+
		"\u01cb\u01cc\u00057\u0000\u0000\u01cc\u01cd\u0003\u0010\b\u0000\u01cd"+
		"\u01ce\u0003\u009cN\u0000\u01ces\u0001\u0000\u0000\u0000\u01cf\u01d0\u0005"+
		"8\u0000\u0000\u01d0\u01d1\u0003\u0010\b\u0000\u01d1\u01d2\u0003\u009c"+
		"N\u0000\u01d2u\u0001\u0000\u0000\u0000\u01d3\u01d4\u00059\u0000\u0000"+
		"\u01d4\u01d5\u0003\u0010\b\u0000\u01d5\u01d6\u0003\u009cN\u0000\u01d6"+
		"w\u0001\u0000\u0000\u0000\u01d7\u01d8\u0005:\u0000\u0000\u01d8\u01d9\u0003"+
		"\u0010\b\u0000\u01d9\u01da\u0003\u009cN\u0000\u01day\u0001\u0000\u0000"+
		"\u0000\u01db\u01dc\u0005;\u0000\u0000\u01dc\u01dd\u0003\u0010\b\u0000"+
		"\u01dd\u01de\u0003\u009cN\u0000\u01de{\u0001\u0000\u0000\u0000\u01df\u01e0"+
		"\u0005\u0019\u0000\u0000\u01e0\u01e1\u0003\u0010\b\u0000\u01e1\u01e2\u0003"+
		"\u009cN\u0000\u01e2}\u0001\u0000\u0000\u0000\u01e3\u01e4\u0005<\u0000"+
		"\u0000\u01e4\u01e5\u0003\u0010\b\u0000\u01e5\u01e6\u0003\u009cN\u0000"+
		"\u01e6\u007f\u0001\u0000\u0000\u0000\u01e7\u01e8\u0005=\u0000\u0000\u01e8"+
		"\u01e9\u0003\u0010\b\u0000\u01e9\u01ea\u0003\u009cN\u0000\u01ea\u0081"+
		"\u0001\u0000\u0000\u0000\u01eb\u01ec\u0005>\u0000\u0000\u01ec\u01ed\u0003"+
		"\u0010\b\u0000\u01ed\u01ee\u0003\u009cN\u0000\u01ee\u0083\u0001\u0000"+
		"\u0000\u0000\u01ef\u01f0\u0005?\u0000\u0000\u01f0\u01f1\u0003\u0010\b"+
		"\u0000\u01f1\u01f2\u0003\u009cN\u0000\u01f2\u0085\u0001\u0000\u0000\u0000"+
		"\u01f3\u01f4\u0005@\u0000\u0000\u01f4\u01f5\u0003\u0010\b\u0000\u01f5"+
		"\u01f6\u0003\u009cN\u0000\u01f6\u0087\u0001\u0000\u0000\u0000\u01f7\u01f8"+
		"\u0005A\u0000\u0000\u01f8\u01f9\u0003\u0010\b\u0000\u01f9\u01fa\u0003"+
		"\u009cN\u0000\u01fa\u0089\u0001\u0000\u0000\u0000\u01fb\u01fc\u0005B\u0000"+
		"\u0000\u01fc\u01fd\u0003\u0010\b\u0000\u01fd\u01fe\u0003\u009cN\u0000"+
		"\u01fe\u008b\u0001\u0000\u0000\u0000\u01ff\u0200\u0005C\u0000\u0000\u0200"+
		"\u0201\u0003\u0010\b\u0000\u0201\u0202\u0003\u009cN\u0000\u0202\u008d"+
		"\u0001\u0000\u0000\u0000\u0203\u0204\u0005D\u0000\u0000\u0204\u0205\u0003"+
		"\u0010\b\u0000\u0205\u0206\u0003\u009cN\u0000\u0206\u008f\u0001\u0000"+
		"\u0000\u0000\u0207\u0208\u0005E\u0000\u0000\u0208\u0209\u0003\u0010\b"+
		"\u0000\u0209\u020a\u0003\u009cN\u0000\u020a\u0091\u0001\u0000\u0000\u0000"+
		"\u020b\u020c\u0005F\u0000\u0000\u020c\u020d\u0003\u0010\b\u0000\u020d"+
		"\u020e\u0003\u009cN\u0000\u020e\u0093\u0001\u0000\u0000\u0000\u020f\u0210"+
		"\u0005G\u0000\u0000\u0210\u0211\u0003\u0010\b\u0000\u0211\u0212\u0003"+
		"\u009cN\u0000\u0212\u0095\u0001\u0000\u0000\u0000\u0213\u0214\u0005H\u0000"+
		"\u0000\u0214\u0215\u0003\u0010\b\u0000\u0215\u0216\u0003\u009cN\u0000"+
		"\u0216\u0097\u0001\u0000\u0000\u0000\u0217\u0218\u0005I\u0000\u0000\u0218"+
		"\u0219\u0003\u0010\b\u0000\u0219\u021a\u0003\u009cN\u0000\u021a\u0099"+
		"\u0001\u0000\u0000\u0000\u021b\u021c\u0005J\u0000\u0000\u021c\u009b\u0001"+
		"\u0000\u0000\u0000\u021d\u021e\u0005K\u0000\u0000\u021e\u009d\u0001\u0000"+
		"\u0000\u0000\n\u00c6\u00cf\u00d3\u00d7\u00d9\u00e2\u0113\u0128\u0169\u0171";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}