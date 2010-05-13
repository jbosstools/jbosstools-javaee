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

/**
 * Represents a session bean.
 * 
 * @author Alexey Kazakov
 */
public interface ISessionBean extends IClassBean {

	/**
	 * Returns "true" if this bean is a stateful session bean.
	 * 
	 * @return "true" if this bean is a stateful session bean.
	 */
	boolean isStateful();

	/**
	 * Returns "true" if this bean is a stateless session bean.
	 * 
	 * @return "true" if this bean is a stateless session bean.
	 */
	boolean isStateless();

	/**
	 * Returns "true" if this bean is a singleton session bean.
	 * 
	 * @return "true" if this bean is a singleton session bean.
	 */
	boolean isSingleton();

	/**
	 * Returns @Statefull annotaion declaration.
	 * 
	 * @return @Statefull annotaion declaration.
	 */
	IAnnotationDeclaration getStatefulDeclaration();

	/**
	 * Returns @Stateless annotaion declaration.
	 * 
	 * @return @Stateless annotaion declaration.
	 */
	IAnnotationDeclaration getStatelessDeclaration();

	/**
	 * Returns @Singleton annotaion declaration.
	 * 
	 * @return @Singleton annotaion declaration.
	 */
	IAnnotationDeclaration getSingletonDeclaration();
}