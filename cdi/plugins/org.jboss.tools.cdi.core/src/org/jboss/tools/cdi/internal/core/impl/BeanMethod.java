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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
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
