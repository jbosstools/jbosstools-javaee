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
package org.jboss.tools.seam.ui.test.wizard;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage;

/**
 * @author eskimo
 *
 */
public class SeamFormNewWizardTest extends TestCase {

	/**
	 * 
	 */
	public void checkNewWizardStartPage(String ID) {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId(
				ID);
		
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			SeamBaseWizardPage page = (SeamBaseWizardPage)dialog.getSelectedPage();
			Map<String,IFieldEditor> editors = (Map<String,IFieldEditor>) page.getAdapter(Map.class);
			for (IFieldEditor editor : editors.values()) {
				Object[] controls = editor.getEditorControls();
				for (Object object : controls) {
					assertNotNull(object);
				}
			}
		} finally {
			dialog.close();
		}
	}
	
	/**
	 * 
	 */
	public void testSeamActionNewWizardIsCreated() {
		checkNewWizardStartPage(
				ISeamUiConstants.NEW_SEAM_ACTION_WIZARD_ID);
	}
	/**
	 * 
	 */
	public void testSeamActionNewFormWizardIsCreated() {
		checkNewWizardStartPage(
				ISeamUiConstants.NEW_SEAM_FORM_WIZARD_ID);
	}
	
	/**
	 * 
	 */
	public void testSeamActionNewConversationIsCreated() {
		checkNewWizardStartPage(
				ISeamUiConstants.NEW_SEAM_CONVERSATION_WIZARD_ID);
	}
	
	/**
	 * 
	 */
	public void testSeamActionNewEntityIsCreated() {
		checkNewWizardStartPage(
				ISeamUiConstants.NEW_SEAM_ENTITY_WIZARD_ID);
	}
}
