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
package org.jboss.tools.cdi.core;

import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IJavaElement;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Represents an injection point.
 * 
 * @author Alexey Kazakov
 */
public interface IInjectionPoint extends ICDIElement, ITextSourceReference {

	/**
	 * Gets the required qualifiers of the injection point.
	 * 
	 * @return the required qualifiers
	 */
	Set<IAnnotation> getQualifiers();

	/**
	 * Gets the object representing the bean that defines the injection point. If
	 * the injection point does not belong to a bean, return a null value.
	 * 
	 * @return the object representing bean that defines the injection point, of
	 *         null if the injection point does not belong to a bean
	 */
	IBean getBean();

	/**
	 * Obtains an instance of IMember or ITypeParameter, depending upon whether
	 * the injection point is an injected field or a constructor/method
	 * parameter.
	 * 
	 * @return an IMember or ITypeParameter
	 */
	IJavaElement getSourceElement();

	/**
	 * Determines if the injection point is a decorator delegate injection
	 * point.
	 * 
	 * @return <tt>true</tt> if the injection point is a decorator delegate
	 *         injection point, and <tt>false</tt> otherwise
	 */
	boolean isDelegate();
}