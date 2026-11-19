// Generated from /home/auo/dev/cad-builder/cad-dsl-step-native/src/main/antlr/step_file.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class step_fileParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, INTEGER_NUMBER=20, DOUBLE_NUMBER=21, REF_LITERAL=22, 
		STRING_LITERAL=23, ESC=24, WS=25;
	public static final int
		RULE_step_file = 0, RULE_header_section = 1, RULE_header_fd = 2, RULE_header_fn = 3, 
		RULE_header_sc = 4, RULE_endsec = 5, RULE_data_length_unit = 6, RULE_par = 7, 
		RULE_exp = 8, RULE_header = 9, RULE_data_section = 10, RULE_data_header = 11, 
		RULE_data_item = 12, RULE_data_list = 13, RULE_data_styled_item = 14, 
		RULE_data_presentation_layer_ass = 15, RULE_data_cartesian_point = 16, 
		RULE_data_area_style = 17, RULE_data_line = 18, RULE_data_oriented_edge = 19, 
		RULE_sep = 20, RULE_nl = 21;
	private static String[] makeRuleNames() {
		return new String[] {
			"step_file", "header_section", "header_fd", "header_fn", "header_sc", 
			"endsec", "data_length_unit", "par", "exp", "header", "data_section", 
			"data_header", "data_item", "data_list", "data_styled_item", "data_presentation_layer_ass", 
			"data_cartesian_point", "data_area_style", "data_line", "data_oriented_edge", 
			"sep", "nl"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'ISO-10303-21;'", "'FILE_DESCRIPTION'", "'FILE_NAME'", "'FILE_SCHEMA'", 
			"'ENDSEC'", "'=( LENGTH_UNIT ( ) NAMED_UNIT ( * ) SI_UNIT ( .MILLI., .METRE. ) );'", 
			"'('", "')'", "'HEADER'", "'DATA'", "'='", "'STYLED_ITEM'", "'PRESENTATION_LAYER_ASSIGNMENT'", 
			"'CARTESIAN_POINT'", "'FILL_AREA_STYLE'", "'LINE'", "'ORIENTED_EDGE'", 
			"','", "';'", null, null, null, null, "'\\\"'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, "INTEGER_NUMBER", "DOUBLE_NUMBER", 
			"REF_LITERAL", "STRING_LITERAL", "ESC", "WS"
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
	public String getGrammarFileName() { return "step_file.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public step_fileParser(TokenStream input) {
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
		public TerminalNode EOF() { return getToken(step_fileParser.EOF, 0); }
		public Step_fileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_step_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterStep_file(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitStep_file(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitStep_file(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Step_fileContext step_file() throws RecognitionException {
		Step_fileContext _localctx = new Step_fileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_step_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			header_section();
			setState(45);
			data_section();
			setState(46);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterHeader_section(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitHeader_section(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitHeader_section(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_sectionContext header_section() throws RecognitionException {
		Header_sectionContext _localctx = new Header_sectionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_header_section);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(T__0);
			setState(49);
			header();
			setState(50);
			header_fd();
			setState(51);
			header_fn();
			setState(52);
			header_sc();
			setState(53);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterHeader_fd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitHeader_fd(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitHeader_fd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_fdContext header_fd() throws RecognitionException {
		Header_fdContext _localctx = new Header_fdContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_header_fd);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(55);
			match(T__1);
			setState(56);
			par();
			setState(57);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterHeader_fn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitHeader_fn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitHeader_fn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_fnContext header_fn() throws RecognitionException {
		Header_fnContext _localctx = new Header_fnContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_header_fn);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(T__2);
			setState(60);
			par();
			setState(61);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterHeader_sc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitHeader_sc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitHeader_sc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Header_scContext header_sc() throws RecognitionException {
		Header_scContext _localctx = new Header_scContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_header_sc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(T__3);
			setState(64);
			par();
			setState(65);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterEndsec(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitEndsec(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitEndsec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EndsecContext endsec() throws RecognitionException {
		EndsecContext _localctx = new EndsecContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_endsec);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(T__4);
			setState(68);
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
		public TerminalNode REF_LITERAL() { return getToken(step_fileParser.REF_LITERAL, 0); }
		public Data_length_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_length_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_length_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_length_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_length_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_length_unitContext data_length_unit() throws RecognitionException {
		Data_length_unitContext _localctx = new Data_length_unitContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_data_length_unit);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(REF_LITERAL);
			setState(71);
			match(T__5);
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
		public ParContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_par; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterPar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitPar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitPar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParContext par() throws RecognitionException {
		ParContext _localctx = new ParContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_par);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(T__6);
			setState(74);
			exp();
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__17) {
				{
				{
				setState(75);
				sep();
				setState(76);
				exp();
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(83);
			match(T__7);
			setState(89);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(84);
					sep();
					setState(85);
					exp();
					}
					} 
				}
				setState(91);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
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
		public ParContext par() {
			return getRuleContext(ParContext.class,0);
		}
		public List<SepContext> sep() {
			return getRuleContexts(SepContext.class);
		}
		public SepContext sep(int i) {
			return getRuleContext(SepContext.class,i);
		}
		public List<TerminalNode> STRING_LITERAL() { return getTokens(step_fileParser.STRING_LITERAL); }
		public TerminalNode STRING_LITERAL(int i) {
			return getToken(step_fileParser.STRING_LITERAL, i);
		}
		public List<TerminalNode> DOUBLE_NUMBER() { return getTokens(step_fileParser.DOUBLE_NUMBER); }
		public TerminalNode DOUBLE_NUMBER(int i) {
			return getToken(step_fileParser.DOUBLE_NUMBER, i);
		}
		public List<TerminalNode> INTEGER_NUMBER() { return getTokens(step_fileParser.INTEGER_NUMBER); }
		public TerminalNode INTEGER_NUMBER(int i) {
			return getToken(step_fileParser.INTEGER_NUMBER, i);
		}
		public List<TerminalNode> REF_LITERAL() { return getTokens(step_fileParser.REF_LITERAL); }
		public TerminalNode REF_LITERAL(int i) {
			return getToken(step_fileParser.REF_LITERAL, i);
		}
		public ExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterExp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitExp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpContext exp() throws RecognitionException {
		ExpContext _localctx = new ExpContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_exp);
		int _la;
		try {
			setState(99);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				par();
				}
				break;
			case INTEGER_NUMBER:
			case DOUBLE_NUMBER:
			case REF_LITERAL:
			case STRING_LITERAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(95); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(93);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 15728640L) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(94);
					sep();
					}
					}
					setState(97); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & 15728640L) != 0) );
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HeaderContext header() throws RecognitionException {
		HeaderContext _localctx = new HeaderContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(T__8);
			setState(102);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_section(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_section(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_section(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_sectionContext data_section() throws RecognitionException {
		Data_sectionContext _localctx = new Data_sectionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_data_section);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			data_header();
			setState(106); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(105);
				data_list();
				}
				}
				setState(108); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==REF_LITERAL );
			setState(110);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_header(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_header(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_header(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_headerContext data_header() throws RecognitionException {
		Data_headerContext _localctx = new Data_headerContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_data_header);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(112);
			match(T__9);
			setState(113);
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
		public Data_itemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_item; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_itemContext data_item() throws RecognitionException {
		Data_itemContext _localctx = new Data_itemContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_data_item);
		try {
			setState(121);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__11:
				enterOuterAlt(_localctx, 1);
				{
				setState(115);
				data_styled_item();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 2);
				{
				setState(116);
				data_presentation_layer_ass();
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 3);
				{
				setState(117);
				data_line();
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 4);
				{
				setState(118);
				data_oriented_edge();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 5);
				{
				setState(119);
				data_cartesian_point();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 6);
				{
				setState(120);
				data_area_style();
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
	public static class Data_listContext extends ParserRuleContext {
		public Data_length_unitContext data_length_unit() {
			return getRuleContext(Data_length_unitContext.class,0);
		}
		public TerminalNode REF_LITERAL() { return getToken(step_fileParser.REF_LITERAL, 0); }
		public Data_itemContext data_item() {
			return getRuleContext(Data_itemContext.class,0);
		}
		public Data_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_list(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_list(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_listContext data_list() throws RecognitionException {
		Data_listContext _localctx = new Data_listContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_data_list);
		try {
			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(123);
				data_length_unit();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(124);
				match(REF_LITERAL);
				setState(125);
				match(T__10);
				setState(126);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_styled_item(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_styled_item(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_styled_item(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_styled_itemContext data_styled_item() throws RecognitionException {
		Data_styled_itemContext _localctx = new Data_styled_itemContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_data_styled_item);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			match(T__11);
			setState(130);
			par();
			setState(131);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_presentation_layer_ass(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_presentation_layer_ass(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_presentation_layer_ass(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_presentation_layer_assContext data_presentation_layer_ass() throws RecognitionException {
		Data_presentation_layer_assContext _localctx = new Data_presentation_layer_assContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_data_presentation_layer_ass);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			match(T__12);
			setState(134);
			par();
			setState(135);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_cartesian_point(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_cartesian_point(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_cartesian_point(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_cartesian_pointContext data_cartesian_point() throws RecognitionException {
		Data_cartesian_pointContext _localctx = new Data_cartesian_pointContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_data_cartesian_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			match(T__13);
			setState(138);
			par();
			setState(139);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_area_style(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_area_style(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_area_style(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_area_styleContext data_area_style() throws RecognitionException {
		Data_area_styleContext _localctx = new Data_area_styleContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_data_area_style);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			match(T__14);
			setState(142);
			par();
			setState(143);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_line(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_line(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_lineContext data_line() throws RecognitionException {
		Data_lineContext _localctx = new Data_lineContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_data_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			match(T__15);
			setState(146);
			par();
			setState(147);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterData_oriented_edge(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitData_oriented_edge(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitData_oriented_edge(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_oriented_edgeContext data_oriented_edge() throws RecognitionException {
		Data_oriented_edgeContext _localctx = new Data_oriented_edgeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_data_oriented_edge);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			match(T__16);
			setState(150);
			par();
			setState(151);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterSep(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitSep(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitSep(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SepContext sep() throws RecognitionException {
		SepContext _localctx = new SepContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_sep);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			match(T__17);
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
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).enterNl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof step_fileListener ) ((step_fileListener)listener).exitNl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof step_fileVisitor ) return ((step_fileVisitor<? extends T>)visitor).visitNl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NlContext nl() throws RecognitionException {
		NlContext _localctx = new NlContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_nl);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(T__18);
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
		"\u0004\u0001\u0019\u009e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007"+
		"\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007"+
		"\u0015\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007O\b"+
		"\u0007\n\u0007\f\u0007R\t\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0005\u0007X\b\u0007\n\u0007\f\u0007[\t\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0004\b`\b\b\u000b\b\f\ba\u0003\bd\b\b\u0001\t\u0001\t\u0001\t\u0001"+
		"\n\u0001\n\u0004\nk\b\n\u000b\n\f\nl\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0003"+
		"\fz\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0003\r\u0080\b\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0014"+
		"\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0000\u0000\u0016\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c"+
		"\u001e \"$&(*\u0000\u0001\u0001\u0000\u0014\u0017\u0092\u0000,\u0001\u0000"+
		"\u0000\u0000\u00020\u0001\u0000\u0000\u0000\u00047\u0001\u0000\u0000\u0000"+
		"\u0006;\u0001\u0000\u0000\u0000\b?\u0001\u0000\u0000\u0000\nC\u0001\u0000"+
		"\u0000\u0000\fF\u0001\u0000\u0000\u0000\u000eI\u0001\u0000\u0000\u0000"+
		"\u0010c\u0001\u0000\u0000\u0000\u0012e\u0001\u0000\u0000\u0000\u0014h"+
		"\u0001\u0000\u0000\u0000\u0016p\u0001\u0000\u0000\u0000\u0018y\u0001\u0000"+
		"\u0000\u0000\u001a\u007f\u0001\u0000\u0000\u0000\u001c\u0081\u0001\u0000"+
		"\u0000\u0000\u001e\u0085\u0001\u0000\u0000\u0000 \u0089\u0001\u0000\u0000"+
		"\u0000\"\u008d\u0001\u0000\u0000\u0000$\u0091\u0001\u0000\u0000\u0000"+
		"&\u0095\u0001\u0000\u0000\u0000(\u0099\u0001\u0000\u0000\u0000*\u009b"+
		"\u0001\u0000\u0000\u0000,-\u0003\u0002\u0001\u0000-.\u0003\u0014\n\u0000"+
		"./\u0005\u0000\u0000\u0001/\u0001\u0001\u0000\u0000\u000001\u0005\u0001"+
		"\u0000\u000012\u0003\u0012\t\u000023\u0003\u0004\u0002\u000034\u0003\u0006"+
		"\u0003\u000045\u0003\b\u0004\u000056\u0003\n\u0005\u00006\u0003\u0001"+
		"\u0000\u0000\u000078\u0005\u0002\u0000\u000089\u0003\u000e\u0007\u0000"+
		"9:\u0003*\u0015\u0000:\u0005\u0001\u0000\u0000\u0000;<\u0005\u0003\u0000"+
		"\u0000<=\u0003\u000e\u0007\u0000=>\u0003*\u0015\u0000>\u0007\u0001\u0000"+
		"\u0000\u0000?@\u0005\u0004\u0000\u0000@A\u0003\u000e\u0007\u0000AB\u0003"+
		"*\u0015\u0000B\t\u0001\u0000\u0000\u0000CD\u0005\u0005\u0000\u0000DE\u0003"+
		"*\u0015\u0000E\u000b\u0001\u0000\u0000\u0000FG\u0005\u0016\u0000\u0000"+
		"GH\u0005\u0006\u0000\u0000H\r\u0001\u0000\u0000\u0000IJ\u0005\u0007\u0000"+
		"\u0000JP\u0003\u0010\b\u0000KL\u0003(\u0014\u0000LM\u0003\u0010\b\u0000"+
		"MO\u0001\u0000\u0000\u0000NK\u0001\u0000\u0000\u0000OR\u0001\u0000\u0000"+
		"\u0000PN\u0001\u0000\u0000\u0000PQ\u0001\u0000\u0000\u0000QS\u0001\u0000"+
		"\u0000\u0000RP\u0001\u0000\u0000\u0000SY\u0005\b\u0000\u0000TU\u0003("+
		"\u0014\u0000UV\u0003\u0010\b\u0000VX\u0001\u0000\u0000\u0000WT\u0001\u0000"+
		"\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001"+
		"\u0000\u0000\u0000Z\u000f\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000"+
		"\u0000\\d\u0003\u000e\u0007\u0000]^\u0007\u0000\u0000\u0000^`\u0003(\u0014"+
		"\u0000_]\u0001\u0000\u0000\u0000`a\u0001\u0000\u0000\u0000a_\u0001\u0000"+
		"\u0000\u0000ab\u0001\u0000\u0000\u0000bd\u0001\u0000\u0000\u0000c\\\u0001"+
		"\u0000\u0000\u0000c_\u0001\u0000\u0000\u0000d\u0011\u0001\u0000\u0000"+
		"\u0000ef\u0005\t\u0000\u0000fg\u0003*\u0015\u0000g\u0013\u0001\u0000\u0000"+
		"\u0000hj\u0003\u0016\u000b\u0000ik\u0003\u001a\r\u0000ji\u0001\u0000\u0000"+
		"\u0000kl\u0001\u0000\u0000\u0000lj\u0001\u0000\u0000\u0000lm\u0001\u0000"+
		"\u0000\u0000mn\u0001\u0000\u0000\u0000no\u0003\n\u0005\u0000o\u0015\u0001"+
		"\u0000\u0000\u0000pq\u0005\n\u0000\u0000qr\u0003*\u0015\u0000r\u0017\u0001"+
		"\u0000\u0000\u0000sz\u0003\u001c\u000e\u0000tz\u0003\u001e\u000f\u0000"+
		"uz\u0003$\u0012\u0000vz\u0003&\u0013\u0000wz\u0003 \u0010\u0000xz\u0003"+
		"\"\u0011\u0000ys\u0001\u0000\u0000\u0000yt\u0001\u0000\u0000\u0000yu\u0001"+
		"\u0000\u0000\u0000yv\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000"+
		"yx\u0001\u0000\u0000\u0000z\u0019\u0001\u0000\u0000\u0000{\u0080\u0003"+
		"\f\u0006\u0000|}\u0005\u0016\u0000\u0000}~\u0005\u000b\u0000\u0000~\u0080"+
		"\u0003\u0018\f\u0000\u007f{\u0001\u0000\u0000\u0000\u007f|\u0001\u0000"+
		"\u0000\u0000\u0080\u001b\u0001\u0000\u0000\u0000\u0081\u0082\u0005\f\u0000"+
		"\u0000\u0082\u0083\u0003\u000e\u0007\u0000\u0083\u0084\u0003*\u0015\u0000"+
		"\u0084\u001d\u0001\u0000\u0000\u0000\u0085\u0086\u0005\r\u0000\u0000\u0086"+
		"\u0087\u0003\u000e\u0007\u0000\u0087\u0088\u0003*\u0015\u0000\u0088\u001f"+
		"\u0001\u0000\u0000\u0000\u0089\u008a\u0005\u000e\u0000\u0000\u008a\u008b"+
		"\u0003\u000e\u0007\u0000\u008b\u008c\u0003*\u0015\u0000\u008c!\u0001\u0000"+
		"\u0000\u0000\u008d\u008e\u0005\u000f\u0000\u0000\u008e\u008f\u0003\u000e"+
		"\u0007\u0000\u008f\u0090\u0003*\u0015\u0000\u0090#\u0001\u0000\u0000\u0000"+
		"\u0091\u0092\u0005\u0010\u0000\u0000\u0092\u0093\u0003\u000e\u0007\u0000"+
		"\u0093\u0094\u0003*\u0015\u0000\u0094%\u0001\u0000\u0000\u0000\u0095\u0096"+
		"\u0005\u0011\u0000\u0000\u0096\u0097\u0003\u000e\u0007\u0000\u0097\u0098"+
		"\u0003*\u0015\u0000\u0098\'\u0001\u0000\u0000\u0000\u0099\u009a\u0005"+
		"\u0012\u0000\u0000\u009a)\u0001\u0000\u0000\u0000\u009b\u009c\u0005\u0013"+
		"\u0000\u0000\u009c+\u0001\u0000\u0000\u0000\u0007PYacly\u007f";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}