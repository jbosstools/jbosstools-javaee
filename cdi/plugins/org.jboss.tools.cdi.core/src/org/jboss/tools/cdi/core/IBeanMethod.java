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

import java.util.List;

import org.eclipse.jdt.core.IMethod;

/**
 * Represents a method declared by a bean class.
 * 
 * @author Alexey Kazakov
 */
public interface IBeanMethod extends IBeanMember {

	/**
	 * Returns the method.
	 * 
	 * @return the method.
	 */
	IMethod getMethod();

	/**
	 * Returns the list of parameters of the method.
	 * 
	 * @return
	 */
	List<IParameter> getParameters();
}