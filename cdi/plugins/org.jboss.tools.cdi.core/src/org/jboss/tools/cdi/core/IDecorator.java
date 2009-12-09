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
 * Represents a decorator.
 * 
 * @author Alexey Kazakov
 */
public interface IDecorator extends IClassBean {

	/**
	 * Returns the @Decorator annotation of this bean class.
	 * 
	 * @return the @Decorator annotation of this bean class
	 */
	IAnnotationDeclaration getDecoratorAnnotation();

	/**
	 * Obtains the decorated types.
	 * 
	 * @return the set of decorated types
	 */
	Set<IParametedType> getDecoratedTypes();
}