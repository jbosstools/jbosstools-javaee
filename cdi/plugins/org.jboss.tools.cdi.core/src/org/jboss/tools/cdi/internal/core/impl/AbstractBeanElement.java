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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParametedTypeFactory;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractBeanElement extends CDIElement {
	protected AbstractMemberDefinition definition;

	public AbstractBeanElement() {}

	public void setDefinition(AbstractMemberDefinition definition) {
		this.definition = definition;
	}

	public AbstractMemberDefinition getDefinition() {
		return definition;
	}

	protected AnnotationDeclaration findNamedAnnotation() {
		AnnotationDeclaration named = getDefinition().getNamedAnnotation();
		if(named != null) return named;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			StereotypeElement s = (StereotypeElement)d.getStereotype();
			if(s == null) continue;
			if(s.getNameDeclaration() != null) return s.getNameDeclaration();
		}
		return null;
	}

	public boolean isAlternative() {
		if(getDefinition().getAlternativeAnnotation() != null) return true;
		Set<IStereotypeDeclaration> ds = getStereotypeDeclarations();
		for (IStereotypeDeclaration d: ds) {
			IStereotype s = d.getStereotype();
			if(s != null && s.isAlternative()) return true;
		}		
		return false;
	}

	public Set<IStereotypeDeclaration> getStereotypeDeclarations() {
		Set<IStereotypeDeclaration> result = new HashSet<IStereotypeDeclaration>();
		for (AnnotationDeclaration d: definition.getAnnotations()) {
			if(d instanceof IStereotypeDeclaration) {
				if(d instanceof IStereotypeDeclaration) {
					result.add((IStereotypeDeclaration)d);
				}
			}
		}
		return result;
	}

	public Set<IAnnotationDeclaration> getQualifierDeclarations() {
		Set<IAnnotationDeclaration> result = new HashSet<IAnnotationDeclaration>();
		for(AnnotationDeclaration a: definition.getAnnotations()) {
			int k = getCDIProject().getNature().getDefinitions().getAnnotationKind(a.getType());
			if(k == AnnotationDefinition.QUALIFIER) {
				result.add(a);
			}
		}
		return result;
	}

	public Set<IScopeDeclaration> getScopeDeclarations() {
		return getScopeDeclarations(getCDIProject().getNature(), definition.getAnnotations());
	}

	public static Set<IScopeDeclaration> getScopeDeclarations(CDICoreNature n, List<? extends IAnnotationDeclaration> ds) {
		Set<IScopeDeclaration> result = new HashSet<IScopeDeclaration>();
		for (IAnnotationDeclaration d: ds) {
			int k = n.getDefinitions().getAnnotationKind(d.getType());
			if(k == AnnotationDefinition.SCOPE) {
				result.add((IScopeDeclaration)d);
			}
		}
		return result;
	}

	public Set<ITypeDeclaration> getRestrictedTypeDeclaratios() {
		Set<ITypeDeclaration> result = new HashSet<ITypeDeclaration>();
		AnnotationDeclaration typed = getDefinition().getTypedAnnotation();
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
						if(!typeName.endsWith(";")) typeName = "Q" + typeName + ";";
						ParametedType p = ParametedTypeFactory.getParametedType(((IMember)definition.getMember()).getDeclaringType(), typeName);
						if(p != null) {
							result.add(new TypeDeclaration(p, -1, 0));
						}
					}
				} else if(value != null) {
					String typeName = value.toString();
					if(!typeName.endsWith(";")) typeName = "Q" + typeName + ";";
					ParametedType p = ParametedTypeFactory.getParametedType(((IMember)definition.getMember()).getDeclaringType(), typeName);
					if(p != null) {
						result.add(new TypeDeclaration(p, -1, 0));
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
		return result;
	}

}
