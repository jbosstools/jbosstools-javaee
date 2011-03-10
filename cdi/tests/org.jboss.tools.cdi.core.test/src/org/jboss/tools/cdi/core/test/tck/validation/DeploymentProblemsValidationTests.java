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

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;

/**
 * @author Alexey Kazakov
 */
public class DeploymentProblemsValidationTests extends ValidationTest {

	/**
	 * 5.1.3. Inconsistent specialization
	 *  - Suppose an enabled bean X specializes a second bean Y. If there is another enabled bean that specializes Y we say that inconsistent
	 *    specialization exists. The container automatically detects inconsistent specialization and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testInconsistentSpecialization() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/inconsistent/Maid.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.INCONSISTENT_SPECIALIZATION, "Maid, Manager", "Employee"), 21);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/inconsistent/Manager.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.INCONSISTENT_SPECIALIZATION, "Manager, Maid", "Employee"), 21);
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
	 * CDI validator should not complain if there ambiguous dependencies for @Inject Instance<[type]>
	 * See https://issues.jboss.org/browse/JBIDE-7949
	 * 
	 * @throws Exception
	 */
	public void testAmbiguousDependencyForInstance() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/InjectionInstance.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 8);
		assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 11);
	}

	/**
	 * CDI validator should not complain if there unsatisfied dependencies for @Inject Instance<[type]>
	 * See https://issues.jboss.org/browse/JBIDE-7949
	 * 
	 * @throws Exception
	 */
	public void testUnsatisfiedDependencyForInstance() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/InjectionInstance.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 9);
		assertMarkerIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 12);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-7967
	 *  
	 * @throws Exception
	 */
	public void testBeansWithDefaultCounstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/defaultconstructors/CurrentProject.java");
		assertMarkerIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 12);
		assertMarkerIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 12);
		assertMarkerIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, 15);
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
	 * 	5.4.1. Unproxyable bean types
	 *  - Array types cannot be proxied by the container.
	 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
	 * 	  the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testInjectionPointWithArrayType() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/InjectionPointBean_Broken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE, "TestType[]", "ArrayProducer.produce()"), 6);
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE, "TestType", "TestType"), 7);
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE, "TestType[]", "ArrayProducer.produce2()"), 8);
	}

	/**
	 * 	5.4.1. Unproxyable bean types
	 *  - Primitive types cannot be proxied by the container.
	 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
	 * 	  the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testInjectionPointWithUnproxyableTypeWhichResolvesToNormalScopedBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/Number_Broken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "int", "NumberProducer.produce()"), 9);
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "long", "NumberProducer.foo"), 13);
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "Short", "NumberProducer.foo2"), 17);
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "NumberProducer.foo3"), 21);
	}

	/**
	 * 	5.4.1. Unproxyable bean types
	 *  - Classes which don't have a non-private constructor with no parameters cannot be proxied by the container.
	 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
	 * 	  the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testClassWithPrivateConstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/privateConstructor/InjectionPointBean.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC, "Unproxyable_Broken", "Unproxyable_Broken"), 23);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-8018
	 * @throws Exception
	 */
	public void testClassWithDefaultConstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/Number_Broken.java");
		assertMarkerIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC, "BeanWithDefaultConsturctor", "BeanWithDefaultConsturctor"), 24);
	}

	/**
	 * 	5.4.1. Unproxyable bean types
	 *  - Classes which are declared final cannot be proxied by the container.
	 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
	 * 	  the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testInjectionPointWhichResolvesToNormalScopedFinalBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalClass/FishFarm.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE, "Tuna_Broken", "Tuna_Broken"), 24);
	}

	/**
	 * 	5.4.1. Unproxyable bean types
	 *  - Classes which have final methods cannot be proxied by the container.
	 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
	 * 	  the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testClassWithFinalMethodCannotBeProxied() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalMethod/FishFarm.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM, "Tuna_Broken", "Tuna_Broken"), 23);
	}

	/**
	 *  5.3.1. Ambiguous EL names
	 *  - All unresolvable ambiguous EL names are detected by the container when the application is initialized.
	 *    Suppose two beans are both available for injection in a certain war, and either:
	 *    • the two beans have the same EL name and the name is not resolvable, or
	 * 
	 * @throws Exception
	 */
	public void testDuplicateNamedBeans() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/duplicateNameResolution/Cod.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, "Cod, Sole"), 21);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/duplicateNameResolution/Sole.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, "Sole, Cod"), 21);
	}

	/**
	 * 	  • the EL name of one bean is of the form x.y, where y is a valid bean EL name, and x is the EL name of the other bean,
	 *      the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testDuplicateBeanNamePrefix() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/duplicatePrefixResolution/ExampleWebsite_Broken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.UNRESOLVABLE_EL_NAME, "example.com", "com", "example", "Example"), 22);
	}

	/**
	 * 	8.3 - Decorator resolution
	 *  - If a decorator matches a managed bean, and the managed bean class is declared final, the container automatically detects
	 *    the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testAppliesToFinalManagedBeanClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/finalBeanClass/TimestampLogger.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_CLASS, "MockLogger"), 31);
	}

	/**
	 * 	8.3 - Decorator resolution
	 *  - If a decorator matches a managed bean with a non-static, non-private, final method, and the decorator also implements that method,
	 *    the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testAppliesToFinalMethodOnManagedBeanClass() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/broken/finalBeanMethod/TimestampLogger.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_METHOD, "MockLogger", "log(String string)"), 31);
	}

	/**
	 * 6.6.4 Validation of passivation capable beans and dependencies
	 * - If a managed bean which declares a passivating scope is not passivation capable, then the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * See https://issues.jboss.org/browse/JBIDE-3126
	 * @throws Exception
	 */
	public void testSimpleWebBeanWithNonSerializableImplementationClassFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/context/passivating/broken/nonPassivationCapableManagedBeanHasPassivatingScope/Hamina_Broken.java");
		assertMarkerIsCreated(file, MessageFormat.format(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN, "Hamina_Broken", "SessionScoped"), 22);
	}
}