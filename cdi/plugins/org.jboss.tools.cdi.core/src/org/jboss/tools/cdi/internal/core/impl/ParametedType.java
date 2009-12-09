package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IParametedType;

public class ParametedType implements IParametedType {
	protected IType type;
	protected String signature;

	public ParametedType() {}

	public IType getType() {
		return type;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
