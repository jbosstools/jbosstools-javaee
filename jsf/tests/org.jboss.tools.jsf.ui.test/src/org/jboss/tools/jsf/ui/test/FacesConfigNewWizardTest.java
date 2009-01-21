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

public class FacesConfigNewWizardTest extends TestCase {
	private IProject project;
	
//	@Override
//	protected void setUp() throws Exception {
//		super.setUp();
//       
//		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestWizards");
//		if(project == null) {
//			ProjectImportTestSetup setup = new ProjectImportTestSetup(
//					this,
//					"org.jboss.tools.jsf.ui.test",
//					"projects/TestWizards",
//					"TestWizards");
//			project = setup.importProject();
//		}
//		this.project = project.getProject();
//		
//		JobUtils.waitForIdle();
//	}
	
//	@Override
//	protected void tearDown() throws Exception {
//		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
//		try {
//			JobUtils.waitForIdle();
//			if(project != null){
//				project.close(new NullProgressMonitor());
//				project.delete(true, new NullProgressMonitor());
//				project = null;
//				JobUtils.waitForIdle();
//			}
//		} finally {
//			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
//		}
//		
//	}
	
	public void testNewFacesConfigNewWizardIsCreated() {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId("org.jboss.tools.jsf.ui.wizard.newfile.NewFacesConfigFileWizard");
		
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			IWizardPage startPage = aWizard.getStartingPage();
			assertNotNull(startPage);
		}catch(Exception ex){
			fail(ex.getMessage());
		} finally {
			dialog.close();
		}
		
	}
	
	public void testFacesConfigNewWizardValidation() {
		// Assert Finish button is enabled by default if wizard is called on Project
		// Assert Finish button is disabled and error is present if 
		// 		Folder field is empty
		// 		All other fields are correct
		// Assert Finish button is disabled and error is present if 
		// 		Folder field points to folder that doesn't exist
		// 		All other fields are correct
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field is empty
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains forbidden characters
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains file name that already exists
		fail("Not implemented yet");
	}
	
	public void testFacesConfigNewWizardResults() {
		// Assert file with name from Name field created in folder with name form Folder field
		// Assert that new file was not registered in web.xml if 'Register in web.xml' is not set
		// Assert that new file was registered in web.xml if 'Register in web.xml is set'
		fail("Not implemented yet");
	}
}
