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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;

public class ExtensionsInSrsAndUsedProjectTest  extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;

	@Override
	protected void setUp() throws Exception {
		project1 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest1");
		project2 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest2");
		project3 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest3");
	}

	public void testRuntimes() {
		CDICoreNature cdi2 = CDICorePlugin.getCDI(project2, true);
		//Extension declared in src of project2
		assertTrue(cdi2.getExtensionManager().isCDIExtensionAvailable("c.d.e"));
		//Extension declared in src of project1
		assertTrue(cdi2.getExtensionManager().isCDIExtensionAvailable("a.b.c"));

		CDICoreNature cdi3 = CDICorePlugin.getCDI(project3, true);
		//Extension declared in src of project2
		assertTrue(cdi3.getExtensionManager().isCDIExtensionAvailable("c.d.e"));
		//Extension declared in src of project1
		assertTrue(cdi3.getExtensionManager().isCDIExtensionAvailable("a.b.c"));
	}
}