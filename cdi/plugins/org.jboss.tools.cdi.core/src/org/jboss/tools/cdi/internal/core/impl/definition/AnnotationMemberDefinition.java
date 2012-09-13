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

import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AnnotationMemberDefinition extends AbstractMemberDefinition {
	AnnotationDefinition annotationDefinition;
	IMethod method;

	public AnnotationMemberDefinition() {}

	public void setAnnotationDefinition(AnnotationDefinition annotationDefinition) {
		this.annotationDefinition = annotationDefinition;
	}

	public AnnotationDefinition getTypeDefinition() {
		return annotationDefinition;
	}

	public void setMethod(IMethod method, IRootDefinitionContext context, int flags) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context, flags);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isCDIAnnotated() {
		return getNonbindingAnnotation() != null;
	}

	public AnnotationDeclaration getNonbindingAnnotation() {
		return getAnnotation(CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME);
	}

}
