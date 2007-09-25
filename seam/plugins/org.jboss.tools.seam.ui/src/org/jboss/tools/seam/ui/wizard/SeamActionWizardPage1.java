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

import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;

/**
 * @author eskimo
 *
 */
public class SeamActionWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamActionWizardPage1() {
		super("seam.new.action.page1", "Seam Action", null);
		setMessage("Select the name of the new Seam Conversation. A new Java interface and SLSB " +
				"with key Seam/EJB annotations will be created.");
	}
	
	protected void createEditors() {
		addEditors(SeamWizardFactory.createActionFormFieldEditors(SeamWizardUtils.getSelectedProjectName()));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(event.getPropertyName().equals(IParameter.SEAM_COMPONENT_NAME)) {
			if(event.getNewValue()==null||"".equals(event.getNewValue().toString().trim())) {
				setDefaultValue(IParameter.SEAM_COMPONENT_NAME, "");
				setDefaultValue(IParameter.SEAM_LOCAL_INTERFACE_NAME, "");
				setDefaultValue(IParameter.SEAM_BEAN_NAME, "");
				setDefaultValue(IParameter.SEAM_METHOD_NAME, "");
				setDefaultValue(IParameter.SEAM_PAGE_NAME, "");
			} else {
				String value = event.getNewValue().toString();
				String valueU = value.substring(0,1).toUpperCase() + value.substring(1);
				setDefaultValue(IParameter.SEAM_LOCAL_INTERFACE_NAME, "I" + valueU);
				setDefaultValue(IParameter.SEAM_BEAN_NAME, valueU+"Bean");
				String valueL = value.substring(0,1).toLowerCase() + value.substring(1);
				setDefaultValue(IParameter.SEAM_METHOD_NAME, valueL);
				setDefaultValue(IParameter.SEAM_PAGE_NAME, valueL);
			}
		}
		super.propertyChange(event);
	}
}
