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

public class Token {
	Token previous;
	Token parent;
	Token firstChild;
	Token prevSibling;
	Token nextSibling;
	Properties attributes;
	
	int kind;
	int indentLevel;
	String name;
	int indentLength = -1;
	int off;
	int length;
	
	String indent = null;

	public Token(int kind, String name, int off, int length, Token previous) {
		this.previous = previous;
		this.kind = kind;
		this.name = name;
		this.off = off;
		this.length = length; 
	}
	
	public String toString() {
		String s = "k=" + kind + " iL=" + indentLevel + " ind=" + indentLength + " n=" + name + " off=" + off + " l=" + length;
		if(attributes != null) {
			Enumeration it = attributes.keys();
			if(it.hasMoreElements()) s += "attributes: ";
			while(it.hasMoreElements()) {
				String n = it.nextElement().toString();
				String v = attributes.getProperty(n);
				s += n + "=" + v + " ";
			}
		}
		return s;
	}
	
	public void addChild(Token t) {
		t.parent = this;
		t.indentLevel = indentLevel + 1;
		if(firstChild == null) {
			firstChild = t;
		} else {
			firstChild.addNextSibling(t);
		}
	}
	
	public void addNextSibling(Token t) {
		if(nextSibling == null) {
			nextSibling = t;
			t.prevSibling = this;
		} else {
			nextSibling.addNextSibling(t);
		}
	}
	
}
