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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class StereotypeElement extends CDIElement implements IStereotype {
	AnnotationDefinition definition;

	public StereotypeElement() {}

	public void setDefinition(AnnotationDefinition definition) {
		this.definition = definition;
	}
	
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

	public IType getSourceType() {
		return definition.getType();
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

	public IType getScope() {
		Set<IAnnotationDeclaration> ss = getScopeDeclarations();
		if(!ss.isEmpty()) {
			return ss.iterator().next().getType();
		}
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			//TODO
		}
		return null;
	}

	public Set<IAnnotationDeclaration> getScopeDeclarations() {
		return ProducerField.getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

}
