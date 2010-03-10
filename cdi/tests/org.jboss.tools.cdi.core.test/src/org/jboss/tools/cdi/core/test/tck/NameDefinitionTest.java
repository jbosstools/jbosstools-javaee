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
		assertEquals("org.jboss.jsr299.tck.tests.definition.name.Moose should have the only bean.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong EL name of org.jboss.jsr299.tck.tests.definition.name.Moose bean.", "aMoose", bean.getName());
		assertLocationEquals(bean.getNameLocation(), 918, 16);
	}

	/**
	 * Section 2 - Concepts
	 * e) A bean comprises of an optional bean EL name (continue).
	 *
	 * @throws JavaModelException 
	 */
	public void testNotNamedInJava() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.name.SeaBass");
		assertEquals("org.jboss.jsr299.tck.tests.definition.name.SeaBass should have the only bean.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertNull("org.jboss.jsr299.tck.tests.definition.name.SeaBass bean should not have any EL name.", bean.getName());
	}
}