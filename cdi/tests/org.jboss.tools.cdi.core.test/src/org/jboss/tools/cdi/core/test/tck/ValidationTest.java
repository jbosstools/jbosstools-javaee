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
	 * 
	 * @throws Exception
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
	 * 
	 * @throws Exception
	 */
	public void testLegalTypesInTyped() throws Exception {
		IFile petShopFile = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(petShopFile, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 25);
		int markerNumbers = getMarkersNumber(petShopFile);
		assertEquals("PetShop.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 * 	 2.7.1.3. Stereotype declares any other qualifier annotation
	 * 
	 * @throws Exception
	 */
	public void testAnnotatedStereotype() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/withBindingType/StereotypeWithBindingTypes_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, 30);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("StereotypeWithBindingTypes_Broken.java should has the only error marker.", markerNumbers, 1);
	}

	/**
	 * 	 2.7.1.3. Stereotype is annotated @Typed
	 * 
	 * @throws Exception
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
	 * 
	 * @throws Exception
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
	 * 
	 * @throws Exception
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
	 * 
	 * @throws Exception
	 */
	public void testBeanWithMultipleScopedStereotypes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/scopeConflict/Scallop_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, 24, 25);
		int markerNumbers = getMarkersNumber(file);
		assertEquals("Scallop_Broken.java should has two error markers.", markerNumbers, 2);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 * 	- method has more than one parameter annotated @Disposes
	 * 
	 * @throws Exception
	 */
	public void testMultipleDisposeParameters() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/multiParams/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.MULTIPLE_DISPOSING_PARAMETERS, 30, 30);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Produces.
	 *  
	 * @throws Exception
	 */
	public void testProducesUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/producesUnallowed/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, 30, 31);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Observes.
	 *  
	 * @throws Exception
	 */
	public void testObserverParameterUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/observesUnallowed/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, 32, 32);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Inject.
	 *  
	 * @throws Exception
	 */
	public void testInitializerUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/initializerUnallowed/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 32, 33);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a non-static method of a session bean class has a parameter annotated @Disposes, and the method is not a business method of the session bean
	 *  
	 * @throws Exception
	 */
	public void testDisposalMethodNotBusinessOrStatic() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/methodOnSessionBean/AppleTree.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN, 31);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/newBean/Fox.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN, 73);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - decorators may not declare disposer methods
	 *  
	 * @throws Exception
	 */
	public void testDecoratorDeclaresDisposer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/disposers/TimestampLogger.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DISPOSER_IN_DECORATOR, 6, 9);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - interceptors may not declare disposer methods
	 *  
	 * @throws Exception
	 */
	public void testInterceptorDeclaresDisposer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/FordInterceptor.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DISPOSER_IN_INTERCEPTOR, 8, 16);
	}

	/**
	 * 3.3.7. Disposer method resolution
	 *  - there is no producer method declared by the (same) bean class that is assignable to the disposed parameter of a disposer method
	 *  
	 * @throws Exception
	 */
	public void testUnresolvedDisposalMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/unresolvedMethod/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER, 35);
	}

	/**
	 * 3.3.7. Disposer method resolution
	 *  - there are multiple disposer methods for a single producer method
	 *  
	 * @throws Exception
	 */
	public void testMultipleDisposersForProducer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/disposers/TimestampLogger_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.MULTIPLE_DISPOSERS_FOR_PRODUCER, 13, 16);
	}

	/**
	 * 3.9.1. Declaring an initializer method
	 *  - an initializer method has a parameter annotated @Disposes
	 *  
	 * @throws Exception
	 */
	public void testInitializerMethodHasParameterAnnotatedDisposes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/initializer/broken/parameterAnnotatedDisposes/Capercaillie_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 25, 1003, 1010);
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 26, 1048, 1057);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - a producer method has a parameter annotated @Disposes
	 *  
	 * @throws Exception
	 */
	public void testProducerMethodWithParameterAnnotatedDisposes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterAnnotatedDisposes/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, 25, 26);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - a producer method has a parameter annotated @Observers
	 *  
	 * @throws Exception
	 */
	public void testProducerMethodWithParameterAnnotatedObserves() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterAnnotatedObserves/SpiderProducer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, 25, 26);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - an observer method is annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodAnnotatedProducesFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/isProducer/BorderTerrier_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED, 25, 25);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - a observer method is annotated @Disposes.
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodWithDisposesParamFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/isDisposer/FoxTerrier_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, 28, 28);
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}
}