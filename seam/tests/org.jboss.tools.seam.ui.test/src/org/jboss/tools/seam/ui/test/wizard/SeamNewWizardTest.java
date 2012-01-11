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

import junit.framework.TestCase;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.wizard.ISeamParameter;
import org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage;
import org.jboss.tools.seam.ui.wizard.SeamGenerateEntitiesWizardPage;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author eskimo
 */
public class SeamNewWizardTest extends TestCase {

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-3752
	 */
	public void testSeamActionNewWizardInstanceIsInitialized() {
		testSeamBaseNewWizardInstanceIsInitialized(ISeamUiConstants.NEW_SEAM_ACTION_WIZARD_ID);
	}

	public void testSeamConversationNewWizardInstanceIsInitialized() {
		testSeamBaseNewWizardInstanceIsInitialized(ISeamUiConstants.NEW_SEAM_CONVERSATION_WIZARD_ID);
	}

	public void testSeamFormNewWizardInstanceIsInitialized() {
		testSeamBaseNewWizardInstanceIsInitialized(ISeamUiConstants.NEW_SEAM_FORM_WIZARD_ID);
	}

	public void testSeamEntityNewWizardInstanceIsInitialized() {
		testSeamBaseNewWizardInstanceIsInitialized(ISeamUiConstants.NEW_SEAM_ENTITY_WIZARD_ID);
	}

	public void testSeamBaseNewWizardInstanceIsInitialized(String wizardId) {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId(wizardId);

		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			SeamBaseWizardPage page = (SeamBaseWizardPage)dialog.getSelectedPage();

			page.getEditor(ISeamParameter.SEAM_PROJECT_NAME).setValue("Test1-ear");
			assertEquals("Seam web parent project was not initialized for Seam EAR project.", "Test1", page.getRootSeamProject());
		} finally {
			dialog.close();
		}
	}

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-10606
	 */
	public void testSeamGenerateEntitiesNewWizardInstanceIsInitialized() {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_GENERATE_SEAM_ENTITY_WIZARD_ID);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			SeamGenerateEntitiesWizardPage page = (SeamGenerateEntitiesWizardPage)dialog.getSelectedPage();

			page.getProjectEditor().setValue("Test1-ear");
			assertEquals("Seam web parent project was not initialized for Seam EAR project.", "Test1", page.getRootSeamProject().getName());
		} finally {
			dialog.close();
		}
	}
}