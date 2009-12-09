package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;

public class BeanMethod extends BeanMember implements IBeanMethod {
	protected IMethod method;
	protected AnnotationDeclaration inject;

	public BeanMethod() {}

	public void setDefinition(MethodDefinition definition) {
		super.setDefinition(definition);
		setMethod(definition.getMethod());
		inject = definition.getInjectAnnotation();
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

	public MethodDefinition getDefinition() {
		return (MethodDefinition)definition;
	}
}
