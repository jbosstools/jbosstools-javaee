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

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IParametedType;
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
		assertContainsBeanType(beans.iterator().next(), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Tuna");
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
	 *   ld) Test with matching beans with matching qualifier with same annotation member value for each member which is not annotated @javax.enterprise.util.NonBinding.
	 *   
	 * @throws CoreException 
	 */
	public void testResolveByTypeWithNonBindingMembers() throws CoreException {
		IQualifierDeclaration expensiveQualifier = getQualifierDeclarationFromBeanClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Expensive");
		IQualifierDeclaration whitefishQualifier = getQualifierDeclarationFromBeanClass("JavaSource/org/jboss/jsr299/tck/tests/lookup/typesafe/resolution/RoundWhitefish.java", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Whitefish");
		IParametedType type = getType("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Animal");
		Set<IBean> beans = cdiProject.getBeans(true, type, new IQualifierDeclaration[]{expensiveQualifier, whitefishQualifier});
		assertContainsBeanClasses(beans, new String[]{"org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.RoundWhitefish", "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.Halibut"});
	}

	// TODO continue implementing the tests
}