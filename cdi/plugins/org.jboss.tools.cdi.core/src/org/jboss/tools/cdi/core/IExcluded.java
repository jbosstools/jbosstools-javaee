/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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
public interface IExcluded {

	public IPath getSource();

	/**
	 * 
	 * @param type
	 * @return
	 */
	public boolean isExcluded(String typeName);

	/**
	 * 
	 * @return
	 */
	public boolean isEnabled(ICDIProject project);
}
