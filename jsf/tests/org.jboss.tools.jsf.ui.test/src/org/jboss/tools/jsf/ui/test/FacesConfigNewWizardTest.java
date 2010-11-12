package org.jboss.tools.jsf.ui.test;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileContextEx;
import org.jboss.tools.common.model.ui.wizard.newfile.NewFileWizardEx;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;


public class FacesConfigNewWizardTest extends WizardTest {
	public FacesConfigNewWizardTest(){
		super("org.jboss.tools.jsf.ui.wizard.newfile.NewFacesConfigFileWizard");
	}
	
	public void testNewFacesConfigNewWizardIsCreated() {
		wizardIsCreated();
	}
	
	public void _testFacesConfigNewWizardValidation() {
		wizard = getWizard();
		
		boolean canFinish = wizard.canFinish();
		
		assertFalse("Finish button is enabled at first wizard page.", canFinish);
	}
	
	public void testFacesConfigNewWizardValidation2() {
		validateFolderAndName();
	}
	
	public void testFacesConfigNewWizardResults() {
		ArrayList<IProject> list = new ArrayList<IProject>();
		
		StructuredSelection selection = new StructuredSelection(list);
		
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
		NewFileWizardEx wiz = (NewFileWizardEx)wizard;
		
		NewFileContextEx context = wiz.getFileContext();
		
		SpecialWizardSupport support = context.getSupport();
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		support.setAttributeValue(0, "name", "faces-config11");
		support.setAttributeValue(0, "folder", "/TestWizards/WebContent/WEB-INF");
		support.setAttributeValue(0, "register in web.xml", "no");
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		
		// Assert file with name from Name field created in folder with name form Folder field
		
		boolean canFinish = wizard.canFinish();
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("/WebContent/WEB-INF/faces-config11.xml");
		
		assertNotNull(res);
		
		// Assert that new file was not registered in web.xml if 'Register in web.xml' is not set
		
		IResource webXml = project.findMember("/WebContent/WEB-INF/web.xml");
		
		assertNotNull(webXml);
		
		String webXmlContent = FileUtil.readFile(new File(webXml.getLocation().toString()));
		
		int p = webXmlContent.indexOf("faces-config11.xml");
		
		assertTrue("File faces-config11.xml is registered in web.xml", p < 0);
	}
	
	public void testFacesConfigNewWizardResults2() {
		// Assert that new file was registered in web.xml if 'Register in web.xml is set'
		
		ArrayList<IProject> list = new ArrayList<IProject>();
		list.add((IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestWizards"));
		
		StructuredSelection selection = new StructuredSelection(list);
		
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
		NewFileWizardEx wiz = (NewFileWizardEx)wizard;
		
		NewFileContextEx context = wiz.getFileContext();
		
		SpecialWizardSupport support = context.getSupport();
		
		((IWorkbenchWizard)wizard).init(PlatformUI.getWorkbench(), selection);
		
		support.setAttributeValue(0, "name", "faces-config22");
		support.setAttributeValue(0, "folder", "/TestWizards/WebContent/WEB-INF");
		support.setAttributeValue(0, "register in web.xml", "yes");
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		JobUtils.delay(6000);
		boolean canFinish = wizard.canFinish();
		
		System.out.println("Message - "+dialog.getCurrentPage().getErrorMessage());
		
		assertTrue("Finish button is disabled.", canFinish);
		
		wizard.performFinish();
		
		JobUtils.waitForIdle();
		
		IResource res = project.findMember("/WebContent/WEB-INF/faces-config22.xml");
		
		assertNotNull(res);
		
		IResource webXml = project.findMember("/WebContent/WEB-INF/web.xml");
		
		assertNotNull(webXml);
		
		String webXmlContent = FileUtil.readFile(new File(webXml.getLocation().toString()));
		
		System.out.println("WebXML - "+webXmlContent);
		
		int p = webXmlContent.indexOf("faces-config22.xml");
		
		assertTrue("File faces-config22.xml is not registered in web.xml", p >= 0);
	}
}
