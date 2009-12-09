package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.ITypeDeclaration;

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
