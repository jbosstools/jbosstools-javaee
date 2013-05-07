/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.ui.wizard.NewBeansXMLCreationWizard;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.refactoring.TestableResolutionWithDialog;

public class CreateBeansXMLMarkerResolution implements IMarkerResolution2, TestableResolutionWithDialog{
	private IProject project;
	
	public CreateBeansXMLMarkerResolution(IProject project){
		this.project = project;
	}
	
	@Override
	public String getLabel() {
		return CDIUIMessages.CREATE_FILE_BEASN_XML_TITLE;
	}

	@Override
	public void run(IMarker marker) {
		run(false);
	}
	
	private void run(boolean test){
		NewBeansXMLCreationWizard wizard = new NewBeansXMLCreationWizard();
		IJavaProject jp = EclipseUtil.getJavaProject(project);
		wizard.init(CDIUIPlugin.getDefault().getWorkbench(), new StructuredSelection(jp));
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		if(test){
			dialog.setBlockOnOpen(false);
		}
		
		dialog.open();
		
		if(test){
			wizard.performFinish();
			dialog.close();
		}
	}

	@Override
	public String getDescription() {
		return getLabel();
	}

	@Override
	public Image getImage() {
		return CDIImages.getImage(CDIImages.BEANS_XML_IMAGE);
	}

	@Override
	public void runForTest(IMarker marker) {
		run(true);
	}

}
