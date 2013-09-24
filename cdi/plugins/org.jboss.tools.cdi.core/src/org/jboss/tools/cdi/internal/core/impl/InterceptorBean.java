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
package org.jboss.tools.cdi.internal.core.impl;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InterceptorBean extends ClassBean implements IInterceptor {

	public InterceptorBean() {}

	public IAnnotationDeclaration getInterceptorAnnotation() {
		return getDefinition().getInterceptorAnnotation();
	}

	public boolean isEnabled() {
		return !getCDIProject().getInterceptorClasses(getBeanClass().getFullyQualifiedName()).isEmpty()
			|| getAnnotation(CDIConstants.PRIORITY_ANNOTATION_TYPE_NAME) != null;
	}

}
