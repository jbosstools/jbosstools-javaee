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
 * Represents an element that can has stereotypes.
 * 
 * @author Alexey Kazakov
 */
public interface IStereotyped {

	/**
	 * Obtains the stereotype declarations of the element (bean class or
	 * producer method or field or stereotype).
	 * 
	 * @return the set of stereotype declarations
	 */
	Set<IStereotypeDeclaration> getStereotypeDeclarations();
}