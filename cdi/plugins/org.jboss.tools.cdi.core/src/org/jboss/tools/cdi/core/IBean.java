/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;

import java.util.Set;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Represents a CDI bean.
 * 
 * @author Alexey Kazakov
 */
public interface IBean extends ICDIElement {

	/**
	 * Obtains the types of the bean.
	 * 
	 * @return the bean types
	 */
	Set<IType> getTypes();

	/**
	 * Returns the location of a type declaration of this bean. If the bean
	 * doesn't have such declaration then null will be returned.
	 * 
	 * @param type
	 * @return the location of the type of this bean.
	 */
	ITextSourceReference getTypeLocation(IType type);

	/**
	 * Obtains the qualifiers of the bean.
	 * 
	 * @return the qualifiers
	 */
	Set<IAnnotation> getQualifiers();

	/**
	 * Returns the location of a qualifier declaration of this bean. If the bean
	 * doesn't have such declaration then null will be returned.
	 * 
	 * @param qualifier
	 * @return the location of the qualifier declaration of this bean.
	 */
	ITextSourceReference getQualifierLocation(IType qualifier);

	/**
	 * Obtains the EL name of the bean, if it has one.
	 * 
	 * @return the EL name
	 */
	String getName();

	/**
	 * Returns the location of a name declaration of this bean. If the bean
	 * doesn't have the name declaration then null will be returned.
	 * 
	 * @return the location of a name declaration of this bean.
	 */
	ITextSourceReference getNameLocation();

	/**
	 * Obtains the scope of the bean.
	 * 
	 * @return the scope
	 */
	IScope getScope();

	/**
	 * Returns the location of a scope declaration of this bean. If the bean
	 * doesn't have the scope declaration then null will be returned.
	 * 
	 * @return the location of a scope declaration of this bean.
	 */
	ITextSourceReference getScopeLocation();

	/**
	 * Obtains the stereotypes of the bean.
	 * 
	 * @return the set of stereotypes
	 */
	Set<IStereotype> getStereotypes();

	/**
	 * Returns the location of a stereotype declaration of this bean. If the
	 * bean doesn't have such stereotype declaration then null will be returned.
	 * 
	 * @param qualifier
	 * @return the location of the stereotype declaration of this bean.
	 */
	ITextSourceReference getStereotypeLocation(IStereotype stereotype);

	/**
	 * Determines if the bean is an alternative.
	 * 
	 * @return <tt>true</tt> if the bean is an alternative, and <tt>false</tt>
	 *         otherwise.
	 */
	boolean isAlternative();

	/**
	 * Obtains the injection points of the bean.
	 * 
	 * @return the set of injection points of the bean
	 */
	Set<IInjectionPoint> getInjectionPoints();

}