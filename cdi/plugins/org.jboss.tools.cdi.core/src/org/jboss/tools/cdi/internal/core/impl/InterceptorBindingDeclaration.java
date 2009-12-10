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

import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InterceptorBindingDeclaration extends AnnotationDeclaration
		implements IInterceptorBindingDeclaration {

	public InterceptorBindingDeclaration(AnnotationDeclaration d) {
		d.copyTo(this);
	}

	public IInterceptorBinding getInterceptorBinding() {
		return project.getDelegate().getInterceptorBinding(getTypeName());
	}

}
