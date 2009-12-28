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
package org.jboss.tools.cdi.core.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.preferences.SeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CDIPreferences extends SeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static CDIPreferences INSTANCE = new CDIPreferences();

	// Test group

	// Test
	public static final String TEST = INSTANCE.createSeverityOption("testKey"); //$NON-NLS-1$
	//Name group
	
//	- stereotype declares a non-empty @Named annotation (2.7.1.3)
	public static final String STEREOTYPE_DECLARES_NON_EMPTY_NAME = INSTANCE.createSeverityOption("stereotypeDeclaresNonEmptyName"); //$NON-NLS-1$
//	- producer field declaration specifies an EL name (together with one of 
//			  @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef) (3.5.1)
	public static final String RESOURCE_PRODUCER_FIELD_SETS_EL_NAME = INSTANCE.createSeverityOption("resourceProducerFieldSetsElName"); //$NON-NLS-1$
//	- injection point other than injected field declares a @Named annotation that 
//	  does not specify the value member
	public static final String PARAM_INJECTION_DECLARES_EMPTY_NAME = INSTANCE.createSeverityOption("paramInjectionDeclaresEmptyName"); //$NON-NLS-1$
//	- interceptor or decorator has a name (2.5.3 non-portable)
	public static final String INTERCEPTOR_HAS_NAME = INSTANCE.createSeverityOption("interceptorHasName"); //$NON-NLS-1$
	public static final String DECORATOR_HAS_NAME = INSTANCE.createSeverityOption("decoratorHasName"); //$NON-NLS-1$

	//Type group

//	- bean class or producer method or field specifies a @Typed annotation, 
//	  and the value member specifies a class which does not correspond to a type 
//	  in the unrestricted set of bean types of a bean (2.2.2)
	public static final String ILLEGAL_TYPE_IN_TYPED_DECLARATION = INSTANCE.createSeverityOption("illegalTypeInTypedDeclaration"); //$NON-NLS-1$
//	- producer method return type contains a wildcard type parameter (3.3)
	public static final String PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD = INSTANCE.createSeverityOption("producerMethodReturnTypeHasWildcard"); //$NON-NLS-1$
//	- producer method return type is a type variable (3.3)
	public static final String PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE = INSTANCE.createSeverityOption("producerMethodReturnTypeIsVariable"); //$NON-NLS-1$
//	- producer field type contains a wildcard type parameter (3.4)
	public static final String PRODUCER_FIELD_TYPE_HAS_WILDCARD = INSTANCE.createSeverityOption("producerFieldTypeHasWildcard"); //$NON-NLS-1$
//	- producer field type is a type variable
	public static final String PRODUCER_FIELD_TYPE_IS_VARIABLE = INSTANCE.createSeverityOption("producerFieldTypeIsVariable"); //$NON-NLS-1$
//	- matching object in the Java EE component environment is not of the same type
//	  as the producer field declaration (3.5.1)
	public static final String PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT = INSTANCE.createSeverityOption("producerFieldTypeDoesNotMatchJavaEeObject"); //$NON-NLS-1$
//	- injection point type is a type variable (5.2.2)
	public static final String INJECTION_TYPE_IS_VARIABLE = INSTANCE.createSeverityOption("injectionTypeIsVariable"); //$NON-NLS-1$
//	- stereotype is annotated @Typed (2.7.1.3 non-portable)
	public static final String STEREOTYPE_IS_ANNOTATED_TYPED = INSTANCE.createSeverityOption("stereotypeIsAnnotatedTyped"); //$NON-NLS-1$
//	- array-valued or annotation-valued member of a qualifier type is not annotated @Nonbinding (5.2.5 non-portable)
	public static final String MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER = INSTANCE.createSeverityOption("missingNonbindingInQualifierTypeMember"); //$NON-NLS-1$
//	- array-valued or annotation-valued member of an interceptor binding type 
//	  is not annotated @Nonbinding (9.5.2 non-portable)
	public static final String MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER = INSTANCE.createSeverityOption("missingNonbindingInInterceptorBindingTypeMember"); //$NON-NLS-1$

	//Scope group

//	- bean class or producer method or field specifies multiple scope type annotations (2.4.3)
	public static final String MULTIPLE_SCOPE_TYPE_ANNOTATIONS = INSTANCE.createSeverityOption("multipleScopeTypeAnnotations"); //$NON-NLS-1$
//	- bean does not explicitly declare a scope when there is no default scope 
//	  (there are two different stereotypes declared by the bean that declare different default scopes) (2.4.4)
	public static final String MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE = INSTANCE.createSeverityOption("missingScopeWhenThereIsNoDefaultScope"); //$NON-NLS-1$
//	- stereotype declares more than one scope (2.7.1.1)
	public static final String STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE = INSTANCE.createSeverityOption("stereotypeDeclaresMoreThanOneScope"); //$NON-NLS-1$
//	- managed bean with a public field declares any scope other than @Dependent (3.1)
//	- managed bean with a parameterized bean class declares any scope other than @Dependent (3.1)
	public static final String ILLEGAL_SCOPE_FOR_MANAGED_BEAN = INSTANCE.createSeverityOption("illegalScopeForManagedBean"); //$NON-NLS-1$
//	- session bean specifies an illegal scope (a stateless session bean must belong 
//	  to the @Dependent pseudo-scope; a singleton bean must belong to either the
//	  @ApplicationScoped scope or to the @Dependent pseudo-scope, a stateful session 
//	  bean may have any scope) (3.2)
//	- session bean with a parameterized bean class declares any scope other than @Dependent (3.2)
	public static final String ILLEGAL_SCOPE_FOR_SESSION_BEAN = INSTANCE.createSeverityOption("illegalScopeForSessionBean"); //$NON-NLS-1$
//	- producer method with a parameterized return type with a type variable declares 
//	  any scope other than @Dependent (3.3)
	public static final String ILLEGAL_SCOPE_FOR_PRODUCER_METHOD = INSTANCE.createSeverityOption("illegalScopeForProducerMethod"); //$NON-NLS-1$
//	- producer field with a parameterized type with a type variable declares any 
//	  scope other than @Dependent (3.4)
	public static final String ILLEGAL_SCOPE_FOR_PRODUCER_FIELD = INSTANCE.createSeverityOption("illegalScopeForProducerField"); //$NON-NLS-1$
//	- bean that declares any scope other than @Dependent has an injection point of type 
//	  InjectionPoint and qualifier @Default (5.5.7)
	public static final String ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED = INSTANCE.createSeverityOption("illegalScopeWhenTypeInjectionPointIsInjected"); //$NON-NLS-1$
//	- interceptor or decorator has any scope other than @Dependent (2.4.1 non-portable)
	public static final String ILLEGAL_SCOPE_FOR_INTERCEPTOR = INSTANCE.createSeverityOption("illegalScopeForInterceptor"); //$NON-NLS-1$
	public static final String ILLEGAL_SCOPE_FOR_DECORATOR = INSTANCE.createSeverityOption("illegalScopeForDecorator"); //$NON-NLS-1$

	//Member group

//	- producer method is annotated @Inject (3.3.2)
//	- producer field is annotated @Inject (3.4.2) = - injected field is annotated @Produces (3.8.1)
	public static final String PRODUCER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("producerAnnotatedInject"); //$NON-NLS-1$
//	- producer method has a parameter annotated @Disposes or @Observes (3.3.2)
	public static final String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("producerParameterIllegallyAnnotated"); //$NON-NLS-1$
//	- observer method is annotated [@Produces or] @Inject (10.4.2)
	public static final String OBSERVER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("observerAnnotatedInject"); //$NON-NLS-1$
//	- has a parameter annotated @Disposes (10.4.2)
	public static final String OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("observerParameterIllegallyAnnotated"); //$NON-NLS-1$
//	- non-static method of a session bean class is annotated @Produces, and the method 
//	  is not a business method of the session bean (3.3.2)
	public static final String ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalProducerMethodInSessionBean"); //$NON-NLS-1$
//	- method has more than one parameter annotated @Disposes (3.3.6)
	public static final String MULTIPLE_DISPOSING_PARAMETERS = INSTANCE.createSeverityOption("multipleDisposingParameters"); //$NON-NLS-1$
//	- disposer method is annotated [@Produces or] @Inject [or has a parameter annotated @Observes] (3.3.6)
	public static final String DISPOSER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("disposerAnnotatedInject"); //$NON-NLS-1$
//	- non-static method of a session bean class has a parameter annotated @Disposes, and 
//	  the method is not a business method of the session bean (3.3.6)
	public static final String ILLEGAL_DISPOSER_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalDisposerInSessionBean"); //$NON-NLS-1$
//	- there is no producer method declared by the (same) bean class that is assignable 
//	  to the disposed parameter of a disposer method (3.3.7)
	public static final String NO_PRODUCER_MATCHING_DISPOSER = INSTANCE.createSeverityOption("noProducerMatchingDisposer"); //$NON-NLS-1$
//	- there are multiple disposer methods for a single producer method (3.3.7)
	public static final String MULTIPLE_DISPOSERS_FOR_PRODUCER = INSTANCE.createSeverityOption("multipleDisposersForProducer"); //$NON-NLS-1$
//	- non-static field of a session bean class is annotated @Produces (3.4.2)
	public static final String ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalProducerFieldInSessionBean"); //$NON-NLS-1$
//	- bean class has more than one constructor annotated @Inject (3.7.1)
	public static final String MULTIPLE_INJECTION_CONSTRUCTORS = INSTANCE.createSeverityOption("multipleInjectionConstructors"); //$NON-NLS-1$
//	- bean constructor has a parameter annotated @Disposes, or @Observes (3.7.1)
	public static final String CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("constructorParameterIllegallyAnnotated"); //$NON-NLS-1$
//	- generic method of a bean is annotated @Inject (initializer method is a non-abstract, 
//	  non-static, non-generic method of a bean class) (3.9.1)
	public static final String GENERIC_METHOD_ANNOTATED_INJECT = INSTANCE.createSeverityOption("genericMethodAnnotatedInject"); //$NON-NLS-1$
//	- method has more than one parameter annotated @Observes (10.4.2)
	public static final String MULTIPLE_OBSERVING_PARAMETERS = INSTANCE.createSeverityOption("multipleObservingParameters"); //$NON-NLS-1$
//	- non-static method of a session bean class has a parameter annotated @Observes, 
//	  and the method is not a business method of the EJB (10.4.2)
	public static final String ILLEGAL_OBSERVER_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalObserverInSessionBean"); //$NON-NLS-1$
//	- bean with scope @Dependent has an observer method declared receive=IF_EXISTS (10.4.3)
	public static final String ILLEGAL_CONDITIONAL_OBSERVER = INSTANCE.createSeverityOption("illegalConditionalObserver"); //$NON-NLS-1$

	//Interceptor & Decorator group

//	- the bean class of a managed bean is annotated with both 
//	  the @Interceptor and @Decorator stereotypes (3.1)
//	- bean class of a session bean is annotated @Interceptor or @Decorator (3.2)
	public static final String BOTH_INTERCEPTOR_AND_DECORATOR = INSTANCE.createSeverityOption("bothInterceptorAndDecorator"); //$NON-NLS-1$
//	- interceptor or decorator has a method annotated @Produces (3.3.2)
//	- interceptor or decorator has a field annotated @Produces (3.4.2)
	public static final String PRODUCER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("producerInInterceptorOrDecorator"); //$NON-NLS-1$
//	- interceptor or decorator has a method annotated @Disposes
	public static final String DISPOSER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("disposerInInterceptorOrDecorator"); //$NON-NLS-1$
//	- decorator has more than one delegate injection point, or
	public static final String MULTIPLE_DELEGATE = INSTANCE.createSeverityOption("multipleDelegate"); //$NON-NLS-1$
//	  does not have a delegate injection point (8.1.2)
	public static final String MISSING_DELEGATE = INSTANCE.createSeverityOption("missingDelegate"); //$NON-NLS-1$
//	- injection point that is not an injected field, initializer method parameter 
//	  or bean constructor method parameter is annotated @Delegate (8.1.2)
	public static final String ILLEGAL_INJECTION_POINT_DELEGATE = INSTANCE.createSeverityOption("illegalInjectionPointDelegate"); //$NON-NLS-1$
//	- bean class that is not a decorator has an injection point annotated @Delegate (8.1.2)
	public static final String ILLEGAL_BEAN_DECLARING_DELEGATE = INSTANCE.createSeverityOption("illegalBeanDeclaringDelegate"); //$NON-NLS-1$
//	- delegate type does not implement or extend a decorated type of the decorator,
//	  or specifies different type parameters (8.1.3)
	public static final String DELEGATE_HAS_ILLEGAL_TYPE = INSTANCE.createSeverityOption("delegateHasIllegalType"); //$NON-NLS-1$
//	- interceptor for lifecycle callbacks declares an interceptor binding type 
//	  that is defined @Target({TYPE, METHOD}) (9.2)
	public static final String ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING = INSTANCE.createSeverityOption("illegalLifecycleCallbackInterceptorBinding"); //$NON-NLS-1$
//	- managed bean has a class level interceptor binding and is declared final 
//	  or has a non-static, non-private, final method (9.3)
//	- non-static, non-private, final method of a managed bean has a method level 
//	  interceptor binding (9.3)
	public static final String ILLEGAL_INTERCEPTOR_BINDING_METHOD = INSTANCE.createSeverityOption("illegalInterceptorBindingMethod"); //$NON-NLS-1$
//	- the set of interceptor bindings of a bean or interceptor, including bindings 
//	  inherited from stereotypes and other interceptor bindings, has two instances 
//	  of a certain interceptor binding type and the instances have different values 
//	  of some annotation member (9.5.2)
	public static final String CONFLICTING_INTERCEPTOR_BINDINGS = INSTANCE.createSeverityOption("conflictingInterceptorBindings"); //$NON-NLS-1$
//	- interceptor or decorator has a method with a parameter annotated @Observes (10.4.2)
	public static final String OBSERVER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("observerInInterceptorOrDecorator"); //$NON-NLS-1$
//	- interceptor or decorator is an alternative (2.6.1 non-portable)
	public static final String INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE = INSTANCE.createSeverityOption("interceptorOrDecoratorIsAlternative"); //$NON-NLS-1$
//	- interceptor declared using @Interceptor does not declare any interceptor binding (9.2 non-portable)
	public static final String MISSING_INTERCEPTOR_BINDING = INSTANCE.createSeverityOption("missingInterceptorBinding"); //$NON-NLS-1$

	//Specialization

//	- managed bean class annotated @Specializes does not directly extend 
//	  the bean class of another managed bean (3.1.4)
	public static final String ILLEGAL_SPECIALIZING_MANAGED_BEAN = INSTANCE.createSeverityOption("illegalSpecializingManagedBean"); //$NON-NLS-1$
//	- session bean class annotated @Specializes does not directly extend 
//	  the bean class of another session bean (3.2.4)
	public static final String ILLEGAL_SPECIALIZING_SESSION_BEAN = INSTANCE.createSeverityOption("illegalSpecializingSessionBean"); //$NON-NLS-1$
//	- method annotated @Specializes is static or does not directly override another producer method (3.3.3)
	public static final String ILLEGAL_SPECIALIZING_PRODUCER = INSTANCE.createSeverityOption("illegalSpecializingProducer"); //$NON-NLS-1$
//	- X specializes Y but does not have some bean type of Y (4.3.1)
	public static final String MISSING_TYPE_IN_SPECIALIZING_BEAN = INSTANCE.createSeverityOption("missingTypeInSpecializingBean"); //$NON-NLS-1$
//	- X specializes Y and Y has a name and X declares a name explicitly, using @Named (4.3.1)
	public static final String CONFLICTING_NAME_IN_SPECIALIZING_BEAN = INSTANCE.createSeverityOption("conflictingNameInSpecializingBean"); //$NON-NLS-1$
//	- interceptor is annotated @Specializes (4.3.1 non-portable)
	public static final String INTERCEPTOR_ANNOTATED_SPECIALIZES = INSTANCE.createSeverityOption("interceptorAnnotatedSpecializes"); //$NON-NLS-1$
//	- decorator is annotated @Specializes (4.3.1 non-portable)
	public static final String DECORATOR_ANNOTATED_SPECIALIZES = INSTANCE.createSeverityOption("decoratorAnnotatedSpecializes"); //$NON-NLS-1$

	//Miscellaneous

//	- Java EE component class has an injection point of type UserTransaction 
//	  and qualifier @Default, and may not validly make use of the JTA UserTransaction 
//	  according to the Java EE platform specification (3.6)
	public static final String ILLEGAL_INJECTING_USERTRANSACTION_TYPE = INSTANCE.createSeverityOption("illegalInjectingUserTransactionType"); //$NON-NLS-1$
//	- Java EE component class supporting injection that is not a bean has an injection 
//	  point of type InjectionPoint and qualifier @Default (5.5.7)
	public static final String ILLEGAL_INJECTING_INJECTIONPOINT_TYPE = INSTANCE.createSeverityOption("illegalInjectingInjectionPointType"); //$NON-NLS-1$
//	- stereotype declares any qualifier annotation other than @Named (2.7.1.3 non-portable)
	public static final String ILLEGAL_QUALIFIER_IN_STEREOTYPE = INSTANCE.createSeverityOption("illegalQualifierInStereotype"); //$NON-NLS-1$
//	- bean class is deployed in two different bean archives (12.1 non-portable)
	// ? is it a definition problem
	/**
	 * @return the only instance of CDIPreferences
	 */
	public static CDIPreferences getInstance() {
		return INSTANCE;
	}

	private CDIPreferences() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#createSeverityOption(java.lang.String)
	 */
	@Override
	protected String createSeverityOption(String shortName) {
		String name = getPluginId() + ".validator.problem." + shortName; //$NON-NLS-1$
		SEVERITY_OPTION_NAMES.add(name);
		return name;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getPluginId()
	 */
	@Override
	protected String getPluginId() {
		return CDICorePlugin.PLUGIN_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.preferences.SeverityPreferences#getSeverityOptionNames()
	 */
	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	public static boolean shouldValidateCore(IProject project) {
		return true;
	}
}