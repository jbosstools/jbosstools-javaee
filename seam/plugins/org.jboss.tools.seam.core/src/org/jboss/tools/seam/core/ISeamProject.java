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

import java.util.Set;

import org.eclipse.core.resources.IProjectNature;

public interface ISeamProject extends IProjectNature {

	public static String NATURE_ID = "org.jboss.tools.seam.core.seam";

	/**
	 * @param name of component.
	 * @return Set of ISeamComponents by name.
	 */
	public Set<ISeamComponent> getComponentsByName(String name);

	/**
	 * @param scope type.
	 * @return Set of ISeamComponents by Scope Type.
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type);

	/**
	 * @param className
	 * @return Set of ISeamComponents by class name.
	 */
	public Set<ISeamComponent> getComponentsByClass(String className);

	/**
	 * @return Set of all ISeamComponents of project
	 */
	public Set<ISeamComponent> getComponents();

	/**
	 * Adds component into project
	 * @param component
	 */
	public void addComponent(ISeamComponent component);

	/**
	 * Removes component from project
	 * @param component
	 */
	public void removeComponent(ISeamComponent component);

	/**
	 * @return all seam context variables of project
	 */
	public Set<ISeamContextVariable> getVariables();

	/**
	 * @param name
	 * @return all seam variables by name from all contexts
	 */
	public Set<ISeamContextVariable> getVariablesByName(String name);

	/**
	 * @param name
	 * @return all seam variables from specific context
	 */
	public Set<ISeamContextVariable> getVariablesByScope(ScopeType scope);

	/**
	 * @return all factories methods of project
	 */
	public Set<ISeamFactory> getFactories();

	/**
	 * @return Factories methods of project by name and scope
	 */
	public Set<ISeamFactory> getFactories(String name, ScopeType scope);

	/**
	 * @return Factories methods of project by name
	 */
	public Set<ISeamFactory> getFactoriesByName(String name);

	/**
	 * @return Factories methods of project by scope
	 */
	public Set<ISeamFactory> getFactoriesByScope(ScopeType scope);

	/**
	 * Adds factory method into project
	 * @param factory
	 */
	public void addFactory(ISeamFactory factory);

	/**
	 * Remove factory method from project
	 * @param factory
	 */
	public void removeFactory(ISeamFactory factory);
}