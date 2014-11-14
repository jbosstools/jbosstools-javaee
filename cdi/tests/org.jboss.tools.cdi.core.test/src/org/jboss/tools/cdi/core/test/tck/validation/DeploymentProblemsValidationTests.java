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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.INCONSISTENT_SPECIALIZATION[getVersionIndex()], "Maid, Manager", "Employee"), 21);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/inheritance/specialization/simple/broken/inconsistent/Manager.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.INCONSISTENT_SPECIALIZATION[getVersionIndex()], "Manager, Maid", "Employee"), 21);
	}

	/**
	 * 5.2.1. Unsatisfied and ambiguous dependencies
	 *  - If an unresolvable ambiguous dependency exists, the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testAmbiguousDependency() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/dependency/resolution/broken/ambiguous/Farm_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 34);
	}

	public void testAmbiguousDependencyWithNamed() throws Exception {
		String path = "JavaSource/org/jboss/jsr299/tck/tests/jbt/lookup/duplicateName/TestNamed.java";
		IFile file = tckProject.getFile(path);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 9, 25, 26);
		
		IInjectionPointField p = getInjectionPointField(path, "s5");
		Collection<IBean> bs = cdiProject.getBeans(false, p);
		assertEquals(3, bs.size());
		
		Set<String> keys = new HashSet<String>();
		for(IBean b: bs) {
			keys.add(b.getElementName());
		}
		assertTrue(!keys.contains("TestNamed.foo4"));
		assertTrue(keys.contains("TestNamed.foo4()"));
		assertTrue(keys.contains("TestNamed.foo5()"));
		assertTrue(keys.contains("TestNamed.foo6()"));
		
		IInjectionPointParameter pp = getInjectionPointParameter(path, "doSmth");
		Collection<IBean> bs2 = cdiProject.getBeans(false, pp);
		assertEquals(3, bs2.size());
		bs2.removeAll(bs);
		assertTrue(bs2.isEmpty());
	}

	/**
	 * 5.2.1. Unsatisfied and ambiguous dependencies
	 *  - If an unsatisfied dependency exists, the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testUnsatisfiedDependency() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/dependency/resolution/broken/unsatisfied/Bean_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 25);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/inject/delegateField/TimestampLogger.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 34);
	}

	public void testUnsatisfiedDependencyWithNamed() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/lookup/duplicateName/TestNamed.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 22, 23, 19, 20);
		int[] lines = {10, 11, 13, 14, 16, 17};
		for (int i: lines) {
			getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], i);
		}
	}

	/**
	 * CDI validator should not complain if there ambiguous dependencies for @Inject Instance<[type]>
	 * See https://issues.jboss.org/browse/JBIDE-7949
	 * 
	 * @throws Exception
	 */
	public void testAmbiguousDependencyForInstance() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/InjectionInstance.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 8);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 11);
	}

	/**
	 * CDI validator should not complain if there unsatisfied dependencies for @Inject Instance<[type]>
	 * See https://issues.jboss.org/browse/JBIDE-7949
	 * 
	 * @throws Exception
	 */
	public void testUnsatisfiedDependencyForInstance() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/InjectionInstance.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 9);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 12);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-7967
	 *  
	 * @throws Exception
	 */
	public void testBeansWithDefaultCounstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/defaultconstructors/CurrentProject.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 12);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNSATISFIED_INJECTION_POINTS[getVersionIndex()], 12);
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS[getVersionIndex()], 15);
	}

	/**
	 * 5.2.4. Primitive types and null values
	 *  - injection point of primitive type resolves to a bean that may have null values, such as a producer method with a non-primitive return type or a producer field with a non-primitive type
	 * 
	 * @throws Exception
	 */
	public void testPrimitiveInjectionPointResolvedToNonPrimitiveProducerMethod() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/inject/GameBroken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 7, 19);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 9);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 10);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 11);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 20);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 21);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN[getVersionIndex()], 22);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE[getVersionIndex()], "TestType[]", "ArrayProducer.produce()"), 6);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE[getVersionIndex()], "TestType", "TestType"), 7);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE[getVersionIndex()], "TestType[]", "ArrayProducer.produce2()"), 8);
	}

	public void testNormalBeanWithArrayType() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/ArrayProducer.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE_2[getVersionIndex()], "TestType[]", "ArrayProducer.produce()"), 8);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE_2[getVersionIndex()], "TestType[]", "ArrayProducer.produce2()"), 8);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE[getVersionIndex()], "int", "NumberProducer.produce()"), 9);
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE[getVersionIndex()], "long", "NumberProducer.foo"), 13);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE[getVersionIndex()], "Short", "NumberProducer.foo2"), 17);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE[getVersionIndex()], "boolean", "NumberProducer.foo3"), 21);
	}

	public void testNormalScopedBeanWithUnproxyableType() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/NumberProducer.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2[getVersionIndex()], "int", "NumberProducer.produce()"), 9);
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2[getVersionIndex()], "long", "NumberProducer.foo"), 16);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2[getVersionIndex()], "Short", "NumberProducer.foo2"), 21);
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2[getVersionIndex()], "boolean", "NumberProducer.foo3"), 21);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC[getVersionIndex()], "Unproxyable_Broken", "Unproxyable_Broken"), 23);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC[getVersionIndex()].substring(0, 0), 25);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-8018
	 * @throws Exception
	 */
	public void testClassWithDefaultConstructor() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/unproxyable/Number_Broken.java");
		getAnnotationTest().assertAnnotationIsNotCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC[getVersionIndex()], "BeanWithDefaultConsturctor", "BeanWithDefaultConsturctor"), 24);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE[getVersionIndex()], "Tuna_Broken", "Tuna_Broken"), 24);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE[getVersionIndex()].substring(0, 0) + ".*", 26);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalClass/Opportunity.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE[getVersionIndex()], "String", "Opportunity.t"), 26);
	}

	public void testNormalScopedFinalBean() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalClass/Tuna_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE_2[getVersionIndex()], "Tuna_Broken", "Tuna_Broken"), 21);

		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalClass/Opportunity.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE_2[getVersionIndex()], "String", "Opportunity.t"), 32);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()], "Tuna_Broken", "Tuna_Broken"), 23);
		getAnnotationTest().assertAnnotationIsNotCreated(file, CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM[getVersionIndex()].substring(0, 0) + ".*", 25);
		
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/clientProxy/unproxyable/finalMethod/Tuna_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM_2[getVersionIndex()], "Tuna_Broken", "Tuna_Broken"), 21);
		
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], "Cod, Sole"), 21);
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/duplicateNameResolution/Sole.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], "Sole, Cod"), 21);
		
		file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/lookup/duplicateName/TestNamed.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], "TestNamed.foo4.*"), 40, 49);
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], "TestNamed.foo5.*"), 43);
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME[getVersionIndex()], "TestNamed.foo6.*"), 46);
	}

	/**
	 * 	  • the EL name of one bean is of the form x.y, where y is a valid bean EL name, and x is the EL name of the other bean,
	 *      the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * @throws Exception
	 */
	public void testDuplicateBeanNamePrefix() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/lookup/byname/duplicatePrefixResolution/ExampleWebsite_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.UNRESOLVABLE_EL_NAME[getVersionIndex()], "example.com", "com", "example", "Example"), 22);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_CLASS[getVersionIndex()], "MockLogger"), 31);
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
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_METHOD[getVersionIndex()], "MockLogger", "log(String string)"), 31);
	}

	/**
	 * 6.6.4 Validation of passivation capable beans and dependencies
	 * - If a managed bean which declares a passivating scope is not passivation capable, then the container automatically detects the problem and treats it as a deployment problem.
	 * 
	 * See https://issues.jboss.org/browse/JBIDE-3126
	 * @throws Exception
	 */
	public void testSimpleWebBeanWithNonSerializableImplementationClassFails() throws Exception {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/context/passivating/broken/nonPassCapableManBeanHasPassScope/Hamina_Broken.java");
		getAnnotationTest().assertAnnotationIsCreated(file, MessageFormat.format(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN[getVersionIndex()], "Hamina_Broken", "SessionScoped"), 22);
	}
}