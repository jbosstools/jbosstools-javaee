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

import java.util.Collection;
import java.util.List;

/**
 * Represents set of properties of seam component
 * which were declared in one source file.
 * @author Alexey Kazakov
 */
public interface ISeamPropertiesDeclaration extends ISeamComponentDeclaration {

	/**
	 * Returns property of the component within this declaration.
	 * If the declaration has a few properties with this name,
	 * then the method will return first one.
	 * @param propertyName
	 * @return
	 */
	public ISeamProperty getProperty(String propertyName);

	/**
	 * Returns all properties of the component by name within this declaration.
	 * @param propertyName
	 * @return
	 */
	public List<ISeamProperty> getProperties(String propertyName);

	/**
	 * Returns all properties of the component within this declaration.
	 * @param propertyName
	 * @return
	 */
	public Collection<ISeamProperty> getProperties();

	/**
	 * Adds property.
	 * @param property
	 */
	public void addProperty(ISeamProperty property);

	/**
	 * Removes property.
	 * @param property
	 */
	public void removeProperty(ISeamProperty property);
}