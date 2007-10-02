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

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage.GridLayoutComposite;

/**
 * @author eskimo
 *
 */
public class SeamConversationWizardPage1 extends SeamBaseWizardPage {

	public SeamConversationWizardPage1() {
		super("seam.new.conversation.page1","Seam Conversation",null);
		setMessage("Select the name of the new Seam Conversation. A set of classes " +
				"managing a coversation will be created.");
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));

		if (!"".equals(editorRegistry.get(IParameter.SEAM_PROJECT_NAME).getValue())){
			Map errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
					getEditor(IParameter.SEAM_PROJECT_NAME).getValue(), null);
			
			if(errors.size()>0) {
				setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
				getEditor(IParameter.SEAM_BEAN_NAME).setEnabled(false);
			}
		}
		setPageComplete(false);
	}
}
