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
public class DynamicLookupTest extends TCKTest {

	/**
	 * Section 5.6 - Programmatic lookup
	 * @throws CoreException 
	 */
	public void testObtainsInjectsInstance() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "paymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.lookup.dynamic.AdvancedPaymentProcessor");
	}

	/**
	 * Section 5.6 - Programmatic lookup
	 * @throws CoreException 
	 */
	public void testObtainsAmbiguousInjectsInstance() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "anyPaymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertContainsBeanClasses(beans, "org.jboss.jsr299.tck.tests.lookup.dynamic.AdvancedPaymentProcessor", "org.jboss.jsr299.tck.tests.lookup.dynamic.SimplePaymentProcessor", "org.jboss.jsr299.tck.tests.lookup.dynamic.RemotePaymentProcessor");
	}
}