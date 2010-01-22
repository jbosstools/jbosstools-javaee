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
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.common.model.project.ext.impl.ValueInfo;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class ProducerMethod extends BeanMethod implements IProducerMethod {
	protected AnnotationDeclaration produces;

	ProducerMethod specialized = null;
	Set<ProducerMethod> specializingProducerMethods = new HashSet<ProducerMethod>();

	public ProducerMethod() {}

	public void setDefinition(MethodDefinition definition) {
		super.setDefinition(definition);
		produces = definition.getProducesAnnotation();
	}

	public Set<ITypeDeclaration> getAllTypeDeclarations() {
		Set<ITypeDeclaration> result = new HashSet<ITypeDeclaration>();
		if(typeDeclaration != null) {
			result.add(typeDeclaration);
		}
		return result;
	}

	public IAnnotationDeclaration getAlternativeDeclaration() {
		return getDefinition().getAlternativeAnnotation();
	}

	public IType getBeanClass() {
		return typeDeclaration != null ? typeDeclaration.getType() : null;
	}

	public Set<IInjectionPoint> getInjectionPoints() {
		return new HashSet<IInjectionPoint>();
	}

	public Set<IParametedType> getLegalTypes() {
		Set<IParametedType> result = new HashSet<IParametedType>();
		if(typeDeclaration != null) result.add(typeDeclaration);
		return result;
	}

	public String getName() {
		ProducerMethod specialized = getSpecializedBean();
		if(specialized != null) {
			return specialized.getName();
		}
		AnnotationDeclaration named = findNamedAnnotation();
		if(named == null) return null;

		String name = getMethod().getElementName();

		IAnnotation a = named.getDeclaration();
		try {
			IMemberValuePair[] vs = a.getMemberValuePairs();
			if(vs == null || vs.length == 0) {
				if(name.startsWith("get") && name.length() > 3) {
					return name.substring(3, 4).toLowerCase() + name.substring(4);
				} else if(name.startsWith("is") && name.length() > 2) {
					return name.substring(2, 3).toLowerCase() + name.substring(3);
				}
			} else {
				Object value = vs[0].getValue();
				if(value != null && value.toString().trim().length() > 0) {
					return value.toString().trim();
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return name;
	}

	public ITextSourceReference getNameLocation() {
		AnnotationDeclaration named = findNamedAnnotation();
		if(named != null) {
			return ValueInfo.getValueInfo(named.getDeclaration(), null);
		}
		return null;
	}

	public void setSpecializedBean(ProducerMethod other) {
		specialized = other;
		if(other != null) {
			other.specializingProducerMethods.add(this);
		}
	}

	public ProducerMethod getSpecializedBean() {
		if(getDefinition().getSpecializesAnnotation() == null) {
			return null;
		}
		return specialized;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return getDefinition().getSpecializesAnnotation();
	}

	public boolean isDependent() {
		IScope scope = getScope();
		return scope != null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(scope.getSourceType().getFullyQualifiedName());
	}

	boolean hasEnabledSpecializingProducerMethod() {
		for (ProducerMethod sb: specializingProducerMethods) {
			if(sb.hasEnabledSpecializingProducerMethod() || sb.isEnabled()) return true;
		}
		return false;
	}

	public boolean isEnabled() {
		if(classBean != null && !classBean.isEnabled()) {
			return false;
		}
		if(hasEnabledSpecializingProducerMethod()) {
			return false;
		}
		if(isAlternative()) {
			if(classBean != null && !getCDIProject().getAlternatives(classBean.getBeanClass().getFullyQualifiedName()).isEmpty()) {
				return true;
			}
			Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
			for (IStereotypeDeclaration d: ds) {
				IStereotype s = d.getStereotype();
				if(s != null && s.isAlternative() && !getCDIProject().getAlternatives(s.getSourceType().getFullyQualifiedName()).isEmpty()) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public boolean isSpecializing() {
		return getDefinition().getSpecializesAnnotation() != null;
	}

	//same as ProducerField.getScope
	public IScope getScope() {
		Set<IScopeDeclaration> ds = getScopeDeclarations();
		if(!ds.isEmpty()) {
			return ds.iterator().next().getScope();
		}
		Set<IStereotypeDeclaration> ss = getStereotypeDeclarations();
		Set<IScope> defaults = new HashSet<IScope>();
		for (IStereotypeDeclaration d: ss) {
			IStereotype s = d.getStereotype();
			IScope sc = s.getScope();
			if(sc != null) {
				defaults.add(sc);
			}
		}
		if(defaults.size() == 1) {
			return defaults.iterator().next();
		} else if(defaults.size() > 1) {
			return null;
		}
		return getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

}
