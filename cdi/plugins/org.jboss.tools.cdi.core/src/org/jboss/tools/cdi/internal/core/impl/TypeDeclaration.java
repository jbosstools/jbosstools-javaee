/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.ITypeDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TypeDeclaration extends ParametedType implements ITypeDeclaration {
	int length;
	int startPosition;

	public TypeDeclaration(ParametedType type, int startPosition, int length) {
		this.setFactory(type.getFactory());
		this.type = type.getType();
		arrayPrefix = type.arrayPrefix;
		this.length = length;
		this.startPosition = startPosition;

		signature = type.signature;
		parameterTypes = type.parameterTypes;

		allInheritedTypes = type.allInheritedTypes;
		inheritanceIsBuilt = type.inheritanceIsBuilt;
		inheritedTypes = type.inheritedTypes;
		superType = type.superType;
		primitive = type.primitive;
		
		isLower = type.isLower;
		isUpper = type.isUpper;
		isVariable = type.isVariable;
	}

	public int getLength() {
		return length;
	}

	public int getStartPosition() {
		return startPosition;
	}
}
