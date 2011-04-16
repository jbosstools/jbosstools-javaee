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
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;

/**
 *   
 * @author Viacheslav Kabanovich
 *
 */
public class DefaultBeanTest extends SeamSolderTest {

	public DefaultBeanTest() {}

	public void testDefaultBeanTest() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
	
		// 1. For injection point with qualifier @Small, the only eligible bean is
		//    default class bean with qualifier @Small
		IInjectionPointField injection = getInjectionPointField(cdi, "src/org/jboss/defaultbean/Town.java", "small");

		Set<IBean> bs = cdi.getBeans(false, injection);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);		
		IType t = b.getBeanClass();
		assertNotNull(t);
		assertEquals("org.jboss.defaultbean.MyDefaultHome", t.getFullyQualifiedName());
		bs = cdi.getBeans(true, injection);
		assertEquals(1, bs.size());
		assertTrue(bs.contains(b));

		// 2. For injection point with qualifier @Big, the only eligible bean is 
		//    default producer bean method with qualifier @Big
		injection = getInjectionPointField(cdi, "src/org/jboss/defaultbean/Town.java", "big");

		bs = cdi.getBeans(false, injection);
		assertEquals(1, bs.size());
		b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);		
		IMethod m = ((IProducerMethod)b).getMethod();
		assertNotNull(m);
		assertEquals("getDefault", m.getElementName());
		bs = cdi.getBeans(true, injection);
		assertEquals(1, bs.size());
		assertTrue(bs.contains(b));
		
		// 3. For injection point with qualifier @Huge, two beans are eligible
		//  a) Default producer method bean with qualifier @Huge, 
		//  b) one more bean with qualifier @Huge		
		// default bean is filtered out at resolving beans.
		injection = getInjectionPointField(cdi, "src/org/jboss/defaultbean/Town.java", "huge");

		bs = cdi.getBeans(false, injection);
		assertEquals(2, bs.size());
		Iterator<IBean> it = bs.iterator();
		b = null;
		while(!(b instanceof IProducerMethod) && it.hasNext()) {
			b = it.next();
		};
		assertTrue(b instanceof IProducerMethod);		
		m = ((IProducerMethod)b).getMethod();
		assertNotNull(m);
		assertEquals("getExclusive", m.getElementName());
		bs = cdi.getBeans(true, injection);
		assertEquals(1, bs.size());
		assertTrue(!bs.contains(b));

		// 4. For injection point with qualifier @Cozy, two beans are eligible
		//	a) Default producer field bean has qualifier @Cozy, producer inherits @DefaultBean from parent class;
		//  b) One more bean with qualifier @Cozy.
		// default bean is filtered out at resolving beans.
		injection = getInjectionPointField(cdi, "src/org/jboss/defaultbean/Town.java", "cozy");

		bs = cdi.getBeans(false, injection);
		assertEquals(2, bs.size());
		it = bs.iterator();
		b = null;
		while(!(b instanceof IProducerField) && it.hasNext()) {
			b = it.next();
		};
		assertTrue(b instanceof IProducerField);		
		IField f = ((IProducerField)b).getField();
		assertNotNull(f);
		assertEquals("cozy", f.getElementName());
		bs = cdi.getBeans(true, injection);
		assertEquals(1, bs.size());
		assertTrue(!bs.contains(b));

		// 5. For injection point without qualifier (= with @Default), two beans are eligible
		//	a) Default producer field bean without qualifier, producer inherits @DefaultBean from parent class; 
		//  b) One more bean without qualifier.
		// default bean is filtered out at resolving beans.
		injection = getInjectionPointField(cdi, "src/org/jboss/defaultbean/Town.java", "ruins");

		bs = cdi.getBeans(false, injection);
		assertEquals(2, bs.size());
		it = bs.iterator();
		b = null;
		while(!(b instanceof IProducerField) && it.hasNext()) {
			b = it.next();
		}
		assertTrue(b instanceof IProducerField);		
		f = ((IProducerField)b).getField();
		assertNotNull(f);
		assertEquals("old", f.getElementName());
		bs = cdi.getBeans(true, injection);
		assertEquals(1, bs.size());
		assertTrue(!bs.contains(b));

	}

}
