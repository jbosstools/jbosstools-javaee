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

import org.eclipse.jdt.core.IAnnotation;

/**
 * Represents a stereotype.
 * 
 * @author Alexey Kazakov
 */
public interface IStereotype extends IScoped, IStereotyped, ICDINamedAnnotation, IAnnotated, IInterceptorBinded {

	/**
	 * Returns the location of @Name declaration of this stereotype. If the bean
	 * doesn't have the @Name declaration then null will be returned.
	 * 
	 * @return the location of @Name declaration of this bean.
	 */
	IAnnotation getNameLocation();

	/**
	 * Determines if the stereotype is an alternative.
	 * 
	 * @return <tt>true</tt> if the stereotype is an alternative, and
	 *         <tt>false</tt> otherwise.
	 */
	boolean isAlternative();

	/**
	 * Returns the location of @Alternative declaration of this stereotype.
	 * 
	 * @return the location of @Alternative declaration.
	 */
	IAnnotationDeclaration getAlternativeDeclaration();
}