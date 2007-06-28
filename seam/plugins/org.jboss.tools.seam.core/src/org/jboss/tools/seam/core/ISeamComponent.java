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

import org.eclipse.jdt.core.IType;

public interface ISeamComponent extends ISeamModelObject {

	/**
	 * @return Name
	 */
	public String getName();

	/**
	 * Sets name
	 * @param name
	 */
	public void setName(String name);

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
	 * @return scope type
	 */
	public ScopeType getScope();

	/**
	 * Sets scope type
	 * @param type
	 */
	public void setScope(ScopeType type);

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
	public List<ISeamProperty<? extends Object>> getProperties(String propertyName);

	/**
	 * Returns properties by name from component.xml.
	 * @param propertyName
	 * @return
	 */
	public Set<ISeamProperty<? extends Object>> getProperties();

	/**
	 * Adds property to component.
	 * @param property
	 */
	public void addProperty(ISeamProperty<? extends Object> property);

	/**
	 * Removes property from component.
	 * @param property
	 */
	public void removeProperty(ISeamProperty<? extends Object> property);

	/**
	 * @return Set of Java source classes
	 */
	public Set<IType> getJavaSourceClasses();

	/**
	 * @param sourceClass
	 */
	public void addJavaSourceClass(IType sourceClass);

	/**
	 * @param sourceClass
	 */
	public void removeJavaSourceClass(IType sourceClass);

	/**
	 * @return Set of source xml elements
	 */
	public Set<ISeamXmlElement> getXmlSourceElements();

	/**
	 * @param element
	 */
	public void addmlSourceElement(ISeamXmlElement element);

	/**
	 * @param element
	 */
	public void removemlSourceElement(ISeamXmlElement element);

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
	 * @return Factories methods and xml elements of component
	 */
	public Set<ISeamFactory> getFactories();

	/**
	 * Adds factory method or xml element
	 * @param factory
	 */
	public void addFactory(ISeamFactory factory);

	/**
	 * Remove factory method or xml element
	 * @param factory
	 */
	public void removeFactory(ISeamFactory factory);
}