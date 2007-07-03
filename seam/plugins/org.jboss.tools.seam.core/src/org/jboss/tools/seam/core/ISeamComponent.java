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

import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponentMethod;

public interface ISeamComponent extends ISeamContextVariable {

	public ISeamJavaComponentDeclaration getJavaDeclaration();

	public Set<ISeamXmlComponentDeclaration> getXmlDeclarations();

	public Set<ISeamPropertiesDeclaration> getPropertiesDeclarations();

	public Set<ISeamComponentDeclaration> getAllDeclarations();

	/**
	 * @return qualified Class name of component
	 */
	public String getClassName();

	/**
	 * @return bijected attributes
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
	 * Returns properties by name from component.xml.
	 * @param propertyName
	 * @return
	 */
	public Collection<ISeamProperty> getProperties();

	/**
	 * Returns all properties from component.xml for that component.
	 * @param propertyName
	 * @return
	 */
	public List<ISeamProperty> getProperties(String propertyName);
}