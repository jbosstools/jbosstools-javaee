/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.cdi.core.test.tck.validation;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlValidationTest extends ValidationTest {

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 *  - Each child <class> element must specify the name of an alternative bean class. If there is no class with the specified
	 *    name the container automatically detects the problem
	 *    and treats it as a deployment problem.
	 */
	public void testNoAlternativeClassWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, false, 7);
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, 19);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 *  - Each child <class> element must specify the name of an alternative bean class. If the class with the specified name is not an alternative bean class,
	 *    the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testIllegalAlternativeClassWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_ALTERNATIVE_BEAN_CLASS, 4);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_ALTERNATIVE_BEAN_CLASS, 19);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - Each child <stereotype> element must specify the name of an @Alternative stereotype annotation. If there is no annotation
	 *   with the specified name the container automatically detects the
	 *   problem and treats it as a deployment problem.
	 */
	public void testNoAlternativeAnnotationWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME, 12);
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME, 17);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - Each child <stereotype> element must specify the name of an @Alternative stereotype annotation. If the annotation is not an @Alternative stereotype,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testIllegalAlternativeAnnotationWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_ALTERNATIVE_ANNOTATION, 15);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_ALTERNATIVE_ANNOTATION, 17);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - If the same type is listed twice under the <alternatives> element, the container automatically detects the problem and
	 *   treats it as a deployment problem.
	 */
	public void testSameAlternativeClassListedTwice() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 20, 22, 26, 27);
		assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 17);
		assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 19);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - Each child <class> element must specify the name of a decorator bean class. If there is no class with the specified name,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonExistantDecoratorClassInBeansXmlNotOK() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.UNKNOWN_DECORATOR_BEAN_CLASS_NAME, 32);
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNKNOWN_DECORATOR_BEAN_CLASS_NAME, 33);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - Each child <class> element must specify the name of a decorator bean class. If the class with the specified name is not a decorator bean class,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testEnabledDecoratorNotADecorator() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_DECORATOR_BEAN_CLASS, 35);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_DECORATOR_BEAN_CLASS, 33);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - If the same class is listed twice under the <decorators> element, the container automatically detects the problem and
	 *   treats it as a deployment problem.
	 */
	public void testDecoratorListedTwiceInBeansXmlNotOK() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_DECORATOR_CLASS, 37, 38);
		assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_DECORATOR_CLASS, 33);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - Each child <class> element must specify the name of an interceptor class. If there is no class with the specified name
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonExistantClassInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME, 42);
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME, 43);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - Each child <class> element must specify the name of an interceptor class. If the class with the specified name is not an interceptor class,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonInterceptorClassInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_INTERCEPTOR_CLASS, 44);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_INTERCEPTOR_CLASS, 43);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - If the same class is listed twice under the <interceptors> element, the container automatically detects the problem and treats it as
	 *   a deployment problem.
	 */
	public void testSameInterceptorClassListedTwiceInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_INTERCEPTOR_CLASS, 46, 47);
		assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_INTERCEPTOR_CLASS, 43);
	}
}