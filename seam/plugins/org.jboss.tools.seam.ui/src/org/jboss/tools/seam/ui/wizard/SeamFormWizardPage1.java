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

import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;


/**
 * @author eskimo
 *
 */
public class SeamFormWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param pageName
	 */
	public SeamFormWizardPage1() {
		super("seam.new.form.page1",SeamUIMessages.SEAM_FORM_WIZARD_PAGE1_SEAM_FORM,null); //$NON-NLS-1$
		setMessage(SeamUIMessages.SEAM_FORM_WIZARD_PAGE1_SELECT_THE_NAME_OF_THE_NEW_SEAM_FORM +
				SeamUIMessages.SEAM_FORM_WIZARD_PAGE1_JAVA_INTERFACE_SLSB_AND_KEY_SEAMEJB3_ANNOTATIONS_WILL_BE_CREATED);
	}
	
	protected void createEditors() {
		addEditors(SeamWizardFactory.createBaseFormFieldEditors(SeamWizardUtils.getSelectedProjectName()));
	}
}
