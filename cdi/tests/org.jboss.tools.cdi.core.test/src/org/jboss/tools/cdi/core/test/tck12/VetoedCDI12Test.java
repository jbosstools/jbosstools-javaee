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
import org.jboss.tools.cdi.core.IInjectionPointField;

/**
 * @author Viacheslav Kabanovich
 */
public class VetoedCDI12Test extends TCK12Test {

	public VetoedCDI12Test() {}

	public void testVetoedClass() throws CoreException {
		assertNumberOfInjectedBeans("pond", 0);
	}

	public void testNonVetoedNestedClassInVetoedClass() throws CoreException {
		assertNumberOfInjectedBeans("creek", 1);
	}

	public void testVetoedNestedClassInVetoedClass() throws CoreException {
		assertNumberOfInjectedBeans("spring", 0);
	}

	public void testClassInVetoedPackage() throws CoreException {
		assertNumberOfInjectedBeans("black", 0);
	}

	public void testClassInSubpackageOfVetoedPackage() throws CoreException {
		assertNumberOfInjectedBeans("caspian", 1);
	}

	private void assertNumberOfInjectedBeans(String fieldName, int numberOfBeans) {
		IInjectionPointField injection = getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/jbt/vetoed/Injections.java", fieldName);
		Collection<IBean> beans = cdiProject.getBeans(true, injection);
		assertTrue(injection.getType().getType().exists());
		assertEquals("Wrong number of the beans", numberOfBeans, beans.size());
	}

}
