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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 * @author Viacheslav Kabanovich
 */
public class InjectionPointWithNewQualifierTest extends TCKTest {

	public void testNewHashSetParameter() throws CoreException {
		IInjectionPointParameter injection = getInjectionPointParameter("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/newSimpleBean/Dragon.java", "initialize");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "java.util.HashSet");
	}

	public void testNewArrayListField() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/newSimpleBean/Griffin.java", "list");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "java.util.ArrayList");
		IBean b = beans.iterator().next();
		assertEquals(1, b.getQualifierDeclarations().size());
	}

	public void testNewCustomBeanField() throws CoreException {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/newSimpleBean/NewLionConsumer.java", "lion");
		Set<IBean> beans = cdiProject.getBeans(true, injection);
		assertEquals("Wrong number of the beans", 1, beans.size());
		assertContainsBeanClass(beans, "org.jboss.jsr299.tck.tests.implementation.simple.newSimpleBean.Lion");
		IBean b = beans.iterator().next();
		assertEquals(1, b.getQualifierDeclarations().size());
		
		//Check that new bean has exactly one qualifier - @New.
		IQualifierDeclaration d = b.getQualifierDeclarations().iterator().next();
		assertEquals(CDIConstants.NEW_QUALIFIER_TYPE_NAME, d.getType().getFullyQualifiedName());
		Set<IQualifier> qs = b.getQualifiers();
		assertEquals(1, qs.size());
		IQualifier q = qs.iterator().next();
		assertEquals(CDIConstants.NEW_QUALIFIER_TYPE_NAME, q.getSourceType().getFullyQualifiedName());
		
	}

}