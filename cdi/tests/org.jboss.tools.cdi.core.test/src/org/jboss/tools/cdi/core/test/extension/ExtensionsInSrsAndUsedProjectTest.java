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