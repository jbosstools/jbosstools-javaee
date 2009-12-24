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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class StereotypeElement extends CDIAnnotationElement implements IStereotype {

	public StereotypeElement() {}

	public AnnotationDeclaration getAlternativeDeclaration() {
		return definition.getAlternativeAnnotation();
	}

	public AnnotationDeclaration getNameDeclaration() {
		return definition.getNamedAnnotation();
	}

	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations() {
		Set<IInterceptorBindingDeclaration> result = new HashSet<IInterceptorBindingDeclaration>();
		List<AnnotationDeclaration> as = definition.getAnnotations();
		for (AnnotationDeclaration a: as) {
			if(a instanceof InterceptorBindingDeclaration) {
				result.add((InterceptorBindingDeclaration)a);
			}
		}
		return result;
	}

	public IAnnotation getNameLocation() {
		return getNameDeclaration() != null ? getNameDeclaration().getDeclaration() : null;
	}

	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		for (AnnotationDeclaration d: definition.getAnnotations()) {
			if(d instanceof IStereotypeDeclaration) {
				result.add((IStereotypeDeclaration)d);
			}
		}
		return result;
	}

	public boolean isAlternative() {
		if(getAlternativeDeclaration() != null) return true;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

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

	public Set<IScopeDeclaration> getScopeDeclarations() {
		return ProducerField.getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

}
