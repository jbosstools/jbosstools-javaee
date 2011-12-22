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
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.impl.AnnotationDeclaration;
import org.jboss.tools.common.java.impl.JavaAnnotation;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2Constants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractMemberDefinition implements IAnnotated {
	public static int FLAG_NO_ANNOTATIONS = 1;
	public static int FLAG_ALL_MEMBERS = 2;

	protected List<IAnnotationDeclaration> annotations = new ArrayList<IAnnotationDeclaration>();
	protected IAnnotatable member;
	protected Map<String, AnnotationDeclaration> annotationsByType = new HashMap<String, AnnotationDeclaration>();
	protected IResource resource;
	
	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, DefinitionContext context, int flags) {
		this.member = member;
		try {
			init(contextType, context, flags);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
	}

	public IAnnotatable getMember() {
		return member;
	}

	public AbstractTypeDefinition getTypeDefinition() {
		return null;
	}

	protected void init(IType contextType, DefinitionContext context, int flags) throws CoreException {
		resource = ((IJavaElement)member).getResource();
		if((flags & FLAG_NO_ANNOTATIONS) == 0) {
			IAnnotation[] ts = member.getAnnotations();
			for (int i = 0; i < ts.length; i++) {
				IJavaAnnotation ja = new JavaAnnotation(ts[i], contextType);
				addAnnotation(ja, context);
			}
		}
	}

	public void addAnnotation(IJavaAnnotation ja, DefinitionContext context) {
		AnnotationDeclaration a = new AnnotationDeclaration();
		a.setDeclaration(ja);
		addAnnotation(a, context);
	}

	private void addAnnotation(AnnotationDeclaration a, DefinitionContext context) {
		annotations.add(a);
		if(a.getTypeName() != null) {
			annotationsByType.put(a.getTypeName(), a);
		}
	}

	public void removeAnnotation(IAnnotationDeclaration a) {
		String name = ((AnnotationDeclaration)a).getTypeName();
		IAnnotationDeclaration b = annotationsByType.get(name);
		if(a == b) {
			annotationsByType.remove(name);
			annotations.remove(a);
		}
	}

	public List<IAnnotationDeclaration> getAnnotations() {
		return annotations;
	}

	public AnnotationDeclaration getAnnotation(String typeName) {
		return annotationsByType.get(typeName);
	}

	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	public boolean isAnnotationPresent(String annotationTypeName) {
		return getAnnotation(annotationTypeName)!=null;
	}

	public AnnotationDeclaration getManagedBeanAnnotation() {
		return annotationsByType.get(JSF2Constants.MANAGED_BEAN_ANNOTATION_TYPE_NAME);
	}

	public IResource getResource() {
		return resource;
	}
}