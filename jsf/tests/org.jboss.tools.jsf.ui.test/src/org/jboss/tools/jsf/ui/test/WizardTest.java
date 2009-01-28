package org.jboss.tools.jsf.ui.test;

import java.util.ArrayList;

import javax.swing.text.View;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

public abstract class WizardTest extends TestCase {
	protected String id;
	protected IProject project;
	
	protected WizardDialog dialog;
	
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
		JobUtils.waitForIdle();
	}
	
	@Override
	protected void tearDown() throws Exception {
		if(dialog != null)
			dialog.close();
		
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
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		//dialog.
		dialog.open();

//		System.out.println("\nWizard ID - "+id);
//		System.out.println("Wizard Class - "+wizard.getClass());
//		
//		System.out.println("Pages - "+wizard.getPages().length);
//		
//		for(int i = 0; i < wizard.getPages().length;i++){
//			System.out.println("Wizard Page Class - "+wizard.getPages()[i].getClass());
//		}
		
		try {
			IWizardPage startPage = wizard.getStartingPage();
			assertNotNull(startPage);
		}catch(Exception ex){
			fail(ex.getMessage());
		} finally {
			dialog.close();
		}
		
	}
	
	public IWizard getWizardWithoutSelection(){
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		return wizard;
	}
	
	public IWizard getWizardOnProject(){
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		list.add(project);
		
		StructuredSelection selection = new StructuredSelection(list);
		
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		return wizard;
	}
}
