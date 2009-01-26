package org.jboss.tools.jsf.ui.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

public abstract class WizardTest extends TestCase {
	protected String id;
	private IProject project;
	
	public WizardTest(String id){
		this.id = id;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
       
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestWizards");
		if(project == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.jsf.ui.test",
					"projects/TestWizards",
					"TestWizards");
			project = setup.importProject();
		}
		this.project = project.getProject();
		System.out.println("Project - "+project);
		JobUtils.waitForIdle();
	}
	
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			JobUtils.waitForIdle();
			if(project != null){
				project.close(new NullProgressMonitor());
				project.delete(true, new NullProgressMonitor());
				project = null;
				JobUtils.waitForIdle();
			}
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
		
	}

	public void wizardIsCreated() {
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			IWizardPage startPage = wizard.getStartingPage();
			assertNotNull(startPage);
		}catch(Exception ex){
			fail(ex.getMessage());
		} finally {
			dialog.close();
		}
		
	}
}
