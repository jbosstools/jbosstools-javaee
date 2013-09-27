/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.extension;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.test.DependentProjectTest;

import junit.framework.TestCase;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SystemExtensionTest extends TestCase {
	IProject project1 = null;
	IProject project2 = null;

	public SystemExtensionTest() {}

	@Override
	protected void setUp() throws Exception {
		project1 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest1");
		project2 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest2");
	}

	public void testSystemExtension() throws Exception {
		ICDIProject cdi1 = CDICorePlugin.getCDIProject(project1, true);
		ICDIExtension e = cdi1.getNature().getExtensionManager().getExtensionByRuntime("cdi.test.extension.MyExtension");
		assertNotNull(e);
		assertTrue(e instanceof CDISystemExtensionImpl);

		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		e = cdi2.getNature().getExtensionManager().getExtensionByRuntime("cdi.test.extension.MyExtension");
		assertNotNull(e);
	}

	public void testInjection() throws Exception {
		ICDIProject cdi1 = CDICorePlugin.getCDIProject(project1, true);

		IType t = cdi1.getNature().getType("cdi.test.extension.MyBeanInterface");
		IClassBean c = cdi1.getBeanClass(t);
		assertNotNull(c);

		IInjectionPointField f = DependentProjectTest.getInjectionPointField(cdi1, "src/cdi/test/extension/MyBeanClient.java", "f");
		assertNotNull(f);
		Collection<IBean> bs = cdi1.getBeans(true, f);
		assertEquals(1, bs.size());
		assertSame(c, bs.iterator().next());
	}

	public void testInjectionInDependentProject() throws Exception {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);

		IType t = cdi2.getNature().getType("cdi.test.extension.MyBeanInterface");
		IClassBean c = cdi2.getBeanClass(t);
		assertNotNull(c);

		IInjectionPointField f = DependentProjectTest.getInjectionPointField(cdi2, "src/cdi/test/extension/MyBeanClient2.java", "f");
		assertNotNull(f);
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertSame(c, bs.iterator().next());
	}

}
