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
package org.jboss.tools.cdi.internal.core.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class StereotypeElement extends CDIAnnotationElement implements IStereotype {

	public StereotypeElement() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IStereotype#getAlternativeDeclaration()
	 */
	public AnnotationDeclaration getAlternativeDeclaration() {
		return definition.getAlternativeAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDINamedAnnotation#getNameDeclaration()
	 */
	public AnnotationDeclaration getNameDeclaration() {
		return definition.getNamedAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInterceptorBinded#getInterceptorBindingDeclarations()
	 */
	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations(boolean includeInherited) {
		return ClassBean.getInterceptorBindingDeclarations(definition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInterceptorBinded#getInterceptorBindings()
	 */
	public Set<IInterceptorBinding> getInterceptorBindings() {
		return CDIUtil.getAllInterceptorBindings(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IStereotype#getStereotypeDeclarations()
	 */
	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		for (IAnnotationDeclaration d: definition.getAnnotations()) {
			if(d instanceof IStereotypeDeclaration) {
				result.add((IStereotypeDeclaration)d);
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IStereotype#isAlternative()
	 */
	public boolean isAlternative() {
		if(getAlternativeDeclaration() != null) return true;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IScoped#getScope()
	 */
	public IScope getScope() {
		Set<IScopeDeclaration> ss = getScopeDeclarations();
		if(!ss.isEmpty()) {
			return ss.iterator().next().getScope();
		}
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			IScope result = s.getScope();
			if(result != null) {
				return result;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IScoped#getScopeDeclarations()
	 */
	public Set<IScopeDeclaration> getScopeDeclarations() {
		return ProducerField.getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotations()
	 */
	public List<IAnnotationDeclaration> getAnnotations() {
		if(definition!=null) {
			return definition.getAnnotations();
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotation(java.lang.String)
	 */
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		if(definition!=null) {
			return definition.getAnnotation(annotationTypeName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotationPosition(java.lang.String)
	 */
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#isAnnotationPresent(java.lang.String)
	 */
	public boolean isAnnotationPresent(String annotationTypeName) {
		return definition!=null && definition.isAnnotationPresent(annotationTypeName);
	}
}