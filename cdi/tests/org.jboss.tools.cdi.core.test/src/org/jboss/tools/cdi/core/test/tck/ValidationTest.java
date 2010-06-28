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

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
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
		assertMarkerIsCreated(petShopFile, CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION, 25);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_INTERCEPTOR, 8);
	}

	/**
	 * 2.4.1. Built-in scope types
	 *	      - decorator has any scope other than @Dependent (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testDecoratorWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/DecoratorWithWrongScopeBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_DECORATOR, 7);
	}

	/**
	 *  2.4.3. Declaring the bean scope
	 *         - bean class or producer method or field specifies multiple scope type annotations
	 * 
	 * @throws Exception
	 */
	public void testMultipleBeanScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/scope/broken/tooManyScopes/BeanWithTooManyScopeTypes_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, 22, 23);
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
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, 24, 25);
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
		assertMarkerIsCreated(file, CDIValidationMessages.INTERCEPTOR_HAS_NAME, 9);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/NamedStereotypedInterceptorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INTERCEPTOR_HAS_NAME, 7);
	}

	/**
	 * 2.5.3. Beans with no EL name
	 *	- decorator has a name (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testNamedDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/NamedDecoratorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DECORATOR_HAS_NAME, 10);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/NamedStereotypedDecoratorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DECORATOR_HAS_NAME, 8);
	}

	/**
	 * 2.6.1. Declaring an alternative
	 *	- interceptor is an alternative (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testAlternativeInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/AlternativeInterceptorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INTERCEPTOR_IS_ALTERNATIVE, 7);
	}

	/**
	 * 2.6.1. Declaring an alternative
	 *	- decorator is an alternative (Non-Portable behavior)
	 *
	 * @throws Exception
	 */
	public void testAlternativeDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/AlternativeDecoratorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DECORATOR_IS_ALTERNATIVE, 7);
	}

	/**
	 *  2.7.1.1. Declaring the default scope for a stereotype
	 *   - stereotype declares more than one scope
	 * 
	 * @throws Exception
	 */
	public void testStereotypeScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/stereotype/broken/tooManyScopes/StereotypeWithTooManyScopeTypes_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, 32, 33);
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
		assertMarkerIsCreated(file, CDIValidationMessages.STEREOTYPE_DECLARES_NON_EMPTY_NAME, 31);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, 30);
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
		assertMarkerIsCreated(file, CDIValidationMessages.STEREOTYPE_IS_ANNOTATED_TYPED, 15);
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
		assertMarkerIsCreated(file, CDIValidationMessages.BOTH_INTERCEPTOR_AND_DECORATOR, 24, 25);
	}

	/**
	 * 3.1. Managed beans
	 * 	- managed bean with a public field declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentScopedBeanCanNotHavePublicField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/definition/dependentWithPublicField/Leopard_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD, 21);
	}

	/**
	 * 3.1. Managed beans
	 * 	- managed bean with a parameterized bean class declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentGenericManagedBeanNotOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/bean/genericbroken/FooBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_GENERIC_TYPE, 21);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, 21);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, 23);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 24);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 25);
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
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, 23);
	}

	/**
	 * 3.2. Session beans
	 *  - bean class of a session bean is annotated @Interceptor 
	 * 
	 * @throws Exception
	 */
	public void testSessionBeanAnnotatedInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/SessionBeanAnnotatedInterceptorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.SESSION_BEAN_ANNOTATED_INTERCEPTOR, 8, 9);
	}

	/**
	 * 3.2. Session beans
	 *  - bean class of a session bean is annotated @Decorator 
	 * 
	 * @throws Exception
	 */
	public void testSessionBeanAnnotatedDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/SessionBeanAnnotatedDecoratorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.SESSION_BEAN_ANNOTATED_DECORATOR, 6, 7);
	}

	/**
	 * 3.2. Session beans
	 * 	- session bean with a parameterized bean class declares any scope other than @Dependent 
	 * 
	 * @throws Exception
	 */
	public void testNonDependentGenericSessionBeanNotOk() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/sessionbeans/FooBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_SESSION_BEAN_WITH_GENERIC_TYPE, 6);
	}

	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingClassDirectlyExtendsSimpleBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/directlyExtendsSimpleBean/Tractor_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
	}
	
	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingEnterpriseClassImplementsInterfaceAndExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/implementInterfaceAndExtendsNothing/Donkey_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
	}

	/**
	 * 3.2.4. Specializing a session bean
	 * 	- session bean class annotated @Specializes does not directly extend the bean class of another session bean 
	 * 
	 * @throws Exception
	 */
	public void testSpecializingEnterpriseClassDirectlyExtendsNothing() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/enterprise/broken/directlyExtendsNothing/Cow_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, 22);
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
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD, 24);
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
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, 13);
	}

	/**
	 * 3.3. Producer methods
	 *  - producer method with a parameterized return type with a type variable declares any scope other than @Dependent
	 *  
	 * @throws Exception
	 */
	public void testParameterizedReturnTypeWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/ParameterizedTypeWithWrongScope_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 25, 39);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 21);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 35);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD, 31);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - producer method is annotated @Inject
	 *  
	 * @throws Exception
	 */
	public void testInitializerMethodAnnotatedProduces() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/initializer/broken/methodAnnotatedProduces/Pheasant_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 25);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - a producer method has a parameter annotated @Disposes
	 *  
	 * @throws Exception
	 */
	public void testProducerMethodWithParameterAnnotatedDisposes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterAnnotatedDisposes/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES, 25, 26);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - a producer method has a parameter annotated @Observers
	 *  
	 * @throws Exception
	 */
	public void testProducerMethodWithParameterAnnotatedObserves() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/parameterAnnotatedObserves/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES, 25, 26);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - non-static method of a session bean class is annotated @Produces, and the method is not a business method of the session bean
	 *  
	 * @throws Exception
	 */
	public void testProducerMethodOnSessionBeanMustBeBusinessMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/method/broken/enterprise/nonbusiness/FooProducer.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN, 25);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - decorator has a method annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testDecoratorMustNotHaveProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/DecoratorHasProducerMethodBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_IN_DECORATOR, 10);
	}

	/**
	 * 3.3.2. Declaring a producer method
	 *  - interceptor has a method annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testInterceptorMustNotHaveProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/InterceptorHasProducerMethodBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_IN_INTERCEPTOR, 17);
	}

	/**
	 * 3.3.3. Specializing a producer method
	 *  - method annotated @Specializes is static
	 * 
	 * @throws Exception
	 */
	public void testSpecializedStaticMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/producer/method/broken/specializesStaticMethod/FurnitureShop_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_PRODUCER_STATIC, 24);
	}

	/**
	 * 3.3.3. Specializing a producer method
	 *  - method annotated @Specializes does not directly override another producer method
	 * 
	 * @throws Exception
	 */
	public void testSpecializedMethodIndirectlyOverridesAnotherProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/producer/method/broken/indirectOverride/ShoeShop_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SPECIALIZING_PRODUCER_OVERRIDE, 24);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 * 	- method has more than one parameter annotated @Disposes
	 * 
	 * @throws Exception
	 */
	public void testMultipleDisposeParameters() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/multiParams/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_DISPOSING_PARAMETERS, 30, 30);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Produces.
	 *  
	 * @throws Exception
	 */
	public void testProducesUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/producesUnallowed/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES, 30, 31);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Observes.
	 *  
	 * @throws Exception
	 */
	public void testObserverParameterUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/observesUnallowed/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, 32, 32);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a disposer method is annotated @Inject.
	 *  
	 * @throws Exception
	 */
	public void testInitializerUnallowed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/initializerUnallowed/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 32, 33);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - a non-static method of a session bean class has a parameter annotated @Disposes, and the method is not a business method of the session bean
	 *  
	 * @throws Exception
	 */
	public void testDisposalMethodNotBusinessOrStatic() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/methodOnSessionBean/AppleTree.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN, 31);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/newBean/Fox.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN, 73);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - decorators may not declare disposer methods
	 *  
	 * @throws Exception
	 */
	public void testDecoratorDeclaresDisposer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/disposers/TimestampLogger.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DISPOSER_IN_DECORATOR, 6, 9);
	}

	/**
	 * 3.3.6. Declaring a disposer method
	 *  - interceptors may not declare disposer methods
	 *  
	 * @throws Exception
	 */
	public void testInterceptorDeclaresDisposer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/FordInterceptor.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DISPOSER_IN_INTERCEPTOR, 8, 16);
	}

	/**
	 * 3.3.7. Disposer method resolution
	 *  - there is no producer method declared by the (same) bean class that is assignable to the disposed parameter of a disposer method
	 *  
	 * @throws Exception
	 */
	public void testUnresolvedDisposalMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/disposal/method/definition/broken/unresolvedMethod/SpiderProducer_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER, 35);
		assertMarkerIsNotCreated(file, CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER, 31);
	}

	/**
	 * 3.3.7. Disposer method resolution
	 *  - there are multiple disposer methods for a single producer method
	 *  
	 * @throws Exception
	 */
	public void testMultipleDisposersForProducer() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/disposers/TimestampLogger_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_DISPOSERS_FOR_PRODUCER, 13, 16);
	}

	/**
	 * 3.4. Producer fields
	 *  - producer field type contains a wildcard type parameter
	 *  
	 * @throws Exception
	 */
	public void testParameterizedTypeWithWildcard() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/producer/field/definition/broken/parameterizedReturnTypeWithWildcard/SpiderProducerWildCardType_Broken.java");
		assertMarkerIsCreatedForGivenPosition(file, CDIValidationMessages.PRODUCER_FIELD_TYPE_HAS_WILDCARD, 23, 1011, 1026);
		assertMarkerIsCreatedForGivenPosition(file, CDIValidationMessages.PRODUCER_FIELD_TYPE_HAS_WILDCARD, 24, 1100, 1125);
	}

	/**
	 * 3.4. Producer fields.
	 *  - producer field type is a type variable
	 *  
	 * @throws Exception
	 */
	public void testTypeVariable() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/SpiderProducerVariableType_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_FIELD_TYPE_IS_VARIABLE, 10);
	}

	/**
	 * 3.4. Producer fields
	 *  - producer field with a parameterized type with a type variable declares any scope other than @Dependent
	 *  
	 * @throws Exception
	 */
	public void testParameterizedTypeWithWrongScope() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/ParameterizedTypeWithWrongScope_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 11, 18);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 9);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 14);
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD, 16);
	}

	/**
	 * 3.4.2. Declaring a producer field
	 *  - producer field is annotated @Inject
	 *  
	 * 3.8.1. Declaring an injected field
	 *  - injected field is annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testProducerAnnotatedInject() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/ProducerAnnotatedInjectBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, 8);
	}

	/**
	 * 3.4.2. Declaring a producer field
	 *  - non-static field of a session bean class is annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testNonStaticProducerOfSessionBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/NonStaticProducerOfSessionBeanBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN, 9);
	}

	/**
	 * 3.4.2. Declaring a producer field
	 *  - decorator has a field annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testDecoratorMustNotHaveProducerField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/DecoratorHasProducerFieldBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_IN_DECORATOR, 9);
	}

	/**
	 * 3.4.2. Declaring a producer field
	 *  - interceptor has a field annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testInterceptorMustNotHaveProducerField() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/producers/InterceptorHasProducerFieldBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_IN_INTERCEPTOR, 16);
	}

	/**
	 * 3.5.1. Declaring a resource
	 * 	- producer field declaration specifies an EL name (together with one of @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef)
	 * 
	 * @throws Exception
	 */
	public void testResourceWithELName() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/resources/ProducerFieldsBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, 15, 19, 24, 27, 31);
	}

	/*
	 * 3.5.1. Declaring a resource
	 * 	- matching object in the Java EE component environment is not of the same type as the producer field declaration
	 * 
	 * TODO needs some investigation.
	 */

	/*
	 * 3.6. Additional built-in beans
	 * - Java EE component class has an injection point of type UserTransaction and qualifier @Default, and may not validly make use of the JTA UserTransaction according to the Java EE platform specification
	 * 
	 * TODO needs some investigation.
	 */

	/**
	 * 3.7.1. Declaring a bean constructor
	 * 	- bean class has more than one constructor annotated @Inject
	 * 
	 * @throws Exception
	 */
	public void testTooManyInitializerAnnotatedConstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/definition/tooManyInitializerAnnotatedConstructors/Goose_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_INJECTION_CONSTRUCTORS, 24, 29);
	}

	/**
	 * 3.7.1. Declaring a bean constructor
	 * 	- bean constructor has a parameter annotated @Disposes
	 * 
	 * @throws Exception
	 */
	public void testConstructorHasDisposesParameter() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/definition/constructorHasDisposesParameter/DisposingConstructor.java");
		assertMarkerIsCreated(file, CDIValidationMessages.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES, 24, 25);
	}

	/**
	 * 3.7.1. Declaring a bean constructor
	 * 	- bean constructor has a parameter annotated @Observes
	 * 
	 * @throws Exception
	 */
	public void testConstructorHasObservesParameter() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/definition/constructorHasObservesParameter/ObservingConstructor.java");
		assertMarkerIsCreated(file, CDIValidationMessages.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES, 25, 26);
	}

	/**
	 * 3.9. Initializer methods
	 *  - initializer method may not be static
	 *  
	 * @throws Exception
	 */
	public void testStaticInitializerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/GenericInitializerMethodBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.STATIC_METHOD_ANNOTATED_INJECT, 11);
	}

	/**
	 * 3.9.1. Declaring an initializer method
	 *  - an initializer method has a parameter annotated @Disposes
	 *  
	 * @throws Exception
	 */
	public void testInitializerMethodHasParameterAnnotatedDisposes() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/implementation/initializer/broken/parameterAnnotatedDisposes/Capercaillie_Broken.java");
		assertMarkerIsCreatedForGivenPosition(file, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 25, 1003, 1010);
		assertMarkerIsCreatedForGivenPosition(file, CDIValidationMessages.DISPOSER_ANNOTATED_INJECT, 26, 1048, 1057);
	}

	/**
	 * 3.9.1. Declaring an initializer method
	 *  - generic method of a bean is annotated @Inject
	 *  
	 * @throws Exception
	 */
	public void testGenericInitializerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/GenericInitializerMethodBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.GENERIC_METHOD_ANNOTATED_INJECT, 7);
	}

	/**
	 * 3.11. The qualifier @Named at injection points
	 *  - injection point other than injected field declares a @Named annotation that does not specify the value member
	 * 
	 * @throws Exception
	 */
	public void testNamedInjectPoint() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/NamedInjectionBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PARAM_INJECTION_DECLARES_EMPTY_NAME, 10, 16);
	}

	/**
	 * 4.3.1. Direct and indirect specialization
	 *  - X specializes Y but does not have some bean type of Y
	 * 
	 * @throws Exception
	 */
	public void testBeanDoesNotHaveSomeTypeOfSpecializedBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/specialization/MissingTypeBeanBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.MISSING_TYPE_IN_SPECIALIZING_BEAN, "MissingTypeBeanBroken", "Farmer", "Farmer"), 6);
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.MISSING_TYPE_IN_SPECIALIZING_BEAN, "MissingTypeBeanBroken", "Farmer", "Simple"), 6);
	}

	/**
	 * 4.3.1. Direct and indirect specialization
	 *  - X specializes Y and Y has a name and X declares a name explicitly, using @Named
	 * 
	 * @throws Exception
	 */
	public void testSpecializingAndSpecializedBeanHasName() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/producer/method/broken/specializingAndSpecializedBeanHaveName/HighSchool_Broken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.CONFLICTING_NAME_IN_SPECIALIZING_BEAN, "HighSchool_Broken.getStarPupil()", "School.getStarPupil()"), 25);
	}

	/**
	 * 4.3.1. Direct and indirect specialization
	 *  - interceptor is annotated @Specializes (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testSpecializingInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/specialization/SpecializingInterceptorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INTERCEPTOR_ANNOTATED_SPECIALIZES, 9);
	}

	/**
	 * 4.3.1. Direct and indirect specialization
	 *  - decorator is annotated @Specializes (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testSpecializingDecorator() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/specialization/SpecializingDecoratorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.DECORATOR_ANNOTATED_SPECIALIZES, 10);
	}

	/**
	 * 5.1.4. Inter-module injection
	 *  - a decorator can not be injected
	 * 
	 * @throws Exception
	 */
	public void testDecoratorNotResolved() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/decorator/House.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INJECTED_DECORATOR, 23);
	}

	/**
	 * 5.1.4. Inter-module injection
	 *  - an interceptor can not be injected
	 * 
	 * @throws Exception
	 */
	public void testInterceptorNotResolved() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/InjectInterceptorBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INJECTED_INTERCEPTOR, 7);
	}

	/**
	 * 5.2.1. Unsatisfied and ambiguous dependencies
	 *  - If an unresolvable ambiguous dependency exists, the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testAmbiguousDependency() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/dependency/resolution/broken/ambiguous/Farm_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 34);
	}

	/**
	 * 5.2.1. Unsatisfied and ambiguous dependencies
	 *  - If an unsatisfied dependency exists, the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testUnsatisfiedDependency() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/dependency/resolution/broken/unsatisfied/Bean_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 34);
	}

	/**
	 * 5.2.2. Legal injection point types
	 *  - injection point type is a type variable
	 * 
	 * @throws Exception
	 */
	public void testTypeVariableInjectionPoint() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/FarmBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INJECTION_TYPE_IS_VARIABLE, 11, 15);
	}

	/**
	 * 5.2.4. Primitive types and null values
	 *  - injection point of primitive type resolves to a bean that may have null values, such as a producer method with a non-primitive return type or a producer field with a non-primitive type
	 * 
	 * @throws Exception
	 */
	public void testPrimitiveInjectionPointResolvedToNonPrimitiveProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/GameBroken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 7, 19);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 9);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 10);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 11);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 20);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 21);
		assertMarkerIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, 22);
	}

	/**
	 * 5.2.5. Qualifier annotations with members
	 *  - annotation-valued member of a qualifier type is not annotated @Nonbinding (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testAnnotationMemberWithoutNonBinding() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/binding/members/annotation/Expensive_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER, 35);
	}

	/**
	 * 5.2.5. Qualifier annotations with members
	 *  - array-valued member of a qualifier type is not annotated @Nonbinding (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testArrayMemberWithoutNonBinding() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/binding/members/array/Expensive_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER, 34);
	}

	/**
	 * 5.5.7. Injection point metadata
	 *  - bean that declares any scope other than @Dependent has an injection point of type InjectionPoint and qualifier @Default
	 * 
	 * @throws Exception
	 */
	public void testSessionScopedBeanWithInjectionPoint() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/broken/normal/scope/Cat_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED, 27);
	}

	/**
	 * 8.1.2. Decorator delegate injection points
	 *  - decorator has more than one delegate injection point
	 * 
	 * @throws Exception
	 */
	public void testMultipleDelegateInjectionPoints() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/multipleDelegateInjectionPoints/TimestampLogger.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_DELEGATE, 31, 32);
	}

	/**
	 * 8.1.2. Decorator delegate injection points
	 *  - decorator does not have a delegate injection point
	 * 
	 * @throws Exception
	 */
	public void testNoDelegateInjectionPoints() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/noDelegateInjectionPoints/TimestampLogger.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_DELEGATE, 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateInitializerMethod/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.MISSING_DELEGATE);
	}

	/**
	 * 8.1.2. Decorator delegate injection points
	 *  - decorator does not have a delegate injection point
	 * 
	 * @throws Exception
	 */
	public void testDecoratorDelegateInjectionPoints() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_INJECTION_POINT_DELEGATE);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateInitializerMethod/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_INJECTION_POINT_DELEGATE);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateConstructor/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_INJECTION_POINT_DELEGATE);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/broken/delegateProducerMethod/TimestampLogger.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_INJECTION_POINT_DELEGATE, 33);
	}

	/**
	 * 8.1.2. Decorator delegate injection points
	 *  - bean class that is not a decorator has an injection point annotated @Delegate
	 * 
	 * @throws Exception
	 */
	public void testNonDecoratorWithDecoratesAnnotationNotOK() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/nonDecoratorWithDecorates/Elf.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_BEAN_DECLARING_DELEGATE, 24);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_BEAN_DECLARING_DELEGATE);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateInitializerMethod/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_BEAN_DECLARING_DELEGATE);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateConstructor/TimestampLogger.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.ILLEGAL_BEAN_DECLARING_DELEGATE);
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type does not implement or extend a decorated type of the decorator
	 * 
	 * @throws Exception
	 */
	public void testNotAllDecoratedTypesImplemented() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/notAllDecoratedTypesImplemented/TimestampLogger.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "EnhancedLogger"), 31);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampLogger.java");
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Logger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "EnhancedLogger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "TimestampLogger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Object"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "MockLogger"));

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampLoggerWithMethod.java");
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Logger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "EnhancedLogger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "TimestampLogger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Object"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "MockLogger"));
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterInInterfcaeInFiled() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "IClazz<org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates.Logger>"), 10);
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterInInterfcaeInMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampWithMethodBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "IClazz<java.lang.String>"), 10);
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterWithInterfaceInFiled() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampLoggerBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates.Logger>"), 10);
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterWithInterfaceInMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampLoggerWithMethodBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<java.lang.String>"), 10);
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterInFiled() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampParametedLoggerBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<java.lang.String>"), 10);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampParametedLogger.java");
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Logger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates.Logger>"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Test"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Object"));
	}

	/**
	 * 8.1.3. Decorator delegate injection points
	 *  - delegate type specifies different type parameters
	 * 
	 * @throws Exception
	 */
	public void testDelegateSpecifiesDifferentTypeParameterInMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampParametedLoggerWithMethodBroken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<org.jboss.jsr299.tck.tests.jbt.validation.decorators.delegates.Logger>"), 10);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/delegates/TimestampParametedLoggerWithMethod.java");
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Logger"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Clazz<java.lang.String>"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Test"));
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, "Object"));
	}

	/**
	 * 9.2. Declaring the interceptor bindings of an interceptor
	 *  - interceptor declared using @Interceptor does not declare any interceptor binding (Non-Portable behavior)
	 * 
	 * @throws Exception
	 */
	public void testNoInterceptorBinfdingsInInterceptor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/NoInterceptorBinfdingsInInterceptor.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MISSING_INTERCEPTOR_BINDING, 7);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/interceptors/definition/SecureTransaction.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.MISSING_INTERCEPTOR_BINDING);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - method has more than one parameter annotated @Observes
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodMustHaveOnlyOneEventParameter() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/tooManyParameters/YorkshireTerrier_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.MULTIPLE_OBSERVING_PARAMETERS, 24, 24);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - an observer method is annotated @Produces
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodAnnotatedProducesFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/isProducer/BorderTerrier_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES, 25, 25);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - observer method is annotated @Inject
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodAnnotatedInitializerFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/isInitializer/AustralianTerrier_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.OBSERVER_ANNOTATED_INJECT, 25, 26);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - a observer method is annotated @Disposes.
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodWithDisposesParamFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/isDisposer/FoxTerrier_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, 28, 28);
	}

	/**
	 * 10.4.2. Declaring an observer method
	 *  - non-static method of a session bean class has a parameter annotated @Observes, and the method is not a business method of the EJB
	 *  
	 * @throws Exception
	 */
	public void testObserverMethodOnEnterpriseBeanNotBusinessMethodOrStaticFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/notBusinessMethod/TibetanTerrier_Broken.java");
		assertMarkerIsCreated(file, CDIValidationMessages.ILLEGAL_OBSERVER_IN_SESSION_BEAN, 25);
	}

	public static int getMarkersNumber(IResource resource) {
		return AbstractResourceMarkerTest.getMarkersNumberByGroupName(resource, null);
	}

	private static void assertMarkerIsCreated(IResource resource, String message, int... expectedLines) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message), expectedLines);
	}

	private static void assertMarkerIsNotCreated(IResource resource, String message) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message));
	}

	private static void assertMarkerIsNotCreated(IResource resource, String message, int expectedLine) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message), expectedLine);
	}

	private static void assertMarkerIsCreatedForGivenPosition(IResource resource, String message, int lineNumber, int startPosition, int endPosition) throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreatedForGivenPosition(resource, AbstractResourceMarkerTest.MARKER_TYPE, convertMessageToPatern(message), lineNumber, startPosition, endPosition);
	}

	private static String convertMessageToPatern(String message) {
		return message.replace("[", "\\[").replace("]", "\\]").replace("<", "\\<").replace(">", "\\>");
	}
}