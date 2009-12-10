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

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractBeanElement extends CDIElement {
	protected AbstractMemberDefinition definition;

	protected AnnotationDeclaration named;
	protected AnnotationDeclaration alternative;
	protected AnnotationDeclaration specializes;
	protected AnnotationDeclaration typed;
	protected AnnotationDeclaration decorator;
	protected AnnotationDeclaration interceptor;
	protected AnnotationDeclaration delegate;

	public AbstractBeanElement() {}

	public void setDefinition(AbstractMemberDefinition definition) {
		this.definition = definition;
		setAnnotations(definition.getAnnotations());
	}

	protected void setAnnotations(List<AnnotationDeclaration> ds) {
		for (AnnotationDeclaration d: ds) {
			String typeName = d.getTypeName();
			if(CDIConstants.NAMED_QUALIFIER_TYPE_NAME.equals(typeName)) {
				named = d;
			} else if(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME.equals(typeName)) {
				alternative = d;
			} else if(CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME.equals(typeName)) {
				specializes = d;
			} else if(CDIConstants.TYPED_ANNOTATION_TYPE_NAME.equals(typeName)) {
				typed = d;
			} else if(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME.equals(typeName)) {
				decorator = d;
			} else if(CDIConstants.DELEGATE_STEREOTYPE_TYPE_NAME.equals(typeName)) {
				delegate = d;
			} else if(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME.equals(typeName)) {
				interceptor = d;
			}
		}
	}

	protected AnnotationDeclaration findNamedAnnotation() {
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

}
