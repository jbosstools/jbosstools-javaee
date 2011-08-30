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
package org.jboss.tools.cdi.core.test.tck.lookup;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.tck.TCKTest;

/**
 * @author Alexey Kazakov
 */
public class DynamicLookupTest extends TCKTest {

	/**
	 * Section 5.6 - Programmatic lookup
	 * @throws CoreException 
	 */
	public void testObtainsInjectsInstance() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "paymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals(1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.dynamic.AdvancedPaymentProcessor");
	}

	public void testObtainsInjectsProvider() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "paymentProcessor2");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals(1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.dynamic.AdvancedPaymentProcessor");
	}

	/**
	 * Section 5.6 - Programmatic lookup
	 * @throws CoreException 
	 */
	public void testObtainsAmbiguousInjectsInstance() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "anyPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals(3, beans.size());
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.dynamic.AdvancedPaymentProcessor", "org.jboss.jsr299.tck.tests.lookup.dynamic.SimplePaymentProcessor", "org.jboss.jsr299.tck.tests.lookup.dynamic.RemotePaymentProcessor");
	}
}