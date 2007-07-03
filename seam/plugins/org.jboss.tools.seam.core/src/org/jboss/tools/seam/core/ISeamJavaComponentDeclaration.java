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

import java.util.Set;

/**
 * @author Alexey Kazakov
 *
 */
public interface ISeamJavaComponentDeclaration extends ISeamComponentDeclaration {
	
	public String getClassName();

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
	 * @return true if component marked as Stateful
	 */
	public boolean isStateful();

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
}