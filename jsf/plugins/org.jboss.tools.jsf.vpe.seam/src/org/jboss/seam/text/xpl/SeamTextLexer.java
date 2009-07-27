// $ANTLR 2.7.6 (2005-12-22): "seam-text.g" -> "SeamTextLexer.java"$

package org.jboss.seam.text.xpl;

import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

@SuppressWarnings("nls")
public class SeamTextLexer extends antlr.CharScanner implements SeamTextParserTokenTypes, TokenStream
 {
public SeamTextLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public SeamTextLexer(Reader in) {
	this(new CharBuffer(in));
}
public SeamTextLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public SeamTextLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = true;
	setCaseSensitive(true);
	literals = new Hashtable();
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '!':  case '$':  case '%':  case '\'':
				case '(':  case ')':  case ',':  case '-':
				case '.':  case ':':  case ';':  case '?':
				case '@':  case '{':  case '}':
				{
					mPUNCTUATION(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mEQ(true);
					theRetToken=_returnToken;
					break;
				}
				case '+':
				{
					mPLUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '_':
				{
					mUNDERSCORE(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mSTAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mSLASH(true);
					theRetToken=_returnToken;
					break;
				}
				case '\\':
				{
					mESCAPE(true);
					theRetToken=_returnToken;
					break;
				}
				case '|':
				{
					mBAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '`':
				{
					mBACKTICK(true);
					theRetToken=_returnToken;
					break;
				}
				case '~':
				{
					mTWIDDLE(true);
					theRetToken=_returnToken;
					break;
				}
				case '"':
				{
					mDOUBLEQUOTE(true);
					theRetToken=_returnToken;
					break;
				}
				case '[':
				{
					mOPEN(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mCLOSE(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mHASH(true);
					theRetToken=_returnToken;
					break;
				}
				case '^':
				{
					mHAT(true);
					theRetToken=_returnToken;
					break;
				}
				case '>':
				{
					mGT(true);
					theRetToken=_returnToken;
					break;
				}
				case '<':
				{
					mLT(true);
					theRetToken=_returnToken;
					break;
				}
				case '&':
				{
					mAMPERSAND(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case ' ':
				{
					mSPACE(true);
					theRetToken=_returnToken;
					break;
				}
				case '\n':  case '\r':
				{
					mNEWLINE(true);
					theRetToken=_returnToken;
					break;
				}
				case '\uffff':
				{
					mEOF(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((_tokenSet_0.member(LA(1)))) {
						mWORD(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mWORD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WORD;
		int _saveIndex;
		
		{
		int _cnt107=0;
		_loop107:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case 'A':  case 'B':  case 'C':  case 'D':
			case 'E':  case 'F':  case 'G':  case 'H':
			case 'I':  case 'J':  case 'K':  case 'L':
			case 'M':  case 'N':  case 'O':  case 'P':
			case 'Q':  case 'R':  case 'S':  case 'T':
			case 'U':  case 'V':  case 'W':  case 'X':
			case 'Y':  case 'Z':
			{
				matchRange('A','Z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '\u00a0':  case '\u00a1':  case '\u00a2':  case '\u00a3':
			case '\u00a4':  case '\u00a5':  case '\u00a6':  case '\u00a7':
			case '\u00a8':  case '\u00a9':  case '\u00aa':  case '\u00ab':
			case '\u00ac':  case '\u00ad':  case '\u00ae':  case '\u00af':
			case '\u00b0':  case '\u00b1':  case '\u00b2':  case '\u00b3':
			case '\u00b4':  case '\u00b5':  case '\u00b6':  case '\u00b7':
			case '\u00b8':  case '\u00b9':  case '\u00ba':  case '\u00bb':
			case '\u00bc':  case '\u00bd':  case '\u00be':  case '\u00bf':
			case '\u00c0':  case '\u00c1':  case '\u00c2':  case '\u00c3':
			case '\u00c4':  case '\u00c5':  case '\u00c6':  case '\u00c7':
			case '\u00c8':  case '\u00c9':  case '\u00ca':  case '\u00cb':
			case '\u00cc':  case '\u00cd':  case '\u00ce':  case '\u00cf':
			case '\u00d0':  case '\u00d1':  case '\u00d2':  case '\u00d3':
			case '\u00d4':  case '\u00d5':  case '\u00d6':  case '\u00d7':
			case '\u00d8':  case '\u00d9':  case '\u00da':  case '\u00db':
			case '\u00dc':  case '\u00dd':  case '\u00de':  case '\u00df':
			case '\u00e0':  case '\u00e1':  case '\u00e2':  case '\u00e3':
			case '\u00e4':  case '\u00e5':  case '\u00e6':  case '\u00e7':
			case '\u00e8':  case '\u00e9':  case '\u00ea':  case '\u00eb':
			case '\u00ec':  case '\u00ed':  case '\u00ee':  case '\u00ef':
			case '\u00f0':  case '\u00f1':  case '\u00f2':  case '\u00f3':
			case '\u00f4':  case '\u00f5':  case '\u00f6':  case '\u00f7':
			case '\u00f8':  case '\u00f9':  case '\u00fa':  case '\u00fb':
			case '\u00fc':  case '\u00fd':  case '\u00fe':  case '\u00ff':
			{
				matchRange('\u00a0','\u00ff');
				break;
			}
			default:
				if (((LA(1) >= '\u0100' && LA(1) <= '\u017f'))) {
					matchRange('\u0100','\u017f');
				}
				else if (((LA(1) >= '\u0180' && LA(1) <= '\u024f'))) {
					matchRange('\u0180','\u024f');
				}
				else if (((LA(1) >= '\u0250' && LA(1) <= '\ufaff'))) {
					matchRange('\u0250','\ufaff');
				}
				else if (((LA(1) >= '\uff00' && LA(1) <= '\uffef'))) {
					matchRange('\uff00','\uffef');
				}
			else {
				if ( _cnt107>=1 ) { break _loop107; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt107++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPUNCTUATION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PUNCTUATION;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '-':
		{
			match('-');
			break;
		}
		case ';':
		{
			match(';');
			break;
		}
		case ':':
		{
			match(':');
			break;
		}
		case '(':
		{
			match('(');
			break;
		}
		case ')':
		{
			match(')');
			break;
		}
		case '{':
		{
			match('{');
			break;
		}
		case '}':
		{
			match('}');
			break;
		}
		case '?':
		{
			match('?');
			break;
		}
		case '!':
		{
			match('!');
			break;
		}
		case '@':
		{
			match('@');
			break;
		}
		case '%':
		{
			match('%');
			break;
		}
		case '.':
		{
			match('.');
			break;
		}
		case ',':
		{
			match(',');
			break;
		}
		case '\'':
		{
			match('\'');
			break;
		}
		case '$':
		{
			match('$');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQ;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mUNDERSCORE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UNDERSCORE;
		int _saveIndex;
		
		match('_');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSLASH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SLASH;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mESCAPE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESCAPE;
		int _saveIndex;
		
		match('\\');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BAR;
		int _saveIndex;
		
		match('|');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mBACKTICK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = BACKTICK;
		int _saveIndex;
		
		match('`');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mTWIDDLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = TWIDDLE;
		int _saveIndex;
		
		match('~');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOUBLEQUOTE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOUBLEQUOTE;
		int _saveIndex;
		
		match('"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOPEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE;
		int _saveIndex;
		
		match(']');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mHASH(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HASH;
		int _saveIndex;
		
		match('#');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mHAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HAT;
		int _saveIndex;
		
		match('^');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GT;
		int _saveIndex;
		
		match('>');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LT;
		int _saveIndex;
		
		match('<');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAMPERSAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AMPERSAND;
		int _saveIndex;
		
		match('&');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSPACE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SPACE;
		int _saveIndex;
		
		{
		int _cnt128=0;
		_loop128:
		do {
			switch ( LA(1)) {
			case ' ':
			{
				match(' ');
				break;
			}
			case '\t':
			{
				match('\t');
				break;
			}
			default:
			{
				if ( _cnt128>=1 ) { break _loop128; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt128++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNEWLINE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NEWLINE;
		int _saveIndex;
		
		if ((LA(1)=='\r') && (LA(2)=='\n')) {
			match("\r\n");
		}
		else if ((LA(1)=='\r') && (true)) {
			match('\r');
		}
		else if ((LA(1)=='\n')) {
			match('\n');
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEOF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Token.EOF_TYPE;
		int _saveIndex;
		
		match('\uFFFF');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[4084];
		data[0]=287948901175001088L;
		data[1]=576460743847706622L;
		data[2]=-4294967296L;
		for (int i = 3; i<=1003; i++) { data[i]=-1L; }
		for (int i = 1020; i<=1022; i++) { data[i]=-1L; }
		data[1023]=281474976710655L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	
	}
