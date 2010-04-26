package org.jboss.tools.jsf.ui.test;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileContextEx;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileWizardEx;
import org.jboss.tools.jst.jsp.outline.cssdialog.CSSClassDialog;
import org.jboss.tools.test.util.WorkbenchUtils;

public abstract class WizardTest extends TestCase {
	protected String id;
	protected IProject project;
	protected boolean projectRemovalRequired = false;
	protected WizardDialog dialog;
	
	protected boolean needClose = true;
	
	public WizardTest(String id){
		this.id = id;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		project = new TestWizardsProject().importProject();

	}
	
	public void wizardIsCreated() {
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		StructuredSelection selection = new StructuredSelection(list);
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		
		dialog.open();

		try {
			IWizardPage startPage = wizard.getStartingPage();
			assertNotNull(startPage);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			fail(ex.getMessage());
		}finally{
			if(needClose)
				dialog.close();
		}
	}
	
	public IWizard getWizard(){
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		StructuredSelection selection = new StructuredSelection(list);
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		return wizard;
	}
	
	public IWizard getWizard(String folder, String name){
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		NewFileWizardEx wiz = (NewFileWizardEx)wizard;
		
		NewFileContextEx context = wiz.getFileContext();
		
		SpecialWizardSupport support = context.getSupport();
		
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		StructuredSelection selection = new StructuredSelection(list);
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		support.setAttributeValue(0, "folder", folder);
		support.setAttributeValue(0, "name", name);
		
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
	
	public IWizard getWizardOnProject(String name){
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		list.add(project);
		
		StructuredSelection selection = new StructuredSelection(list);
		
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
		
		NewFileWizardEx wiz = (NewFileWizardEx)wizard;
		
		NewFileContextEx context = wiz.getFileContext();
		
		SpecialWizardSupport support = context.getSupport();
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		support.setAttributeValue(0, "name", name);
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		return wizard;
	}
	
	protected void validateFolderAndName(){
		IWizard wizard = getWizardOnProject("aaa");
		
		
		boolean canFinish = wizard.canFinish();
		
		// Assert Finish button is enabled by default if wizard is called on Project
		assertTrue("Finish button is disabled at first wizard page.", canFinish);
		
		wizard.performCancel();
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field is empty
		// 		All other fields are correct
		
		wizard = getWizard("","aaa");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when folder field is empty.", canFinish);
		
		wizard.performCancel();
		
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field points to folder that doesn't exist
		// 		All other fields are correct
		
		wizard = getWizard("anyFolder","aaa");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when folders field points to folder that does not exist", canFinish);
		
		wizard.performCancel();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field is empty
		
		wizard = getWizardOnProject("");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field is empty.", canFinish);
		
		wizard.performCancel();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains forbidden characters
		
		wizard = getWizardOnProject("?-/");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field contains forbiden characters.", canFinish);
		
		wizard.performCancel();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains file name that already exists
		
		wizard = getWizardOnProject("abc");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field contains file name that already exists.", canFinish);
		
		wizard.performCancel();
	}
}
