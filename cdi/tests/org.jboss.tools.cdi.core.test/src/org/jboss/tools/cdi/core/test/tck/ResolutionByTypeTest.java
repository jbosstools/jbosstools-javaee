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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * @author Alexey Kazakov
 */
public class ResolutionByTypeTest extends TCKTest {

	/**
	 * Section 5.2 - Typesafe resolution
	 *   lb) Test with beans without required qualifiers.
	 *   
	 * @throws CoreException 
	 */
	public void testDefaultBindingTypeAssumed() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tuna");
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanTypes(false, beans.iterator().next(), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tuna");
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   hc) Check multiple types resolve to a single getBeans().
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByType() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tuna", "javax.enterprise.inject.Default");
		assertEquals("Wrong number of the beans", 1, beans.size());
		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Animal", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.FishILike");
		assertEquals("Wrong number of the beans", 3, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Salmon", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.SeaBass", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Haddock");
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   lc) Test with matching beans with matching qualifier with same type.
	 *   
	 * @throws CoreException 
	 */
	public void testAllQualifiersSpecifiedForResolutionMustAppearOnBean() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Animal", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Chunky", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Whitefish");
		assertEquals("Wrong number of the beans", 1, beans.size());
		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.ScottishFish", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Whitefish");
		assertEquals("Wrong number of the beans", 2, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Cod", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Sole");
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   ka) Test with a parameterized type.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithTypeParameter() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.ScottishFishFarmer");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QFarmer<QScottishFish;>;");
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.ScottishFishFarmer");
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   j) Test with an array type.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithArray() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.SpiderProducer");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "[QSpider;");
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertTrue("The bean should be a producer method.", beans.iterator().next() instanceof IProducerMethod);
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   i) Test with a primitive type.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithPrimitives() throws CoreException {
		Set<IBean> beans = getBeans("java.lang.Double", "javax.enterprise.inject.Any");
		// There is checks for 2 beans (not for 3) in TCK but actually there is one more bean in another package which matches. So let's check for 3 beans.
		assertEquals("Wrong number of the beans", 3, beans.size());
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/NumberProducer.java");
		assertNotNull("Bean can't be a null", bean);
		Set<IInjectionPoint> injections = bean.getInjectionPoints();
		assertEquals("Wrong number of the injection points", 2, injections.size());
		for (IInjectionPoint injectionPoint : injections) {
			beans = cdiProject.getBeans(true, injectionPoint);
			assertEquals("Wrong number of the beans", 1, beans.size());
		}
	}

	public void testAbstractClassIsNotEligibleForInjection() throws CoreException {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/chain/CurrentProject.java");
		Set<IInjectionPoint> injections = bean.getInjectionPoints();
		assertEquals("Wrong number of the injection points", 1, injections.size());
		IInjectionPoint injectionPoint = injections.iterator().next();
		assertNotNull(injectionPoint);
		Set<IBean> bs = cdiProject.getBeans(true, injectionPoint);
		assertEquals(2, bs.size());
		Set<String> names = new HashSet<String>();
		System.out.println(bs.size());
		for (IBean b: bs) {
			names.add(b.getSimpleJavaName());
		}
		names.contains("CurrentProject.getCurrent()");
		names.contains("ProjectImpl");		
	}

	/**
	 * Section 5.2 - Typesafe resolution
	 *   ld) Test with matching beans with matching qualifier with same annotation member value for each member which is not annotated @javax.enterprise.util.NonBinding.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithNonBindingMembers() throws CoreException {
		IQualifierDeclaration expensiveQualifier = getQualifierDeclarationFromClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Expensive");
		IQualifierDeclaration whitefishQualifier = getQualifierDeclarationFromClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Whitefish");
		IParametedType type = getType("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Animal");
		Set<IBean> beans = cdiProject.getBeans(true, type, new IQualifierDeclaration[]{expensiveQualifier, whitefishQualifier});
		assertContainsBeanClasses(beans, new String[]{"org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.RoundWhitefish", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Halibut"});
	}

	/**
	 * 5.1.4. Inter-module injection
	 *   i) Check a disabled managed bean is not injectable.
	 *   
	 * @throws CoreException 
	 */
	public void testPolicyNotAvailableInNonDeploymentArchive() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Spider");
		assertFalse("Wrong number of the beans", beans.isEmpty());
		assertDoesNotContainBeanClasses(beans, new String[]{"org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.CrabSpider", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.DaddyLongLegs"});
		beans = cdiProject.getBeans("crabSpider", true);
		assertTrue("Wrong nuber of the beans", beans.isEmpty());
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   a) Check managed bean.
	 *   
	 * @throws CoreException 
	 */
	public void testBeanTypesOnManagedBean() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Canary");
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Bird");
		assertEquals("Wrong number of the beans", 0, beans.size());
		assertContainsBeanTypes(bean, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Canary", "java.lang.Object");
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   e) Check generic managed bean.
	 *   
	 * @throws CoreException 
	 */
	public void testGenericBeanTypesOnManagedBean() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Emu");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QFlightlessBird<QAustralian;>;");
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();

		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Emu");
		assertEquals("Wrong number of the beans", 0, beans.size());

		type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Emu");
		parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QFlightlessBird<QEuropean;>;");
		beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[0]);
		assertEquals("Wrong number of the beans", 0, beans.size());

		assertContainsBeanTypes(false, bean, "java.lang.Object");
		assertContainsBeanTypes(false, bean, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.FlightlessBird");
		assertContainsBeanTypeSignatures(false, bean, "Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.FlightlessBird<Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Australian;>;");
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   c) Check producer method.
	 *   
	 * @throws CoreException 
	 */
	public void testBeanTypesOnProducerMethod() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Parrot");
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanTypes(beans.iterator().next(), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Parrot", "java.lang.Object");

		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Bird");
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   h) Check generic producer field.
	 *   
	 * @throws CoreException 
	 */
	public void testGenericBeanTypesOnProducerField() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.PetShop");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QCat<QEuropean;>;");
		IQualifierDeclaration qualifier = getQualifierDeclarationFromClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tame");
		assertNotNull("Can't find the qualifier.", qualifier);
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[]{qualifier});
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();

		assertContainsBeanTypes(false, bean, "java.lang.Object");
		assertContainsBeanTypes(false, bean, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Cat");
		assertContainsBeanTypeSignatures(false, bean, "Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Cat<Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.European;>;");

		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.DomesticCat", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tame");
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   g) Check generic producer method.
	 *   
	 * @throws CoreException 
	 */
	public void testGenericBeanTypesOnProducerMethod() throws CoreException {
		IType type = EclipseJavaUtil.findType(EclipseUtil.getJavaProject(cdiProject.getNature().getProject()), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.PetShop");
		IParametedType parametedType = cdiProject.getNature().getTypeFactory().getParametedType(type, "QCat<QAfrican;>;");
		IQualifierDeclaration qualifier = getQualifierDeclarationFromClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/PetShop.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Wild");
		assertNotNull("Can't find the qualifier.", qualifier);
		Set<IBean> beans = cdiProject.getBeans(true, parametedType, new IQualifierDeclaration[]{qualifier});
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();

		assertContainsBeanTypes(false, bean, "java.lang.Object");
		assertContainsBeanTypes(false, bean, "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Cat");
		assertContainsBeanTypeSignatures(false, bean, "Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Cat<Lorg.jboss.jsr299.tck.tests.lookup.typesafe.resolution.African;>;");

		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Lion", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Wild");
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   d) Check producer field.
	 *   
	 * @throws CoreException 
	 */
	public void testBeanTypesOnProducerField() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Dove");
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanTypes(beans.iterator().next(), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Dove", "java.lang.Object");

		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Bird");
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	public void testInjectionResolutionOfRestrictedProducerField() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/Zoo.java", "cats");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue(bean instanceof IProducerField);
	}

	public void testInjectionResolutionOfRestrictedProducerMethod() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/Zoo.java", "lions");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue(bean instanceof IProducerMethod);
	}

}