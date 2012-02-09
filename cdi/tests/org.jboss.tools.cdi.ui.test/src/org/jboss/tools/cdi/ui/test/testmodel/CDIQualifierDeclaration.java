/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.java.IAnnotationType;

public class CDIQualifierDeclaration implements IQualifierDeclaration {
	static private IMemberValuePair[] pairs = new IMemberValuePair[]{
			new MemberValuePair("name", "John", IMemberValuePair.K_STRING),
			new MemberValuePair("size", 5, IMemberValuePair.K_INT),
			new MemberValuePair("p", 0.5, IMemberValuePair.K_DOUBLE),
			new MemberValuePair("ch", 'Q', IMemberValuePair.K_CHAR),
			new MemberValuePair("b", (byte)6, IMemberValuePair.K_BYTE)
	};
	
	public CDIQualifierDeclaration(){
		
	}

	@Override
	public IMemberValuePair[] getMemberValuePairs() {
		return pairs;
	}

	@Override
	public Object getMemberValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMember getParentMember() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotationType getAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IAnnotation getJavaAnnotation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStartPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IMember getSourceMember() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getSourceElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQualifier getQualifier() {
		// TODO Auto-generated method stub
		return null;
	}

}
