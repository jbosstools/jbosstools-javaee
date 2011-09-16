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
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InjectionPointField extends BeanField implements IInjectionPointField {

	public InjectionPointField() {}

	@Override
	public IParametedType getType() {
		if(getDefinition().getOverridenType() != null) {
			return getDefinition().getOverridenType();
		}
		return super.getType();
	}

	@Override
	public String getBeanName() {
		AnnotationDeclaration d = getDefinition().getNamedAnnotation();
		if(d != null) {
			Object n = d.getMemberValue(null);
			if(n != null && n.toString().length() > 0) {
				return n.toString();
			}
			return field.getElementName();
		}
		return null;
	}

	public IParametedType getJavaMemberType() {
		return super.getType();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getDelegateAnnotation()
	 */
	@Override
	public IAnnotationDeclaration getDelegateAnnotation() {
		return getDefinition().getDelegateAnnotation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#isDelegate()
	 */
	@Override
	public boolean isDelegate() {
		return getDelegateAnnotation() != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getInjectAnnotation()
	 */
	@Override
	public IAnnotationDeclaration getInjectAnnotation() {
		return definition.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#containsDefaultQualifier()
	 */
	@Override
	public boolean hasDefaultQualifier() {
		return CDIUtil.containsDefaultQualifier(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.cdi.core.IInjectionPoint#getBean()
	 */
	@Override
	public IBean getBean() {
		// Injected field may be declared in a class bean only. 
		return getClassBean();
	}
}