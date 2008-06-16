 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.el;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * EL string parser.
 * Creates list of tokens for the name, method and separator parts 
 *
 * @author Jeremy
 */
public class SeamELOperandTokenizer {
	protected static final int STATE_INITIAL = 0;
	protected static final int STATE_VAR = 1;
	protected static final int STATE_METHOD = 2;
	protected static final int STATE_SEPARATOR = 3;

	protected String documentContent;
	protected List<ELOperandToken> fTokens;
	protected int index;

	/**
	 * Constructs SeamELTokenizer object.
	 * Parse expression from offset to first operator or space.
	 * Tokenizer parses document from offset to beginning.
	 * For example: documentContetn is '<tag attr="#{var1.pr!=var2.pr}"/>'
	 *              offset =  29 ("...var2.pr|}")
	 *              then tokens are {"pr",".","var2"}
	 * @param documentContent
	 * @param offset
	 */
	public SeamELOperandTokenizer(String documentContent, int offset) {
		this.documentContent = documentContent;
		index = (documentContent == null || documentContent.length() < offset? -1 : offset);
		fTokens = new ArrayList<ELOperandToken>();
		parseBackward();
	}

	/**
	 * Constructs SeamELTokenizer object.
	 * @param document
	 * @param offset
	 */
	public SeamELOperandTokenizer(IDocument document, int offset) {
		this(document.get(), offset);
	}

	/**
	 * Returns list of tokens for the expression parsed
	 * 
	 * @return
	 */
	public List<ELOperandToken> getTokens() {
		return fTokens;
	}

	/*
	 * Performs backward parsing of document text for expression
	 */
	private void parseBackward() {
		ELOperandToken token;
		fState = STATE_INITIAL;
		while ((token = getNextToken()) != ELOperandToken.EOF) {
			if (token.type == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.type == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
					token.type == ELOperandToken.EL_METHOD_TOKEN ||
					token.type == ELOperandToken.EL_SEPARATOR_TOKEN) {

				fTokens.add(0, token);
			}
		}
		// set first token as variable
		for (int i=0; i<fTokens.size(); i++) {
			token = fTokens.get(i);
			if(token.isNameToken()) {
				token.type = ELOperandToken.EL_VARIABLE_NAME_TOKEN;
				break;
			}
		}
	}

	int fState;
	int fEndOfToken;

	/*
	 * Calculates and returns next token for expression
	 * 
	 * @return
	 */
	private ELOperandToken getNextToken() {
		switch (fState) {
		case STATE_INITIAL: // Just started
		{
			int ch = readCharBackward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (Character.isJavaIdentifierPart((char)ch)) {
				releaseChar();
				return readVarToken();
			}
			if (ch == '.') {
				releaseChar();
				return readSeparatorToken();
			}
			if (ch == ')') {
				releaseChar();
				return readMethodToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		case STATE_VAR: // Variable name is read - expecting a separator 
		{
			int ch = readCharBackward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (ch == '.') {
				releaseChar();
				return readSeparatorToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		case STATE_METHOD: // Method name and parameters are read - expecting a separator
		{
			int ch = readCharBackward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (ch == '.') {
				releaseChar();
				return readSeparatorToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		case STATE_SEPARATOR: // Separator is read - expecting a var or method
		{
			int ch = readCharBackward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (Character.isJavaIdentifierPart((char)ch)) {
				releaseChar();
				return readVarToken();
			}
			if (ch == ')') {
				releaseChar();
				return readMethodToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		}
		return ELOperandToken.EOF;
	}
	
	/* Reads and returns the method token from the expression
	 * 
	 * @return
	 */
	private ELOperandToken readMethodToken() {
		fState = STATE_METHOD;
		int endOfToken = index;
		
		// read the method parameters
		if (!skipMethodParameters()) 
			return ELOperandToken.EOF;
		
		// skip spaces between the method's name and it's parameters
		if (!skipSpaceChars())
			return ELOperandToken.EOF;
		// read the method name
		if (!skipMethodName())
			return ELOperandToken.EOF;
		
		return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), ELOperandToken.EL_METHOD_TOKEN) : ELOperandToken.EOF);
	}

	/*
	 * Returns the CharSequence object
	 *  
	 * @param start
	 * @param length
	 * @return
	 */
	protected CharSequence getCharSequence(int start, int length) {
		String text = ""; //$NON-NLS-1$
		try {
			text = documentContent.substring(start, start + length);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError(e);
			text = ""; // For sure //$NON-NLS-1$
		}
		return text.subSequence(0, text.length());
	}

	
	/*
	 * Skips the space characters in the document
	 */
	private boolean skipSpaceChars() {
		int ch;
		while ((ch = readCharBackward()) != -1) {
			if (!Character.isSpaceChar(ch)) {
				releaseChar();
				break;
			}
		}
		return true;
	}
	
	/* 
	 * Skips the method name characters in the document
	 * 
	 * @return boolean true if at least 1 character had been read
	 */
	boolean skipMethodName() {
		int endOfToken = index;
		int ch;
		while((ch = readCharBackward()) != -1) {
			if (!Character.isJavaIdentifierPart(ch)) {
				releaseChar();
				return (endOfToken - index > 0);
			}
		}
		return false;
	}

	/* 
	 * Skips the method parameters characters in the document
	 * 
	 * @return boolean true if complete parameters set had been read
	 */
	private boolean skipMethodParameters() {
		int ch = readCharBackward(); 
		if (ch != ')')
			return false;
		int pCount = 1;
		while (pCount > 0) {
			ch = readCharBackward();
			if (ch == -1)
				return false;
			
			if (ch == '"' || ch == '\'') {
				skipQuotedChars((char)ch);
				continue;
			}
			if (ch == ')') {
				pCount++;
				continue;
			}
			if (ch == '(') {
				pCount--;
				continue;
			}
		}
		return true;
	}

	/* 
	 * Skips the quoted characters 
	 * 
	 */
	private void skipQuotedChars(char pair) {
		int ch = readCharBackward();
		
		while (ch != -1) {
			if (ch == pair) {
				ch = readCharBackward();
				if (ch == '\\') {
					int backSlashCount = 0;
					while (ch == '\\') {
						backSlashCount++;
						ch = readCharBackward();
					}
					releaseChar(); // Return the last non-slash char to the buffer
					if ((backSlashCount/2)*2 == backSlashCount) {
						return;
					}
				} else {
					releaseChar(); // Return the last non-slash char to the buffer
					return;
				}
			}
			ch = readCharBackward();
		}
	}

	/* Reads and returns the separator token from the expression
	 * 
	 * @return
	 */
	private ELOperandToken readSeparatorToken() {
		fState = STATE_SEPARATOR;
		int ch = readCharBackward();
		
		return (ch == '.' ? new ELOperandToken(index, 1, getCharSequence(index, 1), ELOperandToken.EL_SEPARATOR_TOKEN) :
			ELOperandToken.EOF);
	}
	
	/* Reads and returns the variable token from the expression
	 * 
	 * @return
	 */
	private ELOperandToken readVarToken() {
		fState = STATE_VAR;
		int endOfToken = index;
		int ch;
		int type = ELOperandToken.EL_PROPERTY_NAME_TOKEN;
		while((ch = readCharBackward()) != -1) {
			if (!Character.isJavaIdentifierPart(ch)) {
				releaseChar();
				return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), type) : ELOperandToken.EOF);
			}
		}
		releaseChar();
		return (endOfToken - index > 0 ? new ELOperandToken(index, endOfToken - index, getCharSequence(index, endOfToken - index), type) : ELOperandToken.EOF);
	}

	/* Reads the next character in the document
	 * 
	 * @return
	 */
	private int readCharBackward() {
		if (--index < 0 || 
				documentContent == null ||
				documentContent.length() <= index)
			return -1;

		try {
			return documentContent.charAt(index);
		} catch (StringIndexOutOfBoundsException e) {
			return -1;
		}
	}
	
	/* 
	 * returns the character to the document
	 */
	private void releaseChar() {
		if (index < documentContent.length())
			index++;
	}
}