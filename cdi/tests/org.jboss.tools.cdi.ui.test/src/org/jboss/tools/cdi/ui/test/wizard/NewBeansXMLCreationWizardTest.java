/******************************************************************************* 
 * Copyright (c) 2017 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.wizard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;

public class NewBeansXMLCreationWizardTest extends TestCase{
	
	static NewBeansXMLCreationWizard wizard;
	static WizardDialog dialog;
	static IProject project;
	static IProject projectWithoutCDI;
	
	protected void setUp() throws Exception {
		project = ResourcesUtils.importProject("org.jboss.tools.cdi.core.test", "/projects/CDITestCDISupportNotEnabled");
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		
		projectWithoutCDI = ResourcesUtils.importProject("org.jboss.tools.cdi.core.test", "/projects/FacetedProject");
		projectWithoutCDI.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}
	
	protected void tearDown() throws CoreException {
		if(project != null) {
			project.delete(true, true, new NullProgressMonitor());
		}
		if(projectWithoutCDI != null) {
			projectWithoutCDI.delete(true, true, new NullProgressMonitor());
		}
		if(dialog != null) {
			dialog.close();
		}
	}

	private static void cleanDefferedEvents() {
		while (Display.getCurrent().readAndDispatch());
	}
	
	public void testCreateBeansXMLAndEnableCDI() throws CoreException {
		assertNull(project.getNature(CDICoreNature.NATURE_ID));
		openWizard(project);
		wizard.setVersion(CDIVersion.CDI_1_0);
		wizard.performFinish();
		assertNotNull(project.getNature(CDICoreNature.NATURE_ID));
	}
	
	//CDI support should not be enabled
	public void testCreateBeansXMLNoCDIDependency() throws CoreException {
		assertNull(projectWithoutCDI.getNature(CDICoreNature.NATURE_ID));
		openWizard(projectWithoutCDI);
		wizard.setVersion(CDIVersion.CDI_1_0);
		wizard.performFinish();
		assertNull(projectWithoutCDI.getNature(CDICoreNature.NATURE_ID));
	}
	
	private void openWizard(IProject project) {
		wizard= (NewBeansXMLCreationWizard)WorkbenchUtils.findWizardByDefId(NewBeansXMLCreationWizard.WIZARD_ID);
		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(project));
		
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		cleanDefferedEvents();
	}

}
