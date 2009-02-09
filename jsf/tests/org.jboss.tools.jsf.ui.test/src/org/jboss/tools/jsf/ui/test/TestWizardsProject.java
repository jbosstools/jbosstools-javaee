package org.jboss.tools.jsf.ui.test;

import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class TestWizardsProject extends ProjectImportTestSetup {
	
	public static final String NAME = "TestWizards";
	public static final String BUNDLE_NAME = "org.jboss.tools.jsf.ui.test";
	public static final String PATH = "/projects/TestWizards";
	
	public TestWizardsProject(Test test) {
		super(test, BUNDLE_NAME, PATH, NAME);
	}
	
	public TestWizardsProject() {
		super(null, BUNDLE_NAME, PATH, NAME);
	}

	@Override
	public IProject importProject() throws Exception {
		IProject testWizards = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(NAME);

		if(testWizards==null) {
			testWizards =  super.importProject();
		}
		return testWizards;
	}
}
