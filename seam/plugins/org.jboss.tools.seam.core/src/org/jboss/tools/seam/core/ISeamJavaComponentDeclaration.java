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

import org.jboss.tools.common.java.IJavaSourceReference;

/**
 * Represents java class of seam component. 
 * @author Alexey Kazakov
 */
public interface ISeamJavaComponentDeclaration extends ISeamComponentDeclaration, IJavaSourceReference {

	public static final int DEFAULT_PRECEDENCE = SeamComponentPrecedenceType.APPLICATION;

	/**
	 * 
	 * @return scope
	 */
	public ScopeType getScope();

	/**
	 * @return qualified class name
	 */
	public String getClassName();

	/**
	 * @return all bijected attributes which are defined in that java class.
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
	 * Adds bijected attribute
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute);

	/**
	 * Removes bijected attribute
	 */
	public void removeBijectedAttribute(IBijectedAttribute attribute);

	/**
	 * @param type
	 * @return true if class is marked with annotation referenced in type
	 */
	public boolean isOfType(BeanType type);

	/**
	 * @return true if class marked as stateful session bean
	 */
	public boolean isStateful();

	/**
	 * @return true if class marked as stateless session bean
	 */
	public boolean isStateless();

	/**
	 * @return true if class marked as Entity
	 */
	public boolean isEntity();

	/**
	 * @return roles of component which defined in this component class
	 */
	public Set<IRole> getRoles();

	/**
	 * Adds role
	 * @param role
	 */
	public void addRole(IRole role);

	/**
	 * Removes role
	 * @param role
	 */
	public void removeRole(IRole role);

	/**
	 * @return methods (see SeamComponentMethodType)
	 * which are defined in this component class.
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
	 * @return precedence of component.
	 */
	public int getPrecedence();

	/**
	 * Sets precedence of component.
	 * @param precedence
	 */
	public void setPrecedence(int precedence);

	public ISeamJavaComponentDeclaration clone() throws CloneNotSupportedException;

}