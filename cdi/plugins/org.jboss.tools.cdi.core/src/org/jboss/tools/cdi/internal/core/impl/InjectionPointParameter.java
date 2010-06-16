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

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InjectionPointParameter extends Parameter implements
		IInjectionPointParameter {

	public IAnnotationDeclaration getDelegateAnnotation() {
		return null;
	}

	public Set<IQualifierDeclaration> getQualifierDeclarations() {
		Set<IQualifierDeclaration> result = new HashSet<IQualifierDeclaration>();
		//cannot implement
		return result;
	}

	public Set<IQualifier> getQualifiers() {
		Set<IQualifier> result = new HashSet<IQualifier>();
		Set<String> as = getAnnotationTypes();
		for (String s: as) {
			IQualifier q = getCDIProject().getQualifier(s);
			if (q != null) result.add(q);
		}
		return result;
	}

	public boolean isDelegate() {
		return isAnnotationPresent(CDIConstants.DELEGATE_STEREOTYPE_TYPE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getInjectAnnotation()
	 */
	public IAnnotationDeclaration getInjectAnnotation() {
		return beanMethod.inject;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#containsDefaultQualifier()
	 */
	public boolean hasDefaultQualifier() {
		return CDIUtil.containsDefaultQualifier(this);
	}
}