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

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class InjectionPointField extends BeanField implements IInjectionPointField {

	public InjectionPointField() {}

	public IAnnotationDeclaration getDecoratorAnnotation() {
		return decorator;
	}

	public IType getType() {
		return typeDeclaration == null ? null : typeDeclaration.getType();
	}

	public boolean isDelegate() {
		return delegate != null;
	}

}
