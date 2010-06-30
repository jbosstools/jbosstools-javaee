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

import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;

/**
 * @author Alexey Kazakov
 */
public class InterceptorDefinitionTest extends TCKTest {

	/**
	 * Section 9.1.2 - Interceptor bindings for stereotypes
	 *   b) An interceptor binding declared by a stereotype are inherited by any bean that declares that stereotype.
	 * @throws JavaModelException 
	 */
	public void testStereotypeInterceptorBindings() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.interceptors.definition.SecureTransaction");
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue("The bean should be an interceptor", bean instanceof IClassBean);
		IClassBean interceptor = (IClassBean)bean;
		assertFalse("The intercpetor should inherites interceptor bindings", interceptor.getInterceptorBindingDeclarations().isEmpty());
	}
}