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
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.wizard.IParameter;
import org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;

/**
 * @author eskimo
 *
 */
public class SeamActionNewWizardTest extends TestCase {

	/**
	 * https://jira.jboss.org/jira/browse/JBIDE-3752
	 */
	public void testSeamActionNewWizardInstanceIsInitialized() {
		IWizard
		aWizard = WorkbenchUtils.findWizardByDefId(
				ISeamUiConstants.NEW_SEAM_ACTION_WIZARD_ID);

		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				aWizard);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			SeamBaseWizardPage page = (SeamBaseWizardPage)dialog.getSelectedPage();

			page.getEditor(IParameter.SEAM_PROJECT_NAME).setValue("Test1-ear");
			assertEquals("Seam web parent project was not initialized for Seam EAR project.", "Test1", page.getRootSeamProject());
		} finally {
			dialog.close();
		}
	}
}