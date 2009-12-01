 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.ui.wizard;

import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;

/**
 * @author Alexey Kazakov
 */
public abstract class SeamWizardCustomizationPage extends SeamBaseWizardPage implements IAdaptable, PropertyChangeListener {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamWizardCustomizationPage(String pageName, String title,
			ImageDescriptor titleImage, IStructuredSelection initialSelection) {
		super(pageName, title, titleImage, initialSelection);
	}

	/**
	 * @param pageName
	 */
	public SeamWizardCustomizationPage(String pageName, IStructuredSelection initSelection) {
		super(pageName, initSelection);
	}

	protected void createEditors() {
		String selectedProject = SeamWizardUtils.getRootSeamProjectName(initialSelection);
		String packageName = getDefaultPackageName(selectedProject);
		addEditor(SeamWizardFactory.createSeamJavaPackageSelectionFieldEditor(packageName));
		setSeamProjectNameData(selectedProject);
		String projectName = SeamWizardUtils.getRootSeamProjectName(initialSelection);
		addEditor(SeamWizardFactory.createSeamProjectSelectionFieldEditor(projectName));
	}

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));

		if (!"".equals(editorRegistry.get(ISeamParameter.SEAM_PROJECT_NAME).getValue())){ //$NON-NLS-1$
			Map<String, IStatus> errors = SeamValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
					getEditor(ISeamParameter.SEAM_PROJECT_NAME).getValue(), null);
		}

		String selectedProject = getEditor(ISeamParameter.SEAM_PROJECT_NAME).getValueAsString();

		if(selectedProject!=null && !"".equals(selectedProject) && isValidProjectSelected()) {
			isValidRuntimeConfigured(getSelectedProject());
		}
		setPageComplete(false);
	}
}