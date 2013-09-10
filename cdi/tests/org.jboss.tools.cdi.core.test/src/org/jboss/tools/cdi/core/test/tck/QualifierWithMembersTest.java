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

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * @author Alexey Kazakov
 */
public class QualifierWithMembersTest extends TCKTest {

	/**
	 * Factory produces bean with qualifier
	 * IntQualifier(width=2-(5 + 3) + 7*2, height= (12 + W) * 2 / 4)
	 * 
	 * Injection point looks for a bean with qualifier
	 * @IntQualifier(width=(int)(BeanFactory.W + 2) - 2, height=(int)'b' - (int)'a' + 9)
	 * 
	 * In both cases calculated width=8, height=10.
	 * The injection point must be resolved to the bean.  
	 * @throws CoreException
	 */
	public void testQualifierWithArithmeticExpressionsResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/expressions/SomeBean.java", "s");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
	}

	public void testQualifierWithStaticImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor2");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor3");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithStaticImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor2");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor3");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithStaticImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor2");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor3");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	public void testQualifierWithStaticImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor2");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	public void testQualifierWithoutImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor3");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	/**
	 * This test is created to check fixes for issue https://jira.jboss.org/browse/JBIDE-6517
	 * 
	 * However, the fix is postponed until JDT includes method parameters into Java model 
	 * as detailed Java element objects instead of providing limited string data 
	 * (getParameterNames(), getParameterTypes()).
	 * 
	 * We refrain from getting necessary data by building AST, because it would affect performance.
	 * 
	 * TODO The prefix '_' disabling this test must be removed as soon as JBIDE-6517 is assigned 
	 * to be fixed for a concrete version.
	 * 
	 * @throws CoreException
	 */
	public void testQualifierCoincidingSimpleNameInInjectingBeanAndCoincidingSimpleNameInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/coincidence/ObtainsInstanceBean.java", "cashPaymentProcessor");
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.coincidence.FirstPaymentProcessor");
	}
}