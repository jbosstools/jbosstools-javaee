/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
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
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IJavaAnnotation;

public class AnnotationLiteral implements IJavaAnnotation {
	IResource declaringResource;
	IType annotationType;

	String source;
	ISourceRange range;
	IMemberValuePair[] memberValues = new IMemberValuePair[0];

	public AnnotationLiteral(IResource declaringResource, String source, ISourceRange range, IMemberValuePair[] memberValues, IType annotationType) {
		this.declaringResource = declaringResource;
		this.source = source;
		this.range = range;
		this.memberValues = memberValues;
		this.annotationType = annotationType;
	}

	public int getStartPosition() {
		return range == null ? -1 : range.getOffset();
	}

	public int getLength() {
		return range == null ? -1 : range.getLength();
	}

	public IResource getResource() {
		return declaringResource;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IType getType() {
		return annotationType;
	}

	public IMember getParentMember() {
		//Do we need it?
		return null;
	}

	public IMemberValuePair[] getMemberValuePairs() {
		return memberValues;
	}
	
}
