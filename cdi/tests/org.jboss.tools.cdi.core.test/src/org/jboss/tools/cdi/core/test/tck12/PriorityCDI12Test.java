/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck12;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * @author Viacheslav Kabanovich
 */
public class PriorityCDI12Test extends TCK12Test {

	public PriorityCDI12Test() {}

	/**
	 * Priority(300)
	 */
	public void testSimplePriorityValue() throws CoreException {
		Collection<IBean> bs = getBeans("org.jboss.jsr299.tck.tests.jbt.resolution.priority.WoodenTable");
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		IClassBean cb = (IClassBean)b;
		assertTrue(cb.isSelectedAlternative());
		assertTrue(cb.isEnabled());
		Integer priority = cb.getPriority();
		assertNotNull(priority);
		assertEquals(300, priority.intValue());
	}

	/**
	 * Priority(APPLICATION)
	 */
	public void testReferencedConstantPriorityValue() throws CoreException {
		Collection<IBean> bs = getBeans("org.jboss.jsr299.tck.tests.jbt.resolution.priority.StrawTable");
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		IClassBean cb = (IClassBean)b;
		assertTrue(cb.isSelectedAlternative());
		assertTrue(cb.isEnabled());
		Integer priority = cb.getPriority();
		assertNotNull(priority);
		assertEquals(2000, priority.intValue());
	}

	/**
	 * Priority(APPLICATION + 100)
	 */
	public void testExpressionPriorityValue() throws CoreException {
		Collection<IBean> bs = getBeans("org.jboss.jsr299.tck.tests.jbt.resolution.priority.IronTable");
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		IClassBean cb = (IClassBean)b;
		assertTrue(cb.isSelectedAlternative());
		assertTrue(cb.isEnabled());
		Integer priority = cb.getPriority();
		assertNotNull(priority);
		assertEquals(2100, priority.intValue());
	}

	static String OFFICE_PATH = "JavaSource/org/jboss/jsr299/tck/tests/jbt/resolution/priority/Office.java";

	/**
	 * Eligible beans with priorities 2000, 2001, 2100.
	 * Resolved to bean with maximum priority.
	 */
	public void testResolutionWithSingleMaxPriorityValue() throws CoreException {
		IInjectionPointField f = getInjectionPointField(OFFICE_PATH, "marbleTable");
		assertNotNull(f);
		Collection<IBean> beans = cdiProject.getBeans(true, f);
		assertEquals(1, beans.size());
	}

	/**
	 * Eligible beans with priorities 2001, 2100 and qualifier.
	 * Resolved to bean with maximum priority.
	 */
	public void testResolutionWithSingleMaxPriorityValueAndQualifier() throws CoreException {
		IInjectionPointField f = getInjectionPointField(OFFICE_PATH, "marbleTableA");
		assertNotNull(f);
		Collection<IBean> beans = cdiProject.getBeans(true, f);
		assertEquals(1, beans.size());
	}

	/**
	 * Assignable non-eligible (unselected) bean without priority.
	 * Eligible bean with priority.
	 * Resolved to bean with priority.
	 */
	public void testResolutionWithAssignableBeanWithoutPriority() throws CoreException {
		IInjectionPointField f = getInjectionPointField(OFFICE_PATH, "marbleTableY");
		assertNotNull(f);
		Collection<IBean> beans = cdiProject.getBeans(true, f);
		assertEquals(1, beans.size());
	}

	/**
	 * Eligible beans with priorities 2001, 2100, 2100.
	 * Ambiguous dependency.
	 */
	public void testResolutionWithMultipleMaxPriorityValue() throws CoreException {
		IInjectionPointField f = getInjectionPointField(OFFICE_PATH, "marbleTableB");
		assertNotNull(f);
		Collection<IBean> beans = cdiProject.getBeans(true, f);
		assertEquals(2, beans.size());
	}

}