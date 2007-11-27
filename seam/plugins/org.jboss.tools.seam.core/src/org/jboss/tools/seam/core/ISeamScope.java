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

import java.util.Collection;
import java.util.List;

/**
 * @author Viacheslav Kabanovich
 */
public interface ISeamScope extends ISeamElement {
	
	/**
	 * 
	 * @return ScopeType object identifying this object in project
	 */
	public ScopeType getType();
	
	/**
	 *  
	 * @return list of all seam components resolved to this scope
	 */
	public List<ISeamComponent> getComponents();
	
	/**
	 * 
	 * @return collection of root packages
	 */
	public Collection<ISeamPackage> getPackages();
	
	/**
	 * 
	 * @return collection of all packages
	 */
	public Collection<ISeamPackage> getAllPackages();
	
	/**
	 * 
	 * @param c
	 * @return
	 */
	public ISeamPackage getPackage(ISeamComponent c);

}
