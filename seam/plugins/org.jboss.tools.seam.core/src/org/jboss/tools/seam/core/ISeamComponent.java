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
	 * @return qualified Class name of component
	 */
	public String getClassName();

	/**
	 * @return scope type
	 */
	public ScopeType getScope();

	/**
	 * @return bijected attributes
	 */
	public Set<IBijectedAttribute> getBijectedAttributes();

	/**
	 * Adds bijected attribute
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute);

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
	 * @return Set of Java source classes
	 */
	public Set<IType> getJavaSourceClasses();

	/**
	 * @param sourceClass
	 */
	public void addJavaSourceClass(IType sourceClass);

	/**
	 * @return Set of source xml elements
	 */
	public Set<ISeamXmlElement> getXmlSourceElements();

	// TODO add @Rules @Factory ...
}