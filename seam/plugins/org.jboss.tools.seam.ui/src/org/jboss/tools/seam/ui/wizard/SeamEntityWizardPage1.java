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

import java.beans.PropertyChangeEvent;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;

/**
 * @author eskimo
 *
 */
public class SeamEntityWizardPage1 extends SeamBaseWizardPage {

	/**
	 * 
	 */
	public SeamEntityWizardPage1() {
		super("seam.new.entity.page1",SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_SEAM_ENTITY, null); //$NON-NLS-1$
		setMessage(getDefaultMessageText());
	}
	
	/**
	 * 
	 */
	@Override
	protected void createEditors() {
		addEditor(SeamWizardFactory.createSeamProjectSelectionFieldEditor(SeamWizardUtils.getSelectedProjectName()));
		addEditor(SeamWizardFactory.createSeamEntityClasNameFieldEditor());
		addEditor(SeamWizardFactory.createSeamMasterPageNameFieldEditor());
		addEditor(SeamWizardFactory.createSeamPageNameFieldEditor());
	}
	
	@Override
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));
		setPageComplete(false);
	}
	
	@Override
	public void doFillDefaults(PropertyChangeEvent event) {
		if(event.getPropertyName().equals(IParameter.SEAM_ENTITY_CLASS_NAME)) {
			if(event.getNewValue()==null||"".equals(event.getNewValue().toString().trim())) { //$NON-NLS-1$
				setDefaultValue(IParameter.SEAM_ENTITY_CLASS_NAME, ""); //$NON-NLS-1$
				setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, ""); //$NON-NLS-1$
				setDefaultValue(IParameter.SEAM_PAGE_NAME, ""); //$NON-NLS-1$
			} else {
				String value = event.getNewValue().toString();
				String valueU = value.substring(0,1).toUpperCase() + value.substring(1);
				String valueL = value.substring(0,1).toLowerCase() + value.substring(1);
				setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, valueL+SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_LIST);
				setDefaultValue(IParameter.SEAM_PAGE_NAME, valueL);
			}
		}
	}
	
	@Override
	protected void doValidate(PropertyChangeEvent event) {
		Map errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_PROJECT_NAME).getValue(), null);
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		
		IProject project = getSelectedProject();
		
		if(!isValidRuntimeConfigured(project)) return;

		errors = ValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_ENTITY_CLASS_NAME).getValue(), null);
		
		if(errors.size()>0) {
			setErrorMessage(NLS.bind(errors.get(IValidator.DEFAULT_ERROR).toString(),SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_ENTITY_CLASS_NAME));
			setPageComplete(false);
			return;
		}
		
		errors = ValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_MASTER_PAGE_NAME).getValue(), new Object[]{SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_ENTITY_MASTER_PAGE,project});
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		
		errors = ValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_PAGE_NAME).getValue(), new Object[]{SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_PAGE,project});
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setMessage(getDefaultMessageText());
		setPageComplete(true);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage#getDefaultMessageText()
	 */
	@Override
	public String getDefaultMessageText() {
		// TODO Auto-generated method stub
		return "Create a new Entity";
	}
}
