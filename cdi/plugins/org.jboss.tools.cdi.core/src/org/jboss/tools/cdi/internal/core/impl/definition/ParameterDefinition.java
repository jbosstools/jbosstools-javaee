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

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.SourceRange;
import org.eclipse.jdt.internal.core.MemberValuePair;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IAnnotated;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.cdi.internal.core.impl.TypeDeclaration;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.text.ITextSourceReference;

public class ParameterDefinition implements IAnnotated {
	protected MethodDefinition methodDefinition;
	
	protected String name;
	protected ParametedType type;
	protected TypeDeclaration overridenType;
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

	public TypeDeclaration getOverridenType() {
		return overridenType;
	}

	public void setOverridenType(TypeDeclaration overridenType) {
		this.overridenType = overridenType;
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
		IType t = null;
		try {
			t = EclipseJavaUtil.findType(methodDefinition.getMethod().getJavaProject(), annotationTypeName);
		} catch (JavaModelException e) {
			
		}
		if(t == null) return null;
		int b = reference.getStartPosition();
		int e = reference.getLength() + b;
		if(b < 0 || e < b) return null;
		String source = methodDefinition.getTypeDefinition().getContent().substring(b, e);
		
		//compute member value parameters
		IMemberValuePair[] memberValues = getMemberValues(source);
		
		AnnotationLiteral a = new AnnotationLiteral(methodDefinition.getResource(), source, new SourceRange(b, e - b), memberValues, t);
		ad.setDeclaration(a);
		CDICoreNature nature = CDICorePlugin.getCDI(methodDefinition.getResource().getProject(), false);
		ad.setProject(nature);
		return ad;
	}

	private static IMemberValuePair[] EMPTY_PAIRS = new IMemberValuePair[0];

	private IMemberValuePair[] getMemberValues(String source) {
		int p1 = source.indexOf('(');
		int p2 = source.indexOf(')');
		if(p1 >= 0 && p2 > p1) {
			String params = source.substring(p1 + 1, p2).trim();
			if(params.length() > 0) {
				if(params.startsWith("{") && params.endsWith("}")) {
					//TODO
				} else if(params.endsWith(".class")) {
					params = params.substring(0, params.length() - 6);
					IMemberValuePair pair = new MemberValuePair("value", params, IMemberValuePair.K_CLASS);
					return new IMemberValuePair[]{pair};
				} else if(params.startsWith("\"") && params.endsWith("\"")) {
					params = params.substring(1, params.length() - 1);
					IMemberValuePair pair = new MemberValuePair("value", params, IMemberValuePair.K_STRING);
					return new IMemberValuePair[]{pair};
				} else {
					//TODO
				}
			}
		}
		
		return EMPTY_PAIRS;
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