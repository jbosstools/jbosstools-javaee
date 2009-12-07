package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

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

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		super.init(contextType, context);
		//TODO process parameters
		
	}
}
