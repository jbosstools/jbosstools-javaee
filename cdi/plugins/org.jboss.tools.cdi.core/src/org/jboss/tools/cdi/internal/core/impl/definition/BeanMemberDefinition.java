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

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

public class BeanMemberDefinition extends AbstractMemberDefinition {
	AbstractTypeDefinition typeDefinition;

	public BeanMemberDefinition() {}

	public void setTypeDefinition(AbstractTypeDefinition typeDefinition) {
		this.typeDefinition = typeDefinition;
	}

	@Override
	public AbstractTypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	public boolean isCDIAnnotated() {
		return getInjectAnnotation() != null || getProducesAnnotation() != null || getPreDestroyMethod() != null || getPostConstructorMethod() != null;
	}

	public AnnotationDeclaration getProducesAnnotation() {
		return annotationsByType.get(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getPreDestroyMethod() {
		return annotationsByType.get(CDIConstants.PRE_DESTROY_TYPE_NAME);
	}

	public AnnotationDeclaration getPostConstructorMethod() {
		return annotationsByType.get(CDIConstants.POST_CONSTRUCTOR_TYPE_NAME);
	}

	public AnnotationDeclaration getInjectAnnotation() {
		return annotationsByType.get(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
	}

	public AnnotationDeclaration getDelegateAnnotation() {
		return annotationsByType.get(CDIConstants.DELEGATE_STEREOTYPE_TYPE_NAME);
	}
}