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

import java.util.List;
import java.util.Set;

/**
 * @author Alexey Kazakov
 *
 */
public interface ISeamPropertiesDeclaration extends ISeamComponentDeclaration {

	/**
	 * Returns all properties from component.xml for that component.
	 * @param propertyName
	 * @return
	 */
	public List<ISeamProperty> getProperties(String propertyName);

	/**
	 * Returns first property with propertyName from component.xml for that component.
	 * @param propertyName
	 * @return
	 */
	public ISeamProperty getProperty(String propertyName);

	/**
	 * Returns properties by name from component.xml.
	 * @param propertyName
	 * @return
	 */
	public Set<ISeamProperty> getProperties();

	/**
	 * Adds property to component.
	 * @param property
	 */
	public void addProperty(ISeamProperty property);

	/**
	 * Removes property from component.
	 * @param property
	 */
	public void removeProperty(ISeamProperty property);
}