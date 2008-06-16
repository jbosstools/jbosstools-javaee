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
 * Parses EL and creates list of tokens for the operands and operators within one EL.
 * @author Alexey Kazakov
 */
public class SeamELTokenizer {
	private static final int STATE_INITIAL = 0;
	private static final int STATE_OPERAND = 1;
	private static final int STATE_OPERATOR = 2;
	private static final int STATE_RESERVED_WORD = 3;
	private static final int STATE_SEPARATOR = 4;
	private static final int STATE_STRING = 5;

	private String expression;
	private List<ELToken> fTokens;
	private int index;
	private int offset = 0;

	private int fState;
	private static final String OPERATOR_SYMBOLS = "!=&(){}[]:+-*%?,|/%<>";
	private static final int START_ROUND_BRACKET_SYMBOL = '(';
	private static final int END_ROUND_BRACKET_SYMBOL = ')';
	private static final int START_SQUARE_BRACKET_SYMBOL = '[';
	private static final int END_SQUARE_BRACKET_SYMBOL = ']';
	private static final int STRING_SYMBOL = '\'';
	private static final String RESERVED_WORDS = " null empty div and or not mod eq ne lt gt le ge true false instanceof invalid required ";

	/**
	 * Constructs SeamELTokenizer object.
	 * Parse an expression.
	 * For example: expression is "var1.pr != var2.pr"
	 *              then tokens are ["var1.pr"," ", "!=", " ", "var2.pr"]
	 * @param expression
	 */
	public SeamELTokenizer(String expression) {
		this(expression, 0);
	}

	private SeamELTokenizer(String expression, int offset) {
		this.offset = offset;
		this.expression = expression;
		index = 0;
		fTokens = new ArrayList<ELToken>();
		parse();
	}

	/**
	 * Returns list of tokens for the parsed expression
	 * 
	 * @return
	 */
	public List<ELToken> getTokens() {
		return fTokens;
	}

	/*
	 * Performs parsing of expression
	 */
	private void parse() {
		ELToken token;
		fState = STATE_INITIAL;
		while ((token = getNextToken()) != ELToken.EOF) {
			fTokens.add(token);
		}
	}

	/*
	 * Calculates and returns next token of expression
	 * 
	 * @return
	 */
	private ELToken getNextToken() {
		switch (fState) {
			case STATE_INITIAL: // Just started
			case STATE_STRING: { // String is read
				int ch = readNextChar();
				if (ch == -1) {
					return ELToken.EOF;
				}
				releaseChar();
				if (Character.isJavaIdentifierPart((char)ch)) {
					return readOperandOrReservedWordToken();
				}
				if (OPERATOR_SYMBOLS.indexOf(ch)>-1) {
					return readOperatorToken();
				}
				if (ch == STRING_SYMBOL) {
					return readStringToken();
				}
				if (ch == ' ') {
					return readSeparatorToken();
				}
				return ELToken.EOF;
			}
			case STATE_RESERVED_WORD: // Reserved word is read - expecting a separator or operator or string
			case STATE_OPERAND: { // Operand is read - expecting a separator or operator or string 
				int ch = readNextChar();
				if (ch == -1) {
					return ELToken.EOF;
				}
				releaseChar();
				if (OPERATOR_SYMBOLS.indexOf(ch)>-1) {
					return readOperatorToken();
				}
				if (ch == STRING_SYMBOL) {
					return readStringToken();
				}
				if (ch == ' ') {
					return readSeparatorToken();
				}
				return ELToken.EOF;
			}
			case STATE_OPERATOR: { // Operator is read - expecting a separator or operand or string
				int ch = readNextChar();
				if (ch == -1) {
					return ELToken.EOF;
				}
				releaseChar();
				if (Character.isJavaIdentifierPart((char)ch)) {
					return readOperandOrReservedWordToken();
				}
				if (ch == STRING_SYMBOL) {
					return readStringToken();
				}
				if (ch == ' ') {
					return readSeparatorToken();
				}
				return ELToken.EOF;
			}
			case STATE_SEPARATOR: { // Separator is read - expecting a operand or operator or string
				int ch = readNextChar();
				if (ch == -1) {
					return ELToken.EOF;
				}
				releaseChar();
				if (Character.isJavaIdentifierPart((char)ch)) {
					return readOperandOrReservedWordToken();
				}
				if (OPERATOR_SYMBOLS.indexOf(ch)>-1) {
					return readOperatorToken();
				}
				if (ch == STRING_SYMBOL) {
					return readStringToken();
				}
				releaseChar();
				return ELToken.EOF;
			}
		}
		return ELToken.EOF;
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
			text = expression.substring(start, start + length);
		} catch (StringIndexOutOfBoundsException e) {
			SeamCorePlugin.getDefault().logError(e);
			text = ""; // For sure //$NON-NLS-1$
		}
		return text.subSequence(0, text.length());
	}

	/*
	 * Reads and returns the operator token from the expression
	 * @return
	 */
	private ELToken readOperatorToken() {
		fState = STATE_OPERATOR;
		int startOfToken = index;
		int ch;
		while((ch = readNextChar()) != -1) {
			if (OPERATOR_SYMBOLS.indexOf(ch)==-1) {
				break;
			}
		}
		releaseChar();
		int length = index - startOfToken;
		return (length > 0 ? new ELToken(offset + startOfToken, length, getCharSequence(startOfToken, length), ELToken.EL_OPERATOR_TOKEN) : ELToken.EOF);
	}

	/* 
	 * Reads and returns the separator token from the expression
	 * @return
	 */
	private ELToken readSeparatorToken() {
		fState = STATE_SEPARATOR;
		int startOfToken = index;
		int ch;
		while((ch = readNextChar()) != -1) {
			if (ch!=' ') {
				break;
			}
		}
		releaseChar();
		int length = index - startOfToken;
		return (length > 0 ? new ELToken(offset + startOfToken, length, getCharSequence(startOfToken, length), ELToken.EL_SEPARATOR_TOKEN) : ELToken.EOF);
	}

	/*
	 * Reads and returns the string token from the expression
	 * @return
	 */
	private ELToken readStringToken() {
		fState = STATE_STRING;
		int ch = readNextChar(); // skip first '
		int startOfToken = index;
		while((ch = readNextChar()) != -1) {
			if (ch==STRING_SYMBOL) {
				break;
			}
		}
		releaseChar();
		int length = index - startOfToken;

		return (length > 0 ? new ELToken(offset + startOfToken, length, getCharSequence(startOfToken, length), ELToken.EL_STRING_TOKEN) : ELToken.EOF);
	}

	/*
	 * Reads and returns the operand token from the expression
	 * @return
	 */
	private ELToken readOperandOrReservedWordToken() {
		fState = STATE_OPERAND;
		int startOfToken = index;
		int ch;
		while((ch = readNextChar()) != -1) {
			if (ch == START_ROUND_BRACKET_SYMBOL) {
				ch = readTokensWithinBrackets(END_ROUND_BRACKET_SYMBOL);
			} else if (ch == START_SQUARE_BRACKET_SYMBOL) {
				ch = readTokensWithinBrackets(END_SQUARE_BRACKET_SYMBOL);
			}
			if (!Character.isJavaIdentifierPart(ch) && ch!='.') {
				break;
			}
		}
		releaseChar();
		int length = index - startOfToken;
		boolean reservedWord = isResorvedWord(startOfToken, length);
		int tokenType = ELToken.EL_VARIABLE_TOKEN;
		if(reservedWord) {
			tokenType = ELToken.EL_RESERVED_WORD_TOKEN;
			fState = STATE_RESERVED_WORD;
		} else if(isNumber(startOfToken, length)) {
			tokenType = ELToken.EL_NUMBER_TOKEN;
		}

		return (length > 0 ? new ELToken(offset + startOfToken, length, getCharSequence(startOfToken, length), tokenType) : ELToken.EOF);
	}

	private int readTokensWithinBrackets(int expectedEndBracketSymbol) {
		int start = index;
		int ch;
		while((ch = readNextChar()) != -1) {
			if (ch == expectedEndBracketSymbol) {
				CharSequence text = getCharSequence(start, index - 1 - start);
				SeamELTokenizer tokenizer = new SeamELTokenizer(text.toString(), start);
				fTokens.addAll(tokenizer.getTokens());
				return readNextChar();
			}
		}
		return ch;
	}

	private boolean isResorvedWord(String word) {
		return RESERVED_WORDS.indexOf(" " + word.trim() + " ")>-1;
	}

	private boolean isNumber(String word) {
		if(word.length()>0) {
			char firstChar = word.charAt(0);
			if(firstChar=='-' || (firstChar>='0' && firstChar<='9')) {
				try {
					Long.parseLong(word);
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}

	private boolean isResorvedWord(int beginIndex, int length) {
		String word = expression.substring(beginIndex, beginIndex + length);
		return isResorvedWord(word);
	}

	private boolean isNumber(int beginIndex, int length) {
		String word = expression.substring(beginIndex, beginIndex + length);
		return isNumber(word);
	}

	/* Reads the next character
	 * @return
	 */
	private int readNextChar() {
		int c = -1;
		try {
			if (index < expression.length()) {
				c = expression.charAt(index);
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