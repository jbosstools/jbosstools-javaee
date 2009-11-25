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
 * Represents an interceptor binding type.
 * 
 * @author Alexey Kazakov
 */
public interface IInterceptorBinding extends ICDIElement {

	/**
	 * Returns the corresponding IType of the interceptor binding.
	 * 
	 * @return the corresponding IType
	 */
	IType getSourceType();

	/**
	 * Returns the interceptor binding declarations of the interceptor binding.
	 * 
	 * @return the interceptor binding declarations.
	 */
	Set<IInterceptorBindingDeclaration> getInterceptorBindingDeclarations();
}