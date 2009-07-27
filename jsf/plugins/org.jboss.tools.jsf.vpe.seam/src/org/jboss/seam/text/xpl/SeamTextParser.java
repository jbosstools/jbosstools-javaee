// $ANTLR 2.7.6 (2005-12-22): "seam-text.g" -> "SeamTextParser.java"$

package org.jboss.seam.text.xpl;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

@SuppressWarnings("nls")
public class SeamTextParser extends antlr.LLkParser       implements SeamTextParserTokenTypes
 {
   
	private java.util.Set htmlElements = new java.util.HashSet( java.util.Arrays.asList( new String[] { "a", "p", "q", "code", "pre", "table", "tr", "td", "th", "ul", "ol", "li", "b", "i", "u", "tt", "del", "em", "hr", "br", "div", "span", "h1", "h2", "h3", "h4", "img" , "object", "param", "embed"} ) );
	private java.util.Set htmlAttributes = new java.util.HashSet( java.util.Arrays.asList( new String[] { "src", "href", "lang", "class", "id", "style", "width", "height", "name", "value", "type", "wmode" } ) );
	
    private StringBuilder mainBuilder = new StringBuilder();
    private StringBuilder builder = mainBuilder;
    
    public String toString() {
        return builder.toString();
    }
    
    private void append(String... strings) {
        for (String string: strings) builder.append(string);
    }
    
    private static boolean hasMultiple(String string, char c) {
        return string.indexOf(c)!=string.lastIndexOf(c);
    }
    
    private void validateElement(Token t) throws NoViableAltException {
        if ( !htmlElements.contains( t.getText().toLowerCase() ) ) {
            throw new NoViableAltException(t, null);
        }
    }

    private void validateAttribute(Token t) throws NoViableAltException {
        if ( !htmlAttributes.contains( t.getText().toLowerCase() ) ) {
            throw new NoViableAltException(t, null);
        }
    }
    
    private void beginCapture() {
        builder = new StringBuilder();
    }
    
    private String endCapture() {
        String result = builder.toString();
        builder = mainBuilder;
        return result;
    }

    protected String linkTag(String description, String url) {
        return "<a href=\"" + url + "\" styleClass=\"seamTextLink\">" + description + "</a>";
    }

    protected String macroInclude(String macroName) {
        return "";
    }

protected SeamTextParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public SeamTextParser(TokenBuffer tokenBuf) {
  this(tokenBuf,4);
}

protected SeamTextParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public SeamTextParser(TokenStream lexer) {
  this(lexer,4);
}

public SeamTextParser(ParserSharedInputState state) {
  super(state,4);
  tokenNames = _tokenNames;
}

	public final void startRule() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop3:
		do {
			if ((LA(1)==NEWLINE)) {
				newline();
			}
			else {
				break _loop3;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case DOUBLEQUOTE:
		case BACKTICK:
		case WORD:
		case PUNCTUATION:
		case ESCAPE:
		case STAR:
		case SLASH:
		case BAR:
		case HAT:
		case PLUS:
		case EQ:
		case HASH:
		case TWIDDLE:
		case UNDERSCORE:
		case OPEN:
		case LT:
		case SPACE:
		{
			{
			switch ( LA(1)) {
			case PLUS:
			{
				heading();
				{
				_loop7:
				do {
					if ((LA(1)==NEWLINE)) {
						newline();
					}
					else {
						break _loop7;
					}
					
				} while (true);
				}
				break;
			}
			case DOUBLEQUOTE:
			case BACKTICK:
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case EQ:
			case HASH:
			case TWIDDLE:
			case UNDERSCORE:
			case OPEN:
			case LT:
			case SPACE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			text();
			{
			_loop11:
			do {
				if ((LA(1)==PLUS)) {
					heading();
					{
					_loop10:
					do {
						if ((LA(1)==NEWLINE)) {
							newline();
						}
						else {
							break _loop10;
						}
						
					} while (true);
					}
					text();
				}
				else {
					break _loop11;
				}
				
			} while (true);
			}
			break;
		}
		case EOF:
		{
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void newline() throws RecognitionException, TokenStreamException {
		
		Token  n = null;
		
		n = LT(1);
		match(NEWLINE);
		append( n.getText() );
	}
	
	public final void heading() throws RecognitionException, TokenStreamException {
		
		
		{
		if ((LA(1)==PLUS) && (_tokenSet_0.member(LA(2)))) {
			h1();
		}
		else if ((LA(1)==PLUS) && (LA(2)==PLUS) && (_tokenSet_0.member(LA(3)))) {
			h2();
		}
		else if ((LA(1)==PLUS) && (LA(2)==PLUS) && (LA(3)==PLUS) && (_tokenSet_0.member(LA(4)))) {
			h3();
		}
		else if ((LA(1)==PLUS) && (LA(2)==PLUS) && (LA(3)==PLUS) && (LA(4)==PLUS)) {
			h4();
		}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		
		}
		newlineOrEof();
	}
	
	public final void text() throws RecognitionException, TokenStreamException {
		
		
		{
		int _cnt17=0;
		_loop17:
		do {
			if ((_tokenSet_1.member(LA(1)))) {
				{
				switch ( LA(1)) {
				case WORD:
				case PUNCTUATION:
				case ESCAPE:
				case STAR:
				case SLASH:
				case BAR:
				case HAT:
				case TWIDDLE:
				case UNDERSCORE:
				case OPEN:
				case SPACE:
				{
					paragraph();
					break;
				}
				case BACKTICK:
				{
					preformatted();
					break;
				}
				case DOUBLEQUOTE:
				{
					blockquote();
					break;
				}
				case EQ:
				case HASH:
				{
					list();
					break;
				}
				case LT:
				{
					html();
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				{
				_loop16:
				do {
					if ((LA(1)==NEWLINE)) {
						newline();
					}
					else {
						break _loop16;
					}
					
				} while (true);
				}
			}
			else {
				if ( _cnt17>=1 ) { break _loop17; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt17++;
		} while (true);
		}
	}
	
	public final void paragraph() throws RecognitionException, TokenStreamException {
		
		
		append("<p>\n");
		{
		int _cnt20=0;
		_loop20:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				line();
				newlineOrEof();
			}
			else {
				if ( _cnt20>=1 ) { break _loop20; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt20++;
		} while (true);
		}
		append("</p>\n");
		newlineOrEof();
	}
	
	public final void preformatted() throws RecognitionException, TokenStreamException {
		
		
		match(BACKTICK);
		append("<pre>");
		{
		_loop30:
		do {
			switch ( LA(1)) {
			case WORD:
			{
				word();
				break;
			}
			case PUNCTUATION:
			{
				punctuation();
				break;
			}
			case ESCAPE:
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case PLUS:
			case EQ:
			case HASH:
			case TWIDDLE:
			case UNDERSCORE:
			{
				specialChars();
				break;
			}
			case OPEN:
			case CLOSE:
			{
				moreSpecialChars();
				break;
			}
			case DOUBLEQUOTE:
			case GT:
			case LT:
			case AMPERSAND:
			{
				htmlSpecialChars();
				break;
			}
			case SPACE:
			{
				space();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				break _loop30;
			}
			}
		} while (true);
		}
		match(BACKTICK);
		append("</pre>");
	}
	
	public final void blockquote() throws RecognitionException, TokenStreamException {
		
		
		match(DOUBLEQUOTE);
		append("<blockquote>\n");
		{
		_loop27:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case TWIDDLE:
			case UNDERSCORE:
			{
				formatted();
				break;
			}
			case BACKTICK:
			{
				preformatted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			case LT:
			{
				html();
				break;
			}
			case EQ:
			case HASH:
			{
				list();
				break;
			}
			default:
			{
				break _loop27;
			}
			}
		} while (true);
		}
		match(DOUBLEQUOTE);
		newlineOrEof();
		append("</blockquote>\n");
	}
	
	public final void list() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case HASH:
		{
			olist();
			break;
		}
		case EQ:
		{
			ulist();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		newlineOrEof();
	}
	
	public final void html() throws RecognitionException, TokenStreamException {
		
		
		openTag();
		{
		_loop87:
		do {
			if ((LA(1)==SPACE) && (LA(2)==SLASH||LA(2)==GT||LA(2)==SPACE)) {
				space();
			}
			else if ((LA(1)==SPACE) && (LA(2)==WORD)) {
				space();
				attribute();
			}
			else {
				break _loop87;
			}
			
		} while (true);
		}
		{
		switch ( LA(1)) {
		case GT:
		{
			{
			beforeBody();
			body();
			closeTagWithBody();
			}
			break;
		}
		case SLASH:
		{
			closeTagWithNoBody();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void line() throws RecognitionException, TokenStreamException {
		
		
		{
		switch ( LA(1)) {
		case WORD:
		case PUNCTUATION:
		case ESCAPE:
		case OPEN:
		case SPACE:
		{
			plain();
			break;
		}
		case STAR:
		case SLASH:
		case BAR:
		case HAT:
		case TWIDDLE:
		case UNDERSCORE:
		{
			formatted();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
		{
		_loop24:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case TWIDDLE:
			case UNDERSCORE:
			{
				formatted();
				break;
			}
			case BACKTICK:
			{
				preformatted();
				break;
			}
			case DOUBLEQUOTE:
			{
				quoted();
				break;
			}
			case LT:
			{
				html();
				break;
			}
			default:
			{
				break _loop24;
			}
			}
		} while (true);
		}
	}
	
	public final void newlineOrEof() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case NEWLINE:
		{
			newline();
			break;
		}
		case EOF:
		{
			match(Token.EOF_TYPE);
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void plain() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case WORD:
		{
			word();
			break;
		}
		case PUNCTUATION:
		{
			punctuation();
			break;
		}
		case ESCAPE:
		{
			escape();
			break;
		}
		case SPACE:
		{
			space();
			break;
		}
		default:
			if ((LA(1)==OPEN) && (_tokenSet_2.member(LA(2)))) {
				link();
			}
			else if ((LA(1)==OPEN) && (LA(2)==LT)) {
				macro();
			}
		else {
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void formatted() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case STAR:
		{
			bold();
			break;
		}
		case UNDERSCORE:
		{
			underline();
			break;
		}
		case SLASH:
		{
			italic();
			break;
		}
		case BAR:
		{
			monospace();
			break;
		}
		case HAT:
		{
			superscript();
			break;
		}
		case TWIDDLE:
		{
			deleted();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void quoted() throws RecognitionException, TokenStreamException {
		
		
		match(DOUBLEQUOTE);
		append("<q>");
		{
		int _cnt65=0;
		_loop65:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			{
				bold();
				break;
			}
			case UNDERSCORE:
			{
				underline();
				break;
			}
			case SLASH:
			{
				italic();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case HAT:
			{
				superscript();
				break;
			}
			case TWIDDLE:
			{
				deleted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt65>=1 ) { break _loop65; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt65++;
		} while (true);
		}
		match(DOUBLEQUOTE);
		append("</q>");
	}
	
	public final void word() throws RecognitionException, TokenStreamException {
		
		Token  w = null;
		
		w = LT(1);
		match(WORD);
		append( w.getText() );
	}
	
	public final void punctuation() throws RecognitionException, TokenStreamException {
		
		Token  p = null;
		
		p = LT(1);
		match(PUNCTUATION);
		append( p.getText() );
	}
	
	public final void specialChars() throws RecognitionException, TokenStreamException {
		
		Token  st = null;
		Token  sl = null;
		Token  b = null;
		Token  h = null;
		Token  p = null;
		Token  eq = null;
		Token  hh = null;
		Token  e = null;
		Token  t = null;
		Token  u = null;
		
		switch ( LA(1)) {
		case STAR:
		{
			st = LT(1);
			match(STAR);
			append( st.getText() );
			break;
		}
		case SLASH:
		{
			sl = LT(1);
			match(SLASH);
			append( sl.getText() );
			break;
		}
		case BAR:
		{
			b = LT(1);
			match(BAR);
			append( b.getText() );
			break;
		}
		case HAT:
		{
			h = LT(1);
			match(HAT);
			append( h.getText() );
			break;
		}
		case PLUS:
		{
			p = LT(1);
			match(PLUS);
			append( p.getText() );
			break;
		}
		case EQ:
		{
			eq = LT(1);
			match(EQ);
			append( eq.getText() );
			break;
		}
		case HASH:
		{
			hh = LT(1);
			match(HASH);
			append( hh.getText() );
			break;
		}
		case ESCAPE:
		{
			e = LT(1);
			match(ESCAPE);
			append( e.getText() );
			break;
		}
		case TWIDDLE:
		{
			t = LT(1);
			match(TWIDDLE);
			append( t.getText() );
			break;
		}
		case UNDERSCORE:
		{
			u = LT(1);
			match(UNDERSCORE);
			append( u.getText() );
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void moreSpecialChars() throws RecognitionException, TokenStreamException {
		
		Token  o = null;
		Token  c = null;
		
		switch ( LA(1)) {
		case OPEN:
		{
			o = LT(1);
			match(OPEN);
			append( o.getText() );
			break;
		}
		case CLOSE:
		{
			c = LT(1);
			match(CLOSE);
			append( c.getText() );
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void htmlSpecialChars() throws RecognitionException, TokenStreamException {
		
		
		switch ( LA(1)) {
		case GT:
		{
			match(GT);
			append("&gt;");
			break;
		}
		case LT:
		{
			match(LT);
			append("&lt;");
			break;
		}
		case DOUBLEQUOTE:
		{
			match(DOUBLEQUOTE);
			append("&quot;");
			break;
		}
		case AMPERSAND:
		{
			match(AMPERSAND);
			append("&amp;");
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
	}
	
	public final void space() throws RecognitionException, TokenStreamException {
		
		Token  s = null;
		
		s = LT(1);
		match(SPACE);
		append( s.getText() );
	}
	
	public final void escape() throws RecognitionException, TokenStreamException {
		
		
		match(ESCAPE);
		{
		switch ( LA(1)) {
		case ESCAPE:
		case STAR:
		case SLASH:
		case BAR:
		case HAT:
		case PLUS:
		case EQ:
		case HASH:
		case TWIDDLE:
		case UNDERSCORE:
		{
			specialChars();
			break;
		}
		case OPEN:
		case CLOSE:
		{
			moreSpecialChars();
			break;
		}
		case QUOTE:
		{
			evenMoreSpecialChars();
			break;
		}
		case DOUBLEQUOTE:
		case GT:
		case LT:
		case AMPERSAND:
		{
			htmlSpecialChars();
			break;
		}
		default:
		{
			throw new NoViableAltException(LT(1), getFilename());
		}
		}
		}
	}
	
	public final void link() throws RecognitionException, TokenStreamException {
		
		
		match(OPEN);
		beginCapture();
		{
		_loop43:
		do {
			if ((_tokenSet_3.member(LA(1)))) {
				plain();
			}
			else {
				break _loop43;
			}
			
		} while (true);
		}
		String text=endCapture();
		match(EQ);
		match(GT);
		beginCapture();
		attributeValue();
		String link = endCapture(); append(linkTag(text, link));
		match(CLOSE);
	}
	
	public final void macro() throws RecognitionException, TokenStreamException {
		
		
		match(OPEN);
		match(LT);
		match(EQ);
		beginCapture();
		attributeValue();
		String macroName = endCapture(); append(macroInclude(macroName));
		match(CLOSE);
	}
	
	public final void bold() throws RecognitionException, TokenStreamException {
		
		
		match(STAR);
		append("<b>");
		{
		int _cnt47=0;
		_loop47:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case UNDERSCORE:
			{
				underline();
				break;
			}
			case SLASH:
			{
				italic();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case HAT:
			{
				superscript();
				break;
			}
			case TWIDDLE:
			{
				deleted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt47>=1 ) { break _loop47; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt47++;
		} while (true);
		}
		match(STAR);
		append("</b>");
	}
	
	public final void underline() throws RecognitionException, TokenStreamException {
		
		
		match(UNDERSCORE);
		append("<u>");
		{
		int _cnt50=0;
		_loop50:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			{
				bold();
				break;
			}
			case SLASH:
			{
				italic();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case HAT:
			{
				superscript();
				break;
			}
			case TWIDDLE:
			{
				deleted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt50>=1 ) { break _loop50; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt50++;
		} while (true);
		}
		match(UNDERSCORE);
		append("</u>");
	}
	
	public final void italic() throws RecognitionException, TokenStreamException {
		
		
		match(SLASH);
		append("<i>");
		{
		int _cnt53=0;
		_loop53:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			{
				bold();
				break;
			}
			case UNDERSCORE:
			{
				underline();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case HAT:
			{
				superscript();
				break;
			}
			case TWIDDLE:
			{
				deleted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt53>=1 ) { break _loop53; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt53++;
		} while (true);
		}
		match(SLASH);
		append("</i>");
	}
	
	public final void monospace() throws RecognitionException, TokenStreamException {
		
		Token  st = null;
		Token  sl = null;
		Token  h = null;
		Token  p = null;
		Token  eq = null;
		Token  hh = null;
		Token  e = null;
		Token  t = null;
		Token  u = null;
		
		match(BAR);
		append("<tt>");
		{
		int _cnt56=0;
		_loop56:
		do {
			switch ( LA(1)) {
			case WORD:
			{
				word();
				break;
			}
			case PUNCTUATION:
			{
				punctuation();
				break;
			}
			case SPACE:
			{
				space();
				break;
			}
			case STAR:
			{
				st = LT(1);
				match(STAR);
				append( st.getText() );
				break;
			}
			case SLASH:
			{
				sl = LT(1);
				match(SLASH);
				append( sl.getText() );
				break;
			}
			case HAT:
			{
				h = LT(1);
				match(HAT);
				append( h.getText() );
				break;
			}
			case PLUS:
			{
				p = LT(1);
				match(PLUS);
				append( p.getText() );
				break;
			}
			case EQ:
			{
				eq = LT(1);
				match(EQ);
				append( eq.getText() );
				break;
			}
			case HASH:
			{
				hh = LT(1);
				match(HASH);
				append( hh.getText() );
				break;
			}
			case ESCAPE:
			{
				e = LT(1);
				match(ESCAPE);
				append( e.getText() );
				break;
			}
			case TWIDDLE:
			{
				t = LT(1);
				match(TWIDDLE);
				append( t.getText() );
				break;
			}
			case UNDERSCORE:
			{
				u = LT(1);
				match(UNDERSCORE);
				append( u.getText() );
				break;
			}
			case OPEN:
			case CLOSE:
			{
				moreSpecialChars();
				break;
			}
			case DOUBLEQUOTE:
			case GT:
			case LT:
			case AMPERSAND:
			{
				htmlSpecialChars();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt56>=1 ) { break _loop56; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt56++;
		} while (true);
		}
		match(BAR);
		append("</tt>");
	}
	
	public final void superscript() throws RecognitionException, TokenStreamException {
		
		
		match(HAT);
		append("<sup>");
		{
		int _cnt59=0;
		_loop59:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			{
				bold();
				break;
			}
			case UNDERSCORE:
			{
				underline();
				break;
			}
			case SLASH:
			{
				italic();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case TWIDDLE:
			{
				deleted();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt59>=1 ) { break _loop59; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt59++;
		} while (true);
		}
		match(HAT);
		append("</sup>");
	}
	
	public final void deleted() throws RecognitionException, TokenStreamException {
		
		
		match(TWIDDLE);
		append("<del>");
		{
		int _cnt62=0;
		_loop62:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			{
				bold();
				break;
			}
			case UNDERSCORE:
			{
				underline();
				break;
			}
			case SLASH:
			{
				italic();
				break;
			}
			case BAR:
			{
				monospace();
				break;
			}
			case HAT:
			{
				superscript();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
			{
				if ( _cnt62>=1 ) { break _loop62; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			}
			_cnt62++;
		} while (true);
		}
		match(TWIDDLE);
		append("</del>");
	}
	
	public final void evenMoreSpecialChars() throws RecognitionException, TokenStreamException {
		
		Token  q = null;
		
		q = LT(1);
		match(QUOTE);
		append( q.getText() );
	}
	
	public final void attributeValue() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop104:
		do {
			switch ( LA(1)) {
			case AMPERSAND:
			{
				match(AMPERSAND);
				append("&amp;");
				break;
			}
			case WORD:
			{
				word();
				break;
			}
			case PUNCTUATION:
			{
				punctuation();
				break;
			}
			case SPACE:
			{
				space();
				break;
			}
			case ESCAPE:
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case PLUS:
			case EQ:
			case HASH:
			case TWIDDLE:
			case UNDERSCORE:
			{
				specialChars();
				break;
			}
			default:
			{
				break _loop104;
			}
			}
		} while (true);
		}
	}
	
	public final void h1() throws RecognitionException, TokenStreamException {
		
		
		match(PLUS);
		append("<h1>");
		line();
		append("</h1>");
	}
	
	public final void h2() throws RecognitionException, TokenStreamException {
		
		
		match(PLUS);
		match(PLUS);
		append("<h2>");
		line();
		append("</h2>");
	}
	
	public final void h3() throws RecognitionException, TokenStreamException {
		
		
		match(PLUS);
		match(PLUS);
		match(PLUS);
		append("<h3>");
		line();
		append("</h3>");
	}
	
	public final void h4() throws RecognitionException, TokenStreamException {
		
		
		match(PLUS);
		match(PLUS);
		match(PLUS);
		match(PLUS);
		append("<h4>");
		line();
		append("</h4>");
	}
	
	public final void olist() throws RecognitionException, TokenStreamException {
		
		
		append("<ol>\n");
		{
		int _cnt76=0;
		_loop76:
		do {
			if ((LA(1)==HASH)) {
				olistLine();
				newlineOrEof();
			}
			else {
				if ( _cnt76>=1 ) { break _loop76; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt76++;
		} while (true);
		}
		append("</ol>\n");
	}
	
	public final void ulist() throws RecognitionException, TokenStreamException {
		
		
		append("<ul>\n");
		{
		int _cnt80=0;
		_loop80:
		do {
			if ((LA(1)==EQ)) {
				ulistLine();
				newlineOrEof();
			}
			else {
				if ( _cnt80>=1 ) { break _loop80; } else {throw new NoViableAltException(LT(1), getFilename());}
			}
			
			_cnt80++;
		} while (true);
		}
		append("</ul>\n");
	}
	
	public final void olistLine() throws RecognitionException, TokenStreamException {
		
		
		match(HASH);
		append("<li>");
		line();
		append("</li>");
	}
	
	public final void ulistLine() throws RecognitionException, TokenStreamException {
		
		
		match(EQ);
		append("<li>");
		line();
		append("</li>");
	}
	
	public final void openTag() throws RecognitionException, TokenStreamException {
		
		Token  name = null;
		
		match(LT);
		name = LT(1);
		match(WORD);
		validateElement(name); append("<"); append(name.getText());
	}
	
	public final void attribute() throws RecognitionException, TokenStreamException {
		
		Token  att = null;
		
		att = LT(1);
		match(WORD);
		{
		_loop99:
		do {
			if ((LA(1)==SPACE)) {
				space();
			}
			else {
				break _loop99;
			}
			
		} while (true);
		}
		match(EQ);
		{
		_loop101:
		do {
			if ((LA(1)==SPACE)) {
				space();
			}
			else {
				break _loop101;
			}
			
		} while (true);
		}
		match(DOUBLEQUOTE);
		validateAttribute(att); append(att.getText()); append("=\"");
		attributeValue();
		match(DOUBLEQUOTE);
		append("\"");
	}
	
	public final void beforeBody() throws RecognitionException, TokenStreamException {
		
		
		match(GT);
		append(">");
	}
	
	public final void body() throws RecognitionException, TokenStreamException {
		
		
		{
		_loop92:
		do {
			switch ( LA(1)) {
			case WORD:
			case PUNCTUATION:
			case ESCAPE:
			case OPEN:
			case SPACE:
			{
				plain();
				break;
			}
			case STAR:
			case SLASH:
			case BAR:
			case HAT:
			case TWIDDLE:
			case UNDERSCORE:
			{
				formatted();
				break;
			}
			case BACKTICK:
			{
				preformatted();
				break;
			}
			case DOUBLEQUOTE:
			{
				quoted();
				break;
			}
			case EQ:
			case HASH:
			{
				list();
				break;
			}
			case NEWLINE:
			{
				newline();
				break;
			}
			default:
				if ((LA(1)==LT) && (LA(2)==WORD)) {
					html();
				}
			else {
				break _loop92;
			}
			}
		} while (true);
		}
	}
	
	public final void closeTagWithBody() throws RecognitionException, TokenStreamException {
		
		Token  name = null;
		
		match(LT);
		match(SLASH);
		name = LT(1);
		match(WORD);
		match(GT);
		append("</"); append(name.getText()); append(">");
	}
	
	public final void closeTagWithNoBody() throws RecognitionException, TokenStreamException {
		
		
		match(SLASH);
		match(GT);
		append("/>");
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"DOUBLEQUOTE",
		"BACKTICK",
		"WORD",
		"PUNCTUATION",
		"ESCAPE",
		"STAR",
		"SLASH",
		"BAR",
		"HAT",
		"PLUS",
		"EQ",
		"HASH",
		"TWIDDLE",
		"UNDERSCORE",
		"OPEN",
		"CLOSE",
		"QUOTE",
		"GT",
		"LT",
		"AMPERSAND",
		"SPACE",
		"NEWLINE"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 17244096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 21487600L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 17056192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 17039808L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	
	}
