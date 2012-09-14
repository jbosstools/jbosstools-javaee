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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.InterceptorBindingDeclaration;
import org.jboss.tools.cdi.internal.core.impl.QualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.ScopeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.StereotypeDeclaration;
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

	CDICoreNature project;
	protected List<IAnnotationDeclaration> annotations = new ArrayList<IAnnotationDeclaration>(2);
	protected IAnnotatable member;
	private IAnnotationMap annotationsByType = EmptyMap.instance;
	protected IResource resource;
	
	protected ITextSourceReference originalDefinition = null;

	public AbstractMemberDefinition() {}

	protected void setAnnotatable(IAnnotatable member, IType contextType, IRootDefinitionContext context, int flags) {
		this.member = member;
		try {
			init(contextType, context, flags);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	public CDICoreNature getDeclaringProject() {
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

	public PackageDefinition getPackageDefinition() {
		PackageDefinition result = null;
		AbstractTypeDefinition t = getTypeDefinition();
		if(t != null) {
			String qn = t.getQualifiedName();
			int d = qn.lastIndexOf('.');
			String packageName = (d < 0) ? "" : qn.substring(0, d);
			result = project.getDefinitions().getPackageDefinition(packageName);
		}
		return result;
	}

	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		project = context.getProject();
		resource = ((IJavaElement)member).getResource();
		if((flags & FLAG_NO_ANNOTATIONS) == 0) {
			IAnnotation[] ts = member.getAnnotations();
			for (int i = 0; i < ts.length; i++) {
				IJavaAnnotation ja = new JavaAnnotation(ts[i], contextType);
				addAnnotation(ja, context);
			}
		}
	}

	public void addAnnotation(IJavaAnnotation ja, IRootDefinitionContext context) {
		AnnotationDeclaration a = new AnnotationDeclaration();
		a.setProject(context.getProject());
		a.setDeclaration(ja);
		addAnnotation(a, context);
		addDependency(ja.getType(), context);
	}

	protected void addDependency(IMember reference, IRootDefinitionContext context) {
		if(reference == null || reference.isBinary()) return;
		if(!(resource instanceof IFile)) return;
		IFile target = (IFile)resource;
		IFile source = (IFile)reference.getResource();
		if(target.exists() && source != null && source.exists()) {
			context.addDependency(source.getFullPath(), target.getFullPath());
		}
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
			annotationsByType = annotationsByType.put(a.getTypeName(), a);
		}
	}

	public void annotationKindChanged(String typeName, IRootDefinitionContext context) {
		AnnotationDeclaration a = getAnnotation(typeName);
		if(a == null) return;
		Iterator<IAnnotationDeclaration> it = annotations.iterator();
		while(it.hasNext()) {
			IAnnotationDeclaration a1 = it.next();
			if(typeName.equals(a1.getTypeName())) it.remove();
		}
		//Make sure that a is non-specific annotation.
		addAnnotation(new AnnotationDeclaration(a), context);
		
	}

	public void removeAnnotation(IAnnotationDeclaration a) {
		String name = ((AnnotationDeclaration)a).getTypeName();
		IAnnotationDeclaration b = getAnnotation(name);
		if(a == b) {
			annotationsByType = annotationsByType.remove(name);
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
	 * @see org.jboss.tools.common.java.IAnnotated#getAnnotationPosition(java.lang.String)
	 */
	public IJavaSourceReference getAnnotationPosition(String annotationTypeName) {
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
		return getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
	}

	public AnnotationDeclaration getTypedAnnotation() {
		return getAnnotation(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getAlternativeAnnotation() {
		return getAnnotation(CDIConstants.ALTERNATIVE_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getSpecializesAnnotation() {
		return getAnnotation(CDIConstants.SPECIALIZES_ANNOTATION_TYPE_NAME);
	}

	public IResource getResource() {
		return resource;
	}

	public ITextSourceReference getOriginalDefinition() {
		return originalDefinition;
	}

	public boolean exists() {
		return member instanceof IJavaElement && ((IJavaElement)member).exists();
	}
}

interface IAnnotationMap {
	IAnnotationMap put(String type, AnnotationDeclaration d);
	AnnotationDeclaration get(String type);
	IAnnotationMap remove(String type);
}

class EmptyMap implements IAnnotationMap {
	static EmptyMap instance = new EmptyMap();
	
	private EmptyMap() {}

	public IAnnotationMap put(String type, AnnotationDeclaration d) {
		return new OneEntryMap(d);
	}

	public AnnotationDeclaration get(String type) {
		return null;
	}

	public IAnnotationMap remove(String type) {
		return this;
	}
}

class OneEntryMap implements IAnnotationMap {
	AnnotationDeclaration d;
	
	public OneEntryMap(AnnotationDeclaration d) {
		this.d = d;
	}

	@Override
	public IAnnotationMap put(String type, AnnotationDeclaration d) {
		if(this.d.getTypeName().equals(type)) {
			this.d = d;
			return this;
		}
		return new TwoEntryMap(this.d, d);
	}

	public AnnotationDeclaration get(String type) {
		return (d.getTypeName().equals(type)) ? d : null;
	}

	public IAnnotationMap remove(String type) {
		return (get(type) != null) ? EmptyMap.instance : this;
	}	
}

class TwoEntryMap implements IAnnotationMap {
	AnnotationDeclaration d1;
	AnnotationDeclaration d2;
	
	public TwoEntryMap(AnnotationDeclaration d1,AnnotationDeclaration d2) {
		this.d1 = d1;
		this.d2 = d2;
	}

	public IAnnotationMap put(String type, AnnotationDeclaration d) {
		AnnotationDeclaration dc = get(type);
		if(dc == d1) {
			d1 = d;
			return this;
		} else if(dc == d2) {
			d2 = d;
			return this;
		}
		AnnotationMap map = new AnnotationMap();
		map.put(this.d1.getTypeName(), this.d1);
		map.put(this.d2.getTypeName(), this.d2);
		map.put(type, d);
		return map;
	}

	public AnnotationDeclaration get(String type) {
		return (d1.getTypeName().equals(type)) ? d1 : (d2.getTypeName().equals(type)) ? d2 : null;
	}

	public IAnnotationMap remove(String type) {
		AnnotationDeclaration d = get(type);
		return (d == d1) ? new OneEntryMap(d2) : (d == d2) ? new OneEntryMap(d1) : this;
	}	
}

class AnnotationMap implements IAnnotationMap {
	Map<String, AnnotationDeclaration> annotationsByType = new HashMap<String, AnnotationDeclaration>(8);
	
	AnnotationMap() {}
	
	public IAnnotationMap put(String type, AnnotationDeclaration d) {
		annotationsByType.put(type, d);
		return this;
	}

	public AnnotationDeclaration get(String type) {
		return annotationsByType.get(type);
	}

	public IAnnotationMap remove(String type) {
		annotationsByType.remove(type);
		return this;
	}
}
