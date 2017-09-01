/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck20;

import java.util.Collection;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.tck.BuiltInBeanInjectionTest;
import org.jboss.tools.cdi.core.test.tck.ITCKProjectNameProvider;

public class BuiltInBeanInjectionCDI20Test extends BuiltInBeanInjectionTest {

	@Override
	public ITCKProjectNameProvider getProjectNameProvider() {
		return new TCK20ProjectNameProvider();
	}

	/**
	 * Test built-in bean with type javax.servlet.http.HttpSession
	 */
	public void testBuiltInHttpSessionBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/HttpSessionInjectedBean.java", "httpSession");
		assertNotNull(field);
		
		Collection<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof IBuiltInBean);
		IType t = b.getBeanClass();
		assertEquals(CDIConstants.HTTP_SESSION_TYPE_NAME, t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type javax.servlet.http.HttpServletRequest
	 */
	public void testBuiltInHttpServletRequestBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/HttpServletRequestInjectedBean.java", "httpServletRequest");
		assertNotNull(field);
		
		Collection<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof IBuiltInBean);
		IType t = b.getBeanClass();
		assertEquals(CDIConstants.HTTP_SERVLET_REQUEST_TYPE_NAME, t.getFullyQualifiedName());
	}

}