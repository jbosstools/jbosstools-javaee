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

import java.util.Set;

import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InterceptorBindingElement extends CDIAnnotationElement implements IInterceptorBinding {

	public InterceptorBindingElement() {}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInterceptorBinded#getInterceptorBindingDeclarations()
	 */
	public Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations() {
		return ClassBean.getInterceptorBindingDeclarations(definition);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInterceptorBinded#getInterceptorBindings()
	 */
	public Set<IInterceptorBinding> getInterceptorBindings() {
		return ClassBean.getInterceptorBindings(definition);
	}
}