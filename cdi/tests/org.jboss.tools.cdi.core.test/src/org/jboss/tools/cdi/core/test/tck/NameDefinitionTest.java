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

/**
 * @author Alexey Kazakov
 */
public class NameDefinitionTest extends TCKTest {

	/**
	 * Section 2 - Concepts
	 * e) A bean comprises of an optional bean EL name.
	 *
	 * @throws JavaModelException 
	 */
	public void testNonDefaultNamed() throws JavaModelException {
//		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/name/Moose.java");
//		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.Moose");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.name.Moose type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.name.Moose bean.", "aMoose", bean.getName());
		assertLocationEquals(bean.getNameLocation(), 918, 16);
	}

	/**
	 * section 2.5.2 a)
	 * section 3.1.5 a)
	 * section 2.5.1 d)
	 *
	 * @throws JavaModelException 
	 */
	public void testDefaultNamed() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.Haddock");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.name.Haddock type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNotNull("org.jboss.jsr299.tck.tests.definition.name.Haddock bean should have an EL name.", bean.getName());
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.name.Haddock bean.", "haddock", bean.getName());
	}

	/**
	 * section 2.7 a)
	 * section 2.7.1.3 aaa)
	 *
	 * @throws JavaModelException 
	 */
	public void testStereotypeDefaultsName() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.RedSnapper");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.name.RedSnapper type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.name.RedSnapper bean.", "redSnapper", bean.getName());
	}

	/**
	 * Section 2 - Concepts
	 * e) A bean comprises of an optional bean EL name (continue).
	 *
	 * @throws JavaModelException 
	 */
	public void testNotNamedInJava() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.SeaBass");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.name.SeaBass type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNull("org.jboss.jsr299.tck.tests.definition.name.SeaBass bean should not have any EL name.", bean.getName());
	}

	/**
	 * section 2.5.3 a)
	 *
	 * @throws JavaModelException 
	 */
	public void testNotNamedInStereotype() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.Minnow");
		assertEquals("There should be the only bean with org.jboss.jsr299.tck.tests.definition.name.Minnow type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNull("org.jboss.jsr299.tck.tests.definition.name.Minnow bean should not have any EL name.", bean.getName());
	}
}