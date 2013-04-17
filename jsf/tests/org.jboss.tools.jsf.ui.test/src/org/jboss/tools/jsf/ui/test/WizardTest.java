/*******************************************************************************
 * Copyright (c) 2007-2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
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
import org.jboss.tools.test.util.WorkbenchUtils;

public abstract class WizardTest extends TestCase {
	protected String id;
	protected IProject project;
	protected boolean projectRemovalRequired = false;
	
	protected IWizard wizard;
	protected WizardDialog dialog;
	
	public WizardTest(String id){
		this.id = id;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		project = new TestWizardsProject().importProject();

	}
	
	protected void tearDown() throws Exception {
		close();
	}

	
	public void wizardIsCreated() {
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
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
		}
	}
	
	public IWizard getWizard(){
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
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
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
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
		
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
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
		
		wizard = WorkbenchUtils.findWizardByDefId(id);
		
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
	
	protected void validateFolderAndName() {
		validateFolderAndName("aaa");
	}
	
	protected void validateFolderAndName(String name){
		wizard = getWizardOnProject(name);
		
		
		boolean canFinish = wizard.canFinish();
		
		// Assert Finish button is enabled by default if wizard is called on Project
		assertTrue("Finish button is disabled at first wizard page.", canFinish);
		
		close();
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field is empty
		// 		All other fields are correct
		
		wizard = getWizard("",name);
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when folder field is empty.", canFinish);
		
		close();
		
		
		// Assert Finish button is disabled and error is present if 
		// 		Folder field points to folder that doesn't exist
		// 		All other fields are correct
		
		wizard = getWizard("anyFolder",name);
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when folders field points to folder that does not exist", canFinish);
		
		close();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field is empty
		
		wizard = getWizardOnProject("");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field is empty.", canFinish);
		
		close();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains forbidden characters
		
		wizard = getWizardOnProject("?-/");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field contains forbiden characters.", canFinish);
		
		close();
		
		// Assert Finish button is disabled and error is present if
		//		Folder field is correct
		//		Name field contains file name that already exists
		
		wizard = getWizardOnProject("abc");
		canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled when name field contains file name that already exists.", canFinish);
		
		close();
	}
	
	private void close(){
		if(wizard != null){
			wizard.performCancel();
			wizard = null;
		}
		if(dialog != null){
			dialog.close();
			dialog = null;
		}
	}
}
