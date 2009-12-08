package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;

public class BeanMethod extends BeanMember implements IBeanMethod {
	protected MethodDefinition definition;
	protected IMethod method;

	public BeanMethod() {}

	public void setDefinition(MethodDefinition definition) {
		this.definition = definition;
		setMethod(definition.getMethod());
		setAnnotations(definition.getAnnotations());
	}

	public IMethod getMethod() {
		return method;
	}

	public void setMethod(IMethod method) {
		this.method = method;
		setMember(method);
	}

	public IMember getSourceMember() {
		return getMethod();
	}

	public List<IParameter> getParameters() {
		// TODO 
		return new ArrayList<IParameter>();
	}

}
