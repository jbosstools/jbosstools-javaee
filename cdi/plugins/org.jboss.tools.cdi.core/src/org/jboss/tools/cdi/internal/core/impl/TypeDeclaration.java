/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.ITypeDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TypeDeclaration extends ParametedType implements ITypeDeclaration {
	int length;
	int startPosition;

	public TypeDeclaration() {}

	TypeDeclaration(IType type, int startPosition, int length) {
		this.type = type;
		this.length = length;
		this.startPosition = startPosition;
	}

	public int getLength() {
		return length;
	}

	public int getStartPosition() {
		return startPosition;
	}
}
