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
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.event.ISeamProjectChangeListener;

public interface ISeamProject extends IProjectNature, ISeamElement {

	public static String NATURE_ID = "org.jboss.tools.seam.core.seam";

	/**
	 * Returns list of scope objects for all scope types.
	 * @return
	 */
	public ISeamScope[] getScopes();
	
	/**
	 * Returns scope object for specified scope type.
	 * @param scopeType
	 * @return
	 */
	public ISeamScope getScope(ScopeType scopeType);

	/**
	 * Returns seam component. Project can have only one ISeamComponent with
	 * unique name. If project has a few seam components with the same name,
	 * then corresponding ISeamComponent will have all declarations
	 * of components with this name.
	 * @param name of component.
	 * @return ISeamComponent.
	 */
	public ISeamComponent getComponent(String name);

	/**
	 * @param scope type.
	 * @return Set of ISeamComponents by Scope Type.
	 */
	public Set<ISeamComponent> getComponentsByScope(ScopeType type);

	/**
	 * @param resource path of ISeamComponentDeclaration that belongs to component
	 * @return Set of ISeamComponents by resource path.
	 */
	public Set<ISeamComponent> getComponentsByPath(IPath path);

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
	
	/**
	 * Adds listener to the project
	 * @param listener
	 */
	public void addSeamProjectListener(ISeamProjectChangeListener listener);

	/**
	 * Removes listener from the project
	 * @param listener
	 */
	public void removeSeamProjectListener(ISeamProjectChangeListener listener);
	
}