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
package org.jboss.tools.cdi.core.test.tck;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class ValidationTest extends TCKTest {

	/**
	 * 	 2.7.1.3. Stereotype declares a non-empty @Named annotation (Non-Portable behavior)
	 */
	public void testNonEmptyNamedForStereotype() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/nonEmptyNamed/StereotypeWithNonEmptyNamed_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.STEREOTYPE_DECLARES_NON_EMPTY_NAME, 31);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithNonEmptyNamed_Broken.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 * 3.5.1. Declaring a resource
	 * 	- producer field declaration specifies an EL name (together with one of @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef)
	 * 
	 * @throws Exception
	 */
	public void testResourceWithELName() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/resources/ProducerFieldsBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, 15, 19, 24, 27, 31);
	}

	/**
	 * 3.11. The qualifier @Named at injection points
	 *  - injection point other than injected field declares a @Named annotation that does not specify the value member
	 * 
	 * @throws Exception
	 */
	public void testNamedInjectPoint() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/NamedInjectionBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PARAM_INJECTION_DECLARES_EMPTY_NAME, 10, 16);
	}

	/**
	 * 2.5.3. Beans with no EL name
	 *	- interceptor or decorator has a name (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testNamedInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/NamedInterceptorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.INTERCEPTOR_HAS_NAME, 9);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/NamedStereotypedInterceptorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.INTERCEPTOR_HAS_NAME, 7);
	}

	/**
	 * 2.5.3. Beans with no EL name
	 *	- interceptor or decorator has a name (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testNamedDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/NamedDecoratorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DECORATOR_HAS_NAME, 10);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/NamedStereotypedDecoratorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DECORATOR_HAS_NAME, 8);
	}

	/**
	 * 2.2.2. Restricting the bean types of a bean
	 *	      - bean class or producer method or field specifies a @Typed annotation, 
	 *		  and the value member specifies a class which does not correspond to a type 
	 *		  in the unrestricted set of bean types of a bean
	 */
	public void testLegalTypesInTyped() throws Exception {
		IFile petShopFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(petShopFile, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 25);
		int markerNumbers = getMarkersNumber(petShopFile);
		assertEquals("PetShop.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 * 	 2.7.1.3. Stereotype declares any other qualifier annotation
	 */
	public void testAnnotatedStereotype() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithBindingTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, 30);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithBindingTypes_Broken.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 * 	 2.7.1.3. Stereotype is annotated @Typed
	 */
	public void testTypedStereotype() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithTyped_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.STEREOTYPE_IS_ANNOTATED_TYPED, 15);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 *  2.7.1.1. Declaring the default scope for a stereotype
	 *           - stereotype declares more than one scope
	 */
	public void testStereotypeScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/tooManyScopes/StereotypeWithTooManyScopeTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, 32, 33);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTooManyScopeTypes_Broken.java should has two error markers.", markerNumbers, 2);
	}

	/**
	 *  2.4.3. Declaring the bean scope
	 *         - bean class or producer method or field specifies multiple scope type annotations
	 */
	public void testMultipleBeanScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/scope/broken/tooManyScopes/BeanWithTooManyScopeTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, 22, 23);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithTyped_Broken.java should has two error markers.", markerNumbers, 2);
	}

	/**
	 *  2.4.4. Default scope
	 *         - bean does not explicitly declare a scope when there is no default scope 
	 *         (there are two different stereotypes declared by the bean that declare different default scopes)
	 */
	public void testBeanWithMultipleScopedStereotypes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/scopeConflict/Scallop_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, 24, 25);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("Scallop_Broken.java should has two error markers.", markerNumbers, 2);
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}