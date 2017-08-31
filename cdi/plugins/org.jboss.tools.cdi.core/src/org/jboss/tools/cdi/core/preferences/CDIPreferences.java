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
import org.jboss.tools.common.validation.ValidationSeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CDIPreferences extends ValidationSeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static CDIPreferences INSTANCE = new CDIPreferences();

	public static final String WARNING_GROUP_ID = "cdi";

	//Name group

//	- stereotype declares a non-empty @Named annotation (2.7.1.3)
	public static final String STEREOTYPE_DECLARES_NON_EMPTY_NAME = INSTANCE.createSeverityOption("stereotypeDeclaresNonEmptyName", "non-empty-named"); //$NON-NLS-1$

//	- producer field declaration specifies an EL name (together with one of 
//			  @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef) (3.5.1)
	public static final String RESOURCE_PRODUCER_FIELD_SETS_EL_NAME = INSTANCE.createSeverityOption("resourceProducerFieldSetsElName", "named-producer"); //$NON-NLS-1$
//	- injection point other than injected field declares a @Named annotation that 
//	  does not specify the value member
	public static final String PARAM_INJECTION_DECLARES_EMPTY_NAME = INSTANCE.createSeverityOption("paramInjectionDeclaresEmptyName", "empty-named"); //$NON-NLS-1$
//	- interceptor or decorator has a name (2.5.3 non-portable)
	public static final String INTERCEPTOR_OR_DECORATOR_HAS_NAME = INSTANCE.createSeverityOption("interceptorHasName", "named"); //$NON-NLS-1$
// 5.3.1. Ambiguous EL names 
//	- All unresolvable ambiguous EL names are detected by the container when the application is initialized. Suppose two beans are both available for injection in a certain war, and either: 
//		  • the two beans have the same EL name and the name is not resolvable, or 
//		  • the EL name of one bean is of the form x.y, where y is a valid bean EL name, and x is the EL name of the other bean, 
//		    the container automatically detects the problem and treats it as a deployment problem.
	public static final String AMBIGUOUS_EL_NAMES = INSTANCE.createSeverityOption("ambiguousElNames", "ambiguous-name"); //$NON-NLS-1$

	//Type group

//	5.2.1. Unsatisfied and ambiguous dependencies
//	- If an unsatisfied or unresolvable ambiguous dependency exists, the container automatically detects the problem and
//	  treats it as a deployment problem.
	public static final String UNSATISFIED_OR_AMBIGUOUS_INJECTION_POINTS = INSTANCE.createSeverityOption("unsatisfiedInjectionPoints", "ambiguous-dependency"); //$NON-NLS-1$
// 5.4.1. Unproxyable bean types
//	-  If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
//	   the container automatically detects the problem and treats it as a deployment problem.
	public static final String UNPROXYABLE_BEAN_TYPE = INSTANCE.createSeverityOption("unproxyableBeanType", "ambiguous-dependency"); //$NON-NLS-1$
//	- bean class or producer method or field specifies a @Typed annotation, 
//	  and the value member specifies a class which does not correspond to a type 
//	  in the unrestricted set of bean types of a bean (2.2.2)
	public static final String ILLEGAL_TYPE_IN_TYPED_DECLARATION = INSTANCE.createSeverityOption("illegalTypeInTypedDeclaration", "typed"); //$NON-NLS-1$
//	- producer field/method return type contains a wildcard type parameter (3.3, 3.4)
//	- producer field/method return type is a type variable (3.3, 3.4)
	public static final String PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE = INSTANCE.createSeverityOption("producerMethodReturnTypeHasWildcard", "typed-producer"); //$NON-NLS-1$
//  - an injection point of primitive type resolves to a bean that may have null values, such as a producer method	with a non-primitive return type or a producer field with a non-primitive type	
	public static final String INJECT_RESOLVES_TO_NULLABLE_BEAN = INSTANCE.createSeverityOption("injectResolvesToNullableBean", "nullable"); //$NON-NLS-1$
//	- matching object in the Java EE component environment is not of the same type
//	  as the producer field declaration (3.5.1)
	public static final String PRODUCER_FIELD_TYPE_DOES_NOT_MATCH_JAVA_EE_OBJECT = INSTANCE.createSeverityOption("producerFieldTypeDoesNotMatchJavaEeObject", "not-match-object"); //$NON-NLS-1$
//	- injection point type is a type variable (5.2.2)
	public static final String INJECTION_TYPE_IS_VARIABLE = INSTANCE.createSeverityOption("injectionTypeIsVariable", "type-variable"); //$NON-NLS-1$
//	- stereotype is annotated @Typed (2.7.1.3 non-portable)
	public static final String STEREOTYPE_IS_ANNOTATED_TYPED = INSTANCE.createSeverityOption("stereotypeIsAnnotatedTyped", "typed"); //$NON-NLS-1$
//	- array-valued or annotation-valued member of a qualifier type is not annotated @Nonbinding (5.2.5 non-portable)
	public static final String MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER = INSTANCE.createSeverityOption("missingNonbindingInQualifierTypeMember", "nonbinding"); //$NON-NLS-1$
//	- array-valued or annotation-valued member of an interceptor binding type 
//	  is not annotated @Nonbinding (9.5.2 non-portable)
	public static final String MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER = INSTANCE.createSeverityOption("missingNonbindingInInterceptorBindingTypeMember", "nonbinding"); //$NON-NLS-1$

	public static final String MISSING_OR_INCORRECT_TARGET_OR_RETENTION_IN_ANNOTATION_TYPE = INSTANCE.createSeverityOption("missingOrIncorrectTargetOrRetentionInAnnotationType", "target"); //$NON-NLS-1$
//  Section 6.6.4 - Validation of passivation capable beans and dependencies
//  - If a managed bean which declares a passivating scope is not passivation capable, then the container automatically detects the problem and treats it as a deployment problem.
	public static final String NOT_PASSIVATION_CAPABLE_BEAN = INSTANCE.createSeverityOption("notPassivationCapableBean", "not-passivation-capable"); //$NON-NLS-1$

	//Scope group

//	- bean class or producer method or field specifies multiple scope type annotations (2.4.3)
	public static final String MULTIPLE_SCOPE_TYPE_ANNOTATIONS = INSTANCE.createSeverityOption("multipleScopeTypeAnnotations", "multiple-scopes"); //$NON-NLS-1$
//	- bean does not explicitly declare a scope when there is no default scope 
//	  (there are two different stereotypes declared by the bean that declare different default scopes) (2.4.4)
	public static final String MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE = INSTANCE.createSeverityOption("missingScopeWhenThereIsNoDefaultScope", "default-scope"); //$NON-NLS-1$
//	- stereotype declares more than one scope (2.7.1.1)
	public static final String STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE = INSTANCE.createSeverityOption("stereotypeDeclaresMoreThanOneScope", "multiple-scopes"); //$NON-NLS-1$
//	- managed bean with a public field declares any scope other than @Dependent (3.1)
//	- managed bean with a parameterized bean class declares any scope other than @Dependent (3.1)
//
//	- session bean specifies an illegal scope (a stateless session bean must belong 
//	  to the @Dependent pseudo-scope; a singleton bean must belong to either the
//	  @ApplicationScoped scope or to the @Dependent pseudo-scope, a stateful session 
//	  bean may have any scope) (3.2)
//	- session bean with a parameterized bean class declares any scope other than @Dependent (3.2)
//
//	- producer method with a parameterized return type with a type variable declares 
//	  any scope other than @Dependent (3.3)
//	- producer field with a parameterized type with a type variable declares any 
//	  scope other than @Dependent (3.4)
	public static final String ILLEGAL_SCOPE_FOR_BEAN = INSTANCE.createSeverityOption("illegalScopeForManagedBean", "scope"); //$NON-NLS-1$
//	- bean that declares any scope other than @Dependent has an injection point of type 
//	  InjectionPoint and qualifier @Default (5.5.7)
	public static final String ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED = INSTANCE.createSeverityOption("illegalScopeWhenTypeInjectionPointIsInjected", "scope"); //$NON-NLS-1$
//	- interceptor or decorator has any scope other than @Dependent (2.4.1 non-portable)
	public static final String ILLEGAL_SCOPE_FOR_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("illegalScopeForInterceptor", "scope"); //$NON-NLS-1$

	//Member group

//	- producer method is annotated @Inject (3.3.2)
//	- producer field is annotated @Inject (3.4.2) = - injected field is annotated @Produces (3.8.1)
	public static final String PRODUCER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("producerAnnotatedInject", "annotated-inject"); //$NON-NLS-1$
//	- producer method has a parameter annotated @Disposes or @Observes (3.3.2)
	public static final String PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("producerParameterIllegallyAnnotated", "annotated-parameter"); //$NON-NLS-1$
//	- observer method is annotated [@Produces or] @Inject (10.4.2)
	public static final String OBSERVER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("observerAnnotatedInject", "annotated-observer"); //$NON-NLS-1$
//	- observer method is annotated [@Produces or] @Inject (10.4.2)
	public static final String OBSERVER_ASYNC_ANNOTATED_INJECT = INSTANCE.createSeverityOption("observerAnnotatedInjectAsync", "annotated-observer-async"); //$NON-NLS-1$
//	- has a parameter annotated @Disposes (10.4.2)
	public static final String OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("observerParameterIllegallyAnnotated", "annotated-parameter"); //$NON-NLS-1$
//	- non-static method of a session bean class is annotated @Produces, and the method 
//	  is not a business method of the session bean (3.3.2)
//	- non-static field of a session bean class is annotated @Produces (3.4.2)
	public static final String ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalProducerMethodInSessionBean", "annotated-produces"); //$NON-NLS-1$
//	- method has more than one parameter annotated @Disposes (3.3.6)
	public static final String MULTIPLE_DISPOSING_PARAMETERS = INSTANCE.createSeverityOption("multipleDisposingParameters", "multiple-disposes"); //$NON-NLS-1$
//	- disposer method is annotated [@Produces or] @Inject [or has a parameter annotated @Observes] (3.3.6)
	public static final String DISPOSER_ANNOTATED_INJECT = INSTANCE.createSeverityOption("disposerAnnotatedInject", "annotated-disposer"); //$NON-NLS-1$
//	- non-static method of a session bean class has a parameter annotated @Disposes, and 
//	  the method is not a business method of the session bean (3.3.6)
	public static final String ILLEGAL_DISPOSER_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalDisposerInSessionBean", "annotated-disposer"); //$NON-NLS-1$
//	- there is no producer method declared by the (same) bean class that is assignable 
//	  to the disposed parameter of a disposer method (3.3.7)
	public static final String NO_PRODUCER_MATCHING_DISPOSER = INSTANCE.createSeverityOption("noProducerMatchingDisposer", "missing-producer"); //$NON-NLS-1$
//	- there are multiple disposer methods for a single producer method (3.3.7)
	public static final String MULTIPLE_DISPOSERS_FOR_PRODUCER = INSTANCE.createSeverityOption("multipleDisposersForProducer", "multiple-disposers"); //$NON-NLS-1$
//	- bean class has more than one constructor annotated @Inject (3.7.1)
	public static final String MULTIPLE_INJECTION_CONSTRUCTORS = INSTANCE.createSeverityOption("multipleInjectionConstructors", "constructor"); //$NON-NLS-1$
//	- bean constructor has a parameter annotated @Disposes, or @Observes (3.7.1)
	public static final String CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("constructorParameterIllegallyAnnotated", "constructor"); //$NON-NLS-1$
//	- generic method of a bean is annotated @Inject (initializer method is a non-abstract, 
//	  non-static, non-generic method of a bean class) (3.9.1)
	public static final String GENERIC_METHOD_ANNOTATED_INJECT = INSTANCE.createSeverityOption("genericMethodAnnotatedInject", "generic"); //$NON-NLS-1$
//	- method has more than one parameter annotated @Observes (10.4.2)
	public static final String MULTIPLE_OBSERVING_PARAMETERS = INSTANCE.createSeverityOption("multipleObservingParameters", "multiple-observers"); //$NON-NLS-1$
//	- method has more than one parameter annotated @ObservesAsync (10.4.2)
	public static final String MULTIPLE_OBSERVING_PARAMETERS_ASYNC = INSTANCE.createSeverityOption("multipleObservingParametersAsync", "multiple-observers-async"); //$NON-NLS-1$
//	- non-static method of a session bean class has a parameter annotated @Observes, 
//	  and the method is not a business method of the EJB (10.4.2)
	public static final String ILLEGAL_OBSERVER_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalObserverInSessionBean", "observer"); //$NON-NLS-1$
//	- non-static method of a session bean class has a parameter annotated @ObservesAsync, 
//	  and the method is not a business method of the EJB (10.4.2)
	public static final String ILLEGAL_OBSERVER_ASYNC_IN_SESSION_BEAN = INSTANCE.createSeverityOption("illegalObserverInSessionBeanAsync", "observer-async"); //$NON-NLS-1$
//	- bean with scope @Dependent has an observer method declared receive=IF_EXISTS (10.4.3)
	public static final String ILLEGAL_CONDITIONAL_OBSERVER = INSTANCE.createSeverityOption("illegalConditionalObserver", "observer"); //$NON-NLS-1$
//	- bean with scope @Dependent has an observer async method declared receive=IF_EXISTS (10.4.3)
	public static final String ILLEGAL_CONDITIONAL_OBSERVER_ASYNC = INSTANCE.createSeverityOption("illegalConditionalObserverAsync", "observer-async"); //$NON-NLS-1$

	//Interceptor & Decorator group

//	- the bean class of a managed bean is annotated with both 
//	  the @Interceptor and @Decorator stereotypes (3.1)
	public static final String BOTH_INTERCEPTOR_AND_DECORATOR = INSTANCE.createSeverityOption("bothInterceptorAndDecorator", "interceptor-decorator"); //$NON-NLS-1$
//	- bean class of a session bean is annotated @Interceptor or @Decorator (3.2)
	public static final String SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("sessionBeanAnnotatedInterceptorOrDecorator", "interceptor-decorator"); //$NON-NLS-1$
//	- interceptor or decorator has a method annotated @Produces (3.3.2)
//	- interceptor or decorator has a field annotated @Produces (3.4.2)
	public static final String PRODUCER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("producerInInterceptorOrDecorator", "producer"); //$NON-NLS-1$
//	- interceptor or decorator has a method annotated @Disposes
	public static final String DISPOSER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("disposerInInterceptorOrDecorator", "disposer"); //$NON-NLS-1$
//	- decorator has more than one delegate injection point, or does not have a delegate injection point (8.1.2)
	public static final String MULTIPLE_OR_MISSING_DELEGATE = INSTANCE.createSeverityOption("multipleDelegate", "ambiguous-delegate"); //$NON-NLS-1$
//	- injection point that is not an injected field, initializer method parameter 
//	  or bean constructor method parameter is annotated @Delegate (8.1.2)
	public static final String ILLEGAL_INJECTION_POINT_DELEGATE = INSTANCE.createSeverityOption("illegalInjectionPointDelegate", "delegate"); //$NON-NLS-1$
//	- bean class that is not a decorator has an injection point annotated @Delegate (8.1.2)
	public static final String ILLEGAL_BEAN_DECLARING_DELEGATE = INSTANCE.createSeverityOption("illegalBeanDeclaringDelegate", "delegate"); //$NON-NLS-1$
//	- delegate type does not implement or extend a decorated type of the decorator,
//	  or specifies different type parameters (8.1.3)
	public static final String DELEGATE_HAS_ILLEGAL_TYPE = INSTANCE.createSeverityOption("delegateHasIllegalType", "delegate-type"); //$NON-NLS-1$
//	- interceptor for lifecycle callbacks declares an interceptor binding type 
//	  that is defined @Target({TYPE, METHOD}) (9.2)
	public static final String ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING = INSTANCE.createSeverityOption("illegalLifecycleCallbackInterceptorBinding", "interceptor"); //$NON-NLS-1$
//	- managed bean has a class level interceptor binding and is declared final 
//	  or has a non-static, non-private, final method (9.3)
//	- non-static, non-private, final method of a managed bean has a method level 
//	  interceptor binding (9.3)
	public static final String ILLEGAL_INTERCEPTOR_BINDING_METHOD = INSTANCE.createSeverityOption("illegalInterceptorBindingMethod", "interceptor-binding"); //$NON-NLS-1$
//	- the set of interceptor bindings of a bean or interceptor, including bindings 
//	  inherited from stereotypes and other interceptor bindings, has two instances 
//	  of a certain interceptor binding type and the instances have different values 
//	  of some annotation member (9.5.2)
	public static final String CONFLICTING_INTERCEPTOR_BINDINGS = INSTANCE.createSeverityOption("conflictingInterceptorBindings", "ambiguous-interceptor-binding"); //$NON-NLS-1$
//	- interceptor or decorator has a method with a parameter annotated @Observes (10.4.2)
	public static final String OBSERVER_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("observerInInterceptorOrDecorator", "observer"); //$NON-NLS-1$
//	- interceptor or decorator has a method with a parameter annotated @Observes (10.4.2)
	public static final String OBSERVER_ASYNC_IN_INTERCEPTOR_OR_DECORATOR = INSTANCE.createSeverityOption("observerInInterceptorOrDecoratorAsync", "observer-async"); //$NON-NLS-1$
//	- interceptor or decorator is an alternative (2.6.1 non-portable)
	public static final String INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE = INSTANCE.createSeverityOption("interceptorOrDecoratorIsAlternative", "alternative"); //$NON-NLS-1$
//	- interceptor declared using @Interceptor does not declare any interceptor binding (9.2 non-portable)
	public static final String MISSING_INTERCEPTOR_BINDING = INSTANCE.createSeverityOption("missingInterceptorBinding", "missing-interceptor-binding"); //$NON-NLS-1$
//	8.3. Decorator resolution 
//	- If a decorator matches a managed bean, and the managed bean class is declared final, the container automatically detects 
//	  the problem and treats it as a deployment problem. 
//	- If a decorator matches a managed bean with a non-static, non-private, final method, and the decorator also implements that method, the container automatically detects the problem and treats it as a deployment problem.
	public static final String DECORATOR_RESOLVES_TO_FINAL_BEAN = INSTANCE.createSeverityOption("decoratorResolvesToFinalBean", "final"); //$NON-NLS-1$

	//Specialization

//	- managed bean class annotated @Specializes does not directly extend 
//	  the bean class of another managed bean (3.1.4)
//
//	- session bean class annotated @Specializes does not directly extend 
//	  the bean class of another session bean (3.2.4)
//
//	- method annotated @Specializes is static or does not directly override another producer method (3.3.3)
	public static final String ILLEGAL_SPECIALIZING_BEAN = INSTANCE.createSeverityOption("illegalSpecializingManagedBean", "static"); //$NON-NLS-1$
//	- X specializes Y but does not have some bean type of Y (4.3.1)
	public static final String MISSING_TYPE_IN_SPECIALIZING_BEAN = INSTANCE.createSeverityOption("missingTypeInSpecializingBean", "specializes"); //$NON-NLS-1$
//	- X specializes Y and Y has a name and X declares a name explicitly, using @Named (4.3.1)
	public static final String CONFLICTING_NAME_IN_SPECIALIZING_BEAN = INSTANCE.createSeverityOption("conflictingNameInSpecializingBean", "specializes-named"); //$NON-NLS-1$
//	- interceptor is annotated @Specializes (4.3.1 non-portable)
//	- decorator is annotated @Specializes (4.3.1 non-portable)
	public static final String INTERCEPTOR_ANNOTATED_SPECIALIZES = INSTANCE.createSeverityOption("interceptorAnnotatedSpecializes", "specializes"); //$NON-NLS-1$
//  5.1.3. Inconsistent specialization 
//	- Suppose an enabled bean X specializes a second bean Y. If there is another enabled bean that specializes Y we say that inconsistent 
//	  specialization exists. The container automatically detects inconsistent specialization and treats it as a deployment problem.
	public static final String INCONSISTENT_SPECIALIZATION = INSTANCE.createSeverityOption("inconsistentSpecialization", "inconsistent-specialization"); //$NON-NLS-1$

	//Miscellaneous

//	- Java EE component class has an injection point of type UserTransaction 
//	  and qualifier @Default, and may not validly make use of the JTA UserTransaction 
//	  according to the Java EE platform specification (3.6)
	public static final String ILLEGAL_INJECTING_USERTRANSACTION_TYPE = INSTANCE.createSeverityOption("illegalInjectingUserTransactionType", "user-transaction"); //$NON-NLS-1$
//	- Java EE component class supporting injection that is not a bean has an injection 
//	  point of type InjectionPoint and qualifier @Default (5.5.7)
	public static final String ILLEGAL_INJECTING_INJECTIONPOINT_TYPE = INSTANCE.createSeverityOption("illegalInjectingInjectionPointType", "injection-point"); //$NON-NLS-1$
//	- stereotype declares any qualifier annotation other than @Named (2.7.1.3 non-portable)
	public static final String ILLEGAL_QUALIFIER_IN_STEREOTYPE = INSTANCE.createSeverityOption("illegalQualifierInStereotype", "stereotype"); //$NON-NLS-1$
//	- bean class is deployed in two different bean archives (12.1 non-portable)
	// ? is it a definition problem

	public static final String MISSING_BEANS_XML = INSTANCE.createSeverityOption("missingBeansXml"); //$NON-NLS-1$
// - Each child <class> element must specify the name of an alternative/decorator/interceptor bean class/stereotype annotation. If there is no class with the specified
//	  name, or if the class with the specified name is not alternative/decorator/interceptor bean class/stereotype annotation, the container automatically detects the problem
//	  and treats it as a deployment problem.
	public static final String ILLEGAL_TYPE_NAME_IN_BEANS_XML = INSTANCE.createSeverityOption("illegalTypeInBeansXml"); //$NON-NLS-1$
//	- If the same type is listed twice under the <alternatives>, <decorators> or <interceptors> element, the container automatically detects the problem and
//	  treats it as a deployment problem.
	public static final String DUPLICATE_TYPE_IN_BEANS_XML = INSTANCE.createSeverityOption("duplicateTypeInBeansXml"); //$NON-NLS-1$

	public static final String OBSERVER_ASYNC_PARAMETER_ILLEGALLY_ANNOTATED = INSTANCE.createSeverityOption("observerAsyncParameterIllegallyAnnotated", "annotated-parameter"); //$NON-NLS-1$

	public static final String ILLEGAL_OBSERVER_AND_OBSERVER_ASYNC = INSTANCE.createSeverityOption("observerAndObserverAsync", "observer-async"); //$NON-NLS-1$

	/**
	 * @return the only instance of CDIPreferences
	 */
	public static CDIPreferences getInstance() {
		return INSTANCE;
	}

	private CDIPreferences() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.validation.ValidationSeverityPreferences#getWarningGroupID()
	 */
	@Override
	public String getWarningGroupID() {
		return WARNING_GROUP_ID;
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

	public static boolean shouldValidateBeansXml(IProject project) {
		return !(CDIPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, ILLEGAL_TYPE_NAME_IN_BEANS_XML)) && CDIPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, DUPLICATE_TYPE_IN_BEANS_XML)));
	}

	public static boolean isValidationEnabled(IProject project) {
		return INSTANCE.isEnabled(project);
	}

	public static int getMaxNumberOfProblemMarkersPerFile(IProject project) {
		return INSTANCE.getMaxNumberOfProblemMarkersPerResource(project);
	}
}