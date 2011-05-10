/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeansTest extends SeamConfigTest {

	public SeamBeansTest() {}

	/**
	 * Test 01-1.
	 * Sources contain simple bean class MyBean1.
	 * Seam config xml contains declaration:
	 * <test01:MyBean1>
	 *  <s:modifies/>
	 * </test01:MyBean1>
	 * 
	 * ASSERT: Model contains 1 bean with type MyBean1.
	 */
	public void testModifyingATrivialBean() throws CoreException, IOException {
		Set<IBean> beans = getBeansByClassName("org.jboss.beans.test01.MyBean1");
		assertEquals(1, beans.size());
	}
	
	/**
	 * Test 01-2.
	 * Sources contain simple bean class MyBean2.
	 * Seam config xml contains declaration:
	 * <test01:MyBean2>
	 *  <s:replaces/>
	 * </test01:MyBean2>
	 * 
	 * ASSERT: Model contains 1 bean with type MyBean2.
	 */
	public void testReplacingATrivialBean() throws CoreException, IOException {
		Set<IBean> beans = getBeansByClassName("org.jboss.beans.test01.MyBean2");
		assertEquals(1, beans.size());
	}

	/**
	 * Test 01-3.
	 * Sources contain simple bean class MyBean3.
	 * Seam config xml contains declaration:
	 * <test01:MyBean3>
	 * </test01:MyBean3>
	 * 
	 * ASSERT: Model contains 2 beans with type MyBean3.
	 */
	public void testCreatingNewTrivialBean() throws CoreException, IOException {
		Set<IBean> beans = getBeansByClassName("org.jboss.beans.test01.MyBean3");
		assertEquals(2, beans.size());
	}

	/**
	 * Test 01-4.
	 * Sources contain simple bean class MyBean4.
	 * Seam config xml contains 2 declarations:
	 * <test01:MyBean4>
	 * </test01:MyBean4>
	 * <test01:MyBean4>
	 * </test01:MyBean4>
	 * 
	 * ASSERT: Model contains 3 beans with type MyBean4.
	 */
	public void testCreatingTwoNewTrivialBeans() throws CoreException, IOException {
		Set<IBean> beans = getBeansByClassName("org.jboss.beans.test01.MyBean4");
		assertEquals(3, beans.size());
	}

	/**
	 * Test 02-1.
	 * Sources contain simple bean class MyBean1
	 * with qualifier MyQualifier1.
	 * Seam config xml contains declaration:
	 * <test02:MyBean1>
	 *  <s:modifies/>
	 *  <test02:MyQualifier2/>
	 * </test02:MyBean1>
	 * 
	 * ASSERT: Model contains 1 bean with type MyBean1 and qualifier MyQualifier1.
	 * ASSERT: That bean also has qualifier MyQualifier2.
	 * 
	 * @author Viacheslav Kabanovich
	 *
	 */
	public void testModifyingAQualifiedBean() throws CoreException, IOException {
		Set<IBean> beans1 = cdiProject.getBeans(false, "org.jboss.beans.test02.MyBean1", 
				new String[]{"org.jboss.beans.test02.MyQualifier1"});
		assertEquals(1, beans1.size());
		Set<IBean> beans2 = cdiProject.getBeans(false, "org.jboss.beans.test02.MyBean1", 
				new String[]{"org.jboss.beans.test02.MyQualifier1",
							 "org.jboss.beans.test02.MyQualifier2"});
		assertEquals(1, beans2.size());
		assertTrue("Two sets should contain the same bean.", beans1.iterator().next() == beans2.iterator().next());
	}

	/**
	 * Test 02-2.
	 * Sources contain simple bean class MyBean2
	 * with qualifier MyQualifier1.
	 * Seam config xml contains declaration:
	 * <test02:MyBean2>
	 *  <s:replaces/>
	 *  <test02:MyQualifier2/>
	 * </test02:MyBean2>
	 * 
	 * ASSERT: Model contains no bean with type MyBean2 and qualifier MyQualifier1.
	 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier2.
	 */
	public void testReplacingAQualifiedBean() throws CoreException, IOException {
		Set<IBean> beans1 = cdiProject.getBeans(false, "org.jboss.beans.test02.MyBean2", 
				new String[]{"org.jboss.beans.test02.MyQualifier1"});
		assertTrue(beans1.isEmpty());
		Set<IBean> beans2 = cdiProject.getBeans(false, "org.jboss.beans.test02.MyBean2", 
				new String[]{"org.jboss.beans.test02.MyQualifier2"});
		assertEquals(1, beans2.size());
	}

	/**
	 * Test 02-3.
	 * Sources contain simple bean class MyBean3.
	 * Seam config xml contains declarations:
	 * <test02:MyBean3>
	 *  <test02:MyQualifier1/>
	 * </test02:MyBean3>
	 * <test02:MyBean3>
	 *  <test02:MyQualifier2/>
	 * </test02:MyBean3>
	 * 
	 * ASSERT: Model contains 3 bean with type MyBean2.
	 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier1.
	 * ASSERT: Model contains 1 bean with type MyBean2 and qualifier MyQualifier2.
	 * 
	 * @author Viacheslav Kabanovich
	 *
	 */
	public void testCreatingTwoNewQualifiedBeans() throws CoreException, IOException {
		Set<IBean> beans = getBeansByClassName("org.jboss.beans.test01.MyBean4");
		assertEquals(3, beans.size());
	}

	/**
	 * Test 03-1.
	 * Sources contain simple bean class MyBean1 with qualifier Named("test03-1-a").
	 * Seam config xml contains declaration:
	 * <test03:MyBean1>
	 *  <s:modifies/>
	 *  <s:Named>test03-1-b</s:Named>
	 * </test03:MyBean1>
	 * 
	 * ASSERT: Model contains no named bean with name "test03-1-a".
	 * ASSERT: Model contains 1 named bean with name "test03-1-b".
	 */
	public void testModifyingANamedBean() throws CoreException, IOException {
		Set<IBean> beans1 = cdiProject.getBeans(false, "org.jboss.beans.test03.MyBean1");
		assertEquals(1, beans1.size());
		IBean b = beans1.iterator().next();
		assertEquals("test03-1-b", b.getName());
	}

	/**
	 * Test 03-2.
	 * Sources contain simple bean class MyBean2 with qualifier Named("test03-2-a").
	 * Seam config xml contains declaration:
	 * <test03:MyBean2>
	 *  <s:replaces/>
	 *  <s:Named>test03-2-b</s:Named>
	 * </test03:MyBean2>
	 * 
	 * ASSERT: Model contains no named bean with name "test03-2-a".
	 * ASSERT: Model contains 1 named bean with name "test03-2-b".
	 */
	public void testReplacingANamedBean() throws CoreException, IOException {
		Set<IBean> beans1 = cdiProject.getBeans(false, "org.jboss.beans.test03.MyBean2");
		assertEquals(1, beans1.size());
		IBean b = beans1.iterator().next();
		assertEquals("test03-2-b", b.getName());
	}

	/**
	 * Test 03-3.
	 * Sources contain simple bean class MyBean3 with qualifier Named("test03-3-a").
	 * Seam config xml contains declarations:
	 * <test03:MyBean3>
	 *  <s:Named>test03-3-b</s:Named>
	 * </test03:MyBean3>
	 * <test03:MyBean3>
	 *  <s:Named>test03-3-c</s:Named>
	 * </test03:MyBean3>
	 * 
	 * ASSERT: Model contains named beans "test03-3-a", "test03-3-b", "test03-3-c".
	 */
	public void testCreatingNamedBeans() throws CoreException, IOException {
		Set<IBean> beans = cdiProject.getBeans(false, "org.jboss.beans.test03.MyBean3");
		assertEquals(3, beans.size());
		Set<String> names = new HashSet<String>();
		for (IBean b: beans) {
			names.add(b.getName());
		}
		assertTrue(names.contains("test03-3-a"));
		assertTrue(names.contains("test03-3-b"));
		assertTrue(names.contains("test03-3-c"));
	}

	/**
	 * Test 04-1.
	 * Sources contain class MyBean1 that declares producer field of type MyType1,
	 * class MyType1 has no bean constructor.
	 * Seam config xml contains declaration:
	 * <test04:MyBean1>
	 *  <s:modifies/>
	 * </test04:MyBean1>
	 * 
	 * ASSERT: Model contains 1 bean with type MyType1.
	 * ASSERT: That bean is field producer.
	 */
	public void testModifyingBeanWithFieldProducer() {
		Set<IBean> beans = cdiProject.getBeans(false, "org.jboss.beans.test04.MyType1");
		assertEquals(1, beans.size());
		IBean b = beans.iterator().next();
		assertTrue(b instanceof IProducerField);
	}

	/**
	 * Test 04-2.
	 * Sources contain class MyBean2 that declares producer field of type MyType2,
	 * class MyType2 has no bean constructor.
	 * Seam config xml contains declaration:
	 * <test04:MyBean2>
	 *  <s:modifies/>
	 *  <test04:myType2>
	 *   <s:Named>test04-2-a</s:Named>
	 *  </test04:myType2>
	 * </test04:MyBean2>
	 * 
	 * ASSERT: Model contains 1 bean with type MyType2.
	 * ASSERT: That bean is field producer.
	 * ASSERT: That bean has qualifier MyQualifier with kind="kind-04-2".
	 * ASSERT: That bean has name "test04-2-a".
	 */
	public void testModifyingBeanWithModifiedFieldProducer() {
		Set<IBean> beans = cdiProject.getBeans(false, "org.jboss.beans.test04.MyType2",
				new String[]{"org.jboss.beans.test04.MyQualifier"});
		assertEquals(1, beans.size());
		IBean b = beans.iterator().next();
		Set<IQualifierDeclaration> qs = b.getQualifierDeclarations();
		Map<String, IQualifierDeclaration> map = new HashMap<String, IQualifierDeclaration>();
		for (IQualifierDeclaration q: qs) {
			map.put(q.getType().getFullyQualifiedName(), q);
		}
		IQualifierDeclaration myQualifier = map.get("org.jboss.beans.test04.MyQualifier");
		assertNotNull(myQualifier);
		assertEquals("kind-04-2", myQualifier.getMemberValue("kind"));
		assertEquals("test04-2-a", b.getName());
		
		assertTrue(b instanceof IProducerField);
	}

	protected Set<IBean> getBeansByClassName(String className) {
		return cdiProject.getBeans(false, className, new String[0]);
	}

}
