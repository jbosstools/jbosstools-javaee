/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

import org.eclipse.core.runtime.IPath;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IDefinitionContext {

	/**
	 * Returns existing working copy of original context, or this object if it is a working copy.
	 * 
	 * @return
	 */
	public IDefinitionContext getWorkingCopy();

	/**
	 * Creates copy of this object and makes it the working copy bounded to this object.
	 * 
	 * @param forFullBuild
	 */
	public void newWorkingCopy(boolean forFullBuild);

	/**
	 * Submits loaded definitions to original context.
	 */
	public void applyWorkingCopy();

	/**
	 * Removes all definitions.
	 */
	public void clean();

	/**
	 * Removes difinitions loaded from path.
	 * 
	 * @param path
	 */
	public void clean(IPath path);

	/**
	 * Removes definitions loaded from type
	 * 
	 * @param typeName
	 */
	public void clean(String typeName);

}
