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
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDIAnnotationElement extends CDIElement implements ICDIAnnotation {
	protected AnnotationDefinition definition;

	public CDIAnnotationElement() {}

	public void setDefinition(AnnotationDefinition definition) {
		this.definition = definition;
	}	

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIAnnotation#getSourceType()
	 */
	public IType getSourceType() {
		return definition.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIAnnotation#getInheritedDeclaration()
	 */
	public IAnnotationDeclaration getInheritedDeclaration() {
		return definition.getInheritedAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIAnnotation#getAnnotationDeclarations()
	 */
	public List<IAnnotationDeclaration> getAnnotationDeclarations() {
		List<IAnnotationDeclaration> result = new ArrayList<IAnnotationDeclaration>();
		result.addAll(definition.getAnnotations());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.ICDIAnnotation#getAnnotationDeclaration(java.lang.String)
	 */
	public IAnnotationDeclaration getAnnotationDeclaration(String typeName) {
		return definition.getAnnotation(typeName);
	}
}