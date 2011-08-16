package org.jboss.tools.cdi.core.test.extension;

import junit.extensions.TestSetup;
import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.test.DependentProjectTest;

public class ExtensionsInSrsAndUsedProjectTest  extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;

	public ExtensionsInSrsAndUsedProjectTest() {
		project1 = DependentProjectTest.getTestProject(project1, "/projects/CDITest1", "CDITest1");
		project2 = DependentProjectTest.getTestProject(project2, "/projects/CDITest2", "CDITest2");
		project3 = DependentProjectTest.getTestProject(project3, "/projects/CDITest3", "CDITest3");
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
