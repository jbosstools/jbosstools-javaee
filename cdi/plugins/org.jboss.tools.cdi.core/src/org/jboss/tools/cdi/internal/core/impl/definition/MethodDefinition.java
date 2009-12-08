package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

public class MethodDefinition extends AbstractMemberDefinition {
	IMethod method;
	boolean isConstructor;

	public MethodDefinition() {}

	public void setMethod(IMethod method, DefinitionContext context) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isConstructor() {
		return isConstructor();
	}

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		super.init(contextType, context);
		isConstructor = method.isConstructor();
		//TODO process parameters for disposers and observers
		
	}

	public boolean isCDIAnnotated() {
		//TODO return true if it is disposer or observer
		return super.isCDIAnnotated();
	}

}
