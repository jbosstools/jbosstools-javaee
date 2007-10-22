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
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;

/**
 * @author eskimo
 *
 */
public class SeamEntityWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param is 
	 * 
	 */
	public SeamEntityWizardPage1(IStructuredSelection is) {
		super("seam.new.entity.page1","Seam Entity", null, is); 
		setMessage(getDefaultMessageText());
	}

	/**
	 * 
	 */
	@Override
	protected void createEditors() {
		super.createEditors();

		String selectedProject = SeamWizardUtils.getRootSeamProjectName(initialSelection);
		String packageName = getDefaultPackageName(selectedProject);
		addEditor(SeamWizardFactory.createSeamJavaPackageSelectionFieldEditor(packageName));
		setSeamProjectNameData(selectedProject);

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
			if(event.getNewValue()==null||"".equals(event.getNewValue().toString().trim())) { 
				setDefaultValue(IParameter.SEAM_ENTITY_CLASS_NAME, ""); 
				setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, ""); 
				setDefaultValue(IParameter.SEAM_PAGE_NAME, ""); 
			} else {
				String value = event.getNewValue().toString();
				String valueL = value.substring(0,1).toLowerCase() + value.substring(1);
				setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, valueL+"List");
				setDefaultValue(IParameter.SEAM_PAGE_NAME, valueL);
			}
		}
		if(event.getPropertyName().equals(IParameter.SEAM_PROJECT_NAME)) {
			String selectedProject = event.getNewValue().toString();
			setSeamProjectNameData(selectedProject);
			setDefaultValue(IParameter.SEAM_PACKAGE_NAME, getDefaultPackageName(selectedProject));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage#getDefaultPackageName(org.eclipse.core.runtime.preferences.IEclipsePreferences)
	 */
	@Override
	protected String getDefaultPackageName(IEclipsePreferences seamFacetPrefs) {
		return seamFacetPrefs.get(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "");
	}

	@Override
	protected void doValidate(PropertyChangeEvent event) {
		if(!isValidProjectSelected()) return;

		Map errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_PROJECT_NAME).getValue(), null);

		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		getEditor(IParameter.SEAM_BEAN_NAME).setEnabled(true);
		IFieldEditor packageEditor = getEditor(IParameter.SEAM_PACKAGE_NAME);
		if(packageEditor!=null) {
			packageEditor.setEnabled(true);
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

		IFieldEditor editor = editorRegistry.get(IParameter.SEAM_PACKAGE_NAME);
		if(editor!=null) {
			errors = ValidatorFactory.PACKAGE_NAME_VALIDATOR.validate(editor.getValue(), null);
			if(errors.size()>0) {
				setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString()); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}
		}

		errors = ValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_MASTER_PAGE_NAME).getValue(), new Object[]{SeamUIMessages.SEAM_ENTITY_WIZARD_PAGE1_ENTITY_MASTER_PAGE,project,project});

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
		return "Create a new Entity";
	}
}