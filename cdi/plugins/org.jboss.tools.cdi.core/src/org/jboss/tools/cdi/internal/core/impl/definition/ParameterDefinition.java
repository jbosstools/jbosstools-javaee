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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotated;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.JavaAnnotation;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.common.text.ITextSourceReference;

public class ParameterDefinition implements IAnnotated {
	protected MethodDefinition methodDefinition;
	
	protected String name;
	protected ParametedType type;
	protected int index;

	protected ITextSourceReference position = null;
	Map<String, ITextSourceReference> annotationsByTypeName = new HashMap<String, ITextSourceReference>();

	public ParameterDefinition() {}

	public String getName() {
		return name;
	}

	public ParametedType getType() {
		return type;
	}

	public MethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	/**
	 * JDT doesn't have API for annotations for method params. So this method will return a wrapper for ITextSourceReference.
	 * Use getAnnotationPosition() instead.
	 * 
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotation(java.lang.String)
	 */
	public IAnnotationDeclaration getAnnotation(String annotationTypeName) {
		ITextSourceReference reference =  getAnnotationPosition(annotationTypeName);
		if(reference==null) {
			return null;
		}
		// JDT doesn't have API for annotations for method params. So let's wrap ITextSourceReference into IAnnotationDeclaration.
		AnnotationDeclaration ad = new AnnotationDeclaration();
		//TODO it should be annotation literal!
		System.out.println("!!!!!!!!!!!!!Here we are!!!!!!!!");
		ad.setDeclaration(new JavaAnnotation(null, methodDefinition.getMethod().getDeclaringType()));
		CDICoreNature nature = CDICorePlugin.getCDI(methodDefinition.getResource().getProject(), false);
		ad.setProject(nature);
		return ad;
	}

	/**
	 * Returns an empty list because JDT doesn't have API for annotations for method params. Use getAnnotationTypes() instead.
	 * 
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotations()
	 */
	public List<IAnnotationDeclaration> getAnnotations() {
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#isAnnotationPresent(java.lang.String)
	 */
	public boolean isAnnotationPresent(String annotationTypeName) {
		return annotationsByTypeName.containsKey(annotationTypeName);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IAnnotated#getAnnotationPosition(java.lang.String)
	 */
	public ITextSourceReference getAnnotationPosition(String annotationTypeName) {
		return annotationsByTypeName.get(annotationTypeName);
	}

	public Set<String> getAnnotationTypes() {
		return annotationsByTypeName.keySet();
	}

	public void setPosition(ITextSourceReference position) {
		this.position = position;
	}

	public ITextSourceReference getPosition() {
		return position;
	}

	public String getAnnotationText(String annotationTypeName) {
		ITextSourceReference pos = getAnnotationPosition(annotationTypeName);
		if(pos == null) return null;
		String text = methodDefinition.getTypeDefinition().getContent().substring(pos.getStartPosition(), pos.getStartPosition() + pos.getLength());
		return text;
	}
}