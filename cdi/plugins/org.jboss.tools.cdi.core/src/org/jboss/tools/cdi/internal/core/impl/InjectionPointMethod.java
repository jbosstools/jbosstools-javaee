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

import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IInjectionPointMethod;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InjectionPointMethod extends BeanMethod implements
		IInjectionPointMethod {

	public IAnnotationDeclaration getDelegateAnnotation() {
		return getDefinition().getDelegateAnnotation();
	}

	public boolean isDelegate() {
		return getDelegateAnnotation() != null;
	}

	@Override
	protected Parameter newParameter(ParameterDefinition p) {
		return new InjectionPointParameter();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getInjectAnnotation()
	 */
	public IAnnotationDeclaration getInjectAnnotation() {
		return inject;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#containsDefaultQualifier()
	 */
	public boolean hasDefaultQualifier() {
		return CDIUtil.containsDefaultQualifier(this);
	}

	@Override
	public String getBeanName() {
		return null;
	}

}