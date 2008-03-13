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
 * Token of the string with EL expression.
 * @author Alexey Kazakov
 */
public class ELStringToken implements IToken {
	public static final ELStringToken EOF = new ELStringToken(-1, -1, null);

	private int start;
	private int length;
	private CharSequence chars;
	private String body;

	/**
	 * Constructs the ELStringToken object
	 *
	 * @param start
	 * @param length
	 * @param chars
	 */
	public ELStringToken(int start, int length, CharSequence chars) {
		this.start = start;
		this.length = length;
		this.chars = chars;
	}

	/**
	 * Returns string representation for the token
	 */
	public String toString() {
		return "ELStringToken(" + start + ", " + length + ") [" + (chars == null ? "<Empty>" : chars.toString()) + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
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
	 * @param start
	 */
	public void setStart(int start) {
		this.start = start;
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
	 * Returns the token text
	 */
	public String getText() {
		return chars.toString();
	}

	/*
	 * Returns text of EL without brackets
	 */
	public String getBody() {
		if(chars.length()<4) {
			return "";
		}
		if(body==null) {
			body = chars.subSequence(2, chars.length()-1).toString();
		}
		return body;
	}
}