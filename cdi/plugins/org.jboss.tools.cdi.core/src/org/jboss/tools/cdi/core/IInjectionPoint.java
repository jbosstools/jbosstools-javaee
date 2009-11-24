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

import org.eclipse.jdt.core.IType;

/**
 * Represents an injection point.
 * 
 * @author Alexey Kazakov
 */
public interface IInjectionPoint extends ICDIElement, IBeanMember {

	/**
	 * Returns the required type of this injection point.
	 * 
	 * @return the required type of this injection point.
	 */
	IType getType();

	/**
	 * Gets the required qualifiers of the injection point.
	 * 
	 * @return the required qualifiers
	 */
	Set<IAnnotationDeclaration> getQualifierDeclarations();

	/**
	 * Determines if the injection point is a decorator delegate injection
	 * point.
	 * 
	 * @return <tt>true</tt> if the injection point is a decorator delegate
	 *         injection point, and <tt>false</tt> otherwise
	 */
	boolean isDelegate();

	/**
	 * Returns the @Decorator annotation of this injection point field or
	 * parameter of injection point method. Should not return null if
	 * isDelegate() returns "true".
	 * 
	 * @return the @Decorator annotation of this injection point field or
	 *         parameter of injection point method. May be null.
	 */
	IAnnotationDeclaration getDecoratorAnnotation();
}