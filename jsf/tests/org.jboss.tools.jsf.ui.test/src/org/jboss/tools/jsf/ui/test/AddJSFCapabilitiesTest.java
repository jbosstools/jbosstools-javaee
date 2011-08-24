package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.StructuredSelection;
import org.jboss.tools.jsf.ui.action.AddJSFNatureActionDelegate;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class AddJSFCapabilitiesTest extends TestCase {
	IProject project = null;

	public AddJSFCapabilitiesTest() {
		super("Add JSF Capabilities Test");
	}

	public AddJSFCapabilitiesTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		JobUtils.waitForIdle(3000);
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("test_add_jsf_capabilities");
		if(project == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.jsf.ui.test",
					"projects/test_add_jsf_capabilities",
					"test_add_jsf_capabilities");
			project = setup.importProject();
		}
		this.project = project.getProject();

		JobUtils.waitForIdle();
	}

	public void testAddJSFCapabilities() {
		IFile f = project.getFile(new Path(".settings/org.eclipse.wst.common.project.facet.core.xml"));
		assertFalse(f.exists());

		try {
			assertFalse(project.hasNature("org.jboss.tools.jsf.jsfnature"));
		} catch (CoreException e) {
			fail(e.getMessage());
		}

		AddJSFNatureActionDelegate action = new AddJSFNatureActionDelegate(false);
		action.selectionChanged(null, new StructuredSelection(project));
		action.run(null);
		
		try {
			assertTrue(project.hasNature("org.jboss.tools.jsf.jsfnature"));
		} catch (CoreException e) {
			fail(e.getMessage());
		}
		
		assertTrue(f.exists());
	}
}
