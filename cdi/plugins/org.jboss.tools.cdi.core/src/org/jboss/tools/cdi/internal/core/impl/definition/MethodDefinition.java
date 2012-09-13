/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl.definition;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ClassBean;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MethodDefinition extends BeanMemberDefinition {
	IMethod method;
	boolean isConstructor;

	List<ParameterDefinition> parameters = new ArrayList<ParameterDefinition>();

	public MethodDefinition() {}

	public void setMethod(IMethod method, IRootDefinitionContext context, int flags) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context, flags);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isConstructor() {
		return isConstructor;
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		super.init(contextType, context, flags);
		isConstructor = method.isConstructor();
		loadParamDefinitions(contextType, context, flags);
	}

	public boolean parametersAreInjectionPoints() {
		return getProducesAnnotation() != null || getInjectAnnotation() != null;
	}

	void loadParamDefinitions(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		if(method == null) return;
		ILocalVariable[] ps = method.getParameters();
		
		boolean loadAll = (flags & FLAG_ALL_MEMBERS) > 0;
		boolean parametersAreInjectionPoints = parametersAreInjectionPoints();

		if(ps.length == 0) return;
		if(contextType == null) return;

		Set<IProcessAnnotatedMemberFeature> extensions = context.getProject().getExtensionManager().getProcessAnnotatedMemberFeatures();

		ParameterDefinition[] ds = new ParameterDefinition[ps.length];
		for (int i = 0; i < ps.length; i++) {
			ParameterDefinition pd = new ParameterDefinition();
			pd.setMethodDefinition(this);
			pd.index = i;
			pd.setLocalVariable(ps[i], context, flags);		
			for (IProcessAnnotatedMemberFeature e: extensions) {
				e.processAnnotatedMember(pd, context);
			}
			if(pd.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)
				|| pd.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) {
				parametersAreInjectionPoints = true;
			}
			ds[i] = pd;
		}
		for (int i = 0; i < ps.length; i++) {
			if(!loadAll && !parametersAreInjectionPoints && ds[i].getAnnotations().isEmpty()) {
				continue; //do not need parameters without annotation
			}
			parameters.add(ds[i]);
		}
	}

	@Override
	public boolean isCDIAnnotated() {
		return super.isCDIAnnotated() || isDisposer() || isObserver() || getPreDestroyMethod() != null || getPostConstructorMethod() != null || !getInterceptorBindings().isEmpty() || hasStereotypeDeclarations();
	}

	public Collection<IInterceptorBinding> getInterceptorBindings() {
		Collection<IInterceptorBinding> result = new ArrayList<IInterceptorBinding>();
		for (IInterceptorBindingDeclaration declaration: ClassBean.getInterceptorBindingDeclarations(this)) {
			result.add(declaration.getInterceptorBinding());
		}
		return result;
	}

	public boolean hasStereotypeDeclarations() {
		List<IAnnotationDeclaration> as = getAnnotations();
		for (IAnnotationDeclaration a: as) {
			if(a instanceof IStereotypeDeclaration) {
				return true;
			}
		}
		return false;
	}
 
	public List<ParameterDefinition> getParameters() {
		return parameters;
	}

	public boolean isDisposer() {
		for (ParameterDefinition p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}

	public boolean isObserver() {
		for (ParameterDefinition p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) return true;
		}
		return false;
	}

	public AnnotationDeclaration getPreDestroyMethod() {
		return getAnnotation(CDIConstants.PRE_DESTROY_TYPE_NAME);
	}

	public AnnotationDeclaration getPostConstructorMethod() {
		return getAnnotation(CDIConstants.POST_CONSTRUCTOR_TYPE_NAME);
	}

}
