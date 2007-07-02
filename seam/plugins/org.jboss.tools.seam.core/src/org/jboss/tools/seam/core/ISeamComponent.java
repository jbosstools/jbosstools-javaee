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
import java.util.Set;

public interface ISeamComponent extends ISeamContextVariable {

	/**
	 * @return qualified Class name of component
	 */
	public String getClassName();

	/**
	 * Sets qualified Class name of component
	 * @param className
	 */
	public void setClassName(String className);

	/**
	 * @return bijected attributes
	 */
	public Set<IBijectedAttribute> getBijectedAttributes();

	/**
	 * Adds bijected attribute
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute);

	/**
	 * Removes bijected attribute
	 */
	public void removeBijectedAttribute(IBijectedAttribute attribute);

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

	/**
	 * @return sources
	 */
	public Set<ISeamSource> getSourceDeclarations();

	/**
	 * @param source
	 */
	public void addSourceDeclaration(ISeamSource source);

	/**
	 * Removes source
	 */
	public void removeSourceDeclaration(ISeamSource source);

	/**
	 * @return true if component marked as Stateful
	 */
	public boolean isStateful();

	/**
	 * Set true if component marked as Stateful
	 */
	public void setStateful(boolean stateful);

	/**
	 * @return true if component marked as Entity
	 */
	public boolean isEntity();

	/**
	 * Set true if component marked as Entity
	 */
	public void setEntity(boolean entity);

	/**
	 * @return roles of component
	 */
	public Set<IRole> getRoles();

	/**
	 * Adds role to component
	 * @param role
	 */
	public void addRole(IRole role);

	/**
	 * Removes role from component
	 * @param role
	 */
	public void removeRole(IRole role);

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
	 * Adds method to component
	 * @param method
	 */
	public void addMethod(ISeamComponentMethod method);

	/**
	 * Removes method from component
	 * @param method
	 */
	public void removeMethod(ISeamComponentMethod method);

	/**
	 * @return Factories methods of component
	 */
	public Set<ISeamAnnotatedFactory> getFactories();

	/**
	 * Adds factory method
	 * @param factory
	 */
	public void addFactory(ISeamAnnotatedFactory factory);

	/**
	 * Remove factory method
	 * @param factory
	 */
	public void removeFactory(ISeamAnnotatedFactory factory);
}