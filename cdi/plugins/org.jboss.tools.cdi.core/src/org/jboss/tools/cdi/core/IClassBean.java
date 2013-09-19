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

import java.util.Collection;

import org.jboss.tools.common.java.IJavaReference;

/**
 * Represents a class-based bean.
 * 
 * @author Alexey Kazakov
 */
public interface IClassBean extends IBean, IInterceptorBinded, IJavaReference {

	/**
	 * Returns a set of producers that are declared in this bean class.
	 * 
	 * @return a set of producers that are declared in this bean class.
	 */
	Collection<IProducer> getProducers();

	/**
	 * Returns a set of disposer methods that are declared in this bean class.
	 * 
	 * @return a set of disposer methods that are declared in this bean class.
	 */
	Collection<IBeanMethod> getDisposers();

	/**
	 * Returns a set of bean constructors of the bean.
	 * 
	 * @return a set of bean constructors of the bean.
	 */
	Collection<IBeanMethod> getBeanConstructors();

	/**
	 * Returns a set of the methods that are declared in this bean class.
	 * 
	 * @return a set of the methods that are declared in this bean class.
	 */
	Collection<IBeanMethod> getAllMethods();

	/**
	 * Returns a set of observer methods of the bean.
	 * 
	 * @return a set of observer methods of the bean
	 */
	Collection<IObserverMethod> getObserverMethods();

	/**
	 * Returns all the directly derived classes that declare annotation @Specializes
	 * 
	 * @return all the directly derived classes that declare annotation @Specializes
	 */
	Collection<? extends IClassBean> getSpecializingBeans();

	/**
	 * Returns class bean of super class of this bean, or null if it is not a bean.
	 * 
	 * @return class bean of super class of this bean, or null if it is not a bean
	 */
	IClassBean getSuperClassBean();

	/**
	 * Obtains the initializer methods of the class bean.
	 * 
	 * @return the initializer methods of the class bean
	 */
	Collection<IInitializerMethod> getInitializers();

	/**
	 * Returns injection points declared in the bean class.
	 * If all = false, the result includes only injection points managed by this bean, and 
	 * since injection points declared in producer methods are handled by producer method beans,
	 * they are not included. In this case, result is the same as that of getInjectionPoints().
	 * If all = true, the result includes injection points declared in all the producer methods declared in the class.
	 *
	 * @param all
	 * @return
	 */
	Collection<IInjectionPoint> getInjectionPoints(boolean all);

	/**
	 * Returns value of annotation @Priority(int) if it is present for CDI 1.1.
	 * Otherwise, returns null.
	 * @return
	 */
	Integer getPriority();
}