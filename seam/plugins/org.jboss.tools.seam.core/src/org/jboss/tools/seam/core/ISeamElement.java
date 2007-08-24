/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * Common interface for objects of seam model.
 *  
 * @author Viacheslav Kabanovich
 */
public interface ISeamElement extends Cloneable {
	
	/**
	 * Returns seam project that contains this object.
	 * @return
	 */
	public ISeamProject getSeamProject();

	/**
	 * Returns parent object of seam model.
	 * @return
	 */
	public ISeamElement getParent();
	
	/**
	 * Returns path of resource that declares this object.
	 * @return
	 */
	public IPath getSourcePath();

	/**
	 * Returns resource that declares this object.
	 * @return resource 
	 */
	public IResource getResource();
	
	public ISeamElement clone() throws CloneNotSupportedException;

}
