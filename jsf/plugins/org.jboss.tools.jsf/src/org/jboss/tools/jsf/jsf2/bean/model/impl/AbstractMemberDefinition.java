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
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IAnnotationType;
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

	private static final String SPRING_CONTROLLER = "org.springframework.stereotype.Controller";
	private static final String SPRING_COMPONENT = "org.springframework.stereotype.Component";

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

	@Override
	public List<IAnnotationDeclaration> getAnnotations() {
		return annotations;
	}

	@Override
	public AnnotationDeclaration getAnnotation(String typeName) {
		return annotationsByType.get(typeName);
	}

	@Override
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		boolean b = (getAnnotation(annotationTypeName) != null);
		if (!b  &&  JSF2Constants.MANAGED_BEAN_ANNOTATION_TYPE_NAME.equals(annotationTypeName)) {
			// also support Spring @Controller and @Component dependency
			b = (getAnnotation(SPRING_CONTROLLER) != null
					||  getAnnotation(SPRING_COMPONENT) != null);
			/* OPTIONAL, with support for all Spring annotations (but see at getManagedBeanAnnotation() ):
			b = (getAnnotation("org.springframework.stereotype.Controller") != null
					||  getAnnotation("org.springframework.stereotype.Service") != null
					||  getAnnotation("org.springframework.stereotype.Repository") != null
					||  getAnnotation("org.springframework.stereotype.Component") != null);
			*/
		}
		return b;
	}

	public AnnotationDeclaration getManagedBeanAnnotation() {
		AnnotationDeclaration ad = annotationsByType.get(JSF2Constants.MANAGED_BEAN_ANNOTATION_TYPE_NAME);
		if (ad != null)  return ad;
		// also support Spring @Controller and @Component dependency
		ad = annotationsByType.get(SPRING_CONTROLLER);
		if (ad == null)  ad = annotationsByType.get(SPRING_COMPONENT);
		/* OPTIONAL, with support for all Spring annotations
		 * (but other than @Controller or generic @Component does not make sense,
		 *  you will/should NOT access a Service or Repository inside your JSF file.):
		if (ad == null)  ad = annotationsByType.get("org.springframework.stereotype.Service");
		if (ad == null)  ad = annotationsByType.get("org.springframework.stereotype.Repository");
		*/
		if (ad != null) {
			// create wrapper to map "value" (used by Spring) to "name" (which is used by @ManageBean)
			ad = new AnnotationDeclaration() {
					private AnnotationDeclaration wrapped;

					AnnotationDeclaration init(AnnotationDeclaration wrappedAD) {
						this.wrapped = wrappedAD;
						return this;
					}

					@Override
					public Object getMemberValue(String name) {
						Object val = wrapped.getMemberValue(name);
						if (val == null  &&  "name".equals(name)) {
							val = wrapped.getMemberValue(null);
						}
						return val;
					}

					@Override
					public Object getMemberValue(String name, boolean resolve) {
						Object result = null;
						if (resolve) {
							result = this.getMemberConstantValue(name);
						}
						if (result == null) {
							result = this.getMemberValue(name);
						}
						return result;
					}

					@Override
					public void setDeclaration(IJavaAnnotation annotation) {
						wrapped.setDeclaration(annotation);
					}

					@Override
					public IJavaAnnotation getDeclaration() {
						return wrapped.getDeclaration();
					}

					@Override
					public IResource getResource() {
						return wrapped.getResource();
					}

					@Override
					public IMemberValuePair[] getMemberValuePairs() {
						return wrapped.getMemberValuePairs();
					}

					@Override
					public Object getMemberConstantValue(String name) {
						return wrapped.getMemberConstantValue(name);
					}

					@Override
					public Object getMemberDefaultValue(String name) {
						return wrapped.getMemberDefaultValue(name);
					}

					@Override
					public IMember getParentMember() {
						return wrapped.getParentMember();
					}

					@Override
					public String getTypeName() {
						return wrapped.getTypeName();
					}

					@Override
					public IType getType() {
						return wrapped.getType();
					}

					@Override
					public int getLength() {
						return wrapped.getLength();
					}

					@Override
					public int getStartPosition() {
						return wrapped.getStartPosition();
					}

					@Override
					public IAnnotationType getAnnotation() {
						return wrapped.getAnnotation();
					}

					@Override
					public IAnnotation getJavaAnnotation() {
						return wrapped.getJavaAnnotation();
					}

					@Override
					public IMember getSourceMember() {
						return wrapped.getSourceMember();
					}

					@Override
					public IJavaElement getSourceElement() {
						return wrapped.getSourceElement();
					}
				}.init(ad); // class
		}

		return ad;
	}

	public IResource getResource() {
		return resource;
	}
}
