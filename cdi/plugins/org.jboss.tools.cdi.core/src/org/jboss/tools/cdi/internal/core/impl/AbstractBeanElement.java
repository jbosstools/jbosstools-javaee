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
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
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
						IType type = EclipseJavaUtil.findType(getCDIProject().getNature().getDefinitions().getJavaProject(), typeName);
						if(type != null) {
							result.add(new TypeDeclaration(type, -1, 0));
						}
					}
				} else if(value != null) {
					String typeName = value.toString();
					IType type = EclipseJavaUtil.findType(getCDIProject().getNature().getDefinitions().getJavaProject(), typeName);
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

}
