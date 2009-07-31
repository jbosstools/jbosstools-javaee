/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.adopt;

import java.util.*;
import org.eclipse.jface.text.IDocument;

public class JSPTokenizer {
	static int ROOT = 0;
	static int TEXT = 1;
	static int TAG = 2;
	static int TAG_CLOSING = 3;
	static int JSP = 4;
	static int DIRECTIVE = 5;
	static int COMMENT = 6;
	static int DOCTYPE = 7;

	String text;
	StringBuffer sb = new StringBuffer();
	Token root;
	
	int selectionStart;
	int selectionEnd;
	
	int start = -1;
	int end = -1;
	
	public Token parse(IDocument document) {
		selectionStart = 0;
		selectionEnd = document.getLength();
		text = document.get();
		root = new Token(ROOT, "", 0, text.length(), null); //$NON-NLS-1$
		root.indent = ""; //$NON-NLS-1$
		root.indentLevel = -1;
		tokenize();
		return root;
	}
	
	private void tokenize() {
		int cursor = 0;
		Token current = root;
		Token last = null;
		while(cursor < text.length()) {
			int p = text.indexOf('<', cursor);
			if(p < 0) {
				current.addChild(last = createTag(TEXT, "", cursor, text.length() - cursor, last)); //$NON-NLS-1$
				cursor = text.length();
			} else {
				if(p > cursor) {
					current.addChild(last = createTag(TEXT, "", cursor, p - cursor, last)); //$NON-NLS-1$
					cursor = p;
				}
				if(isStringStart(cursor, "<!DOCTYPE")) { //$NON-NLS-1$
					int l = "<!DOCTYPE".length(); //$NON-NLS-1$
					int q = text.indexOf(">", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 1;
					int k = skipToName(cursor + l, nc);
					String tag = readTag(cursor + l + k);
					current.addChild(last = createTag(DOCTYPE, tag, cursor, nc - cursor, last));
					int ab = cursor + l + k + tag.length();
					int al = nc - ab;
					last.attributes = getDoctype(text, ab, al);
					cursor = nc;
				} else if(isStringStart(cursor, "<%@")) {					 //$NON-NLS-1$
					int q = text.indexOf("%>", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 2;
					int k = skipToName(cursor + 3, nc);
					String tag = readTag(cursor + 3 + k);
					current.addChild(last = createTag(DIRECTIVE, tag, cursor, nc - cursor, last));
					int ab = cursor + 3 + k + tag.length();
					int al = nc - ab;
					last.attributes = getAttributes(text, ab, al);
					cursor = nc;
				} else if(isStringStart(cursor, "<%")) { //$NON-NLS-1$
					int q = text.indexOf("%>", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 2;
					current.addChild(last = createTag(JSP, "", cursor, nc - cursor, last)); //$NON-NLS-1$
					cursor = nc;
				} else if(isStringStart(cursor, "<!--")) { //$NON-NLS-1$
					int q = text.indexOf("-->", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 3;
					current.addChild(last = createTag(COMMENT, "", cursor, nc - cursor, last)); //$NON-NLS-1$
					cursor = nc;
				} else if(isStringStart(cursor, "<!")) { //$NON-NLS-1$
					///This is doctype
					int q = text.indexOf(">", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 1;
					current.addChild(last = createTag(COMMENT, "", cursor, nc - cursor, last)); //$NON-NLS-1$
					cursor = nc;
				} else if(isStringStart(cursor, "</")) { //$NON-NLS-1$
					String tag = readTag(cursor + 2);
					int q = text.indexOf(">", cursor); //$NON-NLS-1$
					int nc = (q < 0) ? text.length() : q + 1;
					last = createTag(TAG_CLOSING, tag, cursor, nc - cursor, last);
					current = findParent(current, last);
					current.addChild(last);
					cursor = nc;
				} else {
					String tag = readTag(cursor + 1);
					int q = findTagClosingSymbol(cursor);
					int nc = (q < 0) ? text.length() : q + 1;
					last = createTag(TAG, tag, cursor, nc - cursor, last);
					int ab = cursor + 1 + tag.length();
					int al = nc - ab;
					last.attributes = getAttributes(text, ab, al);
					if(isOptionallyClosed(tag)) {
						current = findParentForOptionallyClosedTag(current, tag);
					}
					current.addChild(last);
					cursor = nc;
					if(q > 0 && text.charAt(q - 1) != '/' && areChildrenAllowed(tag)) {
						current = last;
					}
				}
			}
		}
	}
	
	private Token findParent(Token current, Token t) {
		Token c = current;
		while(c.kind != ROOT) {		
			if(c.name.equals(t.name)) return c.parent;
			c = c.parent;
		}
		return current;
	}
	
	private boolean isOptionallyClosed(String name) {
		return ".body.p.dt.dd.li.ol.option.thead.tfoot.tbody.colgroup.tr.td.th.head.html.".indexOf(name.toLowerCase()) >= 0; //$NON-NLS-1$
	}
	
	private Token findParentForOptionallyClosedTag(Token current, String name) {
		String n1 = name.toLowerCase();
		String n2 = current.name.toLowerCase();
		if("p".equals(n1)) { //$NON-NLS-1$
			if(n2.equals("p")) return current.parent; //$NON-NLS-1$
		} else if("tr".equals(n1)) { //$NON-NLS-1$
			if(n2.equals("tr")) return current.parent; //$NON-NLS-1$
			if(n2.equals("th") || n2.equals("td") || n2.equals("p")) return findParentForOptionallyClosedTag(current.parent, name); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if("td".equals(n1) || "th".equals(n1)) {  //$NON-NLS-1$ //$NON-NLS-2$
			if(n2.equals("th") || n2.equals("td")) return current.parent; //$NON-NLS-1$ //$NON-NLS-2$
			if(n2.equals("p")) return findParentForOptionallyClosedTag(current.parent, name); //$NON-NLS-1$
		}
		return current;
	}
	
	private boolean areChildrenAllowed(String name) {
		return ".br.area.link.img.param.hr.input.col.isindex.base.meta.".indexOf("." + name.toLowerCase() + ".") < 0; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	private Token createTag(int kind, String name, int off, int length, Token previous) {
		Token t = new Token(kind, name, off, length, previous);
		t.indentLength = (kind == TEXT) ? -1 : computeIndentLength(off);
		if(previous != null && previous.kind == JSPTokenizer.TEXT && t.indentLength > 0) {
			previous.length -= t.indentLength;
			t.off -= t.indentLength;
			t.length += t.indentLength;
		}
		return t;
	}
	
	private boolean isStringStart(int c, String s) {
		if(text.length() <= c + s.length()) return false;
		for (int i = 0; i < s.length(); i++) {
			if(text.charAt(c + i) != s.charAt(i)) return false;
		}
		return true;
	}
	
	private int skipToName(int b, int e) {
		int t = b;
		while(t < e && !isNameChar(text.charAt(t))) {
			++t;
		}
		return t - b;
	}
	
	private String readTag(int c) {
		int k = c;
		while(k < text.length() && isNameChar(text.charAt(k))) ++k;
		return text.substring(c, k);
	}
	
	private boolean isNameChar(char ch) {
		return Character.isJavaIdentifierPart(ch) || ch == '-' || ch == ':';
	}
	
	private int computeIndentLength(int off) {
		off--;
		int l = 0;
		while(off >= 0) {
			char ch = text.charAt(off);
			if(ch == '\n' || ch == '\r') return l;
			if(!Character.isWhitespace(ch)) return -1;
			++l;
			--off;
		}
		return (off < 0) ? l : -1;
	}
	
	public int findTagClosingSymbol(int i) {
		int l = text.length();
		char quota = '\0';
		while(i < l) {
			char ch = text.charAt(i);
			if(quota != '\0') {
				if(ch == quota) quota = '\0';
			} else if(ch == '\'' || ch == '"') {
				quota = ch;
			} else if(ch == '>') {
				return i;
			}
			++i;
		}
		return -1;
	}

	public Token getTokenAt(int pos) {
		return getTokenAt(root, pos);
	}
	
	public Token getTokenAt(Token t, int pos) {
		if(t == null || t.off > pos) return null;
		if(t.off + t.length > pos) {
			return (t.firstChild == null || t.firstChild.off > pos) ? t : getTokenAt(t.firstChild, pos);
		}
		if(t.nextSibling != null && t.nextSibling.off <= pos) {
			return getTokenAt(t.nextSibling, pos);
		}
		return (t.firstChild == null) ? t : getTokenAt(t.firstChild, pos);
	}
	
	public boolean isInTagAttributeValue(int pos) {
		Token t = getTokenAt(root, pos);
		if(t == null || t.kind != TAG) return false;
		char quote = '\0';
		for (int i = root.off; i < text.length() && i < pos; i++) {
			char ch = text.charAt(i);
			if(ch == quote) quote = '\0';
			else if(ch == '"' || ch == '\'') quote = ch;
		}
		return (quote != '\0');
	}

    static Properties getAttributes(String text, int off, int length) {
    	Properties p = new Properties();
    	int NOTHING = 0;
    	int NAME = 1;
    	int VALUE = 2;
    	int state = 0;
    	char quote = '\0';
    	StringBuffer name = new StringBuffer();
    	StringBuffer value = new StringBuffer();
    	for (int i = 0; i < length; i++) {
    		char ch = text.charAt(i + off);
    		if(state == NOTHING) {
    			if(" \t\r\n".indexOf(ch) >= 0) continue; //$NON-NLS-1$
    			if("/\\><%".indexOf(ch) >= 0) break; //$NON-NLS-1$
    			state = NAME;
    			name.append(ch);
    		} else if(state == NAME) {
    			if(ch == '=') {
    				state = VALUE;
    			} else {
    				name.append(ch);
    			}
    		} else if(state == VALUE) {				
    			if(ch == quote) {
    				String n = name.toString().trim();
    				String v = value.toString();
    				name.setLength(0);
    				value.setLength(0);
    				p.setProperty(n, v);
    				state = NOTHING;
    				quote = '\0';
    			} else if(quote == '\0' && (ch == '"' || ch == '\'')) {
    				quote = ch;    				
    			} else if(quote != '\0') {
    				value.append(ch);
    			}
    		}
    	}
    	return p;
    }

    static Properties getDoctype(String text, int off, int length) {
    	Properties p = new Properties();
    	int b = off;
    	int i = text.indexOf("PUBLIC", off); //$NON-NLS-1$
    	if(i >= 0) {
    		int i1 = text.indexOf('"', i);
    		if(i1 >= 0) {
    			int i2 = text.indexOf('"', i1 + 1);
    			if(i2 >= 0) {
    				p.setProperty("public", text.substring(i1 + 1, i2)); //$NON-NLS-1$
    				b = i2 + 1;
    			}
    		}
    	}
    	int i1 = text.indexOf('"', b);
		if(i1 >= 0) {
			int i2 = text.indexOf('"', i1 + 1);
			if(i2 >= 0) {
				p.setProperty("system", text.substring(i1 + 1, i2)); //$NON-NLS-1$
			}
		}
    	return p;
    }

}
