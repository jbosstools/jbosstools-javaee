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
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class BeansXmlValidationTest extends ValidationTest {
	static int BAR_DECORATOR_LINE = 35; // <class>org.jboss.jsr299.tck.tests.decorators.resolution.BarDecorator</class>

	static int FOO_LINE = 44; // <class>com.acme.Foo</class>
	static int CAT_INTERCEPTOR_LINE = FOO_LINE + 1; // <class>org.jboss.jsr299.tck.tests.jbt.validation.interceptors.CatInterceptor</class>
	static int NON_INTERCEPTOR_LINE = CAT_INTERCEPTOR_LINE + 1; // <class>org.jboss.jsr299.tck.tests.interceptors.definition.broken.nonInterceptorClassInBeansXml.Foo</class>
	static int FORD_INTERCEPTOR_1_LINE = CAT_INTERCEPTOR_LINE + 3; // <class>org.jboss.jsr299.tck.tests.interceptors.definition.broken.sameClassListedTwiceInBeansXml.FordInterceptor</class>
	static int FORD_INTERCEPTOR_2_LINE = FORD_INTERCEPTOR_1_LINE + 1; // 

	public void testBeansXMLInBin() throws Exception {
		IFile file = tckProject.getFile("JavaSource/META-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, "cdi.test.alternative.Unxisting"), false, 6);
		file = tckProject.getFile("WebContent/WEB-INF/classes/META-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, "cdi.test.alternative.Unxisting"), 6);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 *  - Each child <class> element must specify the name of an alternative bean class. If there is no class with the specified
	 *    name the container automatically detects the problem
	 *    and treats it as a deployment problem.
	 */
	public void testNoAlternativeClassWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, "org.jboss.jsr299.tck.tests.policy.broken.incorrect.name.NonExistingClass"), false, 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME, "org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Dog"), 19);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 *  - Each child <class> element must specify the name of an alternative bean class. If the class with the specified name is not an alternative bean class,
	 *    the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testIllegalAlternativeClassWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_ALTERNATIVE_BEAN_CLASS, "org.jboss.jsr299.tck.tests.policy.broken.not.policy.Cat"), 4);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_ALTERNATIVE_BEAN_CLASS, "org.jboss.jsr299.tck.tests.policy.broken.same.type.twice.Dog"), 19);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - Each child <stereotype> element must specify the name of an @Alternative stereotype annotation. If there is no annotation
	 *   with the specified name the container automatically detects the
	 *   problem and treats it as a deployment problem.
	 */
	public void testNoAlternativeAnnotationWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME, "org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.NotExistingStereotype"), 12);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME, "org.jboss.jsr299.tck.tests.jbt.validation.beansxml.AlternativeStereotype"), 17);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - Each child <stereotype> element must specify the name of an @Alternative stereotype annotation. If the annotation is not an @Alternative stereotype,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testIllegalAlternativeAnnotationWithSpecifiedName() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_ALTERNATIVE_ANNOTATION, "org.jboss.jsr299.tck.tests.policy.broken.not.policy.stereotype.Mock"), 15);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_ALTERNATIVE_ANNOTATION, "org.jboss.jsr299.tck.tests.jbt.validation.beansxml.AlternativeStereotype"), 17);
	}

	/**
	 * 5.1.1. Declaring selected alternatives for a bean archive
	 * - If the same type is listed twice under the <alternatives> element, the container automatically detects the problem and
	 *   treats it as a deployment problem.
	 */
	public void testSameAlternativeClassListedTwice() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 20, 22, 26, 27);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 17);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_ALTERNATIVE_TYPE, 19);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - Each child <class> element must specify the name of a decorator bean class. If there is no class with the specified name,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonExistantDecoratorClassInBeansXmlNotOK() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_DECORATOR_BEAN_CLASS_NAME, "com.acme.NonExistantDecoratorClass"), 34);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_DECORATOR_BEAN_CLASS_NAME, "org.jboss.jsr299.tck.tests.decorators.resolution.BarDecorator"), BAR_DECORATOR_LINE);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - Each child <class> element must specify the name of a decorator bean class. If the class with the specified name is not a decorator bean class,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testEnabledDecoratorNotADecorator() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_DECORATOR_BEAN_CLASS, "org.jboss.jsr299.tck.tests.decorators.definition.broken.enabledDecoratorIsNotDecorator.TimestampLogger"), 37);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_DECORATOR_BEAN_CLASS, "org.jboss.jsr299.tck.tests.decorators.resolution.BarDecorator"), BAR_DECORATOR_LINE);
	}

	/**
	 * 8.2. Decorator enablement and ordering
	 * - If the same class is listed twice under the <decorators> element, the container automatically detects the problem and
	 *   treats it as a deployment problem.
	 */
	public void testDecoratorListedTwiceInBeansXmlNotOK() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_DECORATOR_CLASS, 39, 40);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_DECORATOR_CLASS, BAR_DECORATOR_LINE);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - Each child <class> element must specify the name of an interceptor class. If there is no class with the specified name
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonExistantClassInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME, "com.acme.Foo"), FOO_LINE);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME, "org.jboss.jsr299.tck.tests.jbt.validation.interceptors.CatInterceptor"), CAT_INTERCEPTOR_LINE);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - Each child <class> element must specify the name of an interceptor class. If the class with the specified name is not an interceptor class,
	 *   the container automatically detects the problem and treats it as a deployment problem.
	 */
	public void testNonInterceptorClassInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_INTERCEPTOR_CLASS, "org.jboss.jsr299.tck.tests.interceptors.definition.broken.nonInterceptorClassInBeansXml.Foo"), NON_INTERCEPTOR_LINE);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, NLS.bind(CDIValidationMessages.ILLEGAL_INTERCEPTOR_CLASS, "org.jboss.jsr299.tck.tests.jbt.validation.interceptors.CatInterceptor"), CAT_INTERCEPTOR_LINE);
	}

	/**
	 * 9.4. Interceptor enablement and ordering
	 * - If the same class is listed twice under the <interceptors> element, the container automatically detects the problem and treats it as
	 *   a deployment problem.
	 */
	public void testSameInterceptorClassListedTwiceInBeansXmlNotOk() throws Exception {
		IFile file = tckProject.getFile("WebContent/WEB-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.DUPLICATE_INTERCEPTOR_CLASS, FORD_INTERCEPTOR_1_LINE, FORD_INTERCEPTOR_2_LINE);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, CDIValidationMessages.DUPLICATE_INTERCEPTOR_CLASS, CAT_INTERCEPTOR_LINE);
	}
}