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

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.text.INodeReference;

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
	 * @param attemptToResolveAmbiguousDependency
	 *            if there are a few beans with the given type and qualifiers
	 *            and attemptToResolveAmbiguousDependency==true the manager
	 *            should try to resolve the ambiguity. If any of the beans are
	 *            alternatives, the manager will eliminate all beans that are
	 *            not alternatives, expect for producer methods and fields of
	 *            beans that are alternatives.
	 * 
	 * @return the resulting set of beans
	 */
	Set<IBean> getBeans(boolean attemptToResolveAmbiguousDependency, IType beanType, IAnnotationDeclaration... qualifiers);

	/**
	 * Returns the set of beans which are eligible for the given injection
	 * points.
	 * 
	 * @param injectionPoints
	 * @return the resulting set of beans
	 */
	Set<IBean> getBeans(IInjectionPoint injectionPoints);

	/**
	 * Returns the bean which is declared in the given IType.
	 * 
	 * @param type
	 * @return the bean which is declared in the given IType
	 */
	IClassBean getBeanClass(IType type);

	/**
	 * Returns the set of beans by resource path. 

	 * @param resource path
	 * @return the set of beans by resource path.
	 */
	Set<IBean> getBeans(IPath path);

	/**
	 * Returns all the available qualifier types.
	 * 
	 * @return all the available qualifier types.
	 */
	Set<IType> getQualifierTypes();

	/**
	 * Returns all the available stereotypes.
	 * 
	 * @return all the available stereotypes.
	 */
	Set<IType> getStereotypes();

	/**
	 * Returns the set of observers for an event which is injected by given
	 * injection point.
	 * 
	 * @param injectionPoint
	 * @return the set of observers for an event which is injected by given
	 *         injection point
	 */
	Set<IObserverMethod> resolveObserverMethods(IInjectionPoint injectionPoint);

	/**
	 * Applies the ambiguous dependency resolution rules to a set of beans.
	 * 
	 * @param beans
	 *            a set of beans
	 * @return resolved beans
	 */
	Set<IBean> resolve(Set<IBean> beans);

	/**
	 * Tests the given annotation type to determine if it is a scope type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return true if the annotation type is a scope type
	 */
	boolean isScope(IType annotationType);

	/**
	 * Tests the given annotation type to determine if it is a normal scope
	 * type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <tt>true</tt> if the annotation type is a normal scope type
	 */
	boolean isNormalScope(IType annotationType);

	/**
	 * Tests the given annotation type to determine if it is a passivating scope
	 * type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <tt>true</tt> if the annotation type is a passivating scope type
	 */
	boolean isPassivatingScope(IType annotationType);

	/**
	 * Tests the given annotation type to determine if it is a qualifier type.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <tt>true</tt> if the annotation type is a qualifier type
	 */
	boolean isQualifier(IType annotationType);

	/**
	 * Tests the given annotation type to determine if it is a stereotype.
	 * 
	 * @param annotationType
	 *            the annotation type
	 * @return <tt>true</tt> if the annotation type is a stereotype
	 */
	boolean isStereotype(IType annotationType);

	/**
	 * Returns the source reference to <class>...</class> element of
	 * <alternatives> of beans.xml. For example:
	 *     <alternatives>
	 *        <class>...</class>
	 *     </alternatives>
	 * 
	 * @return the source reference to <class>...</class> element of
	 *         <alternatives> of beans.xml.
	 */
	List<INodeReference> getAlternativeClasses();

	/**
	 * Returns the source reference to <stereotype>...</stereotype> element of
	 * <alternatives> of beans.xml. For example:
	 *     <alternatives>
	 *        <stereotype>...</stereotype>
	 *     </alternatives>
	 * 
	 * @return the source reference to <stereotype>...</stereotype> element of
	 *         <alternatives> of beans.xml.
	 */
	List<INodeReference> getAlternativeStereotypes();

	/**
	 * Returns the source reference to <stereotype>...</stereotype> or <class>...<class> element of
	 * <alternatives> of beans.xml by its full qualified type name. For example:
	 *     <alternatives>
	 *        <stereotype>org.compony.Log</stereotype>
	 *        <class>org.compony.Item</class>
	 *     </alternatives>
	 * 
	 * @return the source reference to <stereotype>...</stereotype> or <class>...</class> element of
	 *         <alternatives> of beans.xml by its full qualified type name
	 */
	List<INodeReference> getAlternatives(String fullQualifiedTypeName);

	/**
	 * Returns the source reference to <class>...</class> element of
	 * <decorators> of beans.xml. For example:
	 *     <decorators>
	 *        <class>...</class>
	 *     </decorators>
	 * 
	 * @return the source reference to <class>...</class> element of
	 *         <decorators> of beans.xml.
	 */
	List<INodeReference> getDecoratorClasses();

	/**
	 * Returns the source reference to <class>...</class> element of
	 * <decorators> of beans.xml by its full qualified type name. For example:
	 *     <decorators>
	 *        <class>org.compony.Item</class>
	 *     </decorators>
	 * 
	 * @return the source reference to <class>...</class> element of
	 *         <decorators> of beans.xml by its full qualified type name.
	 */
	List<INodeReference> getDecoratorClasses(String fullQualifiedTypeName);

	/**
	 * Returns the source reference to <class>...</class> element of
	 * <interceptors> of beans.xml. For example:
	 *     <interceptors>
	 *        <class>...</class>
	 *     </interceptors>
	 * 
	 * @return the source reference to <class>...</class> element of
	 *         <interceptors> of beans.xml.
	 */
	List<INodeReference> getInterceptorClasses();

	/**
	 * Returns the source reference to <class>...</class> element of
	 * <interceptors> of beans.xml by its full qualified type name. For example:
	 *     <interceptors>
	 *        <class>org.compony.Item</class>
	 *     </interceptors>
	 * 
	 * @return the source reference to <class>...</class> element of
	 *         <interceptors> of beans.xml by its full qualified type name.
	 */
	List<INodeReference> getInterceptorClasses(String fullQualifiedTypeName);
}