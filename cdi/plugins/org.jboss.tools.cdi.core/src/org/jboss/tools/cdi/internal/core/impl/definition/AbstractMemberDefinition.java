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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotated;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.InterceptorBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.JavaAnnotation;
import org.jboss.tools.cdi.internal.core.impl.QualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ScopeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.StereotypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractMemberDefinition implements IAnnotated {
	CDICoreNature project;
	protected List<IAnnotationDeclaration> annotations = new ArrayList<IAnnotationDeclaration>();
	protected IAnnotatable member;
	protected Map<String, AnnotationDeclaration> annotationsByType = new HashMap<String, AnnotationDeclaration>();
	protected IResource resource;

	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, IRootDefinitionContext context) {
		this.member = member;
		try {
			init(contextType, context);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	public IAnnotatable getMember() {
		return member;
	}

	public AbstractTypeDefinition getTypeDefinition() {
		return null;
	}

	public PackageDefinition getPackageDefinition() {
		AbstractTypeDefinition t = getTypeDefinition();
		if(t == null) return null;
		String qn = t.getQualifiedName();
		if(qn == null) return null;
		int d = qn.lastIndexOf('.');
		String packageName = (d < 0) ? "" : qn.substring(0, d);
		return project.getDefinitions().getPackageDefinition(packageName);
	}

	protected void init(IType contextType, IRootDefinitionContext context) throws CoreException {
		project = context.getProject();
		resource = ((IJavaElement)member).getResource();
		IAnnotation[] ts = member.getAnnotations();
		for (int i = 0; i < ts.length; i++) {
			IJavaAnnotation ja = new JavaAnnotation(ts[i], contextType);
			addAnnotation(ja, context);
		}
	}

	public void addAnnotation(IJavaAnnotation ja, IRootDefinitionContext context) {
		AnnotationDeclaration a = new AnnotationDeclaration();
		a.setProject(context.getProject());
		a.setDeclaration(ja);
		addAnnotation(a, context);
	}

	private void addAnnotation(AnnotationDeclaration a, IRootDefinitionContext context) {
		AnnotationDeclaration b = null;
		int kind = context.getAnnotationKind(a.getType());
		if(kind > 0 && (kind & AnnotationDefinition.STEREOTYPE) > 0) {
			b = new StereotypeDeclaration(a);
			annotations.add(b);
		}
		if(kind > 0 && (kind & AnnotationDefinition.INTERCEPTOR_BINDING) > 0) {
			b = new InterceptorBindingDeclaration(a);
			annotations.add(b);
		}
		if(kind > 0 && (kind & AnnotationDefinition.QUALIFIER) > 0) {
			b = new QualifierDeclaration(a);
			annotations.add(b);
		}
		if(kind > 0 && (kind & AnnotationDefinition.SCOPE) > 0) {
			b = new ScopeDeclaration(a);
			annotations.add(b);
		}
		if(b == null) {
			annotations.add(a);
		} else {
			a = b;
		}
		
		if(a.getTypeName() != null) {
			annotationsByType.put(a.getTypeName(), a);
		}
	}

	public void annotationKindChanged(String typeName, IRootDefinitionContext context) {
		AnnotationDeclaration a = annotationsByType.get(typeName);
		if(a == null) return;
		Iterator<IAnnotationDeclaration> it = annotations.iterator();
		while(it.hasNext()) {
			IAnnotationDeclaration a1 = it.next();
			IType t = a1.getType();
			if(t != null && typeName.equals(t.getFullyQualifiedName())) it.remove();
		}
		//Make sure that a is non-specific annotation.
		addAnnotation(new AnnotationDeclaration(a), context);
		
	}

	public void removeAnnotation(IAnnotationDeclaration a) {
		String name = ((AnnotationDeclaration)a).getTypeName();
		IAnnotationDeclaration b = annotationsByType.get(name);
		if(a == b) {
			annotationsByType.remove(name);
			annotations.remove(a);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotations()
	 */
	public List<IAnnotationDeclaration> getAnnotations() {
		return annotations;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotation(java.lang.String)
	 */
	public AnnotationDeclaration getAnnotation(String typeName) {
		return annotationsByType.get(typeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotationPosition(java.lang.String)
	 */
	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#isAnnotationPresent(java.lang.String)
	 */
	public boolean isAnnotationPresent(String annotationTypeName) {
		return getAnnotation(annotationTypeName)!=null;
	}

	public AnnotationDeclaration getNamedAnnotation() {
		return annotationsByType.get(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
	}

	public AnnotationDeclaration getTypedAnnotation() {
		return annotationsByType.get(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getAlternativeAnnotation() {
		return annotationsByType.get(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getSpecializesAnnotation() {
		return annotationsByType.get(CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME);
	}

	public IResource getResource() {
		return resource;
	}
}