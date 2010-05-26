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

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InjectionPointField extends BeanField implements IInjectionPointField {

	public InjectionPointField() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getDelegateAnnotation()
	 */
	public IAnnotationDeclaration getDelegateAnnotation() {
		return getDefinition().getDelegateAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#isDelegate()
	 */
	public boolean isDelegate() {
		return getDelegateAnnotation() != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getInjectAnnotation()
	 */
	public IAnnotationDeclaration getInjectAnnotation() {
		return definition.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
	}
}