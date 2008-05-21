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

import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * Parses string and creates list of tokens for each EL of this string.
 * @author Alexey Kazakov
 */
public class SeamELStringTokenizer {

	private String sourceString;
	private List<ELStringToken> fTokens;
	private int index;
	private int offset = 0;

	private static final int START_EL_FIRST_SYMBOL = '#';
	private static final int START_EL_LAST_SYMBOL = '{';
	private static final int END_EL_SYMBOL = '}';

	/**
	 * Constructs SeamELStringTokenizer object.
	 * Parse string and get all EL from it.
	 * For example: string is '#{var1.pr != var2.pr} #{f1.pr1}'
	 *              then tokens are ["#{var1.pr != var2.pr}","#{f1.pr1}"]
	 * @param sourceString
	 */
	public SeamELStringTokenizer(String sourceString) {
		this(sourceString, 0);
	}

	private SeamELStringTokenizer(String sourceString, int offset) {
		this.offset = offset;
		this.sourceString = sourceString;
		index = 0;
		fTokens = new ArrayList<ELStringToken>();
		parse();
	}

	/**
	 * Returns list of tokens for the parsed string
	 * 
	 * @return
	 */
	public List<ELStringToken> getTokens() {
		return fTokens;
	}

	/*
	 * Performs parsing of string
	 */
	private void parse() {
		ELStringToken token;
		while ((token = getNextToken()) != ELStringToken.EOF) {
			fTokens.add(token);
		}
	}

	/*
	 * Calculates and returns next token of string
	 * 
	 * @return
	 */
	private ELStringToken getNextToken() {
		int ch = readNextChar();
		while(ch!=-1) {
			int secondCh = readNextChar();
			if (secondCh == -1) {
				return ELStringToken.EOF;
			}
			releaseChar();

			if(ch == START_EL_FIRST_SYMBOL && secondCh == START_EL_LAST_SYMBOL) {
				releaseChar();
				return readELToken();
			}
			ch = readNextChar();
		}
		return ELStringToken.EOF;
	}

	/*
	 * Returns the CharSequence object
	 *  
	 * @param start
	 * @param length
	 * @return
	 */
	private CharSequence getCharSequence(int start, int length) {
		String text = ""; //$NON-NLS-1$
		try {
			text = sourceString.substring(start, start + length);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return text.subSequence(0, text.length());
	}
	/*
	 * Reads and returns the string token from the expression
	 * @return
	 */
	private ELStringToken readELToken() {
		int startOfToken = index;
		readNextChar();
		readNextChar();
		int ch;
		while((ch = readNextChar()) != -1) {
			if (ch==END_EL_SYMBOL) {
				int length = index - startOfToken;
				return new ELStringToken(offset + startOfToken, length, getCharSequence(startOfToken, length));
			}
		}
		return ELStringToken.EOF;
	}

	/* Reads the next character
	 * @return
	 */
	private int readNextChar() {
		int c = -1;
		try {
			if (index < sourceString.length()) {
				c = sourceString.charAt(index);
			}
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		index++;
		return c;
	}

	/* 
	 * returns the character to the document
	 */
	private void releaseChar() {
		if (index > 0) {
			index--;
		}
	}
}