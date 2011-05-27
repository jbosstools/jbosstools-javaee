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
package org.jboss.tools.cdi.seam.solder.core.test;

import java.io.IOException;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.seam.solder.core.generic.GenericBeanProducerMethod;
import org.jboss.tools.cdi.seam.solder.core.generic.GenericClassBean;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class GenericBeanTest extends SeamSolderTest {

	public GenericBeanTest() {}

	public void testGenericBeanEndPointInjections() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
	
		/*
		 * Case 1. (default qualifier case)
		 * Injection point: in class MyBeanInjections
		 *     @Inject MyBean first1
		 * Generic bean producer method: MyGenericBean.createMyFirstBean()
		 * Configuration producer method: MyConfigurationProducer.getOneConfig()
		 */
		IInjectionPointField injection = getInjectionPointField(cdi, "src/org/jboss/generic/MyBeanInjections.java", "first1");

		Set<IBean> bs = cdi.getBeans(false, injection);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
		IProducerMethod m = (IProducerMethod)b;
		assertEquals("createMyFirstBean", m.getMethod().getElementName());
		assertTrue(b instanceof GenericBeanProducerMethod);
		GenericBeanProducerMethod gm = (GenericBeanProducerMethod)b;
		GenericClassBean cb = (GenericClassBean)gm.getClassBean();
		IBean gb = cb.getGenericProducerBean();
		assertTrue(gb instanceof IProducerMethod);
		IProducerMethod gbm = (IProducerMethod)gb;
		assertEquals("getOneConfig", gbm.getMethod().getElementName());

		/*
		 * Case 2. (non-default qualifier case)
		 * Injection point: in class MyBeanInjections
		 *     @Inject @Qualifier1 MyBean first2
		 * Generic bean producer method: MyGenericBean.createMyFirstBean()
		 * Configuration producer method: MyConfigurationProducer.getSecondConfig()
		 */
		injection = getInjectionPointField(cdi, "src/org/jboss/generic/MyBeanInjections.java", "first2");

		bs = cdi.getBeans(false, injection);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
		m = (IProducerMethod)b;
		assertEquals("createMyFirstBean", m.getMethod().getElementName());
		assertTrue(b instanceof GenericBeanProducerMethod);
		gm = (GenericBeanProducerMethod)b;
		cb = (GenericClassBean)gm.getClassBean();
		gb = cb.getGenericProducerBean();
		assertTrue(gb instanceof IProducerMethod);
		gbm = (IProducerMethod)gb;
		assertEquals("getSecondConfig", gbm.getMethod().getElementName());

		/*
		 * Case 3. (case of configuration provided by extending config class)
		 * Injection point: in class MyBeanInjections
		 *     @Inject @Qualifier2 MyBean first3
		 * Generic bean producer method: MyGenericBean.createMyFirstBean()
		 * Configuration bean: by MyExtendedConfiguration
		 */
		injection = getInjectionPointField(cdi, "src/org/jboss/generic/MyBeanInjections.java", "first3");

		bs = cdi.getBeans(false, injection);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
		m = (IProducerMethod)b;
		assertEquals("createMyFirstBean", m.getMethod().getElementName());
		assertTrue(b instanceof GenericBeanProducerMethod);
		gm = (GenericBeanProducerMethod)b;
		cb = (GenericClassBean)gm.getClassBean();
		gb = cb.getGenericProducerBean();
		assertTrue(gb instanceof IClassBean);
		IClassBean gbc = (IClassBean)gb;
		assertEquals("MyExtendedConfiguration", gbc.getBeanClass().getElementName());

	}
	
	//TODO - more tests

}
