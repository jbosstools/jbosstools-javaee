 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.core;

import org.eclipse.core.runtime.IAdaptable;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * @author Viacheslav Kabanovich
 */
public interface ISeamDeclaration extends ISeamElement, IAdaptable {
	
	/**
	 * @return name of this declaration
	 */
	public String getName();
	
	/**
	 * @param path
	 * @return source reference for some member of declaration.
	 * e.g. if you need source reference for @Name you have to 
	 * invoke getLocationFor("name");
	 */
	public ITextSourceReference getLocationFor(String path);
}