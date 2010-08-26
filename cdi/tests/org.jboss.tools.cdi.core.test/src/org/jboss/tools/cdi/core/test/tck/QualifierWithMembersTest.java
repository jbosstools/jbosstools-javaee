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
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * @author Alexey Kazakov
 */
public class QualifierWithMembersTest extends TCKTest {

	public void testQualifierWithStaticImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndNonStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "chequePaymentProcessor3");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.AdvancedPaymentProcessor");
	}

	public void testQualifierWithStaticImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndStaticInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "cashPaymentProcessor3");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.CashPaymentProcessor");
	}

	public void testQualifierWithStaticImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithoutImportInInjectingBeanAndWithoutImportInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "otherPaymentProcessor3");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.OtherPaymentProcessor");
	}

	public void testQualifierWithNonStaticImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	public void testQualifierWithStaticImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	public void testQualifierWithoutImportInInjectingBeanUnresolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/ObtainsInstanceBean.java", "unresolvedCreditCardPaymentProcessor3");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 0, beans.size());
	}

	// TODO This test fails because of https://jira.jboss.org/browse/JBIDE-6517
	public void testQualifierCoincidingSimpleNameInInjectingBeanAndCoincidingSimpleNameInInjectedBeanResolved() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/coincidence/ObtainsInstanceBean.java", "cashPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.jbt.resolution.coincidence.FirstPaymentProcessor");
	}
}