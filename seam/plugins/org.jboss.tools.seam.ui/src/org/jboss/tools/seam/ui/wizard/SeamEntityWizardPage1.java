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

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage.GridLayoutComposite;

/**
 * @author eskimo
 *
 */
public class SeamEntityWizardPage1 extends SeamBaseWizardPage {

	/**
	 * 
	 */
	public SeamEntityWizardPage1() {
		super("seam.new.entity.page1","Seam Entity", null);
		setMessage("Select the name of the new Seam Entity. A new Seam Entity Bean with key Seam/EJB3 " +
				"annotations and wxample attributes will be created.");
	}
	
	/**
	 * 
	 */
	protected void createEditors() {
		addEditor(SeamWizardFactory.createSeamProjectSelectionFieldEditor(SeamWizardUtils.getSelectedProjectName()));
		addEditor(SeamWizardFactory.createSeamEntityClasNameFieldEditor());
		addEditor(SeamWizardFactory.createSeamMasterPageNameFieldEditor());
		addEditor(SeamWizardFactory.createSeamPageNameFieldEditor());
	}
	
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));
		setPageComplete(false);
	}
	
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getPropertyName().equals(IParameter.SEAM_ENTITY_CLASS_NAME)) {
			if(event.getNewValue()==null||"".equals(event.getNewValue().toString().trim())) {
				setDefaultValue(IParameter.SEAM_COMPONENT_NAME, "");
				setDefaultValue(IParameter.SEAM_LOCAL_INTERFACE_NAME, "");
						setDefaultValue(IParameter.SEAM_METHOD_NAME, "");
				setDefaultValue(IParameter.SEAM_PAGE_NAME, "");
			} else {
				String value = event.getNewValue().toString();
				String valueU = value.substring(0,1).toUpperCase() + value.substring(1);
				String valueL = value.substring(0,1).toLowerCase() + value.substring(1);
				setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, valueL);
				setDefaultValue(IParameter.SEAM_PAGE_NAME, valueL+"List");
			}
		}

		Map errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_PROJECT_NAME).getValue(), null);
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		
		IResource project = getSelectedProject();


		errors = ValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_ENTITY_CLASS_NAME).getValue(), null);
		
		if(errors.size()>0) {
			setErrorMessage(NLS.bind(errors.get(IValidator.DEFAULT_ERROR).toString(),"Entity class name"));
			setPageComplete(false);
			return;
		}
		
		errors = ValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_MASTER_PAGE_NAME).getValue(), (Object)new Object[]{"Entity master page",project});
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		
		errors = ValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(IParameter.SEAM_PAGE_NAME).getValue(), (Object)new Object[]{"Page",project});
		
		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setMessage(null);
		setPageComplete(true);
	}
}
