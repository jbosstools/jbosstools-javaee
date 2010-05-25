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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeanMethod extends BeanMember implements IBeanMethod {
	protected IMethod method;
	protected AnnotationDeclaration inject;

	List<IParameter> parameters = new ArrayList<IParameter>();

	public BeanMethod() {}

	public void setDefinition(MethodDefinition definition) {
		super.setDefinition(definition);
		setMethod(definition.getMethod());
		inject = definition.getInjectAnnotation();
		
		List<ParameterDefinition> ps = definition.getParameters();
		for (ParameterDefinition p: ps) {
			Parameter parameter = newParameter();
			parameter.setBeanMethod(this);
			parameter.setDefinition(p);
			parameters.add(parameter);
		}
	}

	protected Parameter newParameter() {
		return ((MethodDefinition)definition).parametersAreInjectionPoints() ? new InjectionPointParameter() : new Parameter();
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
		return parameters;
	}

	public MethodDefinition getDefinition() {
		return (MethodDefinition)definition;
	}

	public boolean isDisposer() {
		for (IParameter p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}

	public boolean isObserver() {
		for (IParameter p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}

}
