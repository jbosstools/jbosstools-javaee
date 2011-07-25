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
package org.jboss.tools.cdi.seam.core.international;

import java.util.Set;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface IBundle {

	/**
	 * 
	 * @return name of this bundle
	 */
	public String getName();

	/**
	 * 
	 * @return names of properties in all locales
	 */
	public Set<String> getPropertyNames();

	/**
	 * 
	 * @param name
	 * @return property object with given name
	 */
	public IProperty getProperty(String name);

}
