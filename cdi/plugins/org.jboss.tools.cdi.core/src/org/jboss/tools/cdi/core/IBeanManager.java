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

import org.eclipse.jdt.core.IType;

/**
 * @author Alexey Kazakov
 */
public interface IBeanManager {

	/**
	 * Returns the set of beans which match the given EL name.
	 * 
	 * @param name
	 *            the name used to restrict the beans matched
	 * @param attemptToResolveAmbiguousNames
	 *            if there are a few beans with the given name and
	 *            attemptToResolveAmbiguousNames==true the manager should try to
	 *            resolve the EL name. If any of the beans are alternatives, the
	 *            manager will eliminate all beans that are not alternatives,
	 *            expect for producer methods and fields of beans that are
	 *            alternatives.
	 * @return the matched beans
	 */
	Set<IBean> getBeans(String name, boolean attemptToResolveAmbiguousNames);

	/**
	 * Returns the set of beans which have the given required type and qualifier
	 * type If no qualifiers are given, the
	 * {@linkplain javax.enterprise.inject.Default default qualifier} is
	 * assumed.
	 * 
	 * @param beanType
	 *            the required bean type
	 * @param qualifiers
	 *            the required qualifiers
	 * @return the resulting set of beans
	 */
	Set<IBean> getBeans(IType beanType, IAnnotationDeclaration... qualifiers);

	/**
	 * Returns the set of beans which are eligible for the given injection
	 * points.
	 * 
	 * @param injectionPoints
	 * @return the resulting set of beans
	 */
	Set<IBean> getBeans(IInjectionPoint injectionPoints);
}