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

/**
 * Represents a class-based bean.
 * 
 * @author Alexey Kazakov
 */
public interface IClassBean extends IBean {

	/**
	 * Returns a set of producers that are declared in this bean class.
	 * 
	 * @return a set of producers that are declared in this bean class.
	 */
	Set<IProducer> getProducers();

	/**
	 * Returns a set of disposer methods that are declared in this bean class.
	 * 
	 * @return a set of disposer methods that are declared in this bean class.
	 */
	Set<IBeanMethod> getDisposers();

	/**
	 * Returns a set of bean constructors of the bean.
	 * 
	 * @return a set of bean constructors of the bean.
	 */
	Set<IBeanMethod> getBeanConstructors();

	/**
	 * Returns a set of the methods that are declared in this bean class.
	 * 
	 * @return a set of the methods that are declared in this bean class.
	 */
	Set<IBeanMethod> getAllMethods();

	/**
	 * Obtains the interceptor binding declarations of the class bean.
	 * This set includes bindings declared in this java class.
	 * Use getInterceptorBindings() to get all bindings (i.g. from Stereotypes)   
	 * 
	 * @return the set of interceptor binding declarations
	 */
	Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations();

	/**
	 * Obtains the interceptor bindings of the bean.
	 * 
	 * @return the set of interceptor bindings
	 */
	Set<IInterceptorBinding> getInterceptorBindings();

	/**
	 * Returns a set of observer methods of the bean.
	 * 
	 * @return a set of observer methods of the bean
	 */
	Set<IBeanMethod> getObserverMethods();


	/**
	 * Returns all directly derived classes that declare annotation @Specializes
	 * 
	 * @return
	 */
	Set<? extends IClassBean> getSpecializingBeans();

}