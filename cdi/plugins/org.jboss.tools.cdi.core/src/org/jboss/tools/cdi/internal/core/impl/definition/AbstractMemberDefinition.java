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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.InterceptorBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.QualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ScopeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.StereotypeDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractMemberDefinition {
	protected List<AnnotationDeclaration> annotations = new ArrayList<AnnotationDeclaration>();
	protected IAnnotatable member;
	protected Map<String, AnnotationDeclaration> annotationsByType = new HashMap<String, AnnotationDeclaration>();
	protected IResource resource;

	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, DefinitionContext context) {
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

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		resource = contextType.getResource();
		IAnnotation[] ts = member.getAnnotations();
		for (int i = 0; i < ts.length; i++) {
			AnnotationDeclaration a = new AnnotationDeclaration();
			a.setProject(context.getProject());
			a.setDeclaration(ts[i], contextType);
			int kind = context.getAnnotationKind(a.getType());
			if(kind == AnnotationDefinition.STEREOTYPE) {
				a = new StereotypeDeclaration(a);
			} else if(kind == AnnotationDefinition.INTERCEPTOR_BINDING) {
				a = new InterceptorBindingDeclaration(a);
			} else if(kind == AnnotationDefinition.QUALIFIER) {
				a = new QualifierDeclaration(a);
			} else if(kind == AnnotationDefinition.SCOPE) {
				a = new ScopeDeclaration(a);
			}
			annotations.add(a);
			if(a.getTypeName() != null) {
				annotationsByType.put(a.getTypeName(), a);
			}
		}
	}

	public List<AnnotationDeclaration> getAnnotations() {
		return annotations;
	}

	public AnnotationDeclaration getAnnotation(String typeName) {
		return annotationsByType.get(typeName);
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