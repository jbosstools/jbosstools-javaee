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

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.IJavaAnnotation;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AnnotationDeclaration implements IAnnotationDeclaration {
	protected CDICoreNature project;
	protected IJavaAnnotation annotation;

	public AnnotationDeclaration() {}

	public AnnotationDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	protected void copyTo(AnnotationDeclaration other) {
		other.project = project;
		other.annotation = annotation;
	}

	public void setProject(CDICoreNature project) {
		this.project = project;
	}

	public void setDeclaration(IJavaAnnotation annotation) {
		this.annotation = annotation;
	}

	public IResource getResource() {
		return annotation.getResource();
	}

	public IMemberValuePair[] getMemberValuePairs() {
		return annotation.getMemberValuePairs();
	}

	public Object getMemberValue(String name) {
		if(name == null) name = "value";
		IMemberValuePair[] pairs = getMemberValuePairs();
		if(pairs != null) {
			for (IMemberValuePair pair: pairs) {
				if(name.equals(pair.getMemberName())) {
					return pair.getValue();
				}
			}
		}
		return null;
	}

	public IMember getParentMember() {
		return annotation.getParentMember();
	}

	public String getTypeName() {
		return annotation.getTypeName();
	}

	public IType getType() {
		return annotation.getType();
	}

	public int getLength() {
		return annotation.getLength();
	}

	public int getStartPosition() {
		return annotation.getStartPosition();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotationDeclaration#getAnnotation()
	 */
	public ICDIAnnotation getAnnotation() {
		return null;
	}
}