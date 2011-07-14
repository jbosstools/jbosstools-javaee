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
	 * section 5.6 aa) 
	 * @throws CoreException
	 */
	public void testObtainsInjectsInstanceOfInstance() throws CoreException {
		IInjectionPointField injectionPoint = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "paymentProcessor");
		Set<IBean> beans = cdiProject.getBeans(false, injectionPoint);
		assertEquals(1, beans.size());

		injectionPoint = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/lookup/dynamic/ObtainsInstanceBean.java", "anyPaymentProcessor");
		beans = cdiProject.getBeans(false, injectionPoint);
		assertEquals(3, beans.size());
	}
}