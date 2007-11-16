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

import org.eclipse.jface.text.rules.IToken;

/**
 * Token for the EL expression parts
 * @author Alexey Kazakov
 */
public class ELToken implements IToken {
	public static final ELToken EOF = new ELToken(-1, -1, null, -1);
	public static final int EL_VARIABLE_TOKEN = 1;
	public static final int EL_OPERATOR_TOKEN = 2;
	public static final int EL_RESERVED_WORD_TOKEN = 3;
	public static final int EL_SEPARATOR_TOKEN = 4;
	public static final int EL_STRING_TOKEN = 5;
	public static final int EL_NUMBER_TOKEN = 6;

	private int start;
	private int length;
	private CharSequence chars;
	private int type;

	/**
	 * Constructs the ELToken object
	 *
	 * @param start
	 * @param length
	 * @param chars
	 * @param type
	 */
	public ELToken(int start, int length, CharSequence chars, int type) {
		this.start = start;
		this.length = length;
		this.chars = chars;
		this.type = type;
	}

	/**
	 * Returns string representation for the token
	 */
	public String toString() {
		return "ELToken(" + start + ", " + length + ", " + type + ") [" + (chars == null ? "<Empty>" : chars.toString()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#getData()
	 */
	public Object getData() {
//		return (chars == null ? null : chars.subSequence(start, start+length).toString());
		return getText();
	}

	/**
	 * @return offset of token
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return length of token
	 */
	public int getLength() {
		return length;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isEOF()
	 */
	public boolean isEOF() {
		return (start == -1 && length == -1 && chars == null);
	}
	
	/*
	 * @see org.eclipse.jface.text.rules.IToken#isOther()
	 */
	public boolean isOther() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isUndefined()
	 */
	public boolean isUndefined() {
		return false;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IToken#isWhitespace()
	 */
	public boolean isWhitespace() {
		return false;
	}

	/*
	 * Returns the token type
	 */
	public int getType(){
		return type;
	}

	/*
	 * Returns the token text
	 */
	public String getText() {
		return chars.toString();
	}
}