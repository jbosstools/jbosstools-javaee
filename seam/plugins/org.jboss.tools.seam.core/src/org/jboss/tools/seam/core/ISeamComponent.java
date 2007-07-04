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

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents component in seam tools model.
 * @author Alexey Kazakov
 */
public interface ISeamComponent extends ISeamContextVariable {

	/**
	 * Returns java declaration of component.
	 * This declaration represents java class of component.
	 * @return java declaration of component.
	 */
	public ISeamJavaComponentDeclaration getJavaDeclaration();

	/**
	 * Returns XML declarations of component.
	 * These declarations represent <component> elements of components.xml files.
	 * @return java declaration of component.
	 */
	public Set<ISeamXmlComponentDeclaration> getXmlDeclarations();

	/**
	 * Returns Properties declarations of component.
	 * These declarations represent properties from seam.properties file.
	 * @return
	 */
	public Set<ISeamPropertiesDeclaration> getPropertiesDeclarations();

	/**
	 * @return all declarations of component
	 */
	public Set<ISeamComponentDeclaration> getAllDeclarations();

	/**
	 * @return qualified Class name of component
	 */
	public String getClassName();

	/**
	 * @return bijected attributes of component
	 */
	public Set<IBijectedAttribute> getBijectedAttributes();

	/**
	 * Returns bijected attributes by name
	 * @param name
	 * @return
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByName(String name);

	/**
	 * Returns bijected attributes by type
	 * @param type
	 * @return
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByType(BijectedAttributeType type);

	/**
	 * @return true if component marked as Stateful
	 */
	public boolean isStateful();

	/**
	 * @return true if component marked as Entity
	 */
	public boolean isEntity();

	/**
	 * @return roles of component
	 */
	public Set<IRole> getRoles();

	/**
	 * @return methods of component
	 */
	public Set<ISeamComponentMethod> getMethods();

	/**
	 * @param type
	 * @return return methods by type
	 */
	public Set<ISeamComponentMethod> getMethodsByType(SeamComponentMethodType type);

	/**
	 * Returns all properties from all components.xml declarations of component 
	 * @param propertyName
	 * @return
	 */
	public Collection<ISeamProperty> getProperties();

	/**
	 * Returns properties by name from all components.xml declarations of component
	 * @param propertyName
	 * @return
	 */
	public List<ISeamProperty> getProperties(String propertyName);
}