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
		return alternative;
	}

	public IType getBeanClass() {
		return typeDeclaration != null ? typeDeclaration.getType() : null;
	}

	public Set<IInjectionPoint> getInjectionPoints() {
		return new HashSet<IInjectionPoint>();
	}

	public Set<IParametedType> getLegalTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
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

	//similar to ProducerField.getRestrictedTypeDeclaratios
	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		Set<ITypeDeclaration> result = new HashSet<ITypeDeclaration>();
		if(typed != null) {
			IAnnotation a = typed.getDeclaration();
			try {
				IMemberValuePair[] ps = a.getMemberValuePairs();
				if(ps == null || ps.length == 0) return result;
				Object value = ps[0].getValue();
				if(value instanceof Object[]) {
					Object[] os = (Object[])value;
					for (int i = 0; i < os.length; i++) {
						String typeName = os[i].toString();
						IType type = EclipseJavaUtil.findType(getMethod().getJavaProject(), typeName);
						if(type != null) {
							result.add(new TypeDeclaration(type, -1, 0));
						}
					}
				} else if(value != null) {
					String typeName = value.toString();
					IType type = EclipseJavaUtil.findType(getMethod().getJavaProject(), typeName);
					if(type != null) {
						result.add(new TypeDeclaration(type, -1, 0));
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
		return result;
	}

	public IBean getSpecializedBean() {
		if(specializes == null) {
			return null;
		}
		//TODO find producer method in super type
		
		return null;
	}

	public IAnnotationDeclaration getSpecializesAnnotationDeclaration() {
		return specializes;
	}

	public boolean isAlternative() {
		if(alternative != null) return true;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

	public boolean isDependent() {
		IType scope = getScope();
		return scope != null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(scope.getFullyQualifiedName());
	}

	public boolean isEnabled() {
		//if it is overrided by producer method which specializes it, return false!
		return true;
	}

	public boolean isSpecializing() {
		return specializes != null;
	}

	//same as ProducerField.getScope
	public IType getScope() {
		Set<IAnnotationDeclaration> ds = getScopeDeclarations();
		if(!ds.isEmpty()) {
			return ds.iterator().next().getType();
		}
		List<AnnotationDeclaration> ds2 = definition.getAnnotations();
		for (AnnotationDeclaration d: ds2) {
			int k = getCDIProject().getNature().getDefinitions().getAnnotationKind(d.getType());
			if(k == AnnotationDefinition.STEREOTYPE) {
				AnnotationDefinition a = getCDIProject().getNature().getDefinitions().getAnnotation(d.getType());
				ds = ProducerField.getScopeDeclarations(getCDIProject().getNature(), a.getAnnotations());
				if(!ds.isEmpty()) {
					return ds.iterator().next().getType();
				}
			}
		}
		try {
			return EclipseJavaUtil.findType(getMethod().getJavaProject(), CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	public Set<IAnnotationDeclaration> getScopeDeclarations() {
		return ProducerField.getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

}
