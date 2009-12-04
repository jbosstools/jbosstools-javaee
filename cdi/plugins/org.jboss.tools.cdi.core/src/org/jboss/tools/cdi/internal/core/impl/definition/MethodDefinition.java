package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.jdt.core.IMethod;

public class MethodDefinition extends AbstractMemberDefinition {
	IMethod method;

	public MethodDefinition() {}

	public void setMethod(IMethod method, DefinitionContext context) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context);
	}

	public IMethod getMethod() {
		return method;
	}

}
