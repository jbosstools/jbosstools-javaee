/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceReference;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;

public class Parameter extends BeanMember implements IParameter {
	BeanMethod beanMethod;

	public Parameter() {}

	@Override
	public ParameterDefinition getDefinition() {
		return (ParameterDefinition)definition;
	}

	public void setBeanMethod(BeanMethod beanMethod) {
		this.beanMethod = beanMethod;
		setParent(beanMethod);
	}

	public void setDefinition(ParameterDefinition definition) {
		super.setDefinition(definition);
		this.definition = definition;
	}

	public void setLocalVariable(ILocalVariable v) {
		setMember(v);
	}

	public String getName() {
		return getDefinition().getName();
	}

	@Override
	public IParametedType getMemberType() {
		return getDefinition().getType();
	}

	@Override
	public IParametedType getType() {
		if(getDefinition().getOverridenType() != null) {
			return getDefinition().getOverridenType();
		}
		return getDefinition().getType();
	}

	@Override
	public IClassBean getClassBean() {
		return beanMethod.getClassBean();
	}

	public IMember getSourceMember() {
		return getDefinition().getMethodDefinition().getMethod();
	}

	@Override
	public IJavaElement getSourceElement() {
		return getDefinition().getVariable();
	}

	@Override
	protected ISourceReference getSourceReference() {
		return getDefinition().getVariable();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IParameter#getBeanMethod()
	 */
	public IBeanMethod getBeanMethod() {
		return beanMethod;
	}

	public Collection<IQualifier> getQualifiers() {
		Collection<IQualifier> result = new ArrayList<IQualifier>();
		for (IAnnotationDeclaration s: getAnnotations()) {
			if(s instanceof IQualifierDeclaration) {
				IQualifier q = ((IQualifierDeclaration)s).getQualifier();
				if (q != null) result.add(q);
			}
		}
		return result;
	}

	public Collection<IQualifierDeclaration> getQualifierDeclarations() {
		Collection<IQualifierDeclaration> result = new ArrayList<IQualifierDeclaration>();
		
		List<IAnnotationDeclaration> ds = definition.getAnnotations();
		for (IAnnotationDeclaration d: ds) {
			if(d instanceof IQualifierDeclaration) {
				result.add((IQualifierDeclaration)d);
			}
		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIElement#getSimpleJavaName()
	 */
	@Override
	public String getElementName() {
		return getName();
	}

	@Override
	public boolean isDeclaredFor(IJavaElement element) {
		if(getDefinition().getVariable() == element) {
			return true;
		}
		if(element instanceof ILocalVariable) {
			ILocalVariable vThat = (ILocalVariable)element;
			return getName().equals(vThat.getElementName()) && getBeanMethod().isDeclaredFor(vThat.getDeclaringMember());
		}
		return false;
	}
}