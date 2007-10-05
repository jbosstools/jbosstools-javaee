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
public class SeamActionWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamActionWizardPage1() {
		super("seam.new.action.page1", SeamUIMessages.SEAM_ACTION_WIZARD_PAGE1_SEAM_ACTION, null); //$NON-NLS-1$
		setMessage(SeamUIMessages.SEAM_ACTION_WIZARD_PAGE1_SELECT_THE_NAME_OF_THE_SEAM_CONVERSATION +
				SeamUIMessages.SEAM_ACTION_WIZARD_PAGE1_WITH_KEY_SEAM_EJB_ANNOTATIONS_WILL_BE_CREATED);
	}
	
	protected void createEditors() {
		addEditors(SeamWizardFactory.createBaseFormFieldEditors(SeamWizardUtils.getSelectedProjectName()));
	}
}
