/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

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

}
