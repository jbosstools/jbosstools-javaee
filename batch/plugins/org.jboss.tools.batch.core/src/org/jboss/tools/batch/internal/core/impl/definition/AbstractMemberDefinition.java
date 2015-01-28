/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.impl.definition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.BatchConstants;
import org.jboss.tools.batch.internal.core.IRootDefinitionContext;
import org.jboss.tools.batch.internal.core.impl.BatchAnnotationDeclaration;
import org.jboss.tools.batch.internal.core.impl.BatchProject;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.java.impl.JavaAnnotation;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractMemberDefinition implements IAnnotated {
	public static int FLAG_NO_ANNOTATIONS = 1;
	public static int FLAG_ALL_MEMBERS = 2;

	protected BatchProject project;

	protected BatchAnnotationDeclaration namedAnnotation;
	protected BatchAnnotationDeclaration injectAnnotation;
	protected BatchAnnotationDeclaration batchPropertyAnnotation;

	protected IAnnotatable member;
	
	protected ITextSourceReference originalDefinition = null;

	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, IRootDefinitionContext context, int flags) {
		this.member = member;
		try {
			init(contextType, context, flags);
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);;
		}
	}

	public BatchProject getDeclaringProject() {
		return project;
	}

	public void setOriginalDefinition(ITextSourceReference def) {
		originalDefinition = def;
	}

	public IAnnotatable getMember() {
		return member;
	}

	public AbstractTypeDefinition getTypeDefinition() {
		return null;
	}

	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		project = context.getProject();
		if((flags & FLAG_NO_ANNOTATIONS) == 0) {
			IAnnotation[] ts = member.getAnnotations();
			for (int i = 0; i < ts.length; i++) {
				IJavaAnnotation ja = new JavaAnnotation(ts[i], contextType);
				if(isRelevant(ja)) {
					addAnnotation(ja, context);
				}
			}
		}
	}

	private boolean isRelevant(IJavaAnnotation ja) {
		String type = ja.getTypeName();
		if (BatchConstants.INJECT_ANNOTATION_TYPE.equals(type)) {
			return true;
		}
		if (BatchConstants.NAMED_QUALIFIER_TYPE.equals(type)) {
			return true;
		}
		if (BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE.equals(type)) {
			return true;
		}
		return false;
	}

	private void addAnnotation(IJavaAnnotation ja, IRootDefinitionContext context) {
		BatchAnnotationDeclaration a = new BatchAnnotationDeclaration();
		a.setProject(context.getProject());
		a.setDeclaration(ja);
		addAnnotation(a, context);
		addDependency(ja.getType(), context);
	}

	protected void addDependency(IMember reference, IRootDefinitionContext context) {
		if(reference == null || reference.isBinary()) return;
		IResource resource = getResource();
		if(!(resource instanceof IFile)) return;
		IFile target = (IFile)resource;
		IFile source = (IFile)reference.getResource();
		if(target.exists() && source != null && source.exists()) {
			context.addDependency(source.getFullPath(), target.getFullPath());
		}
	}

	private void addAnnotation(BatchAnnotationDeclaration a, IRootDefinitionContext context) {
		String typeName = a.getTypeName();
		if (BatchConstants.INJECT_ANNOTATION_TYPE.equals(typeName)) {
			injectAnnotation = a;
		} else if (BatchConstants.NAMED_QUALIFIER_TYPE.equals(typeName)) {
			namedAnnotation = a;
		} else if (BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE.equals(typeName)) {
			batchPropertyAnnotation = a;
		}
	}

	public void removeAnnotation(IAnnotationDeclaration a) {
		String typeName = ((BatchAnnotationDeclaration)a).getTypeName();
		if (BatchConstants.INJECT_ANNOTATION_TYPE.equals(typeName)) {
			injectAnnotation = null;
		} else if (BatchConstants.NAMED_QUALIFIER_TYPE.equals(typeName)) {
			namedAnnotation = null;
		} else if (BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE.equals(typeName)) {
			batchPropertyAnnotation = null;
		}
	}

	@Override
	public List<IAnnotationDeclaration> getAnnotations() {
		List<IAnnotationDeclaration> result = new ArrayList<IAnnotationDeclaration>();
		if(injectAnnotation != null) {
			result.add(injectAnnotation);
		}
		if(namedAnnotation != null) {
			result.add(namedAnnotation);
		}
		if(batchPropertyAnnotation != null) {
			result.add(batchPropertyAnnotation);
		}
		return result;
	}

	@Override
	public BatchAnnotationDeclaration getAnnotation(String typeName) {
		if (BatchConstants.INJECT_ANNOTATION_TYPE.equals(typeName)) {
			return injectAnnotation;
		} else if (BatchConstants.NAMED_QUALIFIER_TYPE.equals(typeName)) {
			return namedAnnotation;
		} else if (BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE.equals(typeName)) {
			return batchPropertyAnnotation;
		}
		return null;
	}

	@Override
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
		return getAnnotation(annotationTypeName);
	}

	@Override
	public boolean isAnnotationPresent(String annotationTypeName) {
		return getAnnotation(annotationTypeName)!=null;
	}

	public BatchAnnotationDeclaration getNamedAnnotation() {
		return getAnnotation(BatchConstants.NAMED_QUALIFIER_TYPE);
	}

	public BatchAnnotationDeclaration getInjectAnnotation() {
		return getAnnotation(BatchConstants.INJECT_ANNOTATION_TYPE);
	}

	public BatchAnnotationDeclaration getBatchPropertyAnnotation() {
		return getAnnotation(BatchConstants.BATCH_PROPERTY_QUALIFIER_TYPE);
	}


	public IResource getResource() {
		return ((IJavaElement)member).getResource();
	}

	public ITextSourceReference getOriginalDefinition() {
		return originalDefinition;
	}

	public boolean exists() {
		return member instanceof IJavaElement && ((IJavaElement)member).exists();
	}
}
