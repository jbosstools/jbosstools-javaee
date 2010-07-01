/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
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
 * Represents an element which can have interceptor bindings.
 * 
 * @author Alexey Kazakov
 */
public interface IInterceptorBinded {

	/**
	 * Obtains the interceptor binding declarations of the class bean or the method of a class bean.
	 * This set includes bindings declared in this java class/method.
	 * Use getInterceptorBindings() to get all bindings (i.g. from Stereotypes)   
	 * 
	 * @return the set of interceptor binding declarations
	 */
	Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations();

	/**
	 * Obtains the interceptor bindings of the bean class or the method of a class bean.
	 * 
	 * @return the set of interceptor bindings
	 */
	Set<IInterceptorBinding> getInterceptorBindings();
}