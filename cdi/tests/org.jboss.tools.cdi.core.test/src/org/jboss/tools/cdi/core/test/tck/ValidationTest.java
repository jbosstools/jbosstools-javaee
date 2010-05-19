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
	 * 2.4.1. Built-in scope types
	 *	      - interceptor has any scope other than @Dependent (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testInterceptorWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/InterceptorWithWrongScopeBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_INTERCEPTOR, 8);
	}

	/**
	 * 2.4.1. Built-in scope types
	 *	      - decorator has any scope other than @Dependent (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testDecoratorWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/DecoratorWithWrongScopeBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_DECORATOR, 7);
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
	 * 2.5.3. Beans with no EL name
	 *	- interceptor has a name (Non-Portable behavior)
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
	 *	- decorator has a name (Non-Portable behavior)
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
	 * 2.6.1. Declaring an alternative
	 *	- interceptor is an alternative (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testAlternativeInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/AlternativeInterceptorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.INTERCEPTOR_IS_ALTERNATIVE, 7);
	}

	/**
	 * 2.6.1. Declaring an alternative
	 *	- decorator is an alternative (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testAlternativeDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/AlternativeDecoratorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.DECORATOR_IS_ALTERNATIVE, 7);
	}

	/**
	 *  2.7.1.1. Declaring the default scope for a stereotype
	 *   - stereotype declares more than one scope
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
	 * 2.7.1.3. Declaring a @Named stereotype
	 *  - stereotype declares a non-empty @Named annotation (Non-Portable behavior)
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
	 * 2.7.1.3. Declaring a @Named stereotype
	 *  - stereotype declares any other qualifier annotation
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
	 * 2.7.1.3. Declaring a @Named stereotype
	 * 	- stereotype is annotated @Typed 
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
	 * 3.1. Managed beans
	 * 	- the bean class of a managed bean is annotated with both the @Interceptor and @Decorator stereotypes 
	 * 
	 * @throws Exception
	 */
	public void testInterceptorCanNotAlsoBeDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/interceptors/definition/broken/interceptorCanNotBeDecorator/InterceptingDecorator.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.BOTH_INTERCEPTOR_AND_DECORATOR, 24, 25);
	}

	/**
	 * 3.1. Managed beans
	 * 	- managed bean with a public field declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentScopedBeanCanNotHavePublicField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/definition/dependentWithPublicField/Leopard_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD, 21);
	}

	/**
	 * 3.1. Managed beans
	 * 	- managed bean with a parameterized bean class declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentGenericManagedBeanNotOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/genericbroken/FooBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_GENERIC_TYPE, 21);
	}

	/**
	 * 3.1.4. Specializing a managed bean
	 * 	- managed bean class annotated @Specializes does not directly extend the bean class of another managed bean
	 *    (Test a specializing bean extending a non simple bean) 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassExtendsNonSimpleBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/noextend3/Cow_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
	}

	/**
	 * 3.1.4. Specializing a managed bean
	 * 	- managed bean class annotated @Specializes does not directly extend the bean class of another managed bean
	 *    (Test a specializing bean extending nothing) 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassDirectlyExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/noextend2/Cow_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
	}

	/**
	 * 3.1.4. Specializing a managed bean
	 * 	- managed bean class annotated @Specializes does not directly extend the bean class of another managed bean
	 *    (Test a specializing bean directly extending an enterprise bean class) 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassDirectlyExtendsEnterpriseBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/extendejb/Tractor_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
	}

	/**
	 * 3.1.4. Specializing a managed bean
	 * 	- managed bean class annotated @Specializes does not directly extend the bean class of another managed bean
	 *    (Test a specializing bean implementing an interface and extending nothing) 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassImplementsInterfaceAndExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/noextend1/Donkey_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a stateless session bean must belong to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testStatelessWithRequestScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/statelessWithRequestScope/Beagle_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a stateless session bean must belong to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testStatelessWithApplicationScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/statelessWithApplicationScope/Dachshund_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a stateless session bean must belong to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testStatelessWithConversationScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/statelessWithConversationScope/Boxer_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a stateless session bean must belong to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testStatelessWithSessionScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/statelessWithSessionScope/Bullmastiff_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
	}


	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a singleton bean must belong to either the @ApplicationScoped scope or to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testSingletonWithConversationScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/singletonWithConversationScope/Husky_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 24);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a singleton bean must belong to either the @ApplicationScoped scope or to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testSingletonWithSessionScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/singletonWithSessionScope/IrishTerrier_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 25);
	}

	/**
	 * 3.2. Session beans
	 *  - session bean specifies an illegal scope
	 *   (a singleton bean must belong to either the @ApplicationScoped scope or to the @Dependent pseudo-scope) 
	 * 
	 * @throws Exception
	 */
	public void testSingletonWithRequestScopeFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/broken/singletonWithRequestScope/Greyhound_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 23);
	}

	/**
	 * 3.2. Session beans
	 *  - bean class of a session bean is annotated @Interceptor 
	 * 
	 * @throws Exception
	 */
	public void testSessionBeanAnnotatedInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/SessionBeanAnnotatedInterceptorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.SESSION_BEAN_ANNOTATED_INTERCEPTOR, 8, 9);
	}

	/**
	 * 3.2. Session beans
	 *  - bean class of a session bean is annotated @Decorator 
	 * 
	 * @throws Exception
	 */
	public void testSessionBeanAnnotatedDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/SessionBeanAnnotatedDecoratorBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.SESSION_BEAN_ANNOTATED_DECORATOR, 6, 7);
	}

	/**
	 * 3.2. Session beans
	 * 	- session bean with a parameterized bean class declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentGenericSessionBeanNotOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/FooBroken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SESSION_BEAN_WITH_GENERIC_TYPE, 6);
	}

	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassDirectlyExtendsSimpleBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/directlyExtendsSimpleBean/Tractor_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
	}
	
	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingEnterpriseClassImplementsInterfaceAndExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/implementInterfaceAndExtendsNothing/Donkey_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
	}

	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingEnterpriseClassDirectlyExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/directlyExtendsNothing/Cow_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
	}

	/**
	 * 3.3. Producer methods
	 *  - producer method return type contains a wildcard type parameter
	 *  
	 * 2.2.1 - Legal bean types
	 *  - a parameterized type that contains a wildcard type parameter is not a legal bean type.
	 *  
	 * @throws Exception
	 */
	public void testParameterizedReturnTypeWithWildcard() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterizedTypeWithWildcard/SpiderProducer.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD, 24);
	}

	/**
	 * 3.3. Producer methods
	 *  - producer method return type is a type variable
	 * 
	 * 2.2.1 - Legal bean types
	 *  - a type variable is not a legal bean type
	 *  
	 * @throws Exception
	 */
	public void testParameterizedType() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterizedTypeWithTypeParameter2/TProducer.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, 13);
	}

	/**
	 * 3.3. Producer methods
	 *  - producer method with a parameterized return type with a type variable declares any scope other than @Dependent
	 *  
	 * @throws Exception
	 */
	public void testParameterizedReturnTypeWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/ParameterizedTypeWithWrongScope_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 25, 39);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 21);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 35);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 31);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - producer method is annotated @Inject
	 *  
	 * @throws Exception
	 */
	public void testInitializerMethodAnnotatedProduces() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/initializer/broken/methodAnnotatedProduces/Pheasant_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 25);
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
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER, 31);
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
	 * 3.4. Producer fields
	 *  - producer field type contains a wildcard type parameter
	 *  
	 * @throws Exception
	 */
	public void testParameterizedTypeWithWildcard() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/field/definition/broken/parameterizedReturnTypeWithWildcard/SpiderProducerWildCardType_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_FIELD_TYPE_HAS_WILDCARD, 23, 1011, 1026);
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_FIELD_TYPE_HAS_WILDCARD, 24, 1100, 1125);
	}

	/**
	 * 3.4. Producer fields.
	 *  - producer field type is a type variable
	 *  
	 * @throws Exception
	 */
	public void testTypeVariable() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.PRODUCER_FIELD_TYPE_IS_VARIABLE, 10);
	}

	/**
	 * 3.4. Producer fields
	 *  - producer field with a parameterized type with a type variable declares any scope other than @Dependent
	 *  
	 * @throws Exception
	 */
	public void testParameterizedTypeWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/ParameterizedTypeWithWrongScope_Broken.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 11, 18);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 14);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, AbstractResourceMarkerTest.MARKER_TYPE, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 16);
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