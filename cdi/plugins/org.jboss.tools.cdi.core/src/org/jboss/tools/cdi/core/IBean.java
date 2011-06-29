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
import org.jboss.tools.common.el.core.resolver.IVariable;
import org.jboss.tools.common.java.IAnnotated;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * Represents a CDI bean.
 * 
 * @author Alexey Kazakov
 */
public interface IBean extends IScoped, IStereotyped, ICDIElement, IVariable, IAnnotated {

	/**
	 * Returns the corresponding IType of the managed bean or session bean or of
	 * the bean that declares the producer method or field.
	 * 
	 * @return the bean class
	 */
	IType getBeanClass();

	/**
	 * Obtains the EL name of the bean, if it has one.
	 * 
	 * @return the EL name
	 */
	String getName();

	/**
	 * Returns the location of a name declaration of this bean. If the bean
	 * doesn't have the name declaration then null will be returned. May be
	 * declared in a stereotype. May be a declaration of @Name annotation or
	 * location of Java class name declaration.
	 * 
	 * @return the location of a name declaration of this bean.
	 */
	ITextSourceReference getNameLocation();

	/**
	 * Obtains the legal types of the bean class or producer method or field.
	 * 
	 * @return the legal types
	 */
	Set<IParametedType> getLegalTypes();

	/**
	 * Obtains all the types of the bean class or producer method or
	 * field (legal types as well as illegal ones). This set will contain inherited types of the bean.
	 * 
	 * @return the legal types
	 */
	Set<IParametedType> getAllTypes();

	/**
	 * Returns the text representation of this bean.
	 * If the bean is IClassBean then the method will return the simple name of the class of the bean.
	 * If the bean is IProducerField then the method will return the simple name of the type of the field.
	 * If the bean is IProducerMethod then the method will return the simple name of the return type of the producer method.
	 * @return the text representation of this bean.
	 */
	String getSimpleJavaName();

	/**
	 * Obtains all the type declarations of the bean class or producer method or
	 * field (legal types as well as illegal ones).
	 * 
	 * @return the type declarations
	 */
	Set<ITypeDeclaration> getAllTypeDeclarations();

	/**
	 * Obtains the type declarations of the bean class or producer method or
	 * field (legal types as well as illegal ones) which are defined by @Typed
	 * annotation.
	 * 
	 * @return the type declarations
	 */
	Set<ITypeDeclaration> getRestrictedTypeDeclaratios();

	/**
	 * Obtains the qualifier declarations of the bean class or producer method or field.
	 * This method doesn't return inherited qualifiers and equals getQualifierDeclarations(false);
	 * 
	 * @return the qualifiers
	 */
	Set<IQualifierDeclaration> getQualifierDeclarations();

	/**
	 * Obtains the qualifier declarations of the bean class or producer method
	 * or field.
	 * 
	 * @param includeInherited if "true" then the result includes declarations of inherited qualifiers.
	 * @return the qualifiers
	 */
	Set<IQualifierDeclaration> getQualifierDeclarations(boolean includeInherited);

	/**
	 * Obtains the qualifiers of the bean class or producer method or field.
	 * Note a Bean has @Any (except for the special @New) and @Default qualifiers even
	 * if there is not any Qualifier declarations.
	 *  
	 * @return the qualifiers
	 */
	Set<IQualifier> getQualifiers();

	/**
	 * Determines if the bean is an alternative.
	 * 
	 * @return <tt>true</tt> if the bean is an alternative, and <tt>false</tt>
	 *         otherwise.
	 */
	boolean isAlternative();

	/**
	 * Determines if the bean is a selected alternative.
	 * 
	 * @return <tt>true</tt> if the bean is a selected alternative, and <tt>false</tt>
	 *         otherwise.
	 */
	boolean isSelectedAlternative();

	/**
	 * Returns the location of @Alternative declaration of this bean. May be
	 * declared in a stereotype.
	 * 
	 * @return the location of @Alternative declaration.
	 */
	IAnnotationDeclaration getAlternativeDeclaration();

	/**
	 * Obtains the injection points of the bean.
	 * 
	 * @return the set of injection points of the bean
	 */
	Set<IInjectionPoint> getInjectionPoints();

	/**
	 * Returns the bean which is specialized by this bean. May return null.
	 * 
	 * @return the bean which is specialized by this bean.
	 */
	IBean getSpecializedBean();

	/**
	 * Returns the declaration of @Specializes annotation of this bean. May
	 * return null.
	 * 
	 * @return the declaration of @Specializes annotation of this bean.
	 */
	IAnnotationDeclaration getSpecializesAnnotationDeclaration();

	/**
	 * Returns "true" if this bean specializes another.
	 * 
	 * @return "true" if this bean specializes another.
	 */
	boolean isSpecializing();

	/**
	 * Returns "true" if this bean has @Depended scope.
	 * 
	 * @return "true" if this bean has @Depended scope.
	 */
	boolean isDependent();

	/**
	 * Returns "true" if the bean is enabled. Note that implementations of some
	 * sub-interfaces of IBean like IDecorator and IInterceptor use their own
	 * mechanisms of enablement.
	 * 
	 * @return "true" if the bean is enabled
	 */
	boolean isEnabled();

   /**
    * Determines if the bean is nullable such as a producer method with a non-primitive
    * return type or a producer field with a non-primitive type.
    * 
    * @return <tt>true</tt> if the {@code create()} method may return a null 
    *        value, and <tt>false</tt> otherwise
    */
	boolean isNullable();
}