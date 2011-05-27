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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducer;
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

	public void testGenericBeanInjectionIntoGenericPoint() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		
		/*
		 * Injection point: in class MyGenericBean2
		 *     @Inject @Generic MyBean c;
		 * There are 5 configurations, hence there are 5 beans MyGenericBean2, 
		 * each has that injection point; 
		 * in all cases bean is produced by MyGenericBean.createMyFirstBean()
		 */
		Set<IInjectionPointField> injections = getGenericInjectionPointField(cdi, "src/org/jboss/generic/MyGenericBean2.java", "c");
		assertEquals(5, injections.size());
		for (IInjectionPointField injection: injections) {
			Set<IBean> bs = cdi.getBeans(false, injection);
			assertEquals(1, bs.size());
			IBean b = bs.iterator().next();
			assertTrue(b instanceof IProducerMethod);
			IProducerMethod m = (IProducerMethod)b;
			assertEquals("createMyFirstBean", m.getMethod().getElementName());
		}
	

	}

	public void testGenericTypeInjection() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		
		/*
		 * Injection point: in class MyGenericBean2
		 *     @Inject MyGenericType type;
		 * There are 5 configurations, hence there are 5 beans MyGenericBean2, 
		 * each has that injection point; 
		 * in all cases we insert a dummy bean of type org.jboss.generic.MyGenericType
		 */
		Set<IInjectionPointField> injections = getGenericInjectionPointField(cdi, "src/org/jboss/generic/MyGenericBean2.java", "type");
		assertEquals(5, injections.size());
		for (IInjectionPointField injection: injections) {
			Set<IBean> bs = cdi.getBeans(false, injection);
			assertEquals(1, bs.size());
			IBean b = bs.iterator().next();
			assertTrue(b instanceof IClassBean);
			IType t = ((IClassBean)b).getBeanClass();
			assertEquals("org.jboss.generic.MyGenericType", t.getFullyQualifiedName());
		}
		
	}
	//TODO - more tests

	protected Set<IInjectionPointField> getGenericInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		Set<IInjectionPointField> result = new HashSet<IInjectionPointField>();
		IFile file = cdi.getNature().getProject().getFile(beanClassFilePath);
		Set<IBean> beans = cdi.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}

		for (IBean b: beans) {
			Set<IInjectionPoint> injections = b.getInjectionPoints();
			for (IInjectionPoint injectionPoint : injections) {
				if(injectionPoint instanceof IInjectionPointField) {
					IInjectionPointField field = (IInjectionPointField)injectionPoint;
					if(fieldName.equals(field.getField().getElementName())) {
						result.add(field);
					}
				}
			}
		}
		return result;
	}

}
