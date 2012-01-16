/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.jboss.tools.common.text.ext.util.TextScanner;
import org.jboss.tools.common.text.ext.util.Utils;

/**
 * Detects if there is an xhtml is in reader 
 * 
 * @author Victor V. Rubezhny
 *
 */
class XHTMLDetector extends TextScanner {
	
	private static final String XML_DECLARATION_START = "<?";
	private static final String XML_DECLARATION_END = "?>";
	private static final String XML_DECLARATION = "xml";			
	
	private static final String DOCTYPE_DECLARATION = "DOCTYPE";
	private static final String DOCTYPE_DECLARATION_END = ">";
	private static final String VALID_DOCTYPE_ROOT = "html";
	private static final String[] VALID_DOCTYPE_DTD_DECLARATION_REQUIRED_TOKENS = { "W3C", "DTD", "XHTML"};
	private static final String[] VALID_DOCTYPE_DTD_DECLARATION_ONE_OF_TOKENS = { "Strict", "Transitional", "Frameset"};
	private static final String[] VALID_DOCTYPE_DTD_DECLARATION_REQUIRED_SYSTEM_IDS = {
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd",
		"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd",
	    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd"
	};
	private static final String VALID_ELEMENT_XMLNS_ATTRIBUTE = "xmlns";
	private static final String VALID_ELEMENT_XMLNS_ATTRIBUTE_VALUE = "http://www.w3.org/1999/xhtml";
	
	private static final String TEXT_TOKEN = "___TEXT_TOKEN";
	private static final String COMMENT_TOKEN = "___COMMENT_TOKEN";
	private static final String XML_DECL_TOKEN = "___XML_DECL_TOKEN";
	private static final String DECL_TOKEN = "___DECL_TOKEN";
	private static final String ELEMENT_TOKEN = "___ELEMENT_TOKEN";
		
	private static final String PUBLIC = "PUBLIC";
	private static final String SYSTEM = "SYSTEM";
	
	public XHTMLDetector(Reader reader) {
		super(reader);
	}

	public boolean detect () {
		state = STATE_START;
		clearText();

		boolean docTypeFound = false;
		String docTypeRootName = null;
		String docTypeIdKind = null;
		String docTypePublicId = null;
		String docTypeSystemId = null;
		
		for (IToken t = nextToken(); t != null && !t.isEOF() ; t = nextToken()) {
			if (!(t instanceof TextToken)) 
				continue;
			TextToken token = (TextToken)t;
			if (!docTypeFound && DECL_TOKEN.equals(token.getType())) {
				if (declName != null && declName.equals(DOCTYPE_DECLARATION)) {
					docTypeFound = true;
					docTypeRootName = rootName;
					docTypeIdKind = idKind;
					docTypePublicId = publicId;
					docTypeSystemId = systemId;
					
//					if (!VALID_DOCTYPE_ROOT.equals(docTypeRootName))
//						return false;
					if (!PUBLIC.equals(docTypeIdKind))
						return false;
					if (!hasAllTokens(docTypePublicId, VALID_DOCTYPE_DTD_DECLARATION_REQUIRED_TOKENS))
						return false;
					if (!hasOneOfTokens(docTypePublicId, VALID_DOCTYPE_DTD_DECLARATION_ONE_OF_TOKENS))
						return false;
				}
			}
			if (ELEMENT_TOKEN.equals(token.getType())) {
				if (docTypeFound) {
					if (elementName == null)
						return false;

					String name = elementName.substring(elementName.indexOf(':') + 1); // Cut the prefix off
					
					if (!docTypeRootName.equals(name))
						return false;
					if (!elementAttributes.containsKey(VALID_ELEMENT_XMLNS_ATTRIBUTE))
						return false;
					
					String value = elementAttributes.get(VALID_ELEMENT_XMLNS_ATTRIBUTE);
					if (value == null) return false;
					if (!VALID_ELEMENT_XMLNS_ATTRIBUTE_VALUE.equals(Utils.trimQuotes(value).toLowerCase()))
						return false;
					return true;
				} else {
					if (!elementAttributes.containsKey(VALID_ELEMENT_XMLNS_ATTRIBUTE))
						continue;
					
					String value = elementAttributes.get(VALID_ELEMENT_XMLNS_ATTRIBUTE);
					if (value == null) continue;
					if (!VALID_ELEMENT_XMLNS_ATTRIBUTE_VALUE.equals(Utils.trimQuotes(value).toLowerCase()))
						continue;
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean hasAllTokens(String publicId, String[] reqiured) {
		if (publicId == null) return false;
		for (String r : reqiured) {
			if (publicId.indexOf(r) == -1) 
				return false;
		}
		return true;
	}
	private boolean hasOneOfTokens(String publicId, String[] oneOf) {
		if (publicId == null) return false;
		boolean found = false;
		for (String r : oneOf) {
			if (publicId.indexOf(r) != -1) {
				if (found) 
					return false; // Contains more than one token
				found = true;
			}
		}
		return found;
	}

	private static final int STATE_START			= 0;
	private static final int STATE_ELEMENT	= 1;
	private static final int STATE_ELEMENT_END		= 2;
	
	private static final int STATE_XML_DECL	= 3;
	private static final int STATE_XML_DECL_END		= 4;
			
	private static final int STATE_DECL		= 5;
	private static final int STATE_DECL_NAME		= 6;
	private static final int STATE_DECL_ROOT		= 7;
	private static final int STATE_DECL_PUBLIC		= 8;
	private static final int STATE_DECL_SYSTEM		= 9;
	private static final int STATE_DECL_PUBLIC_ID	= 10;
	private static final int STATE_DECL_SYSTEM_ID 	= 11;
	private static final int STATE_DECL_END			= 12;

	private static final int STATE_COMMENT	= 13;
	private static final int STATE_COMMENT_END		= 14;
	private static final int STATE_END				= 15;

	private int state;
	
	private String declName;
	private String rootName;
	private String idKind;
	private String publicId;
	private String systemId;
	
	private String elementName;
	private Map<String, String> elementAttributes = new HashMap<String, String>();
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.text.ext.util.TextScanner#nextToken()
	 */
	public IToken nextToken() {
		offset += length;
		switch (state) {
			case STATE_ELEMENT:
				return nextElementToken();
			case STATE_DECL:
				return nextDeclToken();
			case STATE_XML_DECL:
				return nextXmlDeclToken();
			case STATE_COMMENT:
				return nextCommentToken();
		}
		return nextTextToken();
	}

	private IToken nextTextToken() {
		int count = skipWhitespaceToken();
		int ch = read();
		while (ch != ICharacterScanner.EOF) {
			if (ch == '<') {
				state = STATE_ELEMENT;
				ch = read();
				if (ch == '!') {
					state = STATE_DECL;
					ch = read(); 
					if (ch == '-')  {
						ch = read();
						if (ch == '-') {
							state = STATE_COMMENT;
							unread(); // "-"
							unread(); // "-"
							unread(); // "!" char
							unread(); // "<" char
							return (count > 0 ? getToken(TEXT_TOKEN) : nextCommentToken());
						}
						if (ch != -1) 
							unread(); // last char
						unread(); // "-"
					}
					if (ch != -1) 
						unread(); // last char
					unread(); // "!" char
					unread(); // "<" char
					return (count > 0 ? getToken(TEXT_TOKEN) : nextDeclToken());
				}
				if (ch == '?') {
					state = STATE_XML_DECL;
					unread(); // "?" char
					unread(); // "<" char
					return (count > 0 ? getToken(TEXT_TOKEN) : nextXmlDeclToken());
				}
				if (ch != -1) 
					unread(); // last char
				unread(); // "<" char
				return (count > 0 ? getToken(TEXT_TOKEN) : nextElementToken());
			}
			count++;
			ch = read();
		}
		state = STATE_END;
		return getToken(TEXT_TOKEN);
	}
	
	private IToken nextCommentToken() {
		int count = skip(3); // Skip '<!--' chars
		if (count < 3) {
			state = STATE_END;
			return getToken(COMMENT_TOKEN);
		}
		int ch = read();
		while (ch != ICharacterScanner.EOF) {
			if (ch == '-') {
				ch = read();
				if (ch == ICharacterScanner.EOF)  {
					break; 
				}
				if (ch == '-') {
					ch = read(); 
					if (ch == ICharacterScanner.EOF)  {
						break; 
					}
					if (ch == '>')  {
						state = STATE_START;
						return getToken(COMMENT_TOKEN);
					}
				}
			}
			count++;
			ch = read();
		}
		state = STATE_END;
		return getToken(COMMENT_TOKEN);
	}
	
	private IToken nextXmlDeclToken() {
		int count = skip(2); // Skip '<?' chars
		if (count < 2) {
			state = STATE_END;
			return getToken(XML_DECL_TOKEN);
		}
		int ch = read();
		while (ch != ICharacterScanner.EOF) {
			if (ch == '"' || ch == '\'') {
				count += skipLiteralToken(ch);
				ch = read();
				continue;
			}
			
			if (ch == '?') {
				ch = read();
				if (ch == ICharacterScanner.EOF)  {
					break; 
				}
				if (ch == '>') {
					state = STATE_START;
					return getToken(XML_DECL_TOKEN);
				}
			}
			count++;
			ch = read();
		}
		state = STATE_END;
		return getToken(XML_DECL_TOKEN);
	}
	
	
	private IToken nextDeclToken() {
		int count = skip(2); // Skip '<' chars
		if (count < 2) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		
		// Read declaration name (we're very expecting to see 'DOCTYPE' here)
		declName = readName();
		if (declName == null || declName.length() == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += declName.length();
		
		// At least one WS-char is expected here
		int wsCount = skipWhitespaceToken();
		if (wsCount == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += wsCount;
		
		// Read root element name here (http://www.w3.org/TR/xhtml1/#strict says that 'html' is strictly expected here) 
		rootName = readName();
		if (declName == null || declName.length() == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += declName.length();
		
		// At least one WS-char is expected here
		wsCount = skipWhitespaceToken();
		if (wsCount == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += wsCount;
		
		// Read 'PUBLIC' or 'SYSTEM' word here 
		idKind = readName();
		if (declName == null || declName.length() == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += declName.length();
		
		// At least one WS-char is expected here
		wsCount = skipWhitespaceToken();
		if (wsCount == 0) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}
		count += wsCount;

		if (!PUBLIC.equals(idKind) && !SYSTEM.equals(idKind)) {
			state = STATE_END;
			return getToken(DECL_TOKEN);
		}

		// If ID is PUBLIC then read PUBLIC ID value
		if (PUBLIC.equals(idKind)) {
			publicId = readLiteralValue();
			count += publicId.length();
			
			// At least one WS-char is expected here
			wsCount = skipWhitespaceToken();
			if (wsCount == 0) {
				state = STATE_END;
				return getToken(DECL_TOKEN);
			}
			count += wsCount;
		}
		
		// Read SYSTEM ID value 
		systemId = readLiteralValue();
		count += systemId.length();
		
		// Expecting end of declaration, so don't check count of WS-chars
		count += skipWhitespaceToken();
		count += wsCount;

		int ch = read();
		state = ch == '>' ? STATE_START : STATE_END;
		return getToken(DECL_TOKEN);
	}

	private IToken nextElementToken() {
		int count = skip(1); // Skip '<' char
		if (count < 1) {
			state = STATE_END;
			return getToken(ELEMENT_TOKEN);
		}

		// Check for '/' char (ending tag)
		int ch = read();
		if (ch == -1) {
			state = STATE_END;
			return getToken(ELEMENT_TOKEN);
		}
		boolean closingTag = true;
		if (ch != '/') { // unread if it's not '/' char
			unread(); 
			closingTag = false;
		} else {
			count++;
		}
		
		// Read tag name (the tag that is interesting for us is 'html', but it could be any tag) 
		elementName = readName();
		elementAttributes.clear();
		
		if (elementName == null || elementName.length() == 0) {
			state = STATE_END;
			return getToken(ELEMENT_TOKEN);
		}
		count += elementName.length();
		
		ch = read(); // Check that the next char exists

		while (ch != ICharacterScanner.EOF) {
			if (ch == -1) {
				state = STATE_END;
				return getToken(ELEMENT_TOKEN);
			}
			unread();
			
			int wsCount = skipWhitespaceToken();
			count += wsCount;
			// Check for end of tag:
			ch = read();
			if (!closingTag && ch == '/') { // - end of tag with no body
				ch = read();
				state = ch == '>' ? STATE_START : STATE_END;
				return getToken(ELEMENT_TOKEN);
			} else if (ch == '>') { // - end of tag with body
				state = STATE_START;
				return getToken(ELEMENT_TOKEN);
			} else {
				if (wsCount == 0) {
					state = STATE_END;
					return getToken(ELEMENT_TOKEN);
				}
			}
			unread();
			count += wsCount;

			if (!closingTag) {
				// Read attr name
				String attrName = readName();
				if (attrName == null || attrName.length() == 0) {
					state = STATE_END;
					return getToken(DECL_TOKEN);
				}
				count += attrName.length();
				count += skipWhitespaceToken();
				
				// read eq sign
				ch = read();
				if (ch != '=') {
					state = STATE_END;
					return getToken(ELEMENT_TOKEN);
				}
				count++;
				count += skipWhitespaceToken();

				// read attr value
				String attrValue = readLiteralValue();
				count += attrValue.length();
				
				elementAttributes.put(attrName, attrValue);
			}
			// 

			ch = read();
		}
		
		state = STATE_END;
		return getToken(ELEMENT_TOKEN);
	}
	
	int skip(int count) {
		int skipped = 0;
		for (;skipped < count && read() != -1;skipped++) ;
		return skipped;
	}
	
	public int skipLiteralToken(int quote) {
		int count = 0;
		for (int ch = read(); ch != -1 && ch != quote; ch = read()) count++;
		return count;
	}
	
	String readLiteralValue() {
		StringBuffer sb = new StringBuffer();
		
		int quote = read();
		if (quote != '"' && quote != '\'') {
			unread();
			return sb.toString();
		}
		sb.append((char)quote);
		int ch = read();
		for (; ch != -1 && ch != quote; ch = read()) sb.append((char)ch);
		if (ch != -1)
			sb.append((char)ch);
		return sb.toString();
	}
	
	String readName() {
		StringBuffer sb = new StringBuffer();

		// Check first one char in the stream
		int ch = read();
		if (ch == ICharacterScanner.EOF) {
			return null;
		}
		if (!NMTOKEN_DETECTOR.isWordStart((char)ch)) {
			return null;
		}
		sb.append((char)ch);
		
		ch = read();
		while (ch != ICharacterScanner.EOF) {
			if (!NMTOKEN_DETECTOR.isWordPart((char)ch)) {
				unread();
				break;
			}
			sb.append((char)ch);
			ch = read();
		}
		return sb.toString();
	}
}
