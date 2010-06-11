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

/**
 * @author Alexey Kazakov
 */
public class EnterpriseResolutionByTypeTest extends TCKTest {

	/**
	 * Section 2.2.2 - Restricting the bean types of a bean
	 *   b) Check session bean.
	 *   
	 * @throws CoreException 
	 */
	public void testBeanTypesOnSessionBean() throws CoreException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.CapercaillieLocal");
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanTypes(beans.iterator().next(), "org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.CapercaillieLocal", "java.lang.Object");
		beans = getBeans("org.jboss.jsr299.tck.tests.lookup.typesafe.resolution.ScottishBirdLocal");
		assertEquals("Wrong number of the beans", 0, beans.size());
	}
}