package org.jboss.tools.jsf.ui.test;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.test.util.WorkbenchUtils;


public class CssClassNewWizardTest extends WizardTest {
	
	
	private static String CSS_FILE_PATH="WebContent/pages/main.css";  //$NON-NLS-1$
	
	public CssClassNewWizardTest(){
		super("org.jboss.tools.jst.web.ui.wizards.newfile.NewCSSClassWizard");
	}
	
	public void testCssClassNewWizardTestIsCreated() {
		needClose = false;
		wizardIsCreated();
	}
	
	public void testCssClassNewWizardValidation() {
		IWizard wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testCssClassNewWizardValidation2() {
		IWizard wizard = getWizardOnProject();
		
		boolean canFinish = wizard.canFinish();
		
		// Assert Finish button is enabled by default if wizard is called on Project
		assertFalse("Finish button is disabled at first wizard page.", canFinish);
		
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
	}
	
	public void testCssClassEditing() {

		ArrayList<IResource> list = new ArrayList<IResource>();
		IResource cssFile = project.findMember(CSS_FILE_PATH);
		assertNotNull(cssFile);
		list.add(cssFile);
		StructuredSelection selection = new StructuredSelection(list);
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);

		((IWorkbenchWizard) wizard).init(PlatformUI.getWorkbench(), selection);

		dialog = new WizardDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();

		boolean canFinish = wizard.canFinish();

		assertTrue("Finish button is disabled.", canFinish); //$NON-NLS-1$

		wizard.performFinish();

	}
	
	public void testCssClassWithEditor() {

		IResource cssFile = project.findMember(CSS_FILE_PATH);
		
		IEditorPart facesConfigEditor = WorkbenchUtils.openEditor(cssFile
				.getFullPath().toString());
		
		assertTrue(facesConfigEditor instanceof StructuredTextEditor);
		
//		ArrayList<IResource> list = new ArrayList<IResource>();

//		assertNotNull(cssFile);
//		list.add(cssFile);
//		StructuredSelection selection = new StructuredSelection(list);
		IWizard wizard = WorkbenchUtils.findWizardByDefId(id);
//
//		((IWorkbenchWizard) wizard).init(PlatformUI.getWorkbench(), selection);

		dialog = new WizardDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();

		wizard.performCancel();

	}

}
