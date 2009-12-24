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
 * This interface if common protocol to have access to scoped of bean class or
 * producer method or field or stereotype.
 * 
 * @author Alexey Kazakov
 */
public interface IScoped {

	/**
	 * Obtains the scope type of the bean class or producer method or filed or
	 * stereotype annotation. The scope may or may not be declared in the
	 * object. It may be default one or come from stereotype of the object.
	 * 
	 * @return the scope type
	 */
	IScope getScope();

	/**
	 * Obtains all the scope declarations of the bean class or producer method
	 * or filed or stereotype annotation. This set doesn't contain scope
	 * declarations from stereotypes of this object.
	 * 
	 * @return the scope
	 */
	Set<IScopeDeclaration> getScopeDeclarations();
}