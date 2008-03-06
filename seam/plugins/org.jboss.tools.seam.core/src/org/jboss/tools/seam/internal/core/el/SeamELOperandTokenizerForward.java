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

import org.eclipse.jface.text.IDocument;

/**
 * EL string parser.
 * Creates list of tokens for the name, method and separator parts 
 * Forward parsing
 *
 * @author Jeremy
 */

public class SeamELOperandTokenizerForward extends SeamELOperandTokenizer {

	/**
	 * Constructs SeamELOperandTokenizerForward object.
	 * Parse expression from offset to last operator or space.
	 * Tokenizer parses document from offset to ending.
	 * For example: documentContetn is '<tag attr="#{var1.pr!=var2.pr}"/>'
	 *              offset =  29 ("...var2.pr|}")
	 *              then tokens are {"pr",".","var2"}
	 * @param documentContent
	 * @param offset
	 */
	public SeamELOperandTokenizerForward(String documentContent, int offset) {
		super(documentContent, offset);
		this.documentContent = documentContent;
		index = (documentContent == null || documentContent.length() < offset? -1 : offset);
		fTokens = new ArrayList<ELOperandToken>();
		parseForward();
	}

	/**
	 * Constructs SeamELOperandTokenizerForward object.
	 * Parse expression from offset to last operator or space.
	 * Tokenizer parses document from offset to ending.
	 * For example: documentContetn is '<tag attr="#{var1.pr!=var2.pr}"/>'
	 *              offset =  29 ("...var2.pr|}")
	 *              then tokens are {"pr",".","var2"}
	 * @param document
	 * @param offset
	 */
	public SeamELOperandTokenizerForward(IDocument document, int offset) {
		this(document.get(), offset);
	}

	/*
	 * Performs forward parsing of document text for expression
	 */
	private void parseForward() {
		ELOperandToken token;
		fState = STATE_INITIAL;
		while ((token = getNextToken()) != ELOperandToken.EOF) {
			if (token.type == ELOperandToken.EL_VARIABLE_NAME_TOKEN ||
					token.type == ELOperandToken.EL_PROPERTY_NAME_TOKEN ||
					token.type == ELOperandToken.EL_METHOD_TOKEN ||
					token.type == ELOperandToken.EL_SEPARATOR_TOKEN) {

				fTokens.add(token);
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

	/*
	 * Calculates and returns next token for expression.
	 * Forward parsing
	 * 
	 * @return
	 */
	private ELOperandToken getNextToken() {
		switch (fState) {
		case STATE_INITIAL: // Just started
		{
			int ch = readCharForward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (Character.isJavaIdentifierPart((char)ch)) {
				releaseChar();
				return readVarOrMethodToken();
			}
			if (ch == '.') {
				releaseChar();
				return readSeparatorToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		case STATE_VAR: // Variable name is read - expecting a separator 
		{
			int ch = readCharForward();
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
			int ch = readCharForward();
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
			int ch = readCharForward();
			if (ch == -1) {
				return ELOperandToken.EOF;
			}
			if (Character.isJavaIdentifierStart((char)ch)) {
				releaseChar();
				return readVarOrMethodToken();
			}
			releaseChar();
			return ELOperandToken.EOF;
		}
		}
		return ELOperandToken.EOF;
	}

	/* Reads the next character in the document
	 * 
	 * @return
	 */
	int readCharForward() {
		if (index >= documentContent.length() || 
				documentContent == null ||
				index < 0)
			return -1;

		try {
			int ch = documentContent.charAt(index);
			index++;
			return ch;
		} catch (StringIndexOutOfBoundsException e) {
			return -1;
		}
	}

	/* Reads and returns the variable token from the expression
	 * Forward parsing
	 * 
	 * @return
	 */
	private ELOperandToken readVarOrMethodToken() {
		fState = STATE_VAR;
		int startOfToken = index;
		int ch;
		while((ch = readCharForward()) != -1) {
			if (!Character.isJavaIdentifierPart(ch)) {
				int marker = index - 1; // save the current index (end of var name)
				// skip spaces between the method's name and it's parameters
				if (Character.isSpaceChar(ch)) {
					if (!skipSpaceChars()) {
						// EOF - return the var
						index = marker; // restore the end of var name in the current index
						return (index - startOfToken> 0 ? new ELOperandToken(startOfToken, index - startOfToken, getCharSequence(startOfToken, index - startOfToken), ELOperandToken.EL_PROPERTY_NAME_TOKEN) : ELOperandToken.EOF);
					}
				}
				if (ch == -1) {
					// EOF - return the var
					index = marker; // restore the end of var name in the current index
					return (index - startOfToken> 0 ? new ELOperandToken(startOfToken, index - startOfToken, getCharSequence(startOfToken, index - startOfToken), ELOperandToken.EL_PROPERTY_NAME_TOKEN) : ELOperandToken.EOF);
				}

				if (ch != '(') {
					// not a method - return the var
					index = marker; // restore the end of var name in the current index
					return (index - startOfToken> 0 ? new ELOperandToken(startOfToken, index - startOfToken, getCharSequence(startOfToken, index - startOfToken), ELOperandToken.EL_PROPERTY_NAME_TOKEN) : ELOperandToken.EOF);
				}
				releaseChar();
				fState = STATE_METHOD;
					
				// read the method parameters
				skipMethodParameters(); // Do not use the return value (not a matter)
				// broken/unfinished a method - return part of the method
				releaseChar();
				return (index - startOfToken> 0 ? new ELOperandToken(startOfToken, index - startOfToken, getCharSequence(startOfToken, index - startOfToken), ELOperandToken.EL_METHOD_TOKEN) : ELOperandToken.EOF);
			}
		}
		return (index - startOfToken > 0 ? new ELOperandToken(startOfToken, index - startOfToken, getCharSequence(startOfToken, index - startOfToken), ELOperandToken.EL_PROPERTY_NAME_TOKEN) : ELOperandToken.EOF);
	}

	/* Reads and returns the separator token from the expression
	 * Forward parsing
	 * 
	 * @return
	 */
	private ELOperandToken readSeparatorToken() {
		fState = STATE_SEPARATOR;
		int ch = readCharForward();
		
		return (ch == '.' ? new ELOperandToken(index - 1, 1, getCharSequence(index - 1, 1), ELOperandToken.EL_SEPARATOR_TOKEN) :
			ELOperandToken.EOF);
	}


	/*
	 * Skips the space characters in the document
	 */
	private boolean skipSpaceChars() {
		int ch;
		while ((ch = readCharForward()) != -1) {
			if (!Character.isSpaceChar(ch)) {
				releaseChar();
				break;
			}
		}
		return true;
	}

	/* 
	 * Skips the method parameters characters in the document
	 * Forward parsing
	 * 
	 * @return boolean true if complete parameters set had been read
	 */
	private boolean skipMethodParameters() {
		int ch = readCharForward(); 
		if (ch != '(')
			return false;
		int pCount = 1;
		while (pCount > 0) {
			ch = readCharForward();
			if (ch == -1)
				return false;
			
			if (ch == '"' || ch == '\'') {
				skipQuotedChars((char)ch);
				continue;
			}
			if (ch == '(') {
				pCount++;
				continue;
			}
			if (ch == ')') {
				pCount--;
				continue;
			}
		}
		return true;
	}

	/* 
	 * Skips the quoted characters 
	 * Forward parsing
	 * 
	 */
	private void skipQuotedChars(char pair) {
		int ch = readCharForward();
		
		while (ch != -1) {
			if (ch == pair)
				return;

			if (ch == '\\') {
				int backSlashCount = 0;
				while (ch == '\\') {
					backSlashCount++;
					ch = readCharForward();
				}
				releaseChar(); // Return the last non-slash char to the buffer
				if ((backSlashCount/2)*2 == backSlashCount) {
					return;
				}
			}
			ch = readCharForward();
		}
	}

	/* 
	 * returns the character to the document
	 */
	private void releaseChar() {
		if (index > 0)
			index--;
	}
}
