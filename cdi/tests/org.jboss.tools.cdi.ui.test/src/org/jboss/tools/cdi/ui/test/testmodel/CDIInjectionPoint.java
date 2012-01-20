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

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

public class CDIInjectionPoint implements IInjectionPoint {
	private ICDIProject project;
	private IClassBean bean;
	
	public CDIInjectionPoint(ICDIProject project, IClassBean bean){
		this.project = project;
		this.bean = bean;
	}

	@Override
	public ICDIProject getCDIProject() {
		return project;
	}

	@Override
	public ICDIProject getDeclaringProject() {
		return project;
	}

	@Override
	public IPath getSourcePath() {
		return null;
	}

	@Override
	public IResource getResource() {
		return null;
	}

	@Override
	public IClassBean getClassBean() {
		return bean;
	}

	@Override
	public IParametedType getMemberType() {
		return null;
	}

	@Override
	public IMember getSourceMember() {
		return null;
	}

	@Override
	public int getStartPosition() {
		return 0;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public List<IAnnotationDeclaration> getAnnotations() {
		return null;
	}

	@Override
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		return null;
	}

	@Override
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return null;
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		return false;
	}

	@Override
	public IParametedType getType() {
		return null;
	}

	@Override
	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		return null;
	}

	@Override
	public boolean hasDefaultQualifier() {
		return false;
	}

	@Override
	public boolean isDelegate() {
		return false;
	}

	@Override
	public ITextSourceReference getDelegateAnnotation() {
		return null;
	}

	@Override
	public IAnnotationDeclaration getInjectAnnotation() {
		return null;
	}

	@Override
	public String getBeanName() {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public String getElementName() {
		return null;
	}

	@Override
	public IBean getBean() {
		return bean;
	}

	@Override
	public boolean isDeclaredFor(IJavaElement element) {
		return false;
	}

	@Override
	public IJavaElement getSourceElement() {
		return getSourceMember();
	}
}